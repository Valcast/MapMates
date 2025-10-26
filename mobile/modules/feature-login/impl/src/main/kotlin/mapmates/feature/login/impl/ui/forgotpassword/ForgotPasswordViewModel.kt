package mapmates.feature.login.impl.ui.forgotpassword

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mapmates.core.navigation.api.Navigator
import mapmates.feature.login.impl.interactor.ResetPasswordInteractor
import mapmates.feature.login.impl.model.ResetPasswordResult
import javax.inject.Inject
import mapmates.feature.login.impl.R as LoginR

@HiltViewModel
internal class ForgotPasswordViewModel @Inject constructor(
    private val resetPasswordInteractor: ResetPasswordInteractor,
    private val navigator: Navigator,
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    fun onEmailChanged(email: String) = _state.update { state -> state.copy(email = email) }

    fun onBack() = navigator.navigateUp()

    fun onResetPassword() {
        if (!validateEmail()) return
        _state.update { state -> state.copy(isLoading = true) }

        viewModelScope.launch {
            val result = resetPasswordInteractor(state.value.email)

            when (result) {
                is ResetPasswordResult.Success -> _state.update { state ->
                    startTimer()
                    state.copy(
                        isLoading = false,
                        resultMessageResId = LoginR.string.login_forgot_password_success_message
                    )
                }

                is ResetPasswordResult.UserNotFound -> _state.update { state ->
                    state.copy(
                        isLoading = false,
                        resultMessageResId = LoginR.string.login_forgot_password_user_not_found_message
                    )
                }

                is ResetPasswordResult.UnknownError -> _state.update { state ->
                    state.copy(
                        isLoading = false,
                        resultMessageResId = LoginR.string.auth_unknown_error
                    )
                }
            }
        }
    }

    private fun startTimer() {
        _state.update { it.copy(timerSecondsLeft = 30) }
        viewModelScope.launch {
            for (i in 30 downTo 1) {
                kotlinx.coroutines.delay(1000)
                _state.update { it.copy(timerSecondsLeft = i - 1) }
            }
            _state.update { it.copy(timerSecondsLeft = 0) }
        }
    }

    private fun validateEmail(): Boolean {
        val email = state.value.email

        when {
            email.isEmpty() -> {
                _state.update { state ->
                    state.copy(
                        resultMessageResId = LoginR.string.register_email_empty
                    )
                }
                return false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _state.update { state ->
                    state.copy(
                        resultMessageResId = LoginR.string.register_invalid_email_error
                    )
                }
                return false
            }
        }

        return true
    }
}