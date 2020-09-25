package com.siiri.yc.utils

import com.blankj.utilcode.util.GsonUtils
import com.siiri.yc.BuildConfig
import com.siiri.yc.app.Const
import com.siiri.yc.entity.UserInfo

/**
 * @author: dinglei
 * @date: 2020/8/10 14:55
 */
object UserUtils {

    var userInfoStr by SharePreferenceDelegate("key_user_info", "")

    var webViewIP by SharePreferenceDelegate(Const.WEBVIEW_URL_KEY, BuildConfig.WEBVIEW_URL)

    private var historyUrls by SharePreferenceDelegate(Const.HISTORY_WEBVIEW_URL_KEY, "")

    private fun getUserInfo(): UserInfo? {
        if (userInfoStr.isEmpty()) return null
        return GsonUtils.fromJson<UserInfo>(userInfoStr, UserInfo::class.java)
    }

    fun getInputHistories(): MutableList<String> {
        if (historyUrls.isEmpty()) {
            return mutableListOf()
        }
        return historyUrls.split(",").toMutableList()
    }

    fun addInputHistory(url: String) {
        val histories = getInputHistories()
        if (!histories.contains(url)) {
            when {
                historyUrls.isEmpty() -> {
                    historyUrls = url
                }
                histories.size > 3 -> {
                    histories[2] = url
                    historyUrls = histories.joinToString(",")
                }
                else -> historyUrls += ",${url}"
            }
        }
    }

    fun getUserId(): Int? {
        return getUserInfo()?.id
    }

}