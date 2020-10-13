package com.siiri.yc.di.module

import com.jess.arms.di.scope.ActivityScope
import com.siiri.yc.mvp.contract.UploadContract
import com.siiri.yc.mvp.model.UploadModel
import dagger.Module
import dagger.Provides

/**
 * @author: dinglei
 * @date: 2020/9/29 15:26
 */
@Module
class UploadModule(private val view: UploadContract.View) {
    @ActivityScope
    @Provides
    fun provideUploadView(): UploadContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideUploadModel(model: UploadModel): UploadContract.Model {
        return model
    }

}