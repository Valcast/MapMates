package com.example.socialmeetingapp.presentation.authentication.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.SignUpStatus
import com.example.socialmeetingapp.domain.model.onFailure
import com.example.socialmeetingapp.domain.model.onSuccess
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.CredentialManager
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val credentialManager: CredentialManager, private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun requestCredential() {
        viewModelScope.launch {
            credentialManager.getCredential().onSuccess { credential ->
                    signIn(credential.id, credential.password)
            }.onFailure { error ->
                _uiState.value = LoginUiState.Error("Failed to get credential: $error")
                Log.e("LoginViewModel", "Failed to get credential: $error")
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            credentialManager.getGoogleIdCredential().onSuccess { credential ->
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        userRepository.signUpWithGoogle(googleIdTokenCredential.idToken)
                            .onSuccess { signUpStatus ->
                                _uiState.value = LoginUiState.Success
                                when (signUpStatus) {
                                    is SignUpStatus.NewUser -> NavigationManager.navigateTo(Routes.CreateProfile)
                                    is SignUpStatus.ExistingUser -> NavigationManager.navigateTo(
                                        Routes.Map()
                                    )
                                }
                            }.onFailure { error ->
                                _uiState.value = LoginUiState.Error(error)
                            }
                    } else {
                        _uiState.value = LoginUiState.Error("Invalid credential")
                    }
            }.onFailure { error ->
                _uiState.value = LoginUiState.Error("Failed to get Google credential: $error")
                }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            userRepository.signIn(email, password).onSuccess {
                _uiState.value = LoginUiState.Success
                    NavigationManager.navigateTo(Routes.Map())
            }.onFailure { error ->
                _uiState.value = LoginUiState.Error(error)
                }
        }
    }
}


sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}