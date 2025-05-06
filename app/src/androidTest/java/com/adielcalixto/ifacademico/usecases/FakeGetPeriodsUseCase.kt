package com.adielcalixto.ifacademico.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.Period
import com.adielcalixto.ifacademico.domain.usecases.GetPeriodsUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeGetPeriodsUseCase @Inject constructor(): GetPeriodsUseCase {
    override suspend fun execute(): Result<List<Period>, Error.DataError> {
        delay(1)
        return Result.Success(listOf(Period(2024, 1)))
    }
}