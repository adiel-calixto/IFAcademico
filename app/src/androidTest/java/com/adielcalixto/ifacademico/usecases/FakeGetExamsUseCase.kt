package com.adielcalixto.ifacademico.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.entities.Exam
import com.adielcalixto.ifacademico.domain.usecases.GetExamsUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeGetExamsUseCase @Inject constructor(): GetExamsUseCase {
    override suspend fun execute(
        diaryId: Int,
        registrationId: Int
    ): Result<List<Exam>, Error.DataError> {
        delay(1)
        return Result.Success(emptyList())
    }
}