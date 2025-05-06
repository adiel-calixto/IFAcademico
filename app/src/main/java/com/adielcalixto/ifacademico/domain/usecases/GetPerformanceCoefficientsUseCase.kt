package com.adielcalixto.ifacademico.domain.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.PerformanceCoefficients

interface GetPerformanceCoefficientsUseCase {
    suspend fun execute(): Result<PerformanceCoefficients, Error.DataError>
}