package mapmates.feature.login.impl.model

internal sealed interface AuthenticationResult {
    object NewUser : AuthenticationResult
    object ExistingUser : AuthenticationResult
    object UserNotFound : AuthenticationResult
    object UnknownError : AuthenticationResult
}