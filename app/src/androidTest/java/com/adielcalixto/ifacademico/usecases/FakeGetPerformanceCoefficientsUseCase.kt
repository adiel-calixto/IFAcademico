package com.adielcalixto.ifacademico.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.PerformanceCoefficients
import com.adielcalixto.ifacademico.domain.usecases.GetPerformanceCoefficientsUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeGetPerformanceCoefficientsUseCase @Inject constructor(): GetPerformanceCoefficientsUseCase {
    override suspend fun execute(): Result<PerformanceCoefficients, Error.DataError> {
        delay(1)
        return Result.Success(PerformanceCoefficients(10f, 10f, 10f))
    }
}