package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.data.remote.dto.ExamDTO
import com.adielcalixto.ifacademico.data.remote.dto.PeriodDTO
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.usecases.GetExamsUseCase
import javax.inject.Inject
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.UnknownError
import com.adielcalixto.ifacademico.domain.entities.Exam
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

class GetExamsUseCaseImpl @Inject constructor(
    private val api: AcademicoAPI,
    private val cacheService: CacheService
) : GetExamsUseCase {
    override suspend fun execute(
        diaryId: Int,
        registrationId: Int
    ): Result<List<Exam>, Error.DataError> {
        try {
            val cacheKey = "exams#$registrationId/$diaryId"

            val cachedData = cacheService.get(cacheKey);
            if (cachedData.isNotEmpty()) {
                val data = Json.decodeFromString<List<ExamDTO>>(cachedData)
                return Result.Success(data.map { it.toDomain() })
            }
            val response = api.getExams(diaryId, registrationId)

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