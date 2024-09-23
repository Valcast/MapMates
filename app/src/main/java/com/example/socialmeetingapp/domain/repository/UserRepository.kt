package com.example.socialmeetingapp.domain.repository

import com.example.socialmeetingapp.domain.model.AuthResult

interface UserRepository {
    fun ifUserIsLoggedIn(): Boolean
    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult

}