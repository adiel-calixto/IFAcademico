package com.adielcalixto.ifacademico.domain.usecases

sealed interface SessionState {
    data object Invalid : SessionState
    data object Valid : SessionState
}
