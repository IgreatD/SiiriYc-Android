package com.siiri.yc.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.view.*
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebViewClient
import com.king.zxing.Intents
import com.qmuiteam.qmui.kotlin.onClick
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import com.siiri.push.JPushUtils
import com.siiri.yc.R
import com.siiri.yc.app.PermissionConst
import com.siiri.yc.app.event.EventBusTags
import com.siiri.yc.app.event.ReceiveMessageEvent
import com.siiri.yc.utils.CommonUtils
import com.siiri.yc.utils.SwitchIpUtil
import com.siiri.yc.utils.UserUtils
import com.siiri.yc.webview.AndroidInterface
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.agentweb_error_page.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : BaseActivity() {

    private var mAgentWeb: AgentWeb? = null

    private var exitTime = 0L

    private val mRxPermission by lazy { RxPermissions(this) }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData() {
        initWebView()
        setAliasPush(true)

    }

    private fun initWebView() {
        val url = "http://${UserUtils.webViewIP}:8804"
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                if (!CommonUtils.isIpValid(url, 5000)) {
                    finish()
                    ActivityUtils.startActivity(
                        Intent(
                            this@MainActivity,
                            NetworkErrorActivity::class.java
                        )
                    )
                }
            }
        }
        webviewContainer.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(
                webviewContainer,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            .closeIndicator()
            .setWebViewClient(object : WebViewClient() {

                override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                    LogUtils.dTag("MainActivity-WebView", "onPageStarted")
                    super.onPageStarted(view, url, favicon)

                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    LogUtils.dTag("MainActivity-WebView", "onPageFinished")
                    webviewContainer.visibility = View.VISIBLE
                    loadingView.visibility = View.GONE
                    super.onPageFinished(view, url)
                }

            })
            .setMainFrameErrorView(getErrorView())
            .interceptUnkownUrl()
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .createAgentWeb()
            .ready()
            .go(url)
        mAgentWeb?.let {
            it.jsInterfaceHolder.addJavaObject("android", AndroidInterface(this, it))
            addBGChild(it.webCreator.webParentLayout as FrameLayout)
        }
        // 请求存储权限，请求成功后检查版本更新
        mRxPermission.request(PermissionConst.WRITE_EXTERNAL_STORAGE_PERMISSION)
            .subscribe {
                if (it) {
                    mAgentWeb?.jsAccessEntrace?.quickCallJs("checkUpdate")
                } else {
                    tipPermissions()
                }
            }
    }

    private fun getErrorView(): View {
        val errorView = LayoutInflater.from(this).inflate(R.layout.agentweb_error_page, null)
        errorView.bt_reload.onClick {
            mAgentWeb?.urlLoader?.reload()
        }
        errorView.bt_switch.onClick {
            SwitchIpUtil.switchIp(this)
        }
        return errorView
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (mAgentWeb?.handleKeyEvent(keyCode, event) != false) {
            return true
        }
        if (KeyEvent.KEYCODE_BACK == keyCode && event!!.action == KeyEvent.ACTION_DOWN) {
            exit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun exit() {
        if (System.currentTimeMillis() - exitTime > DELAY_EXIT) {
            Toast.makeText(this, StringUtils.getString(R.string.exit_app), Toast.LENGTH_SHORT)
                .show()
            exitTime = System.currentTimeMillis()
        } else {
            AppUtils.exitApp()
        }
    }

    override fun onPause() {
        mAgentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    override fun onResume() {
        mAgentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mAgentWeb?.webLifeCycle?.onDestroy()
        super.onDestroy()
    }

    private fun addBGChild(frameLayout: FrameLayout) {
        val mTextView = TextView(frameLayout.context)
        mTextView.text = "技术由 国机互联 提供"
        mTextView.textSize = 16f
        mTextView.setTextColor(Color.parseColor("#277de0"))
        frameLayout.setBackgroundColor(Color.parseColor("#272b2d"))
        val mFlp =
            FrameLayout.LayoutParams(-2, -2)
        mFlp.gravity = Gravity.CENTER_HORIZONTAL
        val scale =
            frameLayout.context.resources.displayMetrics.density
        mFlp.topMargin = (15 * scale + 0.5f).toInt()
        frameLayout.addView(mTextView, 0, mFlp)
    }

    /**
     * 推送别名的设置和删除
     *
     * @param flag true: 设置别名，false: 删除别名
     */
    fun setAliasPush(flag: Boolean) {
        val personId = UserUtils.getUserId() ?: -1
        if (personId != -1)
            if (flag)
                JPushUtils.setAliasPush(this, personId)
            else
                JPushUtils.delAliasPush(this, personId)
    }

    /**
     *
     * 接收到推送的通知，通知vue页面获取未处理的消息数据，刷新页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public fun onEvent(event: ReceiveMessageEvent) {
        if (event.event == EventBusTags.RECEIVER_MSG) {
            mAgentWeb?.jsAccessEntrace?.quickCallJs("receiveMsg")
        }
    }

    /**
     * 请求相机权限，成功后扫描二维码
     */
    fun startScan() {
        runOnUiThread {
            mRxPermission.request(PermissionConst.CAMERA_PERMISSION)
                .subscribe {
                    if (it) {
                        startActivityForResult(
                            Intent(this, CaptureActivity::class.java),
                            QRCODE_RESULT
                        )
                    } else {
                        tipPermissions()
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null != data && resultCode == -1) {
            when (requestCode) {
                QRCODE_RESULT -> {
                    val result = data.getStringExtra(Intents.Scan.RESULT)
                    if (result?.isNotEmpty() == true)
                        mAgentWeb?.jsAccessEntrace?.quickCallJs("scanResult", result)
                }
            }
        }
    }

    private fun tipPermissions() {
        QMUIDialog.MessageDialogBuilder(this)
            .setCanceledOnTouchOutside(false)
            .setCancelable(false)
            .setTitle("权限申请")
            .setMessage("为了给您提供更好的服务，${AppUtils.getAppName()} 需要申请权限")
            .setSkinManager(QMUISkinManager.defaultInstance(this))
            .setMaxPercent(.75f)
            .addAction(
                0,
                "取消",
                QMUIDialogAction.ACTION_PROP_NEGATIVE
            ) { dialog, _ -> dialog.dismiss() }
            .addAction(
                0,
                "去设置",
                QMUIDialogAction.ACTION_PROP_POSITIVE
            ) { dialog, _ ->
                dialog.dismiss()
                AppUtils.launchAppDetailsSettings()
            }
            .show()
    }

    fun switchIp() {
        runOnUiThread {
            SwitchIpUtil.switchIp(this)
        }
    }

    companion object {
        private const val DELAY_EXIT = 2000
        const val QRCODE_RESULT = 2
    }

}