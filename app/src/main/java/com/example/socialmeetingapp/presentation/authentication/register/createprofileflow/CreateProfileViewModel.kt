package com.example.socialmeetingapp.presentation.authentication.register.createprofileflow

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.usecase.UploadProfilePictureUseCase
import com.example.socialmeetingapp.domain.user.usecase.UpdateUserUseCase
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val uploadProfilePictureUseCase: UploadProfilePictureUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {
    private var _uiState = MutableStateFlow<CreateProfileFlow>(CreateProfileFlow.ProfileInfo)
    val uiState = _uiState.asStateFlow()

    private var _isNextButtonEnabled = MutableStateFlow(false)
    val isNextButtonEnabled = _isNextButtonEnabled.asStateFlow()

    private var _user = MutableStateFlow(User.EMPTY)
    val user = _user.asStateFlow()

    private val _isRulesAccepted = MutableStateFlow(false)
    val isRulesAccepted = _isRulesAccepted.asStateFlow()

    fun nextStep() {
        if (uiState.value == CreateProfileFlow.Rules) {
            viewModelScope.launch {
                when (val updateUserResult = updateUserUseCase(user.value)) {
                    is Result.Success -> {
                        NavigationManager.navigateTo(Routes.Map)
                    }

                    is Result.Error -> {
                        SnackbarManager.showMessage(updateUserResult.message)
                    }

                    else -> Unit
                }
            }

        }

        _uiState.update { it.inc() }
        validateNextButton()
    }

    fun previousStep() {
        if (uiState.value == CreateProfileFlow.ProfileInfo) {
            return
        }

        _uiState.update { it.dec() }
        validateNextButton()
    }

    fun validateNextButton() {
        _isNextButtonEnabled.value = when (uiState.value) {
            CreateProfileFlow.ProfileInfo -> user.value.username.isNotBlank()
            CreateProfileFlow.ProfilePicture -> user.value.profilePictureUri.toString().isNotBlank()
            CreateProfileFlow.Additional -> true
            CreateProfileFlow.Rules -> isRulesAccepted.value
        }
    }

    fun updateUsername(username: String) {
        _user.update { it.copy(username = username) }
        validateNextButton()
    }

    fun updateBio(bio: String) {
        _user.update { it.copy(bio = bio) }
        validateNextButton()
    }

    fun updateProfilePicture(imageUri: Uri) {
        viewModelScope.launch {
            when (val updateProfilePictureResult = uploadProfilePictureUseCase(imageUri)) {
                is Result.Success<Uri> -> {
                    _user.update { it.copy(profilePictureUri = updateProfilePictureResult.data) }
                    validateNextButton()
                }

                is Result.Error -> {
                    SnackbarManager.showMessage(updateProfilePictureResult.message)
                }

                else -> {}
            }
        }
    }

    fun updateDateOfBirth(dateOfBirth: LocalDateTime) {
        _user.update { it.copy(dateOfBirth = dateOfBirth) }
        validateNextButton()
    }

    fun updateGender(gender: String) {
        _user.update { it.copy(gender = gender) }

    }

    fun updateRulesAccepted() {
        _isRulesAccepted.value = !_isRulesAccepted.value
        validateNextButton()
    }


}

sealed class CreateProfileFlow() {
    object ProfileInfo : CreateProfileFlow()
    object ProfilePicture : CreateProfileFlow()
    object Additional : CreateProfileFlow()
    object Rules : CreateProfileFlow()

    fun inc(): CreateProfileFlow {
        return when (this) {
            ProfileInfo -> ProfilePicture
            ProfilePicture -> Additional
            Additional -> Rules
            Rules -> Rules
        }
    }

    fun dec(): CreateProfileFlow {
        return when (this) {
            ProfileInfo -> ProfileInfo
            ProfilePicture -> ProfileInfo
            Additional -> ProfilePicture
            Rules -> Additional
        }
    }
}