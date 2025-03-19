package com.example.socialmeetingapp.domain.model

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val message: String) : Result<Nothing>()

    fun getOrNull(): T? {
        return when (this) {
            is Success -> this.data
            is Failure -> null
        }
    }
}

suspend fun <T> Result<T>.onSuccess(action: suspend (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(this.data)
    }
    return this
}

suspend fun <T> Result<T>.onFailure(action: suspend (String) -> Unit): Result<T> {
    if (this is Result.Failure) {
        action(this.message)
    }
    return this
}

fun <T> Result<T>.getOrDefault(defaultValue: T): T {
    return when (this) {
        is Result.Success -> this.data
        is Result.Failure -> defaultValue
    }
}