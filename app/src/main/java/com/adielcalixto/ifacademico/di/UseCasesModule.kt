package com.adielcalixto.ifacademico.di

import com.adielcalixto.ifacademico.data.usecases.GetDiariesUseCaseImpl
import com.adielcalixto.ifacademico.data.usecases.GetExamsUseCaseImpl
import com.adielcalixto.ifacademico.data.usecases.GetPerformanceCoefficientsUseCaseImpl
import com.adielcalixto.ifacademico.data.usecases.GetPeriodsUseCaseImpl
import com.adielcalixto.ifacademico.data.usecases.GetSessionUseCaseImpl
import com.adielcalixto.ifacademico.data.usecases.GetSpecialDatesUseCaseImpl
import com.adielcalixto.ifacademico.data.usecases.GetStudentUseCaseImpl
import com.adielcalixto.ifacademico.data.usecases.GetTimeTableUseCaseImpl
import com.adielcalixto.ifacademico.data.usecases.LoginUseCaseImpl
import com.adielcalixto.ifacademico.data.usecases.LogoutUseCaseImpl
import com.adielcalixto.ifacademico.data.usecases.RefreshSessionUseCaseImpl
import com.adielcalixto.ifacademico.data.usecases.SaveSessionUseCaseImpl
import com.adielcalixto.ifacademico.data.usecases.VerifySessionUseCaseImpl
import com.adielcalixto.ifacademico.domain.usecases.GetDiariesUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetExamsUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetPerformanceCoefficientsUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetPeriodsUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetSessionUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetSpecialDatesUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetStudentUseCase
import com.adielcalixto.ifacademico.domain.usecases.GetTimeTableUseCase
import com.adielcalixto.ifacademico.domain.usecases.LoginUseCase
import com.adielcalixto.ifacademico.domain.usecases.LogoutUseCase
import com.adielcalixto.ifacademico.domain.usecases.RefreshSessionUseCase
import com.adielcalixto.ifacademico.domain.usecases.SaveSessionUseCase
import com.adielcalixto.ifacademico.domain.usecases.VerifySessionUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCasesModule {
    @Binds
    @Singleton
    abstract fun bindLoginUseCase(loginUseCaseImpl: LoginUseCaseImpl): LoginUseCase

    @Binds
    @Singleton
    abstract fun bindLogoutUseCase(logoutUseCaseImpl: LogoutUseCaseImpl): LogoutUseCase

    @Binds
    @Singleton
    abstract fun bindGetSessionUseCase(getSessionUseCaseImpl: GetSessionUseCaseImpl): GetSessionUseCase

    @Binds
    @Singleton
    abstract fun bindRefreshSessionUseCase(refreshSessionUseCaseImpl: RefreshSessionUseCaseImpl): RefreshSessionUseCase

    @Binds
    @Singleton
    abstract fun bindSaveSessionUseCase(saveSessionUseCaseImpl: SaveSessionUseCaseImpl): SaveSessionUseCase

    @Binds
    @Singleton
    abstract fun bindVerifySessionUseCase(verifySessionUseCaseImpl: VerifySessionUseCaseImpl): VerifySessionUseCase

    @Binds
    @Singleton
    abstract fun bindGetStudentUseCase(getStudentUseCaseImpl: GetStudentUseCaseImpl): GetStudentUseCase

    @Binds
    @Singleton
    abstract fun bindGetExamsUseCase(getExamsUseCaseImpl: GetExamsUseCaseImpl): GetExamsUseCase

    @Binds
    @Singleton
    abstract fun bindGetDiariesUseCase(getDiariesUseCaseImpl: GetDiariesUseCaseImpl): GetDiariesUseCase

    @Binds
    @Singleton
    abstract fun bindGetTimeTableUseCase(getTimeTableUseCaseImpl: GetTimeTableUseCaseImpl): GetTimeTableUseCase

    @Binds
    @Singleton
    abstract fun bindGetSpecialDatesUseCase(getSpecialDatesUseCaseImpl: GetSpecialDatesUseCaseImpl): GetSpecialDatesUseCase

    @Binds
    @Singleton
    abstract fun bindGetPeriodsUseCase(getPeriodsUseCaseImpl: GetPeriodsUseCaseImpl): GetPeriodsUseCase

    @Binds
    @Singleton
    abstract fun bindGetPerformanceCoefficientsUseCase(getPerformanceCoefficientsUseCaseImpl: GetPerformanceCoefficientsUseCaseImpl): GetPerformanceCoefficientsUseCase
}