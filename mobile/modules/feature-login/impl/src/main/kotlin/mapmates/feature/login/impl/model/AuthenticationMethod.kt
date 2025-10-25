package mapmates.feature.login.impl.model

internal sealed interface AuthenticationMethod {
    data class Google(val idToken: String) : AuthenticationMethod
    data class EmailAndPassword(val email: String, val password: String, val isRegistration: Boolean) : AuthenticationMethod
}