package com.adielcalixto.ifacademico

import com.adielcalixto.ifacademico.di.UseCasesModule
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
import com.adielcalixto.ifacademico.usecases.FakeGetDiariesUseCase
import com.adielcalixto.ifacademico.usecases.FakeGetExamsUseCase
import com.adielcalixto.ifacademico.usecases.FakeGetPerformanceCoefficientsUseCase
import com.adielcalixto.ifacademico.usecases.FakeGetPeriodsUseCase
import com.adielcalixto.ifacademico.usecases.FakeGetSessionUseCase
import com.adielcalixto.ifacademico.usecases.FakeGetSpecialDatesUseCase
import com.adielcalixto.ifacademico.usecases.FakeGetStudentUseCase
import com.adielcalixto.ifacademico.usecases.FakeGetTimeTableUseCase
import com.adielcalixto.ifacademico.usecases.FakeLoginUseCase
import com.adielcalixto.ifacademico.usecases.FakeLogoutUseCase
import com.adielcalixto.ifacademico.usecases.FakeRefreshSessionUseCase
import com.adielcalixto.ifacademico.usecases.FakeSaveSessionUseCase
import com.adielcalixto.ifacademico.usecases.FakeVerifySessionUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [UseCasesModule::class]
)
abstract class FakeUseCasesModule {
    @Binds
    @Singleton
    abstract fun bindLoginUseCase(fakeLoginUseCase: FakeLoginUseCase): LoginUseCase

    @Binds
    @Singleton
    abstract fun bindLogoutUseCase(fakeLogoutUseCase: FakeLogoutUseCase): LogoutUseCase

    @Binds
    @Singleton
    abstract fun bindGetSessionUseCase(fakeGetSessionUseCase: FakeGetSessionUseCase): GetSessionUseCase

    @Binds
    @Singleton
    abstract fun bindRefreshSessionUseCase(fakeRefreshSessionUseCase: FakeRefreshSessionUseCase): RefreshSessionUseCase

    @Binds
    @Singleton
    abstract fun bindSaveSessionUseCase(fakeSaveSessionUseCase: FakeSaveSessionUseCase): SaveSessionUseCase

    @Binds
    @Singleton
    abstract fun bindVerifySessionUseCase(fakeVerifySessionUseCase: FakeVerifySessionUseCase): VerifySessionUseCase

    @Binds
    @Singleton
    abstract fun bindGetStudentUseCase(fakeGetStudentUseCase: FakeGetStudentUseCase): GetStudentUseCase

    @Binds
    @Singleton
    abstract fun bindGetExamsUseCase(fakeGetExamsUseCase: FakeGetExamsUseCase): GetExamsUseCase

    @Binds
    @Singleton
    abstract fun bindGetDiariesUseCase(fakeGetDiariesUseCase: FakeGetDiariesUseCase): GetDiariesUseCase

    @Binds
    @Singleton
    abstract fun bindGetTimeTableUseCase(fakeGetTimeTableUseCase: FakeGetTimeTableUseCase): GetTimeTableUseCase

    @Binds
    @Singleton
    abstract fun bindGetSpecialDatesUseCase(fakeGetSpecialDatesUseCase: FakeGetSpecialDatesUseCase): GetSpecialDatesUseCase

    @Binds
    @Singleton
    abstract fun bindGetPeriodsUseCase(fakeGetPeriodsUseCase: FakeGetPeriodsUseCase): GetPeriodsUseCase

    @Binds
    @Singleton
    abstract fun bindGetPerformanceCoefficientsUseCase(fakeGetPerformanceCoefficientsUseCase: FakeGetPerformanceCoefficientsUseCase): GetPerformanceCoefficientsUseCase
}