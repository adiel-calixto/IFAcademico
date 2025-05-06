package com.adielcalixto.ifacademico.domain.usecases

sealed class SessionState {
    data object Invalid: SessionState()
    data object Valid: SessionState()
    data object CanRefresh: SessionState()
}

interface VerifySessionUseCase {
    suspend fun execute(): SessionState
}