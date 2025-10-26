package mapmates.feature.login.impl.ui.forgotpassword

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val resultMessageResId: Int? = null,
    val timerSecondsLeft: Int = 0,
)
