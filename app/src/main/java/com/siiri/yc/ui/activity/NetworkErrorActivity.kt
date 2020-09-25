package com.siiri.yc.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.NetworkUtils
import com.qmuiteam.qmui.kotlin.onClick
import com.siiri.yc.R
import com.siiri.yc.app.IntentConst
import com.siiri.yc.utils.SwitchIpUtil
import kotlinx.android.synthetic.main.agentweb_error_page.*

/**
 * @author: dinglei
 * @date: 2020/9/1 14:44
 */
class NetworkErrorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agentweb_error_page)
        bt_switch.onClick {
            SwitchIpUtil.switchIp(this)
        }
        bt_reload.onClick {
            NetworkUtils.isAvailableAsync {
                if (it) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(
                        IntentConst.CLEAR_LS,
                        intent.extras?.getBoolean(IntentConst.CLEAR_LS)
                    )
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

}