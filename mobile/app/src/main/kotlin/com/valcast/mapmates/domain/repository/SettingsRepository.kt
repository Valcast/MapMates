package com.valcast.mapmates.domain.repository

import com.valcast.mapmates.domain.model.AppConfig
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getSettings(): Flow<AppConfig>
    fun updateSettings(appConfig: AppConfig)
}