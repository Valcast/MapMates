package com.example.socialmeetingapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.AppConfig
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.model.UserPreview
import com.example.socialmeetingapp.domain.repository.SettingsRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.Routes
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Routes?>(null)
    val startDestination = _startDestination.asStateFlow()

    private val _user = MutableStateFlow<UserPreview?>(null)
    val user = _user.asStateFlow()

    private val _appConfig = MutableStateFlow(AppConfig.DEFAULT)
    val settings = _appConfig.asStateFlow()

    init {
        checkAuthentication()

        viewModelScope.launch {
            settingsRepository.getSettings().collect {
                _appConfig.value = it
            }
        }
    }

    fun checkAuthentication() {
        viewModelScope.launch {
            val isUserAuthenticated = userRepository.isUserAuthenticated()

            if (isUserAuthenticated) {
                val currentUserResult = userRepository.getCurrentUserPreview()

                if (currentUserResult is Result.Success) {
                    val user = currentUserResult.data

                    if (user.username.isBlank()) {
                        _startDestination.value = Routes.CreateProfile
                    } else {
                        _startDestination.value = Routes.Map()
                    }

                    _user.update { user }
                }
            } else {
                _startDestination.value = Routes.Login
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
            when (val result = userRepository.sendEmailVerification()) {
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


