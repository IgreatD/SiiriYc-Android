package com.siiri.yc.mvp.model.entity

/**
 * @author: dinglei
 * @date: 2020/10/12 09:31
 */
data class UploadResponseEntity(
    val directoryUuid: String = "",
    val fileName: String = "",
    val directoryName: String = "",
    val parentDirectoryUuid: String = "",
    val createTime: String = "",
    val lastUpdateTime: String = "",
    val isFile: String = "",
    val md5: String = "",
    val userName: String = ""
)