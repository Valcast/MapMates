package mapmates.feature.login.impl.model

internal sealed interface AuthenticationResult {
    data class Success(val isNewUser: Boolean) : AuthenticationResult
    data class Failure(val errorMessageResId: Int) : AuthenticationResult
}