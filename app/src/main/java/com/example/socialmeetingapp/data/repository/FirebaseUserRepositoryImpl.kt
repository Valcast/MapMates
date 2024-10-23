package com.example.socialmeetingapp.data.repository

import com.example.socialmeetingapp.data.utils.NetworkManager
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.model.UserUpdateData
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import com.google.android.gms.auth.api.Auth
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Date

class FirebaseUserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val networkManager: NetworkManager,
    private val db: FirebaseFirestore
) : UserRepository {
    override fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun getCurrentUser(): Result<User> {
        return getUser(firebaseAuth.currentUser!!.uid)
    }

    override suspend fun getUser(id: String): Result<User> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        try {
            val userDocument = db.collection("users").document(id).get().asDeferred().await()

            val user = User(
                id = userDocument.id,
                email = userDocument.getString("email") ?: return Result.Error("User not found"),
                username = userDocument.getString("username")
                    ?: return Result.Error("User not found"),
                bio = userDocument.getString("bio") ?: return Result.Error("User not found"),
                dateOfBirth = userDocument.getDate("dateOfBirth")
                    ?: return Result.Error("User not found"),
                gender = userDocument.getString("gender") ?: return Result.Error("User not found"),
                role = userDocument.getString("role") ?: return Result.Error("User not found"),
                isVerified = userDocument.getBoolean("isVerified")
                    ?: return Result.Error("User not found"),
                createdAt = userDocument.getString("createdAt")?.let { LocalDateTime.parse(it) }
                    ?: return Result.Error("Created at is missing"),
                updatedAt = userDocument.getString("updatedAt")?.let { LocalDateTime.parse(it) }
                    ?: return Result.Error("Updated at is missing"),
                lastPasswordChange = userDocument.getString("lastPasswordChange")
                    ?.let { LocalDateTime.parse(it) }
                    ?: return Result.Error("Last password change is missing"),
                lastLogin = userDocument.getString("lastLogin")?.let { LocalDateTime.parse(it) }
                    ?: return Result.Error("Last login is missing"),
            )

            return Result.Success(user)
        } catch (e: FirebaseFirestoreException) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }


    override suspend fun registerUser(
        email: String,
        password: String
    ): Result<Unit> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        return try {
            val authResult: AuthResult = firebaseAuth.createUserWithEmailAndPassword(email, password).asDeferred().await()

            val currentMoment = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            db.collection("users").document(authResult.user!!.uid).set(
                hashMapOf(
                    "email" to email,
                    "createdAt" to currentMoment.toString(),
                    "updatedAt" to currentMoment.toString(),
                    "lastLogin" to currentMoment.toString(),
                    "lastPasswordChange" to currentMoment.toString(),
                    "role" to "User",
                    "isVerified" to false,
                    "gender" to "Not specified",
                    "dateOfBirth" to Date(0),
                    "bio" to "",
                    "username" to ""
                )
            )

            Result.Success(Unit)
        } catch (_: FirebaseAuthWeakPasswordException) {
            Result.Error("Password is too weak")
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            Result.Error("Invalid email address")
        } catch (_: FirebaseAuthUserCollisionException) {
            Result.Error("Email address already in use")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<Unit> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).asDeferred().await()
            db.collection("users").document(authResult.user!!.uid).update("lastLogin", Clock.System.now().toLocalDateTime(
                TimeZone.UTC).toString())

            Result.Success(Unit)
        } catch (_: FirebaseAuthException) {
            Result.Error("Email address or password is incorrect")
        }

    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).asDeferred().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun modifyUser(updates: UserUpdateData): Result<Unit> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        if (!isLoggedIn()) {
            return Result.Error("User not authenticated")
        }

        try {
            val userDocument = db.collection("users").document(firebaseAuth.currentUser!!.uid)

            val updateData = hashMapOf<String, Any>().apply {
                updates.username?.let { put("username", it) }
                updates.bio?.let { put("bio", it) }
            }

            userDocument.set(updateData, SetOptions.merge()).asDeferred().await()

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(e.message ?: "Unknown error")
        }

    }


    override fun logout() {
        firebaseAuth.signOut()
    }
}