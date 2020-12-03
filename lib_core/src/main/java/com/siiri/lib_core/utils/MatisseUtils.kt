package com.siiri.lib_core.utils

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import com.siiri.lib_core.R
import com.siiri.lib_core.common.AppFileProvider
import com.siiri.lib_core.common.MyGlideEngine
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy


/**
 * @author: dinglei
 * @date: 2020/9/29 15:18
 */
object MatisseUtils {

    fun choosePic(activity: Activity, requestCode: Int) {
        Matisse.from(activity)
            .choose(MimeType.ofAll())
            .countable(true)
            .maxSelectable(1)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(MyGlideEngine())
            .showPreview(true)
            .capture(true)
            .captureStrategy(CaptureStrategy(true, AppFileProvider.AUTHORITY))
            .theme(R.style.Matisse)
            .forResult(requestCode)
    }

    fun isImage(path: String): Boolean {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        return options.outWidth != -1
    }

}