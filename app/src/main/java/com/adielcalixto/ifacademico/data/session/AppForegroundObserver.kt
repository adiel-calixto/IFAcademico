package com.adielcalixto.ifacademico.data.session

import android.app.Activity
import android.app.Application
import android.os.Bundle

class AppForegroundObserver(onReturn: () -> Unit) : Application.ActivityLifecycleCallbacks {
    private val foreground = ForegroundTracker(onReturn)
    override fun onActivityStarted(activity: Activity) = foreground.started()
    override fun onActivityStopped(activity: Activity) =
        foreground.stopped(activity.isChangingConfigurations)

    override fun onActivityCreated(activity: Activity, state: Bundle?) = Unit
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, state: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
}
