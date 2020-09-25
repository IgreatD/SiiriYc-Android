package com.apm.lib_update.update

import androidx.core.content.FileProvider
import com.apm.lib_update.extension.log
import com.apm.lib_update.extension.yes
import com.apm.lib_update.utils.GlobalContextProvider

/**
 * desc: UpdateFileProvider
 */
class UpdateFileProvider : FileProvider() {
    override fun onCreate(): Boolean {
        val result = super.onCreate()
        (GlobalContextProvider.mContext == null && context != null).yes {
            GlobalContextProvider.mContext = context
            log("内部Provider初始化context：" + GlobalContextProvider.mContext)
        }
        return result
    }
}