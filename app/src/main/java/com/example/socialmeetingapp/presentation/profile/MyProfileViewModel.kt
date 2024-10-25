package com.example.socialmeetingapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.usecase.GetCurrentUserUseCase
import com.example.socialmeetingapp.domain.user.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val _user = MutableStateFlow<Result<User>>(Result.Initial)
    val user = _user.asStateFlow().onStart {
        _user.value = getCurrentUserUseCase()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Result.Initial)

    fun logout() = logoutUseCase()
}