package com.example.socialmeetingapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.usecase.GetCurrentUserUseCase
import com.example.socialmeetingapp.domain.user.usecase.GetUserByIDUseCase
import com.example.socialmeetingapp.domain.user.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserByIDUseCase: GetUserByIDUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val _userData = MutableStateFlow<Result<User>>(Result.Initial)
    val userData = _userData.asStateFlow()

    fun getUserByID(userID: String) {
        viewModelScope.launch {
            if (userID == "me") {
                _userData.value = getCurrentUserUseCase()
            } else {
                _userData.value = getUserByIDUseCase(userID)
            }
        }
    }

    fun logout() = logoutUseCase()
}


