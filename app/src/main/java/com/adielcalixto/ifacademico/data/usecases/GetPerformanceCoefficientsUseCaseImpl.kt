package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.UnknownError
import com.adielcalixto.ifacademico.domain.entities.PerformanceCoefficients
import com.adielcalixto.ifacademico.domain.usecases.GetPerformanceCoefficientsUseCase
import java.io.IOException
import javax.inject.Inject

class GetPerformanceCoefficientsUseCaseImpl @Inject constructor(private val api: AcademicoAPI): GetPerformanceCoefficientsUseCase {
    override suspend fun execute(): Result<PerformanceCoefficients, Error.DataError> {
        try {
            val response = api.getPerformanceGraph()

            if(response.isSuccessful) {
                val body = response.body()

                if (body != null) {
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