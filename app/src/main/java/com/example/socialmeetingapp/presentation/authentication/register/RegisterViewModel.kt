package com.example.socialmeetingapp.presentation.authentication.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.SignUpStatus
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.CredentialManager
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
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
    private var _state = MutableStateFlow<Result<Unit>>(Result.Initial)
    val state: StateFlow<Result<Unit>> = _state.asStateFlow()

    fun signUpWithGoogle() {
        viewModelScope.launch {

            when (val result = credentialManager.getGoogleIdCredential()) {
                is Result.Success -> {
                    val credential = result.data
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                        when (val result = userRepository.signUpWithGoogle(googleIdTokenCredential.idToken)) {
                            is Result.Success -> {
                                when (result.data) {
                                    is SignUpStatus.NewUser -> NavigationManager.navigateTo(Routes.CreateProfile)
                                    is SignUpStatus.ExistingUser -> NavigationManager.navigateTo(Routes.Map)
                                }
                            }

                            is Result.Error -> {
                                _state.value = Result.Error(result.message)
                            }

                            else -> {
                                return@launch
                            }
                        }


                    } else {
                        _state.value = Result.Error("Invalid credential")
                    }
                }
                is Result.Error -> {
                    _state.value = Result.Error(result.message)
                }
                else -> {
                    return@launch
                }
            }
        }
    }

    fun registerUser(email: String, password: String, confirmPassword: String) {
        _state.value = Result.Loading

        if (!isPasswordValid(password, confirmPassword) || !isEmailValid(email)) return

        viewModelScope.launch {
            when (val registerResult = userRepository.signUp(email, password)) {
                is Result.Success<Unit> -> {
                    _state.value = Result.Success(Unit)

                    credentialManager.saveCredential(email, password)

                    NavigationManager.navigateTo(Routes.CreateProfile)
                }

                is Result.Error -> {
                    _state.value = Result.Error(registerResult.message)
                }

                else -> {
                    return@launch
                }
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
            _state.value = Result.Error(errorMessage)
            return false
        }

        return true
    }

    private fun isEmailValid(email: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.value = Result.Error("Invalid email address")
            return false
        }

        return true
    }


}