package com.siiri.yc.webview

import android.app.NotificationManager
import android.content.Context
import android.webkit.JavascriptInterface
import com.apm.lib_update.CheckUpdate
import com.blankj.utilcode.util.GsonUtils
import com.apm.lib_update.bean.UpdateEntity
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.just.agentweb.AgentWeb
import com.siiri.yc.config.Api
import com.siiri.yc.ui.activity.MainActivity
import com.siiri.yc.utils.UserUtils
import java.lang.Exception

/**
 * @author: dinglei
 * @date: 2020/9/13 08:55
 */
class AndroidInterface(
    private val activity: MainActivity,
    private val agentWeb: AgentWeb
) {

    /**
     * 扫描二维码
     */
    @JavascriptInterface
    fun scan() {
        activity.startScan()
    }

    /**
     * 检查更新，获取从vue中传入的更新数据
     */
    @JavascriptInterface
    fun setUpdateInfo(updateInfoStr: String) {
        try {
            val entity = GsonUtils.fromJson(updateInfoStr, UpdateEntity::class.java)
            CheckUpdate.check(entity, UserUtils.webViewIP)
        } catch (e: Exception) {

        }
    }

    /**
     * vue页面 -> 我的 -> 清除缓存
     */
    @JavascriptInterface
    fun clearCache() {
        agentWeb.clearWebCache()
        ToastUtils.showShort("缓存清理完成")
    }

    /**
     * vue页面 -> 我的 -> 关于获取版本名称
     */
    @JavascriptInterface
    fun getAppInfo() {
        agentWeb.jsAccessEntrace.quickCallJs("setAppInfo", AppUtils.getAppVersionName())
    }

    /**
     * 登录成功，缓存用户信息，设置推送的别名
     */
    @JavascriptInterface
    fun saveUserInfo(userInfoStr: String) {
        UserUtils.userInfoStr = userInfoStr
        activity.setAliasPush(true)
    }

    /**
     * 用户登出，清除通知信息，删除推送的别名
     */
    @JavascriptInterface
    fun clearUserInfo() {
        (activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
        activity.setAliasPush(false)
    }

}