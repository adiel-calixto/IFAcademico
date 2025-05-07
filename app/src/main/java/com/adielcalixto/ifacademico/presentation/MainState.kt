package com.adielcalixto.ifacademico.presentation

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.entities.Student
import com.adielcalixto.ifacademico.domain.usecases.SessionState

data class MainState(
    val isFirstLoad: Boolean = true,
    val sessionState: SessionState? = null,
    val student: Student? = null,
    val error: Error.DataError? = null
)
