package com.siiri.yc.di.module

import com.jess.arms.di.scope.ActivityScope
import com.siiri.yc.mvp.contract.WebViewContract
import dagger.Module
import dagger.Provides

@Module
class WebViewModule(private val view: WebViewContract.View) {
    @ActivityScope
    @Provides
    fun provideWebView(): WebViewContract.View {
        return this.view
    }

}