package com.adielcalixto.ifacademico.data.local

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.adielcalixto.ifacademico.presentation.Theme
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal val ThemePreferenceKey = stringPreferencesKey("theme")

internal object ThemePreferenceMigration : DataMigration<Preferences> {
    override suspend fun shouldMigrate(currentData: Preferences): Boolean {
        val saved = currentData[ThemePreferenceKey] ?: return false
        return saved != Theme.from(saved).id
    }

    override suspend fun migrate(currentData: Preferences): Preferences = currentData.toMutablePreferences().apply {
        this[ThemePreferenceKey] = Theme.from(currentData[ThemePreferenceKey].orEmpty()).id
    }

    override suspend fun cleanUp() = Unit
}

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings", produceMigrations = { listOf(ThemePreferenceMigration) },
)

@Singleton
class SettingsPreferences internal constructor(private val datastore: DataStore<Preferences>) {
    @Inject
    constructor(@dagger.hilt.android.qualifiers.ApplicationContext context: Context) : this(context.settingsDataStore)

    val theme: Flow<Theme> = datastore.data.map { preferences ->
        Theme.from(preferences[ThemePreferenceKey] ?: "")
    }

    val isUpdateCheckEnabled: Flow<Boolean> = datastore.data.map { preferences ->
        preferences[KEY_CHECK_UPDATES] ?: true
    }

    val skippedVersion: Flow<String?> = datastore.data.map { preferences ->
        preferences[KEY_SKIPPED_VERSION]
    }

    suspend fun setTheme(theme: Theme) {
        datastore.edit { preferences ->
            preferences[ThemePreferenceKey] = theme.id
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
        private val KEY_CHECK_UPDATES = booleanPreferencesKey("check_updates")
        private val KEY_SKIPPED_VERSION = stringPreferencesKey("skipped_version")
    }
}
