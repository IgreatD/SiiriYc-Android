package com.siiri.yc.mvp.model.entity

/**
 * @author: dinglei
 * @date: 2020/10/11 14:12
 */
enum class FileType {
    IMAGE, AUDIO, VIDEO, ALL
}

data class MultiFileEntity(
    val type: FileType,
    val url: String,
    val fileName: String
)