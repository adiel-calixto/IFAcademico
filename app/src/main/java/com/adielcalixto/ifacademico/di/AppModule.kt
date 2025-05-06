package com.adielcalixto.ifacademico.di

import android.content.Context
import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.local.LruCacheService
import com.adielcalixto.ifacademico.data.local.EncryptionUtil
import com.adielcalixto.ifacademico.data.local.SessionPreferences
import com.adielcalixto.ifacademico.data.local.SessionPreferencesImpl
import com.adielcalixto.ifacademico.data.local.ThemePreferences
import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.data.remote.interceptors.AddCookiesInterceptor
import com.adielcalixto.ifacademico.data.remote.interceptors.CookieInterceptor
import com.adielcalixto.ifacademico.data.remote.interceptors.UnauthorizedErrorInterceptor
import com.adielcalixto.ifacademico.domain.usecases.GetSessionUseCase
import com.adielcalixto.ifacademico.domain.usecases.SaveSessionUseCase
import com.adielcalixto.ifacademico.observers.UnauthorizedApiErrorObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideEncryptionUtil(): EncryptionUtil = EncryptionUtil()

    @Provides
    @Singleton
    fun provideCacheService(): CacheService = LruCacheService()

    @Provides
    @Singleton
    fun provideSessionPreferences(
        @ApplicationContext context: Context,
        encryptionUtil: EncryptionUtil
    ): SessionPreferences {
        return SessionPreferencesImpl(context, encryptionUtil)
    }

    @Provides
    fun provideThemePreferences(@ApplicationContext context: Context): ThemePreferences =
        ThemePreferences(context)

    @Provides
    @Singleton
    fun provideUnauthorizedErrorObserver(): UnauthorizedApiErrorObserver {
        return UnauthorizedApiErrorObserver()
    }

    @Provides
    @Singleton
    fun provideAcademicoApi(
        saveSessionUseCase: SaveSessionUseCase,
        getSessionUseCase: GetSessionUseCase,
        unauthorizedApiErrorObserver: UnauthorizedApiErrorObserver,
        cacheService: CacheService
    ): AcademicoAPI {
        return Retrofit.Builder()
            .baseUrl(AcademicoAPI.BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .addInterceptor(
                        UnauthorizedErrorInterceptor(
                            unauthorizedApiErrorObserver,
                            cacheService
                        )
                    )
                    .addInterceptor(CookieInterceptor(saveSessionUseCase))
                    .addInterceptor(AddCookiesInterceptor(getSessionUseCase))
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AcademicoAPI::class.java)
    }
}