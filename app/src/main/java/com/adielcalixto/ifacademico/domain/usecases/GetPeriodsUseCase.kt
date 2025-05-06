package com.adielcalixto.ifacademico.domain.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.Period

interface GetPeriodsUseCase {
    suspend fun execute(): Result<List<Period>, Error.DataError>
}