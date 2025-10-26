package mapmates.feature.login.impl.ui.login

import android.util.Patterns
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
import mapmates.core.navigation.api.Destination
import mapmates.core.navigation.api.Navigator
import mapmates.feature.login.impl.CredentialManager.CredentialType
import mapmates.feature.login.impl.CredentialManager.RequestCredential
import mapmates.feature.login.impl.interactor.AuthenticateUserInteractor
import mapmates.feature.login.impl.interactor.RequestCredentialInteractor
import mapmates.feature.login.impl.interactor.SavePasswordCredentialInteractor
import mapmates.feature.login.impl.model.AuthenticationMethod
import mapmates.feature.login.impl.model.AuthenticationResult
import javax.inject.Inject
import mapmates.feature.login.impl.R as LoginR

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val authenticateUserInteractor: AuthenticateUserInteractor,
    private val requestCredentialInteractor: RequestCredentialInteractor,
    private val savePasswordCredentialInteractor: SavePasswordCredentialInteractor,
    private val navigator: Navigator,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun requestPasswordCredential() = viewModelScope.launch {
        when (val result = requestCredentialInteractor(CredentialType.Password)) {
            is RequestCredential.Success -> {
                val passwordCredential = result.credential as PasswordCredential

                _state.update { state ->
                    state.copy(
                        email = passwordCredential.id,
                        password = passwordCredential.password,
                        showCredentialManager = false
                    )
                }

                authenticateWithEmailAndPassword()
            }

            is RequestCredential.Canceled -> _state.update { state ->
                state.copy(
                    showCredentialManager = false
                )
            }
        }
    }

    fun onGoogleLogin() = viewModelScope.launch {
        _state.update { state -> state.copy(isLoading = true, isError = false) }

        when (val result = requestCredentialInteractor(CredentialType.Google)) {
            is RequestCredential.Success -> {
                val customCredential = result.credential as CustomCredential

                if (customCredential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(
                        data = customCredential.data
                    )

                    val result =
                        authenticateUserInteractor(AuthenticationMethod.Google(googleCredential.idToken))

                    when (result) {
                        is AuthenticationResult.Success -> if (result.isNewUser) {
                            navigator.navigateTo(Destination("create_account")) {
                                popUpTo(Destination("login")) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        } else {
                            // Navigate to main app for existing users
                        }

                        is AuthenticationResult.Failure -> {
                            _state.update { state ->
                                state.copy(
                                    isLoading = false,
                                    isError = true,
                                    errorMessage = result.errorMessageResId
                                )
                            }
                        }
                    }
                }
            }

            is RequestCredential.Canceled -> {
                _state.update { state -> state.copy(isLoading = false) }
            }
        }
    }

    fun onSignIn() {
        if (validateEmail() && validatePassword()) {
            authenticateWithEmailAndPassword()
        }
    }

    fun onEmailChanged(email: String) = _state.update { state ->
        state.copy(email = email, isError = false, errorMessage = null)
    }

    fun onPasswordChanged(password: String) = _state.update { state ->
        state.copy(password = password, isError = false, errorMessage = null)
    }

    fun onConfirmPasswordChanged(confirmPassword: String) = _state.update { state ->
        state.copy(
            confirmPassword = confirmPassword, isError = false, errorMessage = null
        )
    }

    fun onForgotPassword() = navigator.navigateTo(Destination("forgotPassword"))

    fun onRegister() = _state.update { state ->
        state.copy(
            isRegisterMode = !state.isRegisterMode,
            isError = false,
            errorMessage = null,
            confirmPassword = "",
            password = "",
            email = ""
        )
    }

    fun onSelectedWelcomeImage(index: Int?) = _state.update {
        it.copy(selectedWelcomeImageIndex = index)
    }

    private fun authenticateWithEmailAndPassword() {
        _state.update { state -> state.copy(isLoading = true, isError = false) }

        viewModelScope.launch {
            val result = authenticateUserInteractor(
                AuthenticationMethod.EmailAndPassword(
                    email = state.value.email,
                    password = state.value.password,
                    isRegistration = state.value.isRegisterMode
                )
            )

            when (result) {
                is AuthenticationResult.Success -> if (result.isNewUser) {
                    savePasswordCredentialInteractor(
                        username = state.value.email,
                        password = state.value.password
                    )
                    navigator.navigateTo(Destination("create_account")) {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                } else {
                    // Navigate to main app for existing users
                }

                is AuthenticationResult.Failure -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = result.errorMessageResId
                        )
                    }
                }
            }
        }
    }

    private fun validatePassword(): Boolean {
        val password = state.value.password
        val confirmPassword = state.value.confirmPassword

        if (password.isEmpty()) {
            _state.update { state ->
                state.copy(
                    isError = true, errorMessage = LoginR.string.register_passsword_empty
                )
            }
            return false
        }

        if (state.value.isRegisterMode) {
            if (confirmPassword.isEmpty()) {
                _state.update { state ->
                    state.copy(
                        isError = true, errorMessage = LoginR.string.register_confirm_password_empty
                    )
                }
                return false
            }
            val rules = listOf(
                (password == confirmPassword) to LoginR.string.register_passwords_do_not_match_error,
                (password.length >= 8) to LoginR.string.register_password_at_least_8_characters_error,
                (password.any { it.isDigit() }) to LoginR.string.register_password_at_least_1_number_error,
                (password.any { it.isUpperCase() }) to LoginR.string.register_password_at_least_1_uppercase_error,
                (password.any { it.isLowerCase() }) to LoginR.string.register_passwords_at_least_1_lowercase_error,
                (password.any { !it.isLetterOrDigit() }) to LoginR.string.register_passwords_at_least_1_special_character_error
            )

            rules.firstOrNull { !it.first }?.let {
                _state.update { state ->
                    state.copy(
                        isError = true, errorMessage = it.second
                    )
                }
                return false
            }
        }
        return true
    }

    private fun validateEmail(): Boolean {
        val email = state.value.email

        when {
            email.isEmpty() -> {
                _state.update { state ->
                    state.copy(
                        isError = true, errorMessage = LoginR.string.register_email_empty
                    )
                }
                return false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _state.update { state ->
                    state.copy(
                        isError = true, errorMessage = LoginR.string.register_invalid_email_error
                    )
                }
                return false
            }
        }

        return true
    }
}
