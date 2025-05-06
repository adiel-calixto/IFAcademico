package com.adielcalixto.ifacademico.domain.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.SpecialDate
import java.time.LocalDate

interface GetSpecialDatesUseCase {
    suspend fun execute(startDate: LocalDate, endDate: LocalDate): Result<List<SpecialDate>, Error.DataError>
}