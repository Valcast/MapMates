package mapmates.feature.login.impl.interactor

import mapmates.feature.login.impl.data.LoginRepository
import mapmates.feature.login.impl.model.AuthenticationMethod
import javax.inject.Inject

internal class AuthenticateUserInteractor @Inject constructor(
    private val loginRepository: LoginRepository,
) {
    suspend operator fun invoke(authenticationMethod: AuthenticationMethod) = loginRepository.authenticate(authenticationMethod)
}