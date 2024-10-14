package com.example.socialmeetingapp.domain.common.model

sealed class Resource<out T> {
    data object Initial : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T? = null) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}