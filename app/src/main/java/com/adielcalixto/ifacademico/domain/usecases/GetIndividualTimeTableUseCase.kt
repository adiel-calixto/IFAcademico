package com.adielcalixto.ifacademico.domain.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.IndividualTimeTable

interface GetIndividualTimeTableUseCase {
    suspend fun execute(periodYear: Int, periodNumber: Int): Result<IndividualTimeTable, Error.DataError>
}