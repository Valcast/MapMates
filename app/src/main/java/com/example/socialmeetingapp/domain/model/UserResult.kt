package com.example.socialmeetingapp.domain.model

sealed class UserResult {
    data object Success : UserResult()
    data class Error(val message: String) : UserResult()
}