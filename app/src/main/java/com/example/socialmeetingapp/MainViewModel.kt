package com.example.socialmeetingapp

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.usecase.GetCurrentUserUseCase
import com.example.socialmeetingapp.domain.user.usecase.IsCurrentUserVerifiedUseCase
import com.example.socialmeetingapp.domain.user.usecase.RefreshUserUseCase
import com.example.socialmeetingapp.domain.user.usecase.SendEmailVerificationUseCase
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val isCurrentUserVerifiedUseCase: IsCurrentUserVerifiedUseCase,
    private val refreshUserUseCase: RefreshUserUseCase
) : ViewModel() {
    private var _state = MutableStateFlow<MainState>(MainState.Loading)
    val state = _state.asStateFlow().onStart {
        refreshUser()

    }.stateIn(viewModelScope, SharingStarted.Eagerly, MainState.Loading)

    fun refreshUser() {
        viewModelScope.launch {
            refreshUserUseCase()

            val isFirstTime = isFirstTimeLaunch()

            when (val result = getCurrentUserUseCase()) {
                is Result.Success<User> -> {
                    val isEmailVerified = isCurrentUserVerifiedUseCase()

                    if (result.data.username.isEmpty()) {
                        NavigationManager.navigateTo(Routes.CreateProfile)
                    }

                    _state.value =
                        MainState.Content(isFirstTimeLaunch = isFirstTime, user = result.data, isEmailVerified = isEmailVerified)
                }
                is Result.Error -> {
                    _state.value = MainState.Content(isFirstTimeLaunch = isFirstTime)
                }
                else -> {}
            }
        }
    }


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

    data class Content(
        val isFirstTimeLaunch: Boolean = true,
        val user: User? = null,
        val isEmailVerified: Boolean = false
    ) : MainState()


}
