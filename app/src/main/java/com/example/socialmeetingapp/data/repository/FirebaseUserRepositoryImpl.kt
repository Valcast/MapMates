package com.example.socialmeetingapp.data.repository

import com.example.socialmeetingapp.data.utils.NetworkManager
import com.example.socialmeetingapp.domain.model.AuthResult
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.asDeferred
import javax.inject.Inject

class FirebaseUserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val networkManager: NetworkManager
): UserRepository {

    override fun ifUserIsLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): AuthResult {
        if (!networkManager.isConnected) {
            return AuthResult.Error("No internet connection")
        }

        try {
            val authResult: com.google.firebase.auth.AuthResult? = firebaseAuth.createUserWithEmailAndPassword(email, password).asDeferred().await()
            return if (authResult != null) {
                AuthResult.Success(authResult)

            } else {
                AuthResult.Error("User creation failed")
            }
        } catch (e: Exception) {
            return AuthResult.Error(e.message ?: "Unknown error")
        }
    }
}