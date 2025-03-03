package com.example.socialmeetingapp.domain.repository

import com.example.socialmeetingapp.domain.model.AppConfig
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getSettings(): Flow<AppConfig>
    fun updateSettings(appConfig: AppConfig)
}