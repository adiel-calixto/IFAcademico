package com.adielcalixto.ifacademico.presentation

import org.junit.Assert.*
import org.junit.Test

class ThemeTest {
    @Test
    fun `retired palettes preserve brightness and unknown preferences follow system`() {
        mapOf(
            "light" to Theme.LIGHT, "dark" to Theme.DARK,
            "yellow" to Theme.LIGHT, "blue" to Theme.LIGHT, "purple" to Theme.DARK,
            "system" to Theme.SYSTEM, "" to Theme.SYSTEM, "unknown" to Theme.SYSTEM,
        ).forEach { (id, expected) -> assertEquals(id, expected, Theme.from(id)) }
    }

    @Test
    fun `system follows device while explicit choices override it`() {
        assertFalse(Theme.SYSTEM.isDark(false))
        assertTrue(Theme.SYSTEM.isDark(true))
        listOf(false, true).forEach { deviceDark ->
            assertFalse(Theme.LIGHT.isDark(deviceDark))
            assertTrue(Theme.DARK.isDark(deviceDark))
        }
    }
}
