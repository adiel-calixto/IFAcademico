package com.adielcalixto.ifacademico.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.adielcalixto.ifacademico.presentation.Theme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ThemePreferences(context: Context) {
    private val datastore = context.dataStore

    suspend fun getTheme(): Theme? {
        return datastore.data.map { preferences -> preferences[THEME_KEY] ?: "" }
            .let { Theme.from(it.first()) }
    }

    suspend fun saveTheme(theme: Theme) {
        datastore.edit { preferences ->
            preferences[THEME_KEY] = theme.id
        }
    }

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("theme")
        private val THEME_KEY = stringPreferencesKey("selected_theme")
    }
}