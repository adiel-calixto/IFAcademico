package com.adielcalixto.ifacademico.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.adielcalixto.ifacademico.presentation.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ThemePreferencesTest {
    @get:Rule val folder = TemporaryFolder()

    @Test
    fun `fresh settings follow system and explicit selection survives reopening`() = runTest {
        val file = folder.root.resolve("settings.preferences_pb")
        var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        var store = PreferenceDataStoreFactory.create(scope = scope, produceFile = { file })
        val settings = SettingsPreferences(store)
        assertEquals(Theme.SYSTEM, settings.theme.first())
        settings.setTheme(Theme.LIGHT)
        assertEquals(Theme.LIGHT, settings.theme.first())
        scope.coroutineContext[kotlinx.coroutines.Job]!!.cancel()
        scope.coroutineContext[kotlinx.coroutines.Job]!!.join()
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            store = PreferenceDataStoreFactory.create(scope = scope, produceFile = { file })
            assertEquals(Theme.LIGHT, SettingsPreferences(store).theme.first())
        } finally { scope.cancel() }
    }

    @Test
    fun `migration writes canonical themes and preserves unrelated preferences`() = runTest {
        for ((legacy, expected) in mapOf("purple" to Theme.DARK, "yellow" to Theme.LIGHT, "blue" to Theme.LIGHT, "invalid" to Theme.SYSTEM)) {
            val file = folder.root.resolve("$legacy.preferences_pb")
            var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            val oldStore = PreferenceDataStoreFactory.create(scope = scope, produceFile = { file })
            oldStore.edit {
                it[ThemePreferenceKey] = legacy
                it[booleanPreferencesKey("check_updates")] = false
                it[stringPreferencesKey("skipped_version")] = "2.0.0"
            }
            scope.coroutineContext[kotlinx.coroutines.Job]!!.cancel()
            scope.coroutineContext[kotlinx.coroutines.Job]!!.join()
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            try {
                val store = PreferenceDataStoreFactory.create(
                    migrations = listOf(ThemePreferenceMigration), scope = scope, produceFile = { file },
                )
                val settings = SettingsPreferences(store)
                assertEquals(expected, settings.theme.first())
                assertEquals(expected.id, store.data.first()[ThemePreferenceKey])
                assertFalse(settings.isUpdateCheckEnabled.first())
                assertEquals("2.0.0", settings.skippedVersion.first())
            } finally { scope.cancel() }
        }
    }
}
