package com.example.socialmeetingapp.presentation.profile.editprofile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.onFailure
import com.example.socialmeetingapp.domain.model.onSuccess
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

@HiltViewModel(assistedFactory = EditProfileViewModel.Factory::class)
class EditProfileViewModel @AssistedInject constructor(
    private val userRepository: UserRepository, @Assisted private val userId: String
) : ViewModel(
) {
    @AssistedFactory
    interface Factory {
        fun create(userId: String): EditProfileViewModel
    }

    fun updateBio(bio: String) {
        viewModelScope.launch {
            userRepository.updateBio(bio).onSuccess {
                Log.d("EditProfileViewModel", "Bio updated successfully")
                SnackbarManager.showMessage("Bio updated successfully")
            }.onFailure {
                Log.e("EditProfileViewModel", "Error occurred while updating bio")
                SnackbarManager.showMessage("Error occurred while updating bio")
            }
        }
    }

    fun updateProfilePicture(uri: Uri) {
        viewModelScope.launch {
            userRepository.uploadProfilePicture(uri).onSuccess {
                Log.d("EditProfileViewModel", "Profile picture uploaded successfully")
                SnackbarManager.showMessage("Profile picture uploaded successfully")
            }.onFailure {
                Log.e("EditProfileViewModel", "Error occurred while uploading profile picture")
                SnackbarManager.showMessage("Error occurred while uploading profile picture")
            }
        }
    }

    fun updateUsernameAndDateOfBirth(username: String, dateOfBirth: LocalDateTime) {
        viewModelScope.launch {
            userRepository.updateUsernameAndDateOfBirth(username, dateOfBirth).onSuccess {
                Log.d("EditProfileViewModel", "Username and date of birth updated successfully")
                SnackbarManager.showMessage("Profile updated successfully")
            }.onFailure {
                Log.e(
                    "EditProfileViewModel",
                    "Error occurred while updating username and date of birth"
                )
                SnackbarManager.showMessage("Error occurred while updating profile")
            }
        }
    }
}