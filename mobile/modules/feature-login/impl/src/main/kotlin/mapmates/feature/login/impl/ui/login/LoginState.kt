package mapmates.feature.login.impl.ui.login

internal data class LoginState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isRegisterMode: Boolean = false,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errorMessage: Int? = null,
    val showCredentialManager: Boolean = true,
    val selectedWelcomeImageIndex: Int? = null,
)