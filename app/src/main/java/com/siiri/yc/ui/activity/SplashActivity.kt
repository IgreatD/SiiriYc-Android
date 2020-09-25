package com.siiri.yc.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.ToastUtils
import com.qmuiteam.qmui.kotlin.onClick
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.siiri.yc.R
import com.siiri.yc.app.IntentConst
import com.siiri.yc.utils.UserUtils
import kotlinx.android.synthetic.main.activity_splash.*


/**
 * @author: dinglei
 * @date: 2020/6/8 17:45
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        button.onClick {
            startMainActivity(false)
        }
        iv_switch.onClick {
            switchIp()
        }
        /*asyncAndAwait({
            NetworkUtils.isAvailableAsync {
                if (it) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else
                    startActivity(Intent(this, NetworkErrorActivity::class.java))
                finish()
            }

        }, 50)*/
    }

    /**
     * 切换Ip地址
     */
    private fun switchIp() {
        // 默认设置两个常用的ip
        val autoUrls = mutableListOf("172.16.40.23", "117.158.214.190")
        // 添加缓存的Ip
        autoUrls.addAll(UserUtils.getInputHistories())
        val dialog = QMUIDialog.CustomDialogBuilder(this)
            .setLayout(R.layout.layout_switch_url)
            .setTitle("切换IP地址")
            .setCanceledOnTouchOutside(true)
            .setCancelable(true)
            .addAction("取消") { dialog, _ -> dialog.dismiss() }
            .addAction("确定") { dialog, _ ->
                val inputUrl =
                    dialog.findViewById<AppCompatAutoCompleteTextView>(R.id.etUrl)?.text.toString()
                val ip = RegexUtils.isIP(inputUrl)
                if (!ip) {
                    ToastUtils.showShort("请输入正确的IP地址")
                    return@addAction
                }
                // 输入的ip和之前保存的Ip一样
                if (TextUtils.equals(inputUrl, UserUtils.webViewIP)) {
                    startMainActivity(false)
                    dialog.dismiss()
                } else {
                    UserUtils.webViewIP = inputUrl
                    // 缓存输入的ip
                    if (!autoUrls.contains(inputUrl)) {
                        UserUtils.addInputHistory(inputUrl)
                    }
                    startMainActivity(true)
                    dialog.dismiss()
                }

            }.create()
        val arrayAdapter = object : ArrayAdapter<String>(
            this,
            R.layout.item_drop_down,
            autoUrls
        ) {
            override fun getCount(): Int {
                return autoUrls.size
            }

            override fun getItem(position: Int): String? {
                return autoUrls[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            @SuppressLint("InflateParams")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var view = convertView
                val viewHolder: ViewHolder?
                if (view == null) {
                    viewHolder = ViewHolder()
                    val mInflater = LayoutInflater.from(context)
                    view = mInflater.inflate(R.layout.item_drop_down, null)
                    viewHolder.content =
                        view.findViewById(R.id.tvUrl) as TextView
                    view.tag = viewHolder
                } else {
                    viewHolder = view.tag as ViewHolder
                }
                viewHolder.content?.text = autoUrls[position]
                return view!!
            }

            inner class ViewHolder {
                var content: TextView? = null
            }

        }
        val etUrl = dialog.findViewById<AppCompatAutoCompleteTextView>(R.id.etUrl)
        etUrl?.apply {
            setText(UserUtils.webViewIP)
            setDropDownBackgroundResource(R.color.white)
            setAdapter(arrayAdapter)
            // 添加焦点监听事件，获取到焦点后，显示下拉框
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    etUrl.showDropDown()
                }
            }
        }
        dialog.show()
    }

    private fun startMainActivity(clearLs: Boolean) {
//        判断网络是否可以 -- 同步的方式
        NetworkUtils.isAvailableAsync {
            val intent = Intent()
            intent.putExtra(IntentConst.CLEAR_LS, clearLs)
            if (it) {
                intent.setClass(this, MainActivity::class.java)
            } else {
                intent.setClass(this, NetworkErrorActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}