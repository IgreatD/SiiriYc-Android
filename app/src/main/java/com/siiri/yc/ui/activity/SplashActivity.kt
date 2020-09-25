package com.siiri.yc.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.NetworkUtils
import com.siiri.yc.extension.asyncAndAwait


/**
 * @author: dinglei
 * @date: 2020/6/8 17:45
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setContentView(R.layout.activity_splash)
        SwitchIpUtil.switchIp(this)
        iv_switch.onClick {
            SwitchIpUtil.switchIp(this)
        }*/
        asyncAndAwait({
            NetworkUtils.isAvailableAsync {
                if (it) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else
                    startActivity(Intent(this, NetworkErrorActivity::class.java))
                finish()
            }

        }, 50)
    }

}