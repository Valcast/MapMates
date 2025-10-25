package mapmates.feature.login.impl.ui.login

import androidx.credentials.CustomCredential
import androidx.credentials.PasswordCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mapmates.feature.login.impl.CredentialManager.CredentialType
import mapmates.feature.login.impl.CredentialManager.RequestCredential
import mapmates.feature.login.impl.CredentialManager
import mapmates.feature.login.impl.interactor.AuthenticateUserInteractor
import mapmates.feature.login.impl.interactor.RequestCredentialInteractor
import mapmates.feature.login.impl.model.AuthenticationMethod
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val authenticateUserInteractor: AuthenticateUserInteractor,
    private val requestCredentialInteractor: RequestCredentialInteractor
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun requestCredential() = viewModelScope.launch {
        when (val result = requestCredentialInteractor(CredentialType.Password)) {
            is RequestCredential.Success -> {
                val passwordCredential = result.credential as PasswordCredential

                signInWithEmailAndPassword(
                    email = passwordCredential.id,
                    password = passwordCredential.password
                )
            }

            is CredentialManager.RequestCredential.Canceled ->
                _state.update { state -> state.copy(showCredentialManager = false) }
        }
    }

    fun onGoogleLogin() = viewModelScope.launch {
        _state.update { state -> state.copy(isLoading = true, isError = false) }

        when (val result = requestCredentialInteractor(CredentialType.Google)) {
            is CredentialManager.RequestCredential.Success -> {
                val customCredential = result.credential as CustomCredential

                if (customCredential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(
                        data = customCredential.data
                    )

                    authenticateUserInteractor(AuthenticationMethod.Google(googleCredential.idToken))
                }
            }

            is RequestCredential.Canceled -> {
                _state.update { state -> state.copy(isLoading = false) }
            }
        }
    }

    fun onSignIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.update { state -> state.copy(isError = true) }
            return
        }

        signInWithEmailAndPassword(email, password)
    }

    fun onForgotPassword() {
        // Navigate to ForgotPassword screen
    }

    fun onRegister() {

    }

    fun onSelectedWelcomeImage(index: Int?) = _state.update {
        it.copy(selectedWelcomeImageIndex = index)
    }

    private fun signInWithEmailAndPassword(email: String, password: String) =
        viewModelScope.launch {
            _state.update { state -> state.copy(isLoading = true, isError = false) }

            authenticateUserInteractor(
                AuthenticationMethod.EmailAndPassword(
                    email = email, password = password, isRegistration = false
                )
            )
        }

}