package com.adielcalixto.ifacademico.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.IndividualTimeTable
import com.adielcalixto.ifacademico.domain.usecases.GetIndividualTimeTableUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeGetIndividualTimeTableUseCase @Inject constructor(): GetIndividualTimeTableUseCase {
    override suspend fun execute(
        periodYear: Int,
        periodNumber: Int
    ): Result<IndividualTimeTable, Error.DataError> {
        delay(1)
        return Result.Success(IndividualTimeTable(emptyMap(), 0))
    }
}