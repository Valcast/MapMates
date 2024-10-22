package com.example.socialmeetingapp.presentation.snackbar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SnackbarManager {
    private var _messages = MutableStateFlow("")
    val messages = _messages.asStateFlow()

    suspend fun showMessage(message: String) {
        _messages.emit(message)
    }
}