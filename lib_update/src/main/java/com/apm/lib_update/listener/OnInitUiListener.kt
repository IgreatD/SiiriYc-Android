package com.apm.lib_update.listener

import android.view.View
import com.apm.lib_update.bean.UiConfig
import com.apm.lib_update.bean.UpdateConfig

/**
 * desc: 初始化UI 回调 用于进一步自定义UI
 */
interface OnInitUiListener {

    /**
     * 初始化更新弹窗回调
     * @param view 弹窗view
     * @param updateConfig 当前更新配置
     * @param uiConfig 当前ui配置
     */
    fun onInitUpdateUi(view: View?, updateConfig: UpdateConfig, uiConfig: UiConfig)
}