package com.example.socialmeetingapp.domain.model

sealed class AuthResult {
    data class Success(val authResult: com.google.firebase.auth.AuthResult) : AuthResult()
    data class Error(val message: String) : AuthResult()
}