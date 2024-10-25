package com.example.socialmeetingapp.domain.user.repository

import android.net.Uri
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.model.UserUpdateData
import com.google.firebase.firestore.DocumentReference

interface UserRepository {
    suspend fun getCurrentUser(): Result<User>
    suspend fun getUser(id: String): Result<User>
    fun isCurrentUserVerified(): Boolean
    suspend fun refreshUser(): Result<Unit>

    suspend fun registerUser(email: String, password: String): Result<Unit>
    suspend fun loginUser(email: String, password: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun updateUser(user: User): Result<Unit>

    suspend fun uploadProfilePicture(imageUri: Uri): Result<Uri>
    suspend fun sendEmailVerification(): Result<Unit>

    fun logout()
}