package com.adielcalixto.ifacademico.usecases

import com.adielcalixto.ifacademico.domain.usecases.SessionState
import com.adielcalixto.ifacademico.domain.usecases.VerifySessionUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeVerifySessionUseCase @Inject constructor(): VerifySessionUseCase {
    override suspend fun execute(): SessionState {
        delay(1)
        return SessionState.Invalid
    }
}