package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.session.SessionCoordinator
import com.adielcalixto.ifacademico.domain.usecases.LoginUseCase
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(private val session: SessionCoordinator) : LoginUseCase {
    override suspend fun execute(registration: String, password: String) =
        session.login(registration, password)
}
