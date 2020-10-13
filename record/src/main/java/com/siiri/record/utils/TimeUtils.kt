package com.siiri.record.utils


/**
 * @author: dinglei
 * @date: 2020/10/11 16:42
 */

object TimeUtils {
    fun secondToTime(second: Long): String {
        val hours = second / 3600 //转换小时数
        var totalSeconds = second % 3600 //剩余秒数
        val minutes = second / 60 //转换分钟
        totalSeconds %= 60 //剩余秒数
        return if (hours > 0) {
            unitFormat(hours) + ":" + unitFormat(
                minutes
            ) + ":" + unitFormat(
                totalSeconds
            )
        } else {
            unitFormat(minutes) + ":" + unitFormat(
                totalSeconds
            )
        }
    }

    private fun unitFormat(i: Long): String {
        return if (i in 0..9) "0$i" else "" + i
    }
}