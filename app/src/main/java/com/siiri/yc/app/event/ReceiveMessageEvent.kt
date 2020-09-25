package com.siiri.yc.app.event

/**
 * @author: dinglei
 * @date: 2020/9/15 17:47
 */
data class ReceiveMessageEvent(
    val event: Int = EventBusTags.RECEIVER_MSG
)