package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.data.remote.dto.DiaryDTO
import com.adielcalixto.ifacademico.data.remote.dto.PeriodDTO
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.UnknownError
import com.adielcalixto.ifacademico.domain.entities.Period
import com.adielcalixto.ifacademico.domain.usecases.GetPeriodsUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

class GetPeriodsUseCaseImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val cacheService: CacheService
) : GetPeriodsUseCase {
    override suspend fun execute(): Result<List<Period>, Error.DataError> {
        try {
            val cacheKey = "periods"

            val cachedData = cacheService.get(cacheKey);
            if (cachedData.isNotEmpty()) {
                val data = Json.decodeFromString<List<PeriodDTO>>(cachedData)
                return Result.Success(data.map { it.toDomain() })
            }

            val response = api.getPeriods()
            val body = response.body()

            if (body != null) {
                cacheService.put(cacheKey, Json.encodeToString(body))

                return Result.Success(body.map { it.toDomain() })
            }
        } catch (e: IOException) {
            return Result.Error(IOError)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Result.Error(UnknownError)
    }
}