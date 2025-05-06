package com.adielcalixto.ifacademico.usecases

import com.adielcalixto.ifacademico.domain.entities.Session
import com.adielcalixto.ifacademico.domain.usecases.GetSessionUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeGetSessionUseCase @Inject constructor(): GetSessionUseCase {
    override suspend fun execute(): Session {
        delay(1)
        return Session(emptySet(), 0, "", "")
    }
}