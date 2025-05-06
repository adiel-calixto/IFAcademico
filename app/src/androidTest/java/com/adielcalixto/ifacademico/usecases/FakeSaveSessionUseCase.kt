package com.adielcalixto.ifacademico.usecases

import com.adielcalixto.ifacademico.domain.entities.Session
import com.adielcalixto.ifacademico.domain.usecases.SaveSessionUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeSaveSessionUseCase @Inject constructor(): SaveSessionUseCase {
    override suspend fun execute(session: Session) {
        delay(1)
    }
}