package com.example.socialmeetingapp.presentation.authentication.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.onFailure
import com.example.socialmeetingapp.domain.model.onSuccess
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun resetPassword(email: String) {
        _uiState.value = ForgotPasswordUiState.Loading

        viewModelScope.launch {
            userRepository.resetPassword(email)
                .onSuccess {
                    _uiState.value = ForgotPasswordUiState.Success
                    SnackbarManager.showMessage("Password reset email sent")
                }
                .onFailure { error ->
                    _uiState.value = ForgotPasswordUiState.Error(error)
                }
        }
    }
}

sealed class ForgotPasswordUiState {
    object Initial : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    object Success : ForgotPasswordUiState()
    data class Error(val message: String) : ForgotPasswordUiState()
}