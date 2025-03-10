package com.example.socialmeetingapp.domain.repository

import android.net.Uri
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.SignUpStatus
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.model.UserPreview
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val authenticationStatus: Flow<FirebaseUser?>
    suspend fun getCurrentUser(): Result<User>
    suspend fun getCurrentUserPreview(): Result<UserPreview>

    suspend fun getUser(id: String): Result<User>
    suspend fun getUsers(ids: List<String>): Result<List<User>>
    suspend fun getUserPreview(id: String): Result<UserPreview>
    suspend fun getUsersPreviews(ids: List<String>): Result<List<UserPreview>>
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUpWithGoogle(idToken: String): Result<SignUpStatus>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun followUser(friendID: String): Result<Unit>
    suspend fun unfollowUser(friendID: String): Result<Unit>
    suspend fun deleteFollower(friendID: String): Result<Unit>
    suspend fun uploadProfilePicture(imageUri: Uri): Result<Uri>
    suspend fun sendEmailVerification(): Result<Unit>
    fun signOut()
}