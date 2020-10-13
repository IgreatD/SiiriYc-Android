package com.siiri.yc.mvp.contract

import android.app.Activity
import com.jess.arms.mvp.IView

/**
 * @author: dinglei
 * @date: 2020/9/29 10:57
 */
interface WebViewContract {
    interface View : IView {
        fun startScan()
        fun exit()
        fun getActivity(): Activity
        fun loadFinished(show: Boolean)
        fun setAliasPush(action: Boolean)
        fun requestCameraPermission()
        fun chooseImage()
        fun showChooseFile()
    }
}