package com.adielcalixto.ifacademico.domain.usecases

import com.adielcalixto.ifacademico.domain.entities.Session

interface SaveSessionUseCase {
    suspend fun execute(session: Session)
}