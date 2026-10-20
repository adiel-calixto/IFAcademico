package com.adielcalixto.ifacademico.di

import android.content.Context
import com.adielcalixto.ifacademico.BuildConfig
import com.adielcalixto.ifacademico.data.local.CacheService
import com.adielcalixto.ifacademico.data.local.LruCacheService
import com.adielcalixto.ifacademico.data.local.EncryptionUtil
import com.adielcalixto.ifacademico.data.local.SessionPreferences
import com.adielcalixto.ifacademico.data.local.SessionPreferencesImpl
import com.adielcalixto.ifacademico.data.local.SettingsPreferences
import com.adielcalixto.ifacademico.data.AndroidLogger
import com.adielcalixto.ifacademico.data.Logger
import com.adielcalixto.ifacademico.data.remote.AcademicoAPI
import com.adielcalixto.ifacademico.data.remote.GitHubAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
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
    @Singleton
    fun provideSettingsPreferences(@ApplicationContext context: Context): SettingsPreferences =
        SettingsPreferences(context)

    @Provides
    @Singleton
    fun provideSessionClock(): com.adielcalixto.ifacademico.data.session.SessionClock =
        com.adielcalixto.ifacademico.data.session.SystemSessionClock()

    @Provides
    @Named("academicUrl")
    fun provideAcademicUrl(): String = AcademicoAPI.BASE_URL

    @Provides
    @Singleton
    @Named("sessionScope")
    fun provideSessionScope(): kotlinx.coroutines.CoroutineScope =
        kotlinx.coroutines.CoroutineScope(
            kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.IO,
        )

    @Provides
    @Singleton
    @Named("raw")
    fun provideRawAcademicoApi(): AcademicoAPI = Retrofit.Builder()
        .baseUrl(AcademicoAPI.BASE_URL)
        .client(com.adielcalixto.ifacademico.data.remote.academicHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(AcademicoAPI::class.java)

    @Provides
    @Singleton
    fun provideAcademicoApi(
        @Named("raw") raw: AcademicoAPI,
        session: com.adielcalixto.ifacademico.data.session.SessionCoordinator,
    ): AcademicoAPI = com.adielcalixto.ifacademico.data.remote.SessionAcademicoAPI(raw, session)

    @Provides
    @Singleton
    fun provideGitHubApi(): GitHubAPI {
        return Retrofit.Builder()
            .baseUrl(GitHubAPI.BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideLogger(): Logger = AndroidLogger

    @Provides
    @Singleton
    @Named("currentVersion")
    fun provideCurrentVersion(): String = BuildConfig.VERSION_NAME
}