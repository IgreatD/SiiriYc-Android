package com.siiri.lib_core.common

import androidx.core.content.FileProvider
import com.blankj.utilcode.util.AppUtils
import com.bumptech.glide.Glide

/**
 * @author: dinglei
 * @date: 2020/7/22 16:52
 */
class AppFileProvider : FileProvider() {

    companion object {
        val AUTHORITY = AppUtils.getAppPackageName() + ".fileprovider"

    }

}