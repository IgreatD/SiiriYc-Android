package com.siiri.yc.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.ActivityScope
import com.siiri.yc.di.module.PermissionModule
import com.siiri.yc.di.module.UploadModule
import com.siiri.yc.di.module.WebViewModule
import com.siiri.yc.ui.activity.MainActivity
import dagger.Component

/**
 * @author: dinglei
 * @date: 2020/8/6 14:00
 */
@ActivityScope
@Component(
    modules = [PermissionModule::class, WebViewModule::class, UploadModule::class],
    dependencies = [AppComponent::class]
)
interface MainComponent {
    fun inject(activity: MainActivity)
}