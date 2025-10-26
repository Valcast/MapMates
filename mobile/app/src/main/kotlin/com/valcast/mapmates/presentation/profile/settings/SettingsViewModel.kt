package com.valcast.mapmates.presentation.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcast.mapmates.domain.model.AppConfig
import com.valcast.mapmates.domain.repository.SettingsRepository
import com.valcast.mapmates.domain.repository.UserRepository
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
    }
}