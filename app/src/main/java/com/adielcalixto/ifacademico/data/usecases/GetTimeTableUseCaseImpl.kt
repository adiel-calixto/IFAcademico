package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.UnknownError
import com.adielcalixto.ifacademico.domain.entities.TimeTable
import com.adielcalixto.ifacademico.domain.usecases.GetTimeTableUseCase
import java.io.IOException
import javax.inject.Inject

class GetTimeTableUseCaseImpl @Inject constructor(private val api: AcademicoAPI): GetTimeTableUseCase {
    override suspend fun execute(): Result<TimeTable, Error.DataError> {
        try {
            val response = api.getTimeTable()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    val timeTable = body.toDomain()
                    return Result.Success(timeTable.copy(classes = timeTable.classes.sortedBy { it.startTime }))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Result.Error(IOError)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return Result.Error(UnknownError)
    }
}