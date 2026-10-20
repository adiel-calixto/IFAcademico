package com.adielcalixto.ifacademico.data.session

/** First launch and activity recreation do not constitute a background return. */
class ForegroundTracker(private val onReturn: () -> Unit) {
    private var started = 0
    private var background = false
    fun started() {
        if (started++ == 0 && background) {
            background = false
            onReturn()
        }
    }

    fun stopped(changingConfigurations: Boolean) {
        started = (started - 1).coerceAtLeast(0)
        if (started == 0 && !changingConfigurations) background = true
    }
}
