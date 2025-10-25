package com.example.socialmeetingapp.presentation.authentication.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.SignUpStatus
import com.example.socialmeetingapp.domain.model.onFailure
import com.example.socialmeetingapp.domain.model.onSuccess
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.CredentialManager
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val credentialManager: CredentialManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Initial)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun signUpWithGoogle() {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            credentialManager.getGoogleIdCredential()
                .onSuccess { credential ->
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        userRepository.signUpWithGoogle(googleIdTokenCredential.idToken)
                            .onSuccess { signUpStatus ->
                                _uiState.value = RegisterUiState.Success
                            }
                            .onFailure { error ->
                                _uiState.value = RegisterUiState.Error(error)
                            }
                    } else {
                        _uiState.value = RegisterUiState.Error("Invalid credential")
                    }
                }
                .onFailure { error ->
                    _uiState.value =
                        RegisterUiState.Error("Failed to get Google credential: $error")
                }
        }
    }

    fun registerUser(email: String, password: String, confirmPassword: String) {
        _uiState.value = RegisterUiState.Loading

        if (!isPasswordValid(password, confirmPassword) || !isEmailValid(email)) return

        viewModelScope.launch {
            userRepository.signUp(email, password)
                .onSuccess {
                    _uiState.value = RegisterUiState.Success
                    credentialManager.saveCredential(email, password)
                }
                .onFailure { error ->
                    _uiState.value = RegisterUiState.Error("Failed to register: $error")
                }
        }
    }

    private fun isPasswordValid(password: String, confirmPassword: String): Boolean {
        val rules = listOf(
            (password == confirmPassword) to "Passwords do not match",
            (password.length >= 8) to "Password must be at least 8 characters long",
            (password.any { it.isDigit() }) to "Password must contain at least one number",
            (password.any { it.isUpperCase() }) to "Password must contain at least one uppercase letter",
            (password.any { it.isLowerCase() }) to "Password must contain at least one lowercase letter",
            (password.any { !it.isLetterOrDigit() }) to "Password must contain at least one special character"
        )

        rules.firstOrNull { !it.first }?.let { (_, errorMessage) ->
            _uiState.value = RegisterUiState.Error(errorMessage)
            return false
        }

        return true
    }

    private fun isEmailValid(email: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = RegisterUiState.Error("Invalid email address")
            return false
        }

        return true
    }
}

sealed class RegisterUiState {
    object Initial : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}