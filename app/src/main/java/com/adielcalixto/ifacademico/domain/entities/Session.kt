package com.adielcalixto.ifacademico.domain.entities

/** Epoch milliseconds refer to successful authentication, never cookie traffic. */
data class Session(
    val cookies: Set<String> = emptySet(),
    val expiresAt: Long = 0, // Legacy schema only; never used as an authentication clock.
    val registration: String = "",
    val password: String = "",
    val loggedInAt: Long? = null,
    val schemaVersion: Int = 2,
)
