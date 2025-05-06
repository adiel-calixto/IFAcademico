package com.adielcalixto.ifacademico.usecases

import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.usecases.RefreshSessionUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeRefreshSessionUseCase @Inject constructor(): RefreshSessionUseCase {
    override suspend fun execute(): Result<Unit, Error.DataError> {
        delay(1)
        return Result.Success(Unit)
    }
}