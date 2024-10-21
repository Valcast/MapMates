package com.example.socialmeetingapp.domain.user.repository

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.model.UserUpdateData
import com.google.firebase.firestore.DocumentReference

interface UserRepository {
    fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): Result<User>
    suspend fun getUser(id: String): Result<User>

    suspend fun registerUser(email: String, password: String): Result<Unit>
    suspend fun loginUser(email: String, password: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun modifyUser(updates: UserUpdateData): Result<Unit>


    fun logout()
}