package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.data.remote.dto.IndividualTimeTableDTO
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.UnknownError
import com.adielcalixto.ifacademico.domain.entities.IndividualTimeTable
import com.adielcalixto.ifacademico.domain.usecases.GetIndividualTimeTableUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

class GetIndividualTimeTableImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val cacheService: CacheService
): GetIndividualTimeTableUseCase {
    override suspend fun execute(
        periodYear: Int,
        periodNumber: Int
    ): Result<IndividualTimeTable, Error.DataError> {
        try {
            val cacheKey = "individual_time_table#${periodNumber}/${periodYear}"

            val cacheData = cacheService.get(cacheKey)
            if (cacheData.isNotEmpty()) {
                val data = Json.decodeFromString<IndividualTimeTableDTO>(cacheData)
                return Result.Success(data.toDomain())
            }

            val response = api.getIndividualTimeTable(periodYear, periodNumber)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    cacheService.put(cacheKey, Json.encodeToString(body))
                    return Result.Success(body.toDomain())
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