package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.*
import com.adielcalixto.ifacademico.domain.usecases.GetStudentUseCase
import java.time.LocalDate
import javax.inject.Inject

class GetStudentUseCaseImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val load: AcademicLoad,
) : GetStudentUseCase {
    override suspend fun execute(): Result<Student, Error.DataError> =
        load.cached(null, { api.getStudent() }) { it.toDomainEntity() }
}
