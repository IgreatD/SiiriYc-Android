package com.siiri.yc.webview

import android.os.Handler
import android.webkit.WebView
import com.just.agentweb.AgentWebUIControllerImplBase
import com.siiri.yc.mvp.contract.WebViewContract

class WebViewUIController(private val mRootView: WebViewContract.View) :
    AgentWebUIControllerImplBase() {

    override fun onSelectItemsPrompt(
        view: WebView?,
        url: String?,
        ways: Array<out String>?,
        callback: Handler.Callback?
    ) {
        mRootView.chooseImage()
    }
}
