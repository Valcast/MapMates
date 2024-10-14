package com.example.socialmeetingapp.data.repository

import com.example.socialmeetingapp.data.utils.NetworkManager
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.model.UserUpdateData
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.asDeferred
import java.util.Date

class FirebaseUserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val networkManager: NetworkManager,
    private val db: FirebaseFirestore
) : UserRepository {
    override fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun getCurrentUser(): UserResult {
        return getUser(firebaseAuth.currentUser!!.uid)
    }

    override suspend fun getUser(id: String): UserResult {
        if (!networkManager.isConnected) {
            return UserResult.Error("No internet connection")
        }

        try {
            val userDocument =
                db.collection("users").document(id).get().asDeferred()
                    .await()

            val user = User(
                id = userDocument.id,
                email = userDocument.getString("email")!!,
                username = userDocument.getString("username"),
                bio = userDocument.getString("bio"),
                dateOfBirth = userDocument.getDate("dateOfBirth"),
                gender = userDocument.getString("gender"),
                status = userDocument.getString("status"),
                role = userDocument.getString("role"),
                isVerified = userDocument.getBoolean("isVerified"),
                createdAt = userDocument.getDate("createdAt")!!,
                lastLogin = userDocument.getDate("lastLogin"),
                lastPasswordChange = userDocument.getDate("lastPasswordChange")
            )

            return UserResult.SuccessSingle(user)
        } catch (e: FirebaseFirestoreException) {
            return UserResult.Error(e.message ?: "Unknown error")
        }
    }


    override suspend fun registerUser(
        email: String,
        password: String
    ): UserResult {
        if (!networkManager.isConnected) {
            return UserResult.Error("No internet connection")
        }

        try {
            val authResult: com.google.firebase.auth.AuthResult? =
                firebaseAuth.createUserWithEmailAndPassword(email, password).asDeferred().await()
            return if (authResult != null) {
                db.collection("users").document(authResult.user!!.uid).set(
                    hashMapOf(
                        "email" to email,
                        "createdAt" to Timestamp(Date(System.currentTimeMillis())),
                        "lastLogin" to Timestamp(Date(System.currentTimeMillis())),
                    )
                )

                UserResult.Success
            } else {
                UserResult.Error("User creation failed")
            }
        } catch (_: FirebaseAuthWeakPasswordException) {
            return UserResult.Error("Password is too weak")
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            return UserResult.Error("Invalid email address")
        } catch (_: FirebaseAuthUserCollisionException) {
            return UserResult.Error("Email address already in use")
        } catch (e: Exception) {
            return UserResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun loginUser(email: String, password: String): UserResult {
        if (!networkManager.isConnected) {
            return UserResult.Error("No internet connection")
        }

        try {
            val authResult: com.google.firebase.auth.AuthResult? =
                firebaseAuth.signInWithEmailAndPassword(email, password).asDeferred().await()
            return if (authResult != null) {
                UserResult.Success
            } else {
                UserResult.Error("Login failed")
            }
        } catch (_: FirebaseAuthException) {
            return UserResult.Error("Email address or password is incorrect")
        }

    }

    override suspend fun resetPassword(email: String): UserResult {
        try {
            firebaseAuth.sendPasswordResetEmail(email).asDeferred().await()
            return UserResult.Success
        } catch (e: Exception) {
            return UserResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun modifyUser(updates: UserUpdateData): UserResult {
        if (!networkManager.isConnected) {
            return UserResult.Error("No internet connection")
        }

        if (!isLoggedIn()) {
            return UserResult.Error("User not authenticated")
        }

        try {
            val userDocument = db.collection("users").document(firebaseAuth.currentUser!!.uid)

            val updateData = hashMapOf<String, Any>().apply {
                updates.username?.let { put("username", it) }
                updates.bio?.let { put("bio", it) }
                updates.lastLogin?.let { put("lastLogin", Timestamp(it)) }
                updates.createdAt?.let { put("createdAt", Timestamp(it)) }
                updates.updatedAt?.let { put("updatedAt", Timestamp(it)) }
            }

            userDocument.set(updateData, SetOptions.merge()).asDeferred().await()

            return UserResult.Success
        } catch (e: Exception) {
            return UserResult.Error(e.message ?: "Unknown error")
        }

    }


    override fun logout() {
        firebaseAuth.signOut()
    }
}