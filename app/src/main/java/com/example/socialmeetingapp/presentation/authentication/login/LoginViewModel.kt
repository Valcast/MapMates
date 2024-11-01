package com.example.socialmeetingapp.presentation.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.usecase.LoginUserUseCase
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private var _state = MutableStateFlow<Result<Unit>>(Result.Initial)
    val state = _state.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = Result.Loading

            when (val result = loginUserUseCase(email, password)) {
                is Result.Success -> {
                    NavigationManager.navigateTo(Routes.Map)
                }

                is Result.Error -> {
                    _state.value = Result.Error(result.message)
                }

                else -> { return@launch }
            }
        }
    }
}