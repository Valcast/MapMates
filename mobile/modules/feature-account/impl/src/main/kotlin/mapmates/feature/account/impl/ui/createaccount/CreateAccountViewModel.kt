package mapmates.feature.account.impl.ui.createaccount

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import mapmates.core.navigation.api.Destination
import mapmates.core.navigation.api.Navigator
import mapmates.feature.account.impl.interactor.CreateAccountInteractor
import mapmates.feature.account.impl.model.AccountCreationResult
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@HiltViewModel
internal class CreateAccountViewModel @Inject constructor(
    private val createAccountInteractor: CreateAccountInteractor,
    private val navigator: Navigator,
) : ViewModel() {
    private var _state = MutableStateFlow(CreateAccountState())
    val state = _state.asStateFlow()

    fun onNext() {
        if (state.value.uiState == CreateAccountUiState.RULES) {
            _state.update { state -> state.copy(isLoading = true) }
            viewModelScope.launch {
                val result = createAccountInteractor(
                    firstName = state.value.firstName,
                    middleName = state.value.middleName,
                    lastName = state.value.lastName,
                    bio = state.value.bio,
                    picturesList = state.value.profilePictureUri,
                    dateOfBirth = state.value.dateOfBirth!!,
                    gender = state.value.gender!!,
                )

                when (result) {
                    is AccountCreationResult.Success -> navigator.navigateTo(
                        Destination("home")
                    ) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }

                    is AccountCreationResult.Failure -> {
                        _state.update { state ->
                            state.copy(
                                isLoading = false,
                                errorMessageResId = result.errorMeesageResId
                            )
                        }
                    }
                }
            }
            return
        }

        _state.update { state ->
            state.copy(uiState = state.uiState.next())
        }
        validateNextButton()
    }

    fun onPrevious() {
        if (state.value.uiState == CreateAccountUiState.INFO) {
            return
        }

        _state.update { state ->
            state.copy(uiState = state.uiState.previous())
        }
        validateNextButton()
    }

    private fun validateNextButton() {
        _state.update { state ->
            val isNextButtonEnabled = when (state.uiState) {
                CreateAccountUiState.INFO -> state.firstName.isNotBlank() && state.lastName.isNotBlank()
                CreateAccountUiState.PICTURE -> state.profilePictureUri.isNotEmpty()
                CreateAccountUiState.ADDITIONAL -> {
                    val now =
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

                    state.dateOfBirth != null && state.gender != null && state.dateOfBirth.plus(
                        18,
                        DateTimeUnit.YEAR
                    ) <= now
                }

                CreateAccountUiState.RULES -> state.isTermsOfServiceAccepted && state.isPrivacyPolicyAccepted
            }
            state.copy(isNextButtonEnabled = isNextButtonEnabled)
        }
    }

    fun onUpdateFirstName(firstName: String) {
        _state.update { state -> state.copy(firstName = firstName) }
        validateNextButton()
    }

    fun onUpdateMiddleName(middleName: String) {
        _state.update { state -> state.copy(middleName = middleName) }
    }

    fun onUpdateLastName(lastName: String) {
        _state.update { state -> state.copy(lastName = lastName) }
        validateNextButton()
    }

    fun onUpdateBio(bio: String) {
        _state.update { state -> state.copy(bio = bio) }
        validateNextButton()
    }

    fun onAddPicture(profilePicture: Uri) {
        _state.update { state ->
            val updatedPictures = state.profilePictureUri.toMutableSet()
            updatedPictures.add(profilePicture)
            state.copy(
                profilePictureUri = linkedSetOf(*updatedPictures.toTypedArray()),
                selectedProfilePictureURi = profilePicture
            )
        }
        validateNextButton()
    }

    fun onDeletePicture() {
        _state.update { state ->
            val updatedPictures = state.profilePictureUri.toMutableSet()
            updatedPictures.remove(state.selectedProfilePictureURi)
            state.copy(
                profilePictureUri = linkedSetOf(*updatedPictures.toTypedArray()),
                selectedProfilePictureURi = updatedPictures.lastOrNull()
            )
        }
        validateNextButton()
    }

    fun onPreviewPicture(profilePicture: Uri) = _state.update { state ->
        state.copy(selectedProfilePictureURi = profilePicture)
    }


    fun onUpdateDateOfBirth(dateOfBirth: LocalDate) {
        _state.update { state ->
            state.copy(dateOfBirth = dateOfBirth)
        }
        validateNextButton()
    }

    fun onUpdateGender(gender: Gender) {
        _state.update { state ->
            state.copy(gender = gender)
        }
        validateNextButton()
    }

    fun onUpdateTermsOfService() {
        _state.update { state ->
            state.copy(isTermsOfServiceAccepted = !state.isTermsOfServiceAccepted)
        }
        validateNextButton()
    }

    fun onUpdatePrivacyPolicy() {
        _state.update { state ->
            state.copy(isPrivacyPolicyAccepted = !state.isPrivacyPolicyAccepted)
        }
        validateNextButton()
    }
}