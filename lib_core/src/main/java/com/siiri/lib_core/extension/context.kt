package com.siiri.lib_core.extension

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat

/**
 * @author: dinglei
 * @date: 2020/9/29 10:30
 */
fun View.toggle(flag: Boolean) {
    this.visibility = if (flag) View.VISIBLE else View.GONE
}

fun Context.getResColor(resId: Int) = ContextCompat.getColor(this, resId)

val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)

fun Context.inflateLayout(
    @LayoutRes layoutId: Int,
    parent: ViewGroup? = null,
    attachToRoot: Boolean = false
): View = inflater.inflate(layoutId, parent, attachToRoot)