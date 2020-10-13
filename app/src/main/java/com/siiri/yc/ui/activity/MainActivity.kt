package com.siiri.yc.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.blankj.utilcode.util.*
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.king.zxing.Intents
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.siiri.lib_core.extension.toggle
import com.siiri.lib_core.ui.activity.CaptureActivity
import com.siiri.lib_core.utils.MatisseUtils
import com.siiri.push.JPushUtils
import com.siiri.record.audio.AndroidAudioRecorder
import com.siiri.record.audio.AudioRecordActivity
import com.siiri.record.video.VideoRecordActivity
import com.siiri.yc.R
import com.siiri.yc.app.PermissionConst
import com.siiri.yc.app.event.EventBusTags
import com.siiri.yc.app.event.ReceiveMessageEvent
import com.siiri.yc.di.component.DaggerMainComponent
import com.siiri.yc.di.module.PermissionModule
import com.siiri.yc.di.module.UploadModule
import com.siiri.yc.di.module.WebViewModule
import com.siiri.yc.mvp.contract.PermissionContract
import com.siiri.yc.mvp.contract.UploadContract
import com.siiri.yc.mvp.contract.WebViewContract
import com.siiri.yc.mvp.model.entity.FileType
import com.siiri.yc.mvp.presenter.PermissionPresenter
import com.siiri.yc.mvp.presenter.UploadPresenter
import com.siiri.yc.mvp.presenter.WebViewPresenter
import com.siiri.yc.utils.CommonUtils
import com.siiri.yc.utils.UserUtils
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.internal.utils.PathUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class MainActivity : BaseActivity<WebViewPresenter>(), WebViewContract.View,
    PermissionContract.View, UploadContract.View {

    private var exitTime = 0L

    @Inject
    lateinit var mPermissionPresenter: PermissionPresenter

    @Inject
    lateinit var mUploadPresenter: UploadPresenter

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerMainComponent.builder()
            .appComponent(appComponent)
            .webViewModule(WebViewModule(this))
            .permissionModule(PermissionModule(this))
            .uploadModule(UploadModule(this))
            .build()
            .inject(this)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        validateIP()
        initWebView()
    }

    private fun initWebView() {
        loadFinished(false)
    }

    private fun validateIP() {
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                if (!CommonUtils.isIpValid(UserUtils.getWebViewUrl(), 5000)) {
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
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (mPresenter?.handleKeyEvent(keyCode, event) == true) {
            return true
        }
        if (KeyEvent.KEYCODE_BACK == keyCode && event!!.action == KeyEvent.ACTION_DOWN) {
            exit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun exit() {
        if (System.currentTimeMillis() - exitTime > DELAY_EXIT) {
            Toast.makeText(this, StringUtils.getString(R.string.exit_app), Toast.LENGTH_SHORT)
                .show()
            exitTime = System.currentTimeMillis()
        } else {
            AppUtils.exitApp()
        }
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun loadFinished(show: Boolean) {
        webviewContainer.toggle(show)
        loadingView.toggle(!show)
        if (show) {
            mPermissionPresenter.requestWritePermission()
            setAliasPush(true)
        }
    }

    override fun requestPermissionSuccess(code: Int) {
        when (code) {
            REQUEST_CODE_QR -> {
                startScan()
            }
            REQUEST_CODE_UPDATE -> {
                mPresenter?.checkUpdate()
            }
            REQUEST_CODE_CHOOSE_IMAGE -> {
                chooseImage()
            }
            REQUEST_CODE_CHOOSE_AUDIO -> {
                chooseAudio()
            }
            REQUEST_CODE_CHOOSE_VIDEO -> {
                chooseVideo()
            }
            REQUEST_CODE_CHOOSE_ALL -> {
                chooseLocalFile()
            }
        }
    }

    override fun chooseImage() {
        MatisseUtils.choosePic(this, REQUEST_CODE_CHOOSE_IMAGE)
    }

    override fun showChooseFile() {
        QMUIBottomSheet.BottomListSheetBuilder(this)
            .setTitle("选择文件")
            .addItem("图片", FileType.IMAGE.name)
            .addItem("录制语音", FileType.AUDIO.name)
            .addItem("录制视频", FileType.VIDEO.name)
//            .addItem("从文件选择", FileType.ALL.name)
            .setOnSheetItemClickListener { dialog, _, _, tag ->
                dialog.dismiss()
                when (tag) {
                    FileType.IMAGE.name -> {
                        mPermissionPresenter.requestPermission(
                            REQUEST_CODE_CHOOSE_IMAGE,
                            PermissionConst.WRITE_EXTERNAL_STORAGE_PERMISSION
                        )
                    }
                    FileType.AUDIO.name -> {
                        mPermissionPresenter.requestPermission(
                            REQUEST_CODE_CHOOSE_AUDIO,
                            PermissionConst.AUDIO_PERMISSION
                        )
                    }
                    FileType.VIDEO.name -> {
                        mPermissionPresenter.requestPermission(
                            REQUEST_CODE_CHOOSE_VIDEO,
                            PermissionConst.CAMERA_PERMISSION,
                            PermissionConst.WRITE_EXTERNAL_STORAGE_PERMISSION
                        )
                    }
                    FileType.ALL.name -> {
                        mPermissionPresenter.requestPermission(
                            REQUEST_CODE_CHOOSE_ALL,
                            PermissionConst.WRITE_EXTERNAL_STORAGE_PERMISSION
                        )
                    }
                }
            }
            .build()
            .show()
    }

    private fun chooseLocalFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "video/*;audio/*;image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_ALL)
    }

    private fun chooseVideo() {
        startActivityForResult(
            Intent(this, VideoRecordActivity::class.java),
            REQUEST_CODE_CHOOSE_VIDEO
        )
    }

    private fun chooseAudio() {
        AndroidAudioRecorder.with(this)
            .setAutoStart(true)
            .setKeepDisplayOn(true)
            .setRequestCode(REQUEST_CODE_CHOOSE_AUDIO)
            .record()
    }

    override fun uploadSuccess(fileName: String) {
        mPresenter?.uploadSuccess(fileName)
    }

    private var loading: QMUITipDialog? = null

    private var dialogVisible = false

    override fun showUploadDialog() {
        if (dialogVisible) return
        loading = QMUITipDialog.Builder(this)
            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
            .setTipWord(getString(R.string.uploading))
            .create(false)
        dialogVisible = true
        loading?.show()
    }

    override fun hideUploadDialog() {
        if (dialogVisible) {
            dialogVisible = false
            loading?.dismiss()
        }
    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
    }

    /**
     * 推送别名的设置和删除
     *
     * @param action true: 设置别名，false: 删除别名
     */
    override fun setAliasPush(action: Boolean) {
        val personId = UserUtils.getUserId() ?: -1
        if (personId != -1)
            if (action)
                JPushUtils.setAliasPush(this, personId)
            else
                JPushUtils.delAliasPush(this, personId)
    }

    override fun requestCameraPermission() {
        mPermissionPresenter.requestCameraPermission(REQUEST_CODE_QR)
    }

    /**
     *
     * 接收到推送的通知，通知vue页面获取未处理的消息数据，刷新页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public fun onEvent(event: ReceiveMessageEvent) {
        if (event.event == EventBusTags.RECEIVER_MSG) {
            mPresenter?.receiveMsg()
        }
    }

    /**
     * 请求相机权限，成功后扫描二维码
     */
    override fun startScan() {
        startActivityForResult(
            Intent(this, CaptureActivity::class.java),
            REQUEST_CODE_QR
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null != data && resultCode == -1) {
            when (requestCode) {
                REQUEST_CODE_QR -> {
                    val result = data.getStringExtra(Intents.Scan.RESULT)
                    if (result?.isNotEmpty() == true)
                        mPresenter?.scanResult(result)
                }
                REQUEST_CODE_CHOOSE_IMAGE -> {
                    val paths = Matisse.obtainPathResult(data) ?: mutableListOf()
                    if (paths.isNotEmpty()) {
                        mUploadPresenter.uploadFiles(paths[0], false)
                    }
                }
                REQUEST_CODE_CHOOSE_AUDIO -> {
                    val path =
                        data.getStringExtra(AudioRecordActivity.AUDIO_RECORD_RESULT_PATH) ?: ""
                    mUploadPresenter.uploadFiles(path)
                }
                REQUEST_CODE_CHOOSE_VIDEO -> {
                    val path =
                        data.getStringExtra(VideoRecordActivity.VIDEO_RECORD_RESULT_PATH) ?: ""
                    mUploadPresenter.uploadFiles(path)
                }
                REQUEST_CODE_CHOOSE_ALL -> {
                    val path = PathUtils.getPath(this, data.data) ?: ""
                    mUploadPresenter.uploadFiles(path)
                }
            }
        }
    }

    companion object {
        private const val DELAY_EXIT = 2000
        const val REQUEST_CODE_QR = 0x02
        const val REQUEST_CODE_UPDATE = 0x03
        const val REQUEST_CODE_CHOOSE_IMAGE = 0x04
        const val REQUEST_CODE_CHOOSE_VIDEO = 0x05
        const val REQUEST_CODE_CHOOSE_AUDIO = 0x06
        const val REQUEST_CODE_CHOOSE_ALL = 0x07
    }

}