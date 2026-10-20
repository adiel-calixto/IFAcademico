package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.*
import com.adielcalixto.ifacademico.domain.usecases.GetIndividualTimeTableUseCase
import java.time.LocalDate
import javax.inject.Inject

class GetIndividualTimeTableImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val load: AcademicLoad,
) : GetIndividualTimeTableUseCase {
    override suspend fun execute(
        periodYear: Int,
        periodNumber: Int
    ): Result<IndividualTimeTable, Error.DataError> =
        load.cached(
            "individual/$periodYear/$periodNumber",
            { api.getIndividualTimeTable(periodYear, periodNumber) }) { it.toDomain() }
}
