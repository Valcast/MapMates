package com.example.socialmeetingapp.presentation.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.presentation.authentication.AuthenticationState
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import com.example.socialmeetingapp.domain.user.usecase.LoginUserUseCase
import com.example.socialmeetingapp.presentation.authentication.AuthenticationState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private var _state =
        MutableStateFlow<AuthenticationState>(Initial)
    val state: StateFlow<AuthenticationState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = Loading
            val result = loginUserUseCase(email, password)

            when (result) {
                is UserResult.Success -> {
                    _state.value = Success
                }

                is UserResult.Error -> {
                    _state.value = Error(result.message)
                }

                else -> {}

            }

        }
    }


}