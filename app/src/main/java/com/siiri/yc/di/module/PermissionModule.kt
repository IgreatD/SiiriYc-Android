package com.siiri.yc.di.module

import androidx.fragment.app.FragmentActivity
import com.jess.arms.di.scope.ActivityScope
import com.siiri.yc.mvp.contract.PermissionContract
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.Module
import dagger.Provides

/**
 * @author: dinglei
 * @date: 2020/8/6 13:59
 */
@Module
class PermissionModule(private val view: PermissionContract.View) {

    @ActivityScope
    @Provides
    fun providePermissionView(): PermissionContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideRxPermission(): RxPermissions {
        return RxPermissions(this.view.getActivity() as FragmentActivity)
    }

}