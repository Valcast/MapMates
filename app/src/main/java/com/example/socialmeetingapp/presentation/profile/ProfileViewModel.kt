package com.example.socialmeetingapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import com.example.socialmeetingapp.domain.user.usecase.GetCurrentUserUseCase
import com.example.socialmeetingapp.domain.user.usecase.LogoutUseCase
import com.example.socialmeetingapp.presentation.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val _userData = MutableStateFlow<Result<User>>(Result.Initial)
    val userData = _userData.asStateFlow().onStart {
        _userData.value = Result.Loading
        _userData.value = getCurrentUserUseCase()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Result.Initial
    )

    fun logout() = logoutUseCase()
}


