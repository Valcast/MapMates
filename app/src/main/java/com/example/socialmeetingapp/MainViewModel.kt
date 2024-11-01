package com.example.socialmeetingapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.usecase.GetCurrentUserUseCase
import com.example.socialmeetingapp.domain.user.usecase.CheckIfCurrentUserVerifiedUseCase
import com.example.socialmeetingapp.domain.user.usecase.RefreshUserUseCase
import com.example.socialmeetingapp.domain.user.usecase.SendEmailVerificationUseCase
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val isCurrentUserVerifiedUseCase: CheckIfCurrentUserVerifiedUseCase,
    private val refreshUserUseCase: RefreshUserUseCase
) : ViewModel() {
    private var _state = MutableStateFlow<MainState>(MainState.Loading)
    val state = _state.asStateFlow().onStart {
        val isFirstTimeLaunch = isFirstTimeLaunch()

        if (isFirstTimeLaunch) {
            _state.value = MainState.Welcome
            return@onStart
        }

        when (val userResult = getCurrentUserUseCase()) {
            is Result.Success -> {
                val user = userResult.data

                if (user.username.isEmpty()) {
                    _state.value = MainState.CreateProfile
                    return@onStart
                }

                val isEmailVerified = isCurrentUserVerifiedUseCase()
                _state.value = MainState.Content(user, isEmailVerified)
            }
            is Result.Error -> {
                _state.value = MainState.Content(null, false)
            }
            else -> { }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, MainState.Loading)

    fun onIntroductionFinished() {
        viewModelScope.launch {
            dataStore.edit {
                it[FIRST_TIME_LAUNCH] = false
            }
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            when (val result = sendEmailVerificationUseCase()) {
                is Result.Success -> {
                    SnackbarManager.showMessage("Verification email sent")
                }
                is Result.Error -> {
                    SnackbarManager.showMessage(result.message)
                }
                else -> {}
            }
        }
    }

    private suspend fun isFirstTimeLaunch(): Boolean {
        return dataStore.data.first()[FIRST_TIME_LAUNCH] != false

    }

    companion object {
        val FIRST_TIME_LAUNCH = booleanPreferencesKey("FIRST_TIME_LAUNCH")
    }

}

sealed class MainState(
) {
    data object Loading : MainState()
    data object Welcome : MainState()
    data object CreateProfile : MainState()

    data class Content(
        val user: User? = null,
        val isEmailVerified: Boolean = false,
    ) : MainState()


}
