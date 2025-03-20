package com.example.socialmeetingapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.AppConfig
import com.example.socialmeetingapp.domain.model.UserPreview
import com.example.socialmeetingapp.domain.model.onFailure
import com.example.socialmeetingapp.domain.model.onSuccess
import com.example.socialmeetingapp.domain.repository.NotificationRepository
import com.example.socialmeetingapp.domain.repository.SettingsRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    private val settingsRepository: SettingsRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Routes?>(null)
    val startDestination = _startDestination.asStateFlow()

    private val authenticationStatus = userRepository.authenticationStatus.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )

    private var _user = MutableStateFlow<UserPreview?>(null)
    val user = _user.asStateFlow()

    private var _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _appConfig = MutableStateFlow(AppConfig.DEFAULT)
    val settings = _appConfig.asStateFlow()

    val notReadNotificationsCount = notificationRepository.notifications.map { notifications ->
        notifications.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            settingsRepository.getSettings().collect {
                _appConfig.value = it
            }
        }

        viewModelScope.launch {
            authenticationStatus.collectLatest { authStatus ->
                if (authStatus == null) {
                    _startDestination.update { Routes.Login }
                    _user.value = null
                    _isLoading.value = false
                } else {
                    refresh()
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            userRepository.getCurrentUserPreview()
                .onSuccess { currentUserData ->
                    _startDestination.value = Routes.Map()
                    _user.value = currentUserData
                    _isLoading.value = false
                }
                .onFailure { _ ->
                    _user.value = null
                    _isLoading.value = false
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

    private suspend fun isFirstTimeLaunch(): Boolean {
        return dataStore.data.first()[FIRST_TIME_LAUNCH] != false
    }

    companion object {
        val FIRST_TIME_LAUNCH = booleanPreferencesKey("FIRST_TIME_LAUNCH")
    }

}


