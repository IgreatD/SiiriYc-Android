package com.siiri.push

import android.content.Context
import cn.jpush.android.api.JPushInterface

/**
 * @author: dinglei
 * @date: 2020/9/12 09:51
 */
object JPushUtils {

    fun init(context: Context) {
        JPushInterface.setDebugMode(BuildConfig.DEBUG)
        JPushInterface.init(context)
    }

    fun setAliasPush(context: Context, id: Int) {
        updateAliasPush(context, TagAliasOperatorHelper.ACTION_SET, id)
    }

    fun delAliasPush(context: Context, id: Int) {
        updateAliasPush(context, TagAliasOperatorHelper.ACTION_DELETE, id)
    }

    private fun updateAliasPush(context: Context, action: Int, id: Int) {
        val tagAliasBean = TagAliasOperatorHelper.TagAliasBean()
        tagAliasBean.action = action
        tagAliasBean.alias = id.toString()
        tagAliasBean.isAliasAction = true
        TagAliasOperatorHelper
            .getInstance()
            ?.handleAction(context, 1, tagAliasBean)
    }

}