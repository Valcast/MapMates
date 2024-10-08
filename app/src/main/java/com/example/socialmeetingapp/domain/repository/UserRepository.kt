package com.example.socialmeetingapp.domain.repository

import com.example.socialmeetingapp.domain.model.UserResult
import com.example.socialmeetingapp.domain.model.User

interface UserRepository {
    fun isLoggedIn(): Boolean
    fun getCurrentUser(): User

    suspend fun registerUser(email: String, password: String): UserResult
    suspend fun loginUser(email: String, password: String): UserResult
    suspend fun resetPassword(email: String): UserResult
    suspend fun updateUsername(username: String): UserResult
    suspend fun updateAvatar(avatar: String): UserResult
    suspend fun modifyUser(user: User): UserResult

    fun logout()
}