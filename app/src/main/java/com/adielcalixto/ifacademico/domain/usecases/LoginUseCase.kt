package com.adielcalixto.ifacademico.domain.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result

interface LoginUseCase {
    suspend fun execute(registration: String, password: String): Result<Unit, Error.DataError>
}