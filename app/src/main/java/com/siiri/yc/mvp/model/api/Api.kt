package com.siiri.yc.mvp.model.api

import com.siiri.yc.BuildConfig


/**
 * @author: dinglei
 * @date: 2020/9/13 09:51
 */
interface Api {
    companion object {
        const val BASE_URL = "http://${BuildConfig.WEBVIEW_URL}:8812/api/"
    }
}
