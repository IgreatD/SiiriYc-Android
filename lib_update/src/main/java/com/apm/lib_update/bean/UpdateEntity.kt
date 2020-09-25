package com.apm.lib_update.bean


/**
 * @author: dinglei
 * @date: 2020/6/4 10:44
 */
data class UpdateEntity(
    val content: String = "",
    val createTime: String = "",
    val id: Int = 0,
    val isMandatory: Int = 0,
    val lastModifiedTime: String = "",
    val packageName: String = "",
    val title: String = "",
    val type: Int = 0,
    val url: String = "",
    val version: Int = 0,
    val versionName: String = "",
    val showToast: Boolean = false
)