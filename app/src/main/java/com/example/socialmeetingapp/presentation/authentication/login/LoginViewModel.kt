package com.example.socialmeetingapp.presentation.authentication.login

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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val credentialManager: CredentialManager,
    private val userRepository: UserRepository
) : ViewModel() {
    private var _state = MutableStateFlow<Result<Unit>>(Result.Initial)
    val state = _state.asStateFlow()

    fun requestCredential() {
        viewModelScope.launch {
            when (val result = credentialManager.getCredential()) {
                is Result.Success -> {
                    val credential = result.data
                    signIn(credential.id, credential.password)
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

    fun signInWithGoogle() {
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

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.value = Result.Loading

            when (val result = userRepository.signIn(email, password)) {
                is Result.Success -> {
                    NavigationManager.navigateTo(Routes.Map)
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
}