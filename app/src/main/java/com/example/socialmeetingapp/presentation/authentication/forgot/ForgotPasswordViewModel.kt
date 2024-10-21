package com.example.socialmeetingapp.presentation.authentication.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import com.example.socialmeetingapp.domain.user.usecase.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
): ViewModel() {
    private var _state =
        MutableStateFlow<Result<Unit>>(Result.Initial)
    val state = _state.asStateFlow()

    suspend fun resetPassword(email: String) {
        _state.value = Result.Loading

        viewModelScope.launch {
            when (val resetResult = resetPasswordUseCase(email)) {
                is Result.Success -> {
                    _state.value = Result.Success()
                }

                is Result.Error -> {
                    _state.value = Result.Error(resetResult.message)
                }

                else -> { return@launch }
            }
        }
    }
}