package com.example.socialmeetingapp.presentation.authentication.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.usecase.RegisterUserUseCase
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {
    private var _state = MutableStateFlow<Result<Unit>>(Result.Initial)
    val state: StateFlow<Result<Unit>> = _state.asStateFlow()

    fun registerUser(email: String, password: String, confirmPassword: String) {
        _state.value = Result.Loading

        viewModelScope.launch {
            when (val registerResult = registerUserUseCase(email, password, confirmPassword)) {
                is Result.Success<Unit> -> {
                    _state.value = Result.Success(Unit)
                    NavigationManager.navigateTo(Routes.RegisterProfileInfo)
                }

                is Result.Error -> {
                    _state.value = Result.Error(registerResult.message)
                }

                else -> {
                    return@launch
                }
            }
        }
    }
}