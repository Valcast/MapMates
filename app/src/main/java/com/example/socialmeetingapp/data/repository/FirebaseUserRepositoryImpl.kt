package com.example.socialmeetingapp.data.repository

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.socialmeetingapp.data.utils.NetworkManager
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.SignUpStatus
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class FirebaseUserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val networkManager: NetworkManager,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val dataStore: DataStore<Preferences>
) : UserRepository {

    private fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun isCurrentUserVerified(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified == true
    }

    override suspend fun refreshUser(): Result<Unit> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        if (!isLoggedIn()) {
            return Result.Error("User not authenticated")
        }

        return try {
            firebaseAuth.currentUser!!.reload().await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }

    }

    override suspend fun getCurrentUser(): Result<User> {
        if (!isLoggedIn()) {
            return Result.Error("User not authenticated")
        }

        return getUser(firebaseAuth.currentUser!!.uid)
    }

    override suspend fun getUser(id: String): Result<User> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        try {
            val userDocument = db.collection("users").document(id).get().await()

            val user = User(
                id = userDocument.id,
                email = userDocument.getString("email") ?: return Result.Error("User not found"),
                username = userDocument.getString("username")
                    ?: return Result.Error("User not found"),
                bio = userDocument.getString("bio") ?: return Result.Error("User not found"),
                dateOfBirth = userDocument.getString("dateOfBirth")?.let {
                    if (it == "Not specified") {
                        Clock.System.now().toLocalDateTime(TimeZone.UTC)
                    } else {
                        LocalDateTime.parse(it)
                    }
                }
                    ?: return Result.Error("Birth Date is missing"),
                gender = userDocument.getString("gender") ?: return Result.Error("User not found"),
                role = userDocument.getString("role") ?: return Result.Error("User not found"),
                createdAt = userDocument.getString("createdAt")?.let { LocalDateTime.parse(it) }
                    ?: return Result.Error("Created at is missing"),
                updatedAt = userDocument.getString("updatedAt")?.let { LocalDateTime.parse(it) }
                    ?: return Result.Error("Updated at is missing"),
                lastPasswordChange = userDocument.getString("lastPasswordChange")
                    ?.let { LocalDateTime.parse(it) }
                    ?: return Result.Error("Last password change is missing"),
                lastLogin = userDocument.getString("lastLogin")?.let { LocalDateTime.parse(it) }
                    ?: return Result.Error("Last login is missing"),
                profilePictureUri = userDocument.getString("profilePictureUri")
                    ?.let { Uri.parse(it) } ?: Uri.EMPTY
            )



            return Result.Success(user)
        } catch (e: FirebaseFirestoreException) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }


    override suspend fun signUp(
        email: String,
        password: String
    ): Result<Unit> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        return try {
            val authResult: AuthResult =
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            val currentMoment = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            db.collection("users").document(authResult.user!!.uid).set(
                hashMapOf(
                    "email" to email,
                    "createdAt" to currentMoment.toString(),
                    "updatedAt" to currentMoment.toString(),
                    "lastLogin" to currentMoment.toString(),
                    "lastPasswordChange" to currentMoment.toString(),
                    "role" to "User",
                    "gender" to "Not specified",
                    "dateOfBirth" to "Not specified",
                    "bio" to "",
                    "username" to "",
                    "profilePictureUri" to ""
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

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            db.collection("users").document(authResult.user!!.uid).update(
                "lastLogin", Clock.System.now().toLocalDateTime(
                    TimeZone.UTC
                ).toString()
            )

            Result.Success(Unit)
        } catch (_: FirebaseAuthException) {
            Result.Error("Email address or password is incorrect")
        }

    }

    override suspend fun signUpWithGoogle(idToken: String): Result<SignUpStatus> {
            if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()

            val currentMoment = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            if (db.collection("users").document(authResult.user!!.uid).get().await().exists()) {
                db.collection("users").document(authResult.user!!.uid).update(
                    "lastLogin", currentMoment.toString()
                )
                return Result.Success(SignUpStatus.ExistingUser)
            }

            db.collection("users").document(authResult.user!!.uid).set(
                hashMapOf(
                    "email" to authResult.user!!.email,
                    "createdAt" to currentMoment.toString(),
                    "updatedAt" to currentMoment.toString(),
                    "lastLogin" to currentMoment.toString(),
                    "lastPasswordChange" to currentMoment.toString(),
                    "role" to "User",
                    "gender" to "Not specified",
                    "dateOfBirth" to "Not specified",
                    "bio" to "",
                    "username" to "",
                    "profilePictureUri" to ""
                )
            )

            Result.Success(SignUpStatus.NewUser)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e.message ?: "Unknown error")
        } }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        if (!isLoggedIn()) {
            return Result.Error("User not authenticated")
        }

        try {
            val userDocument = db.collection("users").document(firebaseAuth.currentUser!!.uid)

            val currentMoment = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            userDocument.set(
                hashMapOf(
                    "updatedAt" to currentMoment.toString(),
                    "gender" to user.gender,
                    "dateOfBirth" to user.dateOfBirth.toString(),
                    "bio" to user.bio,
                    "username" to user.username,
                    "profilePictureUri" to user.profilePictureUri.toString(),
                ), SetOptions.merge()
            ).await()

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(e.message ?: "Unknown error")
        }

    }

    override suspend fun uploadProfilePicture(imageUri: Uri): Result<Uri> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        if (!isLoggedIn()) {
            return Result.Error("User not authenticated")
        }

        try {
            val storageRef =
                storage.reference.child("profile_pictures/${firebaseAuth.currentUser!!.uid}")

            storageRef.putFile(imageUri).await()

            val downloadUrl = storageRef.downloadUrl.await()

            return Result.Success(downloadUrl)
        } catch (e: Exception) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        if (!networkManager.isConnected) {
            return Result.Error("No internet connection")
        }

        if (!isLoggedIn()) {
            return Result.Error("User not authenticated")
        }

        try {
            firebaseAuth.currentUser!!.sendEmailVerification().await()
            return Result.Success(Unit)


        } catch (e: Exception) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getUserPreferences(): Result<Map<String, Any>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserPreferences(preferences: Map<String, Any>): Result<Unit> {
        TODO("Not yet implemented")
    }


    override fun signOut() = firebaseAuth.signOut()
}