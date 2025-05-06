package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.domain.entities.Session
import com.adielcalixto.ifacademico.domain.usecases.GetSessionUseCase
import com.adielcalixto.ifacademico.domain.usecases.SessionState
import com.adielcalixto.ifacademico.domain.usecases.VerifySessionUseCase
import javax.inject.Inject

private val Session.canBeRefreshed get() = this.password.isNotEmpty() && this.registration.isNotEmpty()

class VerifySessionUseCaseImpl @Inject constructor(private val getSessionUseCase: GetSessionUseCase) :
    VerifySessionUseCase {
    override suspend fun execute(): SessionState {
        val session = getSessionUseCase.execute()

        if (session.isExpired) {
            return if (session.canBeRefreshed) SessionState.CanRefresh else SessionState.Invalid
        }

        return SessionState.Valid
    }
}