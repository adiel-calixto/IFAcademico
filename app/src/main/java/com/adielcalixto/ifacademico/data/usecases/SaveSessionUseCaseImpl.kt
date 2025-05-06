package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.local.SessionPreferences
import com.adielcalixto.ifacademico.domain.entities.Session
import com.adielcalixto.ifacademico.domain.usecases.SaveSessionUseCase
import javax.inject.Inject

class SaveSessionUseCaseImpl @Inject constructor(private val sessionPreferences: SessionPreferences): SaveSessionUseCase {
    override suspend fun execute(session: Session) {
        if (session.expiresAt > 0 && session.cookies.isNotEmpty()) {
            sessionPreferences.saveCookies(session.expiresAt, session.cookies)
        }

        if(session.registration.isNotEmpty() && session.password.isNotEmpty()) {
            sessionPreferences.saveUser(session.registration, session.password)
        }
    }
}