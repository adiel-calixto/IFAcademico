package com.adielcalixto.ifacademico.data.usecases

import android.util.Log
import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.data.remote.dto.DiaryDTO
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.UnknownError
import com.adielcalixto.ifacademico.domain.entities.Diary
import com.adielcalixto.ifacademico.domain.usecases.GetDiariesUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

class GetDiariesUseCaseImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val cacheService: CacheService
) : GetDiariesUseCase {
    override suspend fun execute(
        registrationId: Int,
        periodNumber: Int,
        periodYear: Int
    ): Result<List<Diary>, Error.DataError> {
        try {
            val cacheKey = "diaries#${periodNumber}/${periodYear}"

            val cachedData = cacheService.get(cacheKey);
            if (cachedData.isNotEmpty()) {
                val data = Json.decodeFromString<List<DiaryDTO>>(cachedData)
                return Result.Success(data.map { it.toDomain() })
            }

            val response = api.getDiaries(periodNumber, periodYear, registrationId = registrationId)

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