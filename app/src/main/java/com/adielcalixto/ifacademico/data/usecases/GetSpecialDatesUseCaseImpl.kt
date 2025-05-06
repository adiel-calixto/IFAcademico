package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.data.remote.dto.SpecialDateDTO
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.UnknownError
import com.adielcalixto.ifacademico.domain.entities.SpecialDate
import com.adielcalixto.ifacademico.domain.usecases.GetSpecialDatesUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject

class GetSpecialDatesUseCaseImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val cacheService: CacheService
) : GetSpecialDatesUseCase {
    override suspend fun execute(
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<SpecialDate>, Error.DataError> {
        try {
            val cacheKey = "special_dates#$startDate-$endDate"

            val cachedData = cacheService.get(cacheKey);
            if (cachedData.isNotEmpty()) {
                val data = Json.decodeFromString<List<SpecialDateDTO>>(cachedData)
                return Result.Success(data.map { it.toDomain() })
            }

            val response = api.getSpecialDates(startDate, endDate)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    cacheService.put(cacheKey, Json.encodeToString(body))
                    return Result.Success(body.map { it.toDomain() })
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