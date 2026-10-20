package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.*
import com.adielcalixto.ifacademico.domain.usecases.GetDiariesUseCase
import java.time.LocalDate
import javax.inject.Inject

class GetDiariesUseCaseImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val load: AcademicLoad,
) : GetDiariesUseCase {
    override suspend fun execute(
        registrationId: Int,
        periodNumber: Int,
        periodYear: Int
    ): Result<List<Diary>, Error.DataError> =
        load.cached(
            "diaries/$registrationId/$periodNumber/$periodYear",
            {
                api.getDiaries(
                    periodNumber,
                    periodYear,
                    registrationId,
                    null
                )
            }) { it.map { dto -> dto.toDomain() } }
}
