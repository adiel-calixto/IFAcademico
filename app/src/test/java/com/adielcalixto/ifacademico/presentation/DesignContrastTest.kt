package com.adielcalixto.ifacademico.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertTrue
import org.junit.Test

class DesignContrastTest {
    private fun contrast(a: Color, b: Color): Float {
        val light = maxOf(a.luminance(), b.luminance())
        val dark = minOf(a.luminance(), b.luminance())
        return (light + .05f) / (dark + .05f)
    }

    @Test
    fun `calendar text markers and today outlines remain readable in each theme`() {
        listOf(LightColors, DarkColors).forEach { colors ->
            listOf(
                colors.primaryContainer to colors.primary,
                colors.notice to colors.onNotice,
                colors.calendarEventSurface to colors.onCalendarEvent,
                colors.calendarAssessmentSurface to colors.onCalendarAssessment,
                colors.surfaceSubtle to colors.secondary,
            ).forEach { (background, foreground) ->
                assertTrue("Calendar date text", contrast(foreground, background) >= 4.5f)
                assertTrue("Calendar underline", contrast(foreground, background) >= 3f)
                assertTrue("Today outline", contrast(colors.primary, background) >= 3f)
            }
        }
    }

    @Test
    fun `text pairings meet ordinary text contrast in each appearance`() {
        listOf(LightColors, DarkColors).forEach { c ->
            listOf(c.background, c.surface, c.surfaceSubtle).forEach { surface ->
                assertTrue("Primary text on $surface", contrast(c.onSurface, surface) >= 4.5f)
                assertTrue("Supporting text on $surface", contrast(c.secondary, surface) >= 4.5f)
            }
            listOf(
                Triple("Button", c.onPrimary, c.primary),
                Triple("Pressed button", c.onPrimary, c.primaryHover),
                Triple("Selected control", c.primary, c.primaryContainer),
                Triple("Performance", c.onPerformance, c.performanceSurface),
                Triple("Performance label", c.performanceSecondary, c.performanceSurface),
                Triple("Notice", c.onNotice, c.notice),
                Triple("Toast", c.onToast, c.toast),
                Triple("Destructive button", c.background, c.error),
            ).forEach { (label, foreground, surface) ->
                assertTrue(
                    "$label: ${contrast(foreground, surface)}",
                    contrast(foreground, surface) >= 4.5f
                )
            }
        }
    }
}
