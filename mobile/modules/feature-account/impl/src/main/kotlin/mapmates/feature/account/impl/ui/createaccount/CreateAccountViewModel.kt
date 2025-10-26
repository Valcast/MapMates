package mapmates.feature.account.impl.ui.createaccount

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import mapmates.feature.account.impl.interactor.CreateAccountInteractor
import javax.inject.Inject

@HiltViewModel
internal class CreateAccountViewModel @Inject constructor(
    private val createAccountInteractor: CreateAccountInteractor,
) : ViewModel() {
    private var _state = MutableStateFlow(CreateAccountState())
    val state = _state.asStateFlow()

    fun onNext() {
        if (state.value.uiState == CreateAccountUiState.RULES) {
            viewModelScope.launch {
                createAccountInteractor(
                    username = state.value.username,
                    bio = state.value.bio,
                    profilePictureUri = state.value.profilePictureUri,
                    dateOfBirth = state.value.dateOfBirth!!,
                    gender = state.value.gender!!,
                )
            }
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
                CreateAccountUiState.INFO -> state.username.isNotBlank()
                CreateAccountUiState.PICTURE -> state.profilePictureUri.toString().isNotBlank()
                CreateAccountUiState.ADDITIONAL -> true
                CreateAccountUiState.RULES -> state.isRulesAccepted
            }
            state.copy(isNextButtonEnabled = isNextButtonEnabled)
        }
    }

    fun onUpdateUsername(username: String) {
        _state.update { state -> state.copy(username = username) }
        validateNextButton()
    }

    fun onUpdateBio(bio: String) {
        _state.update { state -> state.copy(bio = bio) }
        validateNextButton()
    }

    fun onUpdateProfilePicture(profilePicture: Uri) {
        viewModelScope.launch {
        }
    }

    fun onUpdateDateOfBirth(dateOfBirth: LocalDateTime) {
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

    fun onUpdateRules() {
        _state.update { state ->
            state.copy(isRulesAccepted = !state.isRulesAccepted)
        }
        validateNextButton()
    }
}