package com.example.socialmeetingapp.presentation.profile.editprofile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel(
) {
    fun updateBio(bio: String) {
        viewModelScope.launch {
            when (val result = userRepository.currentUser.value) {
                is Result.Success -> {
                    val user = result.data
                    if (user != null) {
                        val updatedUser = user.copy(bio = bio)
                        userRepository.updateUser(updatedUser)
                    }
                    }
                is Result.Error -> {
                    // Handle error
                }
                else -> {}
            }
        }
    }

    fun updateProfilePicture(uri: Uri) {
        viewModelScope.launch {
            userRepository.uploadProfilePicture(uri)
        }
    }

    fun updateUsernameAndDateOfBirth(username: String, dateOfBirth: LocalDateTime) {
        viewModelScope.launch {
            when (val result = userRepository.currentUser.value) {
                is Result.Success -> {
                    val user = result.data
                    if (user != null) {
                        val updatedUser = user.copy(username = username, dateOfBirth = dateOfBirth)
                        userRepository.updateUser(updatedUser)
                    }
                }
                is Result.Error -> {
                    // Handle error
                }
                else -> {}
            }
        }
    }
}