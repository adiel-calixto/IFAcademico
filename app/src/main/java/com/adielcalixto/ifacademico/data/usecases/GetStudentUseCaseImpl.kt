package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.UnknownError
import com.adielcalixto.ifacademico.domain.entities.Student
import com.adielcalixto.ifacademico.domain.usecases.GetStudentUseCase
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetStudentUseCaseImpl @Inject constructor(private val api: AcademicoAPI): GetStudentUseCase {
    override suspend fun execute(): Result<Student, Error.DataError> {
        try {
            val response = api.getStudent()
            val body = response.body()

            if (body != null) {
                return Result.Success(body.toDomainEntity())
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