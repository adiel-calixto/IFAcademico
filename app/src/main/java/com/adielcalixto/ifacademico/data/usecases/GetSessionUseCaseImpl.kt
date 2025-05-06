package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.local.SessionPreferences
import com.adielcalixto.ifacademico.domain.entities.Session
import com.adielcalixto.ifacademico.domain.usecases.GetSessionUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetSessionUseCaseImpl @Inject constructor(private val sessionPreferences: SessionPreferences): GetSessionUseCase {
    override suspend fun execute(): Session {
        val session = sessionPreferences.getSession().first()
        return session
    }
}