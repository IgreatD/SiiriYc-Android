package com.siiri.yc.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.greenrobot.eventbus.EventBus

/**
 * @author: dinglei
 * @date: 2020/9/11 13:19
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this);
        setContentView(getLayoutId())
        initData()
    }

    abstract fun initData()

    abstract fun getLayoutId(): Int

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

}