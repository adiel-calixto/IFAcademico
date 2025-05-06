package com.adielcalixto.ifacademico.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.TimeTable
import com.adielcalixto.ifacademico.domain.usecases.GetTimeTableUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeGetTimeTableUseCase @Inject constructor(): GetTimeTableUseCase {
    override suspend fun execute(): Result<TimeTable, Error.DataError> {
        delay(1)
        return Result.Success(TimeTable(emptyList(), "", ""))
    }
}