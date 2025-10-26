package mapmates.feature.login.impl.model

internal sealed interface ResetPasswordResult {
    object Success : ResetPasswordResult
    object UserNotFound : ResetPasswordResult
    object UnknownError : ResetPasswordResult
}