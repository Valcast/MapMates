package mapmates.feature.account.impl.ui.createaccount

import android.net.Uri
import kotlinx.datetime.LocalDate
import mapmates.feature.account.impl.R as AccountR

internal data class CreateAccountState(
    val uiState: CreateAccountUiState = CreateAccountUiState.INFO,
    val firstName: String = "",
    val middleName: String = "",
    val lastName: String = "",
    val bio: String = "",
    val profilePictureUri: LinkedHashSet<Uri> = linkedSetOf(),
    val selectedProfilePictureURi: Uri? = null,
    val dateOfBirth: LocalDate? = null,
    val gender: Gender? = null,
    val isTermsOfServiceAccepted: Boolean = false,
    val isPrivacyPolicyAccepted: Boolean = false,
    val isNextButtonEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null,
)

internal enum class CreateAccountUiState {
    INFO,
    PICTURE,
    ADDITIONAL,
    RULES;

    fun next(): CreateAccountUiState = entries[(ordinal + 1) % entries.size]

    fun previous(): CreateAccountUiState =
        entries[if (ordinal - 1 < 0) entries.size - 1 else ordinal - 1]
}

internal enum class Gender(val resId: Int) {
    MAN(AccountR.string.account_additional_gender_male),
    FEMALE(AccountR.string.account_additional_gender_female),
    OTHER(AccountR.string.account_additional_gender_other)
}

