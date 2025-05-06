package com.adielcalixto.ifacademico.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.Student
import com.adielcalixto.ifacademico.domain.usecases.GetStudentUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeGetStudentUseCase @Inject constructor(): GetStudentUseCase {
    override suspend fun execute(): Result<Student, Error.DataError> {
        delay(1)
        return Result.Success(Student("Test", 1, 1, "Test"))
    }
}