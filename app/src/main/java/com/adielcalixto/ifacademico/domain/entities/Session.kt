package com.adielcalixto.ifacademico.domain.entities

import java.time.LocalDateTime
import java.time.ZoneOffset

data class Session (
    val cookies: Set<String>,
    val expiresAt: Long = getExpirationDate(),
    val registration: String,
    val password: String
) {
    val isExpired: Boolean get() = expiresAt < LocalDateTime.now().atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();

    companion object {
        fun getExpirationDate(dateTime: LocalDateTime = LocalDateTime.now()): Long {
            return dateTime.plusMinutes(15).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
    }
}