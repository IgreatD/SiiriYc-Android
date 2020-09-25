package com.siiri.yc.extension

import kotlinx.coroutines.*

/**
 * @author: dinglei
 * @date: 2020/6/8 13:40
 */

fun asyncAndAwait(block: () -> Unit, delay: Long = 1000L) {
    GlobalScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) { delay(delay) }
        block()
    }
}

inline fun <reified T> instanceOf(): T = T::class.java.newInstance()