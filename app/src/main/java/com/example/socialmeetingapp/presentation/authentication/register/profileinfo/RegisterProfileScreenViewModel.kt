package com.example.socialmeetingapp.presentation.authentication.register.profileinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.UserResult
import com.example.socialmeetingapp.domain.model.AuthenticationState
import com.example.socialmeetingapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterProfileScreenViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private var _state =
        MutableStateFlow<AuthenticationState>(AuthenticationState.Initial)
    val state: StateFlow<AuthenticationState> = _state.asStateFlow()

    fun updateUsername(username: String) {
        _state.value = AuthenticationState.Loading

        viewModelScope.launch {
            when (val updateResult = userRepository.updateUsername(username)) {
                is UserResult.Success -> {
                    _state.value = AuthenticationState.Success
                }

                is UserResult.Error -> {
                    _state.value = AuthenticationState.Error(updateResult.message)
                }
            }
        }
    }
}