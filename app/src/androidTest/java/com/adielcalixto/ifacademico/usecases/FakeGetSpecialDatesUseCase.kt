package com.adielcalixto.ifacademico.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.SpecialDate
import com.adielcalixto.ifacademico.domain.usecases.GetSpecialDatesUseCase
import kotlinx.coroutines.delay
import java.time.LocalDate
import javax.inject.Inject

class FakeGetSpecialDatesUseCase @Inject constructor(): GetSpecialDatesUseCase {
    override suspend fun execute(
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<SpecialDate>, Error.DataError> {
        delay(1)
        return Result.Success(emptyList())
    }
}