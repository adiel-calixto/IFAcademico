package com.adielcalixto.ifacademico.presentation

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.entities.Student
import com.adielcalixto.ifacademico.domain.usecases.SessionState

data class MainState(
    val isResolvingIdentity: Boolean = false,
    val isFirstLoad: Boolean = true,
    val sessionState: SessionState? = null,
    val student: Student? = null,
    val sessionError: Error.DataError? = null,
    val profileError: Error.DataError? = null,
    val profileLoading: Boolean = false,
    val error: Error.DataError? = null
)
