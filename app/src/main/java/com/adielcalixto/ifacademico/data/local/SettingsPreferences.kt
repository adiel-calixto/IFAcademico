package com.adielcalixto.ifacademico.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.adielcalixto.ifacademico.presentation.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsPreferences @Inject constructor(
    private val context: Context
) {
    private val datastore = context.settingsDataStore

    val theme: Flow<Theme> = datastore.data.map { preferences ->
        Theme.from(preferences[KEY_THEME] ?: "") ?: Theme.DARK
    }

    val isUpdateCheckEnabled: Flow<Boolean> = datastore.data.map { preferences ->
        preferences[KEY_CHECK_UPDATES] ?: true
    }

    val skippedVersion: Flow<String?> = datastore.data.map { preferences ->
        preferences[KEY_SKIPPED_VERSION]
    }

    suspend fun setTheme(theme: Theme) {
        datastore.edit { preferences ->
            preferences[KEY_THEME] = theme.id
        }
    }

    suspend fun setUpdateCheckEnabled(enabled: Boolean) {
        datastore.edit { preferences ->
            preferences[KEY_CHECK_UPDATES] = enabled
        }
    }

    suspend fun setSkippedVersion(version: String) {
        datastore.edit { preferences ->
            preferences[KEY_SKIPPED_VERSION] = version
        }
    }

    companion object {
        private val KEY_THEME = stringPreferencesKey("theme")
        private val KEY_CHECK_UPDATES = booleanPreferencesKey("check_updates")
        private val KEY_SKIPPED_VERSION = stringPreferencesKey("skipped_version")
    }
}
