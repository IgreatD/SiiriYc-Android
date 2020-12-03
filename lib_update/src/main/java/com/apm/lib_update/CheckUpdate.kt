package com.apm.lib_update

import com.apm.lib_update.bean.UpdateConfig
import com.apm.lib_update.bean.UpdateEntity
import com.apm.lib_update.listener.OnBtnClickListener
import com.apm.lib_update.update.UpdateAppUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * @author: dinglei
 * @date: 2020/9/13 09:29
 */
object CheckUpdate {

    fun check(entity: UpdateEntity, apkUrl: String) {
        val serverVersion = entity.version
        val currentVersion = AppUtils.getAppVersionCode()
        if (currentVersion < serverVersion)
            updateApk(entity, apkUrl)
        else {
            if (entity.showToast) {
                ToastUtils.showShort("你已经是最新版本了")
            }
        }
    }

    private fun updateApk(entity: UpdateEntity, appDomain: String) {
        val apkUrl = "${appDomain}/${entity.url}"
        UpdateAppUtils.getInstance()
            .apkUrl(apkUrl)
            .updateTitle("${entity.title}  V${entity.versionName}")
            .updateContent(entity.content)
            .updateConfig(
                UpdateConfig(
                    force = entity.isMandatory == 1
                )
            )
            .setBackPressBtnClickListener(object : OnBtnClickListener {
                override fun onClick(): Boolean {
                    AppUtils.exitApp()
                    return false
                }

            })
            .setCancelBtnClickListener(object : OnBtnClickListener {
                override fun onClick(): Boolean {
                    return false
                }

            })
            .update()
    }

}