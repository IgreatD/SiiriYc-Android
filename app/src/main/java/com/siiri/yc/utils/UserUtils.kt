package com.siiri.yc.utils

import com.blankj.utilcode.util.GsonUtils
import com.siiri.lib_core.utils.SharePreferenceDelegate
import com.siiri.yc.BuildConfig
import com.siiri.yc.app.Const
import com.siiri.yc.entity.UserInfo
import me.jessyan.retrofiturlmanager.RetrofitUrlManager


/**
 * @author: dinglei
 * @date: 2020/8/10 14:55
 */
object UserUtils {

    var userInfoStr by SharePreferenceDelegate("key_user_info", "")

    var webViewIP by SharePreferenceDelegate(Const.WEBVIEW_URL_KEY, "")

    private var historyUrls by SharePreferenceDelegate(Const.HISTORY_WEBVIEW_URL_KEY, "")

    fun getWebViewUrl(): String {
        RetrofitUrlManager.getInstance().setGlobalDomain("http://${webViewIP}:8812/")
        return "http://${webViewIP}:8804"
    }

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

    fun removeInputHistory(url: String) {
        val histories = getInputHistories()
        if (histories.isNotEmpty()) {
            histories.remove(url)
            historyUrls = histories.joinToString(",")
        }
    }

    fun addInputHistory(url: String) {
        val histories = getInputHistories()
        if (!histories.contains(url)) {
            when {
                historyUrls.isEmpty() -> {
                    historyUrls = url
                }
                /*histories.size > 3 -> {
                    histories[2] = url
                    historyUrls = histories.joinToString(",")
                }*/
                else -> historyUrls += ",${url}"
            }
        }
    }

    fun getUserId(): Int? {
        return getUserInfo()?.id
    }

}