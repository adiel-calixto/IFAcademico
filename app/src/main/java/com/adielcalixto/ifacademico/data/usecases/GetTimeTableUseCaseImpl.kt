package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.data.remote.dto.PeriodDTO
import com.adielcalixto.ifacademico.data.remote.dto.TimeTableDTO
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.UnknownError
import com.adielcalixto.ifacademico.domain.entities.TimeTable
import com.adielcalixto.ifacademico.domain.usecases.GetTimeTableUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

class GetTimeTableUseCaseImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val cacheService: CacheService
) : GetTimeTableUseCase {
    override suspend fun execute(): Result<TimeTable, Error.DataError> {
        try {
            val cacheKey = "timetables"

            val cachedData = cacheService.get(cacheKey);
            if (cachedData.isNotEmpty()) {
                val data = Json.decodeFromString<TimeTableDTO>(cachedData)
                return Result.Success(data.toDomain())
            }

            val response = api.getTimeTable()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    val timeTable = body.copy(classes = body.classes.sortedBy { it.startTime })
                    cacheService.put(cacheKey, Json.encodeToString(timeTable))

                    return Result.Success(timeTable.toDomain())
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