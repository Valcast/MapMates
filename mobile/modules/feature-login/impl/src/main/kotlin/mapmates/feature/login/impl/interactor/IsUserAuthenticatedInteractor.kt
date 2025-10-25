package mapmates.feature.login.impl.interactor

import mapmates.feature.login.api.interactor.IsUserAuthenticatedInteractor
import mapmates.feature.login.impl.data.LoginRepository
import javax.inject.Inject

internal class IsUserAuthenticatedInteractorImpl @Inject constructor(
    private val loginRepository: LoginRepository
) : IsUserAuthenticatedInteractor {
    override fun invoke(): Boolean = loginRepository.isUserAuthenticated()
}