package com.example.socialmeetingapp.domain.repository

import android.net.Uri
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.SignUpStatus
import com.example.socialmeetingapp.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val currentUser: StateFlow<Result<User?>>

    suspend fun getUser(id: String): Result<User>

    suspend fun signIn(email: String, password: String): Result<Unit>

    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signUpWithGoogle(idToken: String): Result<SignUpStatus>

    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun updateUser(user: User): Result<Unit>

    suspend fun uploadProfilePicture(imageUri: Uri): Result<Uri>

    suspend fun sendEmailVerification(): Result<Unit>

    suspend fun followUser(friendID: String): Result<Unit>
    suspend fun unfollowUser(friendID: String): Result<Unit>


    suspend fun deleteFollower(friendID: String): Result<Unit>

    suspend fun getUserPreferences(): Result<Map<String, Any>>
    suspend fun updateUserPreferences(preferences: Map<String, Any>): Result<Unit>

    suspend fun markNotificationAsRead(notificationId: String): Result<Unit>

    fun signOut()
}