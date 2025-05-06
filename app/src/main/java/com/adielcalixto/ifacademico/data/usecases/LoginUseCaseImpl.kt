package com.adielcalixto.ifacademico.data.usecases

import com.adielcalixto.ifacademico.data.local.SessionPreferences
import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.data.remote.dto.LoginDTO
import com.adielcalixto.ifacademico.data.remote.dto.LoginErrorDTO
import com.adielcalixto.ifacademico.domain.Error
import com.adielcalixto.ifacademico.domain.IOError
import com.adielcalixto.ifacademico.domain.NetworkError
import com.adielcalixto.ifacademico.domain.Result
import com.adielcalixto.ifacademico.domain.UnknownError
import com.adielcalixto.ifacademico.domain.usecases.LoginUseCase
import com.google.gson.Gson
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginUseCaseImpl @Inject constructor(private val api: AcademicoAPI, private val sessionPreferences: SessionPreferences): LoginUseCase {
    override suspend fun execute(
        registration: String,
        password: String
    ): Result<Unit, Error.DataError> {
        try {
            api.login(LoginDTO(registration, password))
            sessionPreferences.saveUser(registration, password)

            return Result.Success(Unit)
        } catch(e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()

            if (errorBody != null) {
                val errorResponse = Gson().fromJson(errorBody, LoginErrorDTO::class.java)
                return Result.Error(NetworkError.BadRequest(errorResponse.messages.joinToString()))
            }
        } catch(e: IOException) {
            e.printStackTrace()
            return Result.Error(IOError)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return Result.Error(UnknownError)
    }
}