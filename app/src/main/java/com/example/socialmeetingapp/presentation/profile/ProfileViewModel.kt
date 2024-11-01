package com.example.socialmeetingapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.usecase.GetUserByIDUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserByIDUseCase: GetUserByIDUseCase,
) : ViewModel() {
    private val _userData = MutableStateFlow<Result<User>>(Result.Initial)
    val userData = _userData.asStateFlow()

    fun getUserByID(userID: String) {
        viewModelScope.launch {
            _userData.value = getUserByIDUseCase(userID)
        }
    }

}


