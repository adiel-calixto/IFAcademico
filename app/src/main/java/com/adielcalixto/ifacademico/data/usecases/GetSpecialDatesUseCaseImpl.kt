package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.*
import com.adielcalixto.ifacademico.domain.usecases.GetSpecialDatesUseCase
import java.time.LocalDate
import javax.inject.Inject

class GetSpecialDatesUseCaseImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val load: AcademicLoad,
) : GetSpecialDatesUseCase {
    override suspend fun execute(
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<SpecialDate>, Error.DataError> =
        load.cached(
            "dates/$startDate/$endDate",
            { api.getSpecialDates(startDate, endDate) }) { it.map { dto -> dto.toDomain() } }
}
