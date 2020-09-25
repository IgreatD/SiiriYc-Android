package com.apm.lib_update.extension

import java.io.File

/**
 * 根据文件路径删除文件
 */
fun String?.deleteFile() {
    kotlin.runCatching {
        val file = File(this ?: "")
        (file.isFile).yes {
            file.delete()
            log("删除成功")
        }
    }.onFailure {
        log(it.message)
    }
}