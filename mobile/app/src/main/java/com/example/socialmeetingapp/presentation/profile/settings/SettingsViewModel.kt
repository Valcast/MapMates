package com.example.socialmeetingapp.presentation.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.AppConfig
import com.example.socialmeetingapp.domain.repository.SettingsRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private var _settings = MutableStateFlow<AppConfig>(AppConfig.DEFAULT)
    val settings = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getSettings().collect {
                _settings.value = it
            }
        }
    }

    fun updateSettings(appConfig: AppConfig) = settingsRepository.updateSettings(appConfig)


    fun restoreDefaultSettings() = settingsRepository.updateSettings(AppConfig.DEFAULT)

    fun signOut() {
        userRepository.signOut()
        NavigationManager.navigateTo(Routes.Login)
    }
}