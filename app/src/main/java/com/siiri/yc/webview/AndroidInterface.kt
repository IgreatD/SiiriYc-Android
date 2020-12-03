package com.siiri.yc.webview

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.webkit.JavascriptInterface
import com.apm.lib_update.CheckUpdate
import com.blankj.utilcode.util.GsonUtils
import com.apm.lib_update.bean.UpdateEntity
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.utils.ArmsUtils
import com.just.agentweb.AgentWeb
import com.siiri.lib_core.ui.activity.ImagePreviewActivity
import com.siiri.record.audio.AndroidAudioRecorder
import com.siiri.record.video.VideoPlayActivity
import com.siiri.yc.mvp.contract.WebViewContract
import com.siiri.yc.mvp.model.api.Api
import com.siiri.yc.mvp.model.entity.FileType
import com.siiri.yc.utils.SwitchIpUtil
import com.siiri.yc.utils.UserUtils
import java.lang.Exception

/**
 * @author: dinglei
 * @date: 2020/9/13 08:55
 */
class AndroidInterface(
    private val view: WebViewContract.View,
    private val agentWeb: AgentWeb
) {

    /**
     * 切换Ip地址
     */
    @JavascriptInterface
    fun switchIp() {
        SwitchIpUtil.switchIp(view.getActivity())
    }

    /**
     * 扫描二维码
     */
    @JavascriptInterface
    fun scan() {
        view.requestCameraPermission()
    }

    /**
     * 检查更新，获取从vue中传入的更新数据
     */
    @JavascriptInterface
    fun setUpdateInfo(updateInfoStr: String) {
        try {
            val entity = GsonUtils.fromJson(updateInfoStr, UpdateEntity::class.java)
            CheckUpdate.check(entity, Api.UPDATE_URL)
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
        view.setAliasPush(true)
    }

    /**
     * 用户登出，清除通知信息，删除推送的别名
     */
    @JavascriptInterface
    fun clearUserInfo() {
        (view.getActivity()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
        view.setAliasPush(false)
    }


    /**
     * 选择文件
     */
    @JavascriptInterface
    fun chooseFile() {
        view.showChooseFile()
    }

    /**
     * 跳转视频播放页面
     */
    @JavascriptInterface
    fun startFilePreview(fileName: String, type: Int) {
        when (type) {
            FileType.IMAGE.ordinal -> {
                val intent = Intent(view.getActivity(), ImagePreviewActivity::class.java)
                intent.putExtra("url", Api.getDownloadApi(fileName))
                ArmsUtils.startActivity(intent)
            }
            FileType.AUDIO.ordinal -> {
                AndroidAudioRecorder.with(view.getActivity())
                    .setFilePath(Api.getDownloadApi(fileName))
                    .setAutoPlay(true)
                    .setAutoStart(false)
                    .record()
            }
            FileType.VIDEO.ordinal -> {
                val intent = Intent(view.getActivity(), VideoPlayActivity::class.java)
                intent.putExtra("url", Api.getDownloadApi(fileName))
                ArmsUtils.startActivity(intent)
            }
        }

    }


}