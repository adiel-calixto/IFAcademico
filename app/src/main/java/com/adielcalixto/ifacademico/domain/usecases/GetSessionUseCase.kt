package com.adielcalixto.ifacademico.domain.usecases

import com.adielcalixto.ifacademico.domain.entities.Session

interface GetSessionUseCase {
    suspend fun execute(): Session
}