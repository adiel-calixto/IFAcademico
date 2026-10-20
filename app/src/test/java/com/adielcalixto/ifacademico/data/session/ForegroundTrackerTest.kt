package com.adielcalixto.ifacademico.data.session

import org.junit.Assert.assertEquals
import org.junit.Test

class ForegroundTrackerTest {
    @Test
    fun `initial start rotation resume and in app sheets do not refresh but every stopped return does`() {
        var refreshes = 0
        val tracker = ForegroundTracker { refreshes++ }
        tracker.started()
        assertEquals(0, refreshes)
        tracker.stopped(changingConfigurations = true)
        tracker.started()
        assertEquals(0, refreshes)
        repeat(3) {
            tracker.stopped(changingConfigurations = false)
            tracker.started()
        }
        assertEquals(3, refreshes)
    }
}
