package com.example.socialmeetingapp.presentation.authentication

sealed class AuthenticationState {
    data object Initial : AuthenticationState()
    data object Loading : AuthenticationState()
    data object Success : AuthenticationState()
    class Error(val message: String) : AuthenticationState()
}