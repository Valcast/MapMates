package com.example.socialmeetingapp.presentation.authentication.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Result
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
): ViewModel() {
    private var _state =
        MutableStateFlow<Result<Unit>>(Result.Initial)
    val state = _state.asStateFlow()

    fun resetPassword(email: String) {
        _state.value = Result.Loading

        viewModelScope.launch {
            when (val resetResult = userRepository.resetPassword(email)) {
                is Result.Success -> {
                    _state.value = Result.Success(Unit)
                    SnackbarManager.showMessage("Password reset email sent")
                }

                is Result.Error -> {
                    _state.value = Result.Error(resetResult.message)
                }

                else -> { return@launch }
            }
        }
    }
}