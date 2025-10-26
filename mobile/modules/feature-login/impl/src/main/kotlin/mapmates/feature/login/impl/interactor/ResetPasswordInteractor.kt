package mapmates.feature.login.impl.interactor

import mapmates.feature.login.impl.data.LoginRepository
import javax.inject.Inject

internal class ResetPasswordInteractor @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(email: String) = loginRepository.resetPassword(email)
}