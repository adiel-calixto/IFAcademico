package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.*
import com.adielcalixto.ifacademico.domain.usecases.GetExamsUseCase
import java.time.LocalDate
import javax.inject.Inject

class GetExamsUseCaseImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val load: AcademicLoad,
) : GetExamsUseCase {
    override suspend fun execute(
        diaryId: Int,
        registrationId: Int
    ): Result<List<Exam>, Error.DataError> =
        load.cached(
            "exams/$registrationId/$diaryId",
            { api.getExams(diaryId, registrationId) }) { it.map { dto -> dto.toDomain() } }
}
