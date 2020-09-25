package com.apm.lib_update.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * appName
 */
val Context.appName
    get() = packageManager.getPackageInfo(
        packageName,
        0
    )?.applicationInfo?.loadLabel(packageManager).toString()

/**
 * 跳转安装
 */
fun Context.installApk(apkPath: String?) {

    if (apkPath.isNullOrEmpty()) return

    val intent = Intent(Intent.ACTION_VIEW)
    val apkFile = File(apkPath)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val contentUri =
            FileProvider.getUriForFile(this, this.packageName + ".fileprovider", apkFile)
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
    } else {
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
    }

    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
}


