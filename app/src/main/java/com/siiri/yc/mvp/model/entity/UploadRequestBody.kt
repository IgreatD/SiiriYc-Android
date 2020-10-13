package com.siiri.yc.mvp.model.entity

/**
 * @author: dinglei
 * @date: 2020/10/11 13:02
 */
data class UploadRequestBody(
    val fileName: String,
    val directoryUuid: String = "-1",
    val userName: String = ""
)