package com.example.socialmeetingapp.presentation.authentication.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.user.usecase.RegisterUserUseCase
import com.example.socialmeetingapp.presentation.authentication.AuthenticationState
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
    private var _state = MutableStateFlow<AuthenticationState>(AuthenticationState.Initial)
    val state: StateFlow<AuthenticationState> = _state.asStateFlow()

    fun registerUser(email: String, password: String, confirmPassword: String) {
        _state.value = AuthenticationState.Loading

        viewModelScope.launch {
            when (val registerResult = registerUserUseCase(email, password, confirmPassword)) {
                is UserResult.Success -> {
                    _state.value = AuthenticationState.Success
                }

                is UserResult.Error -> {
                    _state.value =
                        AuthenticationState.Error(registerResult.message)
                }

                else -> {
                    return@launch
                }
            }
        }
    }
}