package com.siiri.lib_core.app

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentManager
import com.jess.arms.base.delegate.AppLifecycles
import com.jess.arms.integration.ConfigModule

/**
 * @author: dinglei
 * @date: 2020/9/29 09:35
 */
abstract class GlobalConfiguration : ConfigModule {

    override fun injectFragmentLifecycle(
        context: Context,
        lifecycles: MutableList<FragmentManager.FragmentLifecycleCallbacks>
    ) {
        lifecycles.add(FragmentLifecycleCallbacksImpl())
    }

    override fun injectAppLifecycle(context: Context, lifecycles: MutableList<AppLifecycles>) {
        lifecycles.add(AppLifeImpl())
    }

    override fun injectActivityLifecycle(
        context: Context,
        lifecycles: MutableList<Application.ActivityLifecycleCallbacks>
    ) {
        lifecycles.add(ActivityLifecycleCallbacksImpl())
    }
}