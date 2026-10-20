package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.*
import com.adielcalixto.ifacademico.domain.usecases.GetTimeTableUseCase
import java.time.LocalDate
import javax.inject.Inject

class GetTimeTableUseCaseImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val load: AcademicLoad,
) : GetTimeTableUseCase {
    override suspend fun execute(): Result<TimeTable, Error.DataError> =
        load.cached(
            "timetable",
            { api.getTimeTable() }) {
            it.copy(classes = it.classes.sortedBy { course -> course.startTime }).toDomain()
        }
}
