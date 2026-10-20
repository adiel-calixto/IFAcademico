package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.session.SessionCoordinator
import com.adielcalixto.ifacademico.domain.usecases.LogoutUseCase
import javax.inject.Inject

class LogoutUseCaseImpl @Inject constructor(private val session: SessionCoordinator) :
    LogoutUseCase {
    override fun execute() = session.logout()
}
