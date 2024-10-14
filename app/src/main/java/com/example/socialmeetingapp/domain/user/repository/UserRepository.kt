package com.example.socialmeetingapp.domain.user.repository

import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.model.UserUpdateData
import com.google.firebase.firestore.DocumentReference

interface UserRepository {
    fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): UserResult
    suspend fun getUser(id: String): UserResult

    suspend fun registerUser(email: String, password: String): UserResult
    suspend fun loginUser(email: String, password: String): UserResult
    suspend fun resetPassword(email: String): UserResult
    suspend fun modifyUser(updates: UserUpdateData): UserResult


    fun logout()
}