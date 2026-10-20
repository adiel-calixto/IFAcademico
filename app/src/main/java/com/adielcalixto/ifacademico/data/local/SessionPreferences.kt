package com.adielcalixto.ifacademico.data.local

import com.adielcalixto.ifacademico.domain.entities.Session
import kotlinx.coroutines.flow.Flow

interface SessionPreferences {
    fun getSession(): Flow<Session>
    suspend fun replaceSession(session: Session)
    suspend fun clearSession()
}
