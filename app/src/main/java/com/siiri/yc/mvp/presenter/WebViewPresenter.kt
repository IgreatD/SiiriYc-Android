package com.siiri.yc.mvp.presenter

import android.graphics.Bitmap
import android.graphics.Color
import android.view.*
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.blankj.utilcode.util.LogUtils
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.mvp.BasePresenter
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebViewClient
import com.qmuiteam.qmui.kotlin.onClick
import com.siiri.lib_core.extension.inflateLayout
import com.siiri.yc.R
import com.siiri.yc.mvp.contract.WebViewContract
import com.siiri.yc.utils.SwitchIpUtil
import com.siiri.yc.utils.UserUtils
import com.siiri.yc.webview.AndroidInterface

import kotlinx.android.synthetic.main.agentweb_error_page.view.*
import javax.inject.Inject

/**
 * @author: dinglei
 * @date: 2020/9/29 10:56
 */
@ActivityScope
class WebViewPresenter @Inject constructor(
    view: WebViewContract.View
) : BasePresenter<Nothing, WebViewContract.View>(view) {

    private var mAgentWeb: AgentWeb? = null

    private var mPageLoadFinished = false

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        mAgentWeb = AgentWeb.with(mRootView.getActivity())
            .setAgentWebParent(
                mRootView.getActivity().findViewById(R.id.webviewContainer),
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            .closeIndicator()
            .setWebViewClient(object : WebViewClient() {

                override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                    LogUtils.dTag("MainActivity-WebView", "onPageStarted")
                    mPageLoadFinished = false
                    super.onPageStarted(view, url, favicon)

                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    LogUtils.dTag("MainActivity-WebView", "onPageFinished")
                    if (!mPageLoadFinished)
                        mRootView.loadFinished(true)
                    mPageLoadFinished = true
                    super.onPageFinished(view, url)
                }

            })
            .setMainFrameErrorView(getErrorView())
            .interceptUnkownUrl()
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .createAgentWeb()
            .ready()
            .go(UserUtils.getWebViewUrl())
        mAgentWeb?.let {
            it.jsInterfaceHolder.addJavaObject(
                "android",
                AndroidInterface(mRootView, it)
            )
            addBGChild(it.webCreator.webParentLayout as FrameLayout)
        }
    }

    private fun getErrorView(): View {
        val errorView = mRootView.getActivity().inflateLayout(R.layout.agentweb_error_page)
        errorView.bt_reload.onClick {
            mAgentWeb?.urlLoader?.reload()
        }
        errorView.bt_switch.onClick {
            SwitchIpUtil.switchIp(mRootView.getActivity())
        }
        return errorView
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

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        mAgentWeb?.webLifeCycle?.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        mAgentWeb?.webLifeCycle?.onResume()
    }

    override fun onDestroy() {
        mAgentWeb?.webLifeCycle?.onDestroy()
    }

    fun handleKeyEvent(keyCode: Int, event: KeyEvent?): Boolean? {
        return mAgentWeb?.handleKeyEvent(keyCode, event)
    }

    fun receiveMsg() {
        mAgentWeb?.jsAccessEntrace?.quickCallJs("receiveMsg")
    }

    fun scanResult(result: String) {
        mAgentWeb?.jsAccessEntrace?.quickCallJs("scanResult", result)
    }

    fun checkUpdate() {
        mAgentWeb?.jsAccessEntrace?.quickCallJs("checkUpdate")
    }

    fun uploadSuccess(fileName: String) {
        mAgentWeb?.jsAccessEntrace?.quickCallJs("uploadSuccess", fileName)
    }


}