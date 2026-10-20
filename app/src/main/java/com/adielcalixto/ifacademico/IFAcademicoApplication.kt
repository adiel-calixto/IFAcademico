package com.adielcalixto.ifacademico

import android.app.Application
import com.adielcalixto.ifacademico.data.session.AppForegroundObserver
import com.adielcalixto.ifacademico.data.session.SessionCoordinator
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class IFAcademicoApplication : Application() {
    @Inject
    lateinit var session: SessionCoordinator
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(AppForegroundObserver { scope.launch { session.foreground() } })
    }
}
