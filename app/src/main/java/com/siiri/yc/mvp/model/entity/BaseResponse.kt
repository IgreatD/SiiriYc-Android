package com.siiri.yc.mvp.model.entity

/**
 * @author: dinglei
 * @date: 2020/9/29 15:01
 */
data class BaseResponse<T>(
    val code: Int,
    val message: String,
    var data: T,
    var isSuccess: Boolean = false
)