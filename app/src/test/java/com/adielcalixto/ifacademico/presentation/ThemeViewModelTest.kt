package com.adielcalixto.ifacademico.presentation

import com.adielcalixto.ifacademico.data.local.SettingsPreferences
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {
    @Test
    fun `view model observes external changes and persists selections`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val saved = MutableStateFlow(Theme.DARK)
            val settings = mockk<SettingsPreferences>()
            every { settings.theme } returns saved
            coEvery { settings.setTheme(any()) } answers { saved.value = firstArg() }
            val model = ThemeViewModel(settings)
            runCurrent()
            assertEquals(Theme.DARK, model.theme.value)
            saved.value = Theme.LIGHT
            runCurrent()
            assertEquals(Theme.LIGHT, model.theme.value)
            model.changeTheme(Theme.SYSTEM)
            runCurrent()
            assertEquals(Theme.SYSTEM, saved.value)
            assertEquals(Theme.SYSTEM, model.theme.value)
        } finally { Dispatchers.resetMain() }
    }
}
