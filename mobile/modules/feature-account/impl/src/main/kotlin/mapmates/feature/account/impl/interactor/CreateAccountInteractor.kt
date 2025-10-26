package mapmates.feature.account.impl.interactor

import android.net.Uri
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import mapmates.feature.account.impl.data.AccountRepository
import mapmates.feature.account.impl.model.AccountCreationResult
import mapmates.feature.account.impl.ui.createaccount.Gender
import mapmates.feature.login.api.interactor.GetUserAuthenticatedId
import javax.inject.Inject

internal class CreateAccountInteractor @Inject constructor(
    private val getUserAuthenticatedId: GetUserAuthenticatedId,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(
        firstName: String,
        middleName: String,
        lastName: String,
        bio: String,
        picturesList: LinkedHashSet<Uri>,
        dateOfBirth: LocalDate,
        gender: Gender
    ): AccountCreationResult {
        val userId = getUserAuthenticatedId().getOrElse {
            return AccountCreationResult.UserNotAuthenticated
        }

        return accountRepository.createAccount(
            userId = userId,
            firstName = firstName,
            middleName = middleName,
            lastName = lastName,
            bio = bio,
            picturesList = picturesList,
            dateOfBirth = dateOfBirth,
            gender = gender
        )
    }
}
