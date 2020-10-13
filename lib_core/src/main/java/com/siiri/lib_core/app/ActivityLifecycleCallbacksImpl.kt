package com.siiri.lib_core.app

import android.app.Activity
import android.app.Application
import android.os.Bundle

import timber.log.Timber

class ActivityLifecycleCallbacksImpl : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?
    ) {
        Timber.i("%s - onActivityCreated", activity)
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.i("%s - onActivityStarted", activity)
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.i("%s - onActivityResumed", activity)
    }

    override fun onActivityPaused(activity: Activity) {
        Timber.i("%s - onActivityPaused", activity)
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.i("%s - onActivityStopped", activity)
    }

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle
    ) {
        Timber.i("%s - onActivitySaveInstanceState", activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.i("%s - onActivityDestroyed", activity)
        activity.intent.removeExtra("isInitToolbar")
    }
}