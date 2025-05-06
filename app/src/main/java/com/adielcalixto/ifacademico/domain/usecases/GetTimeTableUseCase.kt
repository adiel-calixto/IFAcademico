package com.adielcalixto.ifacademico.domain.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.TimeTable

interface GetTimeTableUseCase {
    suspend fun execute(): Result<TimeTable, Error.DataError>
}