package com.example.socialmeetingapp.presentation.snackbar

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SnackbarManager {
    private var _messages = MutableSharedFlow<String>(replay = 0)
    val messages = _messages.asSharedFlow()

    suspend fun showMessage(message: String) {
        _messages.emit(message)
    }
}