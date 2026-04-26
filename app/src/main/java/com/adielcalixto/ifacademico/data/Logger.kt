package com.adielcalixto.ifacademico.data

import android.util.Log

interface Logger {
    fun i(tag: String, message: String, throwable: Throwable? = null)
}

object AndroidLogger : Logger {
    override fun i(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) Log.i(tag, message, throwable) else Log.i(tag, message)
    }
}
