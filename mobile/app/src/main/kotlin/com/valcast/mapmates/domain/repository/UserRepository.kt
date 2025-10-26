package com.valcast.mapmates.domain.repository

import android.net.Uri
import com.valcast.mapmates.data.source.FollowersPagingSource
import com.valcast.mapmates.data.source.FollowingPagingSource
import com.valcast.mapmates.domain.model.Result
import com.valcast.mapmates.domain.model.SignUpStatus
import com.valcast.mapmates.domain.model.User
import com.valcast.mapmates.domain.model.UserPreview
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface UserRepository {
    val authenticationStatus: Flow<FirebaseUser?>
    suspend fun getCurrentUserId(): String?
    suspend fun getCurrentUser(): Result<User>
    suspend fun getCurrentUserPreview(): Result<UserPreview>
    suspend fun getUser(id: String): Result<User>
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
    suspend fun updateProfilePicture(imageUri: Uri): Result<Uri>
    suspend fun sendEmailVerification(): Result<Unit>

    fun getFollowersPagingSource(userId: String): FollowersPagingSource
    fun getFollowingPagingSource(userId: String): FollowingPagingSource

    suspend fun updateUsernameAndDateOfBirth(
        username: String,
        dateOfBirth: LocalDateTime
    ): Result<Unit>

    suspend fun updateBio(bio: String): Result<Unit>


    fun signOut()
}