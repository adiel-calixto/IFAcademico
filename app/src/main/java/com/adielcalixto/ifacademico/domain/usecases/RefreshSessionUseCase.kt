package com.adielcalixto.ifacademico.domain.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result

interface RefreshSessionUseCase {
    suspend fun execute(): Result<Unit, Error.DataError>
}