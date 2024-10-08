package com.example.socialmeetingapp.data.repository

import com.example.socialmeetingapp.data.utils.NetworkManager
import com.example.socialmeetingapp.domain.model.UserResult
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.asDeferred

class FirebaseUserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val networkManager: NetworkManager,
    private val firestoreDatabase: FirebaseFirestore
): UserRepository {
    override fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun getCurrentUser(): User {
        val firebaseUser = firebaseAuth.currentUser
        return if (firebaseUser != null) {
            User(firebaseUser.displayName ?: "", firebaseUser.email ?: "", )
        } else {
            User("", "",)
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
            val authResult: com.google.firebase.auth.AuthResult? = firebaseAuth.createUserWithEmailAndPassword(email, password).asDeferred().await()
            return if (authResult != null) {
                firestoreDatabase.collection("users").document(authResult.user!!.uid).set(User())

                UserResult.Success
            } else {
                UserResult.Error("User creation failed")
            }
        } catch (e: FirebaseAuthWeakPasswordException) {
            return UserResult.Error("Password is too weak")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            return UserResult.Error("Invalid email address")
        } catch (e: FirebaseAuthUserCollisionException) {
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
            val authResult: com.google.firebase.auth.AuthResult? = firebaseAuth.signInWithEmailAndPassword(email, password).asDeferred().await()
            return if (authResult != null) {
                UserResult.Success
            } else {
                UserResult.Error("Login failed")
            }
        } catch (e: FirebaseAuthException) {
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

    override suspend fun updateUsername(username: String): UserResult {
        if (!networkManager.isConnected) {
            return UserResult.Error("No internet connection")
        }

        if (!isLoggedIn()) {
            return UserResult.Error("User not authenticated")
        }

        try {
            val profileUpdates = userProfileChangeRequest {
                displayName = username
            }

            firebaseAuth.currentUser!!.updateProfile(profileUpdates).asDeferred().await()
            return UserResult.Success
        } catch (e: Exception) {
            return UserResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateAvatar(avatar: String): UserResult {
        TODO()
    }

    override suspend fun modifyUser(user: User): UserResult {
        TODO()
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}