package com.example.socialmeetingapp.presentation.authentication.register.profileinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.UserUpdateData
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
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
        MutableStateFlow<Result<Unit>>(Result.Initial)
    val state: StateFlow<Result<Unit>> = _state.asStateFlow()

    fun modifyUser(username: String, bio: String?) {
        _state.value = Result.Loading

        viewModelScope.launch {
            when (val updateResult = userRepository.modifyUser(
                UserUpdateData(username = username, bio = bio)
            )) {
                is Result.Success<*> -> {
                    _state.value = Result.Success(Unit)
                    NavigationManager.navigateTo(Routes.RegisterLocation)
                }
                is Result.Error -> {
                    _state.value = Result.Error(updateResult.message)
                }
                else -> {}
            }
        }
    }
}