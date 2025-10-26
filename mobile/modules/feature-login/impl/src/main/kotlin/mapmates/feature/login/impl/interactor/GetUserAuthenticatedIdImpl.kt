package mapmates.feature.login.impl.interactor

import mapmates.feature.login.api.interactor.GetUserAuthenticatedId
import mapmates.feature.login.impl.data.LoginRepository
import javax.inject.Inject

internal class GetUserAuthenticatedIdImpl @Inject constructor(
    private val loginRepository: LoginRepository
) : GetUserAuthenticatedId {
    override fun invoke(): Result<String> = loginRepository.getCurrentUserId()
}