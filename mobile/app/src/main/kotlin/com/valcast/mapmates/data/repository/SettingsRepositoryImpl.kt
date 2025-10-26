package com.valcast.mapmates.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.valcast.mapmates.domain.model.AppConfig
import com.valcast.mapmates.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun getSettings(): Flow<AppConfig> = callbackFlow {
        val job = launch {
            dataStore.data.collect { preferences ->
                val appConfig = AppConfig(
                    theme = convertStringToEnum(
                        preferences[THEME_KEY] ?: AppConfig.DEFAULT.theme.name
                    )
                )

                trySend(appConfig)
            }
        }

        awaitClose { job.cancel() }
    }

    override fun updateSettings(appConfig: AppConfig) {
        coroutineScope.launch {
            dataStore.edit { preferences ->
                preferences[THEME_KEY] = appConfig.theme.name
            }
        }
    }

    private inline fun <reified T : Enum<T>> convertStringToEnum(value: String): T {
        return enumValueOf<T>(value)
    }

    companion object {
        private val THEME_KEY = stringPreferencesKey("THEME")
    }
}