package com.siiri.yc.mvp.contract

import android.app.Activity
import android.content.Context
import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView

/**
 * @author: dinglei
 * @date: 2020/8/6 14:14
 */
interface PermissionContract {
    interface View : IView {
        fun getActivity(): Activity
        fun requestPermissionSuccess(code: Int)
    }
}