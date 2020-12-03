package com.siiri.yc.mvp.model.api

import com.siiri.yc.BuildConfig
import com.siiri.yc.utils.UserUtils


/**
 * @author: dinglei
 * @date: 2020/9/13 09:51
 */
interface Api {

    companion object {

        val BASE_URL =
            if (BuildConfig.DEBUG) "http://172.16.40.23:8804/" else "http://${BuildConfig.WEBVIEW_URL}/"

        val UPDATE_URL =
            if (BuildConfig.DEBUG) "http://172.16.40.23:8804/update-apk-service" else "http://${BuildConfig.WEBVIEW_URL}/update-apk-service"

        fun getDownloadApi(fileName: String): String {
            return "http://${UserUtils.webViewIP}/download-service/${fileName}"
        }

    }

}
