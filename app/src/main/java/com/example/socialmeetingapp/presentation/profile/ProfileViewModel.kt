package com.example.socialmeetingapp.presentation.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Error(val message: String) : ProfileState()
    data class Content(val user: User, val isMyProfile: Boolean = false) : ProfileState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _userData = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val userData = _userData.asStateFlow()

    private val _newUser = MutableStateFlow(User.EMPTY)
    val newUser = _newUser.asStateFlow()

    fun getUserByID(userID: String) {
        viewModelScope.launch {
            val userResult = async { userRepository.getUser(userID) }.await()
            val currentUser = async { userRepository.getCurrentUser() }.await()

            if (userResult is Result.Success && currentUser is Result.Success) {
                _userData.update {
                    ProfileState.Content(
                        user = userResult.data,
                        isMyProfile = userResult.data.id == currentUser.data.id
                    )
                }
            } else {
                _userData.update { ProfileState.Error("Failed to load user") }
            }

        }
    }

    fun updateUsername(username: String) {
        _newUser.update { it.copy(username = username) }
    }

    fun updateBio(bio: String) {
        _newUser.update { it.copy(bio = bio) }
    }

    fun updateProfilePicture(imageUri: Uri) {
        viewModelScope.launch {
            when (val updateProfilePictureResult = userRepository.uploadProfilePicture(imageUri)) {
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

    fun logout() = userRepository.signOut()

}


