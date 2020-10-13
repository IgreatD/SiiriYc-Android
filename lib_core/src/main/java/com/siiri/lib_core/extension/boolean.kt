package com.siiri.lib_core.extension

/**
 * @author: dinglei
 * @date: 2020/9/29 10:35
 */
sealed class BooleanExt<out T>

object Otherwise : BooleanExt<Nothing>()

class TransferData<T>(val data: T) : BooleanExt<T>()

inline fun <T> Boolean.yes(block: () -> T): BooleanExt<T> = when {
    this -> {
        TransferData(block.invoke())
    }
    else -> Otherwise
}

inline fun <T> BooleanExt<T>.no(block: () -> T): T = when (this) {
    is Otherwise ->
        block()
    is TransferData ->
        this.data
}
