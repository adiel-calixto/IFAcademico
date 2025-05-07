package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.data.remote.dto.PerformanceGraphDTO
import com.adielcalixto.ifacademico.data.remote.dto.TimeTableDTO
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.UnknownError
import com.adielcalixto.ifacademico.domain.entities.PerformanceCoefficients
import com.adielcalixto.ifacademico.domain.usecases.GetPerformanceCoefficientsUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

class GetPerformanceCoefficientsUseCaseImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val cacheService: CacheService
) : GetPerformanceCoefficientsUseCase {
    override suspend fun execute(): Result<PerformanceCoefficients, Error.DataError> {
        try {
            val cacheKey = "performance_coefficients"

            val cachedData = cacheService.get(cacheKey);
            if (cachedData.isNotEmpty()) {
                val data = Json.decodeFromString<PerformanceGraphDTO>(cachedData)
                return Result.Success(data.toDomain())
            }

            val response = api.getPerformanceGraph()

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