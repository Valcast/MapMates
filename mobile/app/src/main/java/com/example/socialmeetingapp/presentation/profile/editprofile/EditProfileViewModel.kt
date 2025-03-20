package com.example.socialmeetingapp.presentation.profile.editprofile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.model.onFailure
import com.example.socialmeetingapp.domain.model.onSuccess
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel(
) {
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _profilePictureUri = MutableStateFlow<Uri>(Uri.EMPTY)
    val profilePictureUri = _profilePictureUri.asStateFlow()

    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _bio = MutableStateFlow<String>("")
    val bio = _bio.asStateFlow()

    private val _username = MutableStateFlow<String>("")
    val username = _username.asStateFlow()

    private val _dateOfBirth = MutableStateFlow<LocalDateTime?>(null)
    val dateOfBirth = _dateOfBirth.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .onSuccess {
                    _user.value = it
                    _bio.value = it.bio
                    _username.value = it.username
                    _dateOfBirth.value = it.dateOfBirth
                    _profilePictureUri.value = it.profilePictureUri
                }
                .onFailure {
                    Log.e("EditProfileViewModel", "Error occurred while fetching user")
                    SnackbarManager.showMessage("Error occurred while fetching user")
                }
        }
    }

    fun updateBio(bio: String) {
        _bio.value = bio
    }

    fun saveBio() {
        viewModelScope.launch {
            userRepository.updateBio(bio.value)
                .onSuccess {
                    SnackbarManager.showMessage("Bio updated successfully")
                }
                .onFailure { error ->
                    SnackbarManager.showMessage(error)
                }
        }
    }

    fun updateProfilePicture(uri: Uri) {
        _profilePictureUri.value = uri
    }

    fun saveProfilePicture() {
        _isLoading.value = true
        viewModelScope.launch {
            userRepository.updateProfilePicture(profilePictureUri.value)
                .onSuccess {
                    _isLoading.value = false
                    SnackbarManager.showMessage("Profile picture updated successfully")
                }
                .onFailure { error ->
                    SnackbarManager.showMessage(error)
                }
        }
    }

    fun updateUsername(username: String) {
        _username.value = username
    }

    fun updateDateOfBirth(dateOfBirth: LocalDateTime) {
        _dateOfBirth.value = dateOfBirth
    }

    fun saveUsernameAndDateOfBirth() {
        viewModelScope.launch {
            userRepository.updateUsernameAndDateOfBirth(
                username.value,
                dateOfBirth.value ?: return@launch
            )
                .onSuccess {
                    SnackbarManager.showMessage("Username and date of birth updated successfully")
                }
                .onFailure { error ->
                    SnackbarManager.showMessage(error)
                }
        }
    }
}