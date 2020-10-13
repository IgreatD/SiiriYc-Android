package com.siiri.lib_core.app

import android.app.Application
import android.content.Context
import cn.jpush.android.api.JPushInterface
import com.blankj.utilcode.util.Utils
import com.jess.arms.base.delegate.AppLifecycles
import timber.log.Timber
import kotlin.properties.Delegates

class AppLifeImpl : AppLifecycles {

    companion object {
        var instance: Application by Delegates.notNull()

        fun instance() = instance
    }

    override fun attachBaseContext(base: Context) {
        JPushInterface.init(base)
        JPushInterface.setDebugMode(true)
    }

    override fun onCreate(application: Application) {
        instance = application
        Utils.init(application)
        Timber.plant(Timber.DebugTree())
    }

    override fun onTerminate(application: Application) {}
}