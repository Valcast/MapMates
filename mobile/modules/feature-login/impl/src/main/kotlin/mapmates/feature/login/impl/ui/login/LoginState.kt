package mapmates.feature.login.impl.ui.login

internal data class LoginState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val showCredentialManager: Boolean = true,
    val selectedWelcomeImageIndex: Int? = null,
)