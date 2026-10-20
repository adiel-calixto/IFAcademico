package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.*
import com.adielcalixto.ifacademico.domain.usecases.GetPeriodsUseCase
import java.time.LocalDate
import javax.inject.Inject

class GetPeriodsUseCaseImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val load: AcademicLoad,
) : GetPeriodsUseCase {
    override suspend fun execute(): Result<List<Period>, Error.DataError> =
        load.cached("periods", { api.getPeriods() }) { it.map { dto -> dto.toDomain() } }
}
