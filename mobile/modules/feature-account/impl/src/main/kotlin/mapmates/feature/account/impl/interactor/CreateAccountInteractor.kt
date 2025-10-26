package mapmates.feature.account.impl.interactor

import kotlinx.datetime.LocalDateTime
import mapmates.feature.account.impl.ui.createaccount.Gender
import javax.inject.Inject

internal class CreateAccountInteractor @Inject constructor(

) {

    suspend operator fun invoke(
        username: String,
        bio: String,
        profilePictureUri: String?,
        dateOfBirth: LocalDateTime,
        gender: Gender
    ) {
    }
}