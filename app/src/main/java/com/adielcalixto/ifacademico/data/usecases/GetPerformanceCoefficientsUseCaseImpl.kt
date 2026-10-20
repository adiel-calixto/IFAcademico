package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.*
import com.adielcalixto.ifacademico.domain.usecases.GetPerformanceCoefficientsUseCase
import java.time.LocalDate
import javax.inject.Inject

class GetPerformanceCoefficientsUseCaseImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val load: AcademicLoad,
) : GetPerformanceCoefficientsUseCase {
    override suspend fun execute(): Result<PerformanceCoefficients, Error.DataError> =
        load.cached("coefficients", { api.getPerformanceGraph() }) { it.toDomain() }
}
