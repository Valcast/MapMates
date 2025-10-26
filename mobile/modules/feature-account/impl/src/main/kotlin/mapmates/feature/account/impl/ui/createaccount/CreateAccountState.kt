package mapmates.feature.account.impl.ui.createaccount

import kotlinx.datetime.LocalDateTime
import mapmates.feature.account.impl.R as AccountR

internal data class CreateAccountState(
    val uiState: CreateAccountUiState = CreateAccountUiState.INFO,
    val username: String = "",
    val bio: String = "",
    val profilePictureUri: String? = null,
    val dateOfBirth: LocalDateTime? = null,
    val gender: Gender? = null,
    val isRulesAccepted: Boolean = false,
    val isNextButtonEnabled: Boolean = false,
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

