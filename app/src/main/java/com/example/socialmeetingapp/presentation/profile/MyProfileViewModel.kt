package com.example.socialmeetingapp.presentation.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.usecase.GetCurrentUserUseCase
import com.example.socialmeetingapp.domain.user.usecase.LogoutUseCase
import com.example.socialmeetingapp.domain.user.usecase.UploadProfilePictureUseCase
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase
) : ViewModel() {
    private val _user = MutableStateFlow<Result<User>>(Result.Initial)
    val user = _user.asStateFlow().onStart {
        _user.value = getCurrentUserUseCase()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Result.Initial)

    private val _newUser = MutableStateFlow<User>(User.EMPTY)
    val newUser = _newUser.asStateFlow()

    fun updateUsername(username: String) {
        _newUser.update { it.copy(username = username) }
    }

    fun updateBio(bio: String) {
        _newUser.update { it.copy(bio = bio) }
    }

    fun updateProfilePicture(imageUri: Uri) {
        viewModelScope.launch {
            when (val updateProfilePictureResult = uploadProfilePictureUseCase(imageUri)) {
                is Result.Success<Uri> -> {
                    _newUser.update { it.copy(profilePictureUri = updateProfilePictureResult.data) }
                }

                is Result.Error -> {
                    SnackbarManager.showMessage(updateProfilePictureResult.message)
                }

                else -> {}
            }
        }
    }

    fun updateDateOfBirth(dateOfBirth: LocalDateTime) {
        _newUser.update { it.copy(dateOfBirth = dateOfBirth) }
    }

    fun updateGender(gender: String) {
        _newUser.update { it.copy(gender = gender) }

    }

    fun logout() = logoutUseCase()
}