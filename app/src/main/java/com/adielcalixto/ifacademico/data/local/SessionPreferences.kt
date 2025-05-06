package com.adielcalixto.ifacademico.data.local

import com.adielcalixto.ifacademico.domain.entities.Session
import kotlinx.coroutines.flow.Flow

interface SessionPreferences {
    suspend fun saveCookies(expiresAt: Long, cookies: Set<String>)

    suspend fun saveUser(registration: String, password: String)

    fun getSession(): Flow<Session>

    suspend fun clearSession()
}