package com.siiri.yc.app

import com.jess.arms.mvp.IView
import com.jess.arms.utils.RxLifecycleUtils
import com.siiri.lib_core.app.execption.ResponseException
import com.siiri.lib_core.extension.instanceOf
import com.siiri.yc.mvp.model.api.Api
import com.siiri.yc.mvp.model.entity.BaseResponse
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 放置便于使用 RxJava 的一些工具方法
 */

inline fun <reified T> applySchedulers(
    view: IView
): ObservableTransformer<T, T> {
    return ObservableTransformer { observable ->
        observable.subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                view.showLoading() //显示进度条
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                view.hideLoading() //隐藏进度条
            }.compose(RxLifecycleUtils.bindToLifecycle(view))

    }
}