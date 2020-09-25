package com.siiri.yc.utils

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import cn.jpush.android.api.JPushMessage
import cn.jpush.android.api.NotificationMessage
import cn.jpush.android.service.JPushMessageReceiver
import com.blankj.utilcode.util.ActivityUtils
import com.siiri.yc.app.event.ReceiveMessageEvent
import com.siiri.yc.ui.activity.MainActivity
import com.siiri.push.TagAliasOperatorHelper
import org.greenrobot.eventbus.EventBus

/**
 * @author: dinglei
 * @date: 2020/9/12 09:49
 */
class JPushMessageReceiver : JPushMessageReceiver() {

    override fun onNotifyMessageOpened(
        context: Context,
        message: NotificationMessage
    ) {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.action = "com.yc.main"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ActivityUtils.startActivity(intent)
    }

    override fun onNotifyMessageArrived(
        context: Context?,
        message: NotificationMessage
    ) {
        EventBus.getDefault().postSticky(ReceiveMessageEvent())
    }

    override fun onTagOperatorResult(context: Context, jPushMessage: JPushMessage) {
        TagAliasOperatorHelper.getInstance()
            .onTagOperatorResult(context, jPushMessage)
        super.onTagOperatorResult(context, jPushMessage)
    }

    override fun onCheckTagOperatorResult(context: Context, jPushMessage: JPushMessage) {
        TagAliasOperatorHelper.getInstance()
            .onCheckTagOperatorResult(context, jPushMessage)
        super.onCheckTagOperatorResult(context, jPushMessage)
    }

    override fun onAliasOperatorResult(context: Context, jPushMessage: JPushMessage) {
        TagAliasOperatorHelper.getInstance()
            .onAliasOperatorResult(context, jPushMessage)
        super.onAliasOperatorResult(context, jPushMessage)
    }

}