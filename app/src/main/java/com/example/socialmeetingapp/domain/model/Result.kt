package com.example.socialmeetingapp.domain.model

sealed class Result<out T> {
    data object Initial : Result<Nothing>()
    data object Loading : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}