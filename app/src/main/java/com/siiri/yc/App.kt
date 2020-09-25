package com.siiri.yc

import android.app.Application
import com.siiri.push.JPushUtils
import kotlin.properties.Delegates

/**
 * @author: dinglei
 * @date: 2020/9/12 09:53
 */
class App : Application() {

    companion object {
        var instance: Application by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        JPushUtils.init(this)
    }
}