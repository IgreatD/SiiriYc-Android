package com.siiri.yc.mvp.presenter

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.blankj.utilcode.util.AppUtils
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.utils.PermissionUtil
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import com.siiri.yc.app.PermissionConst
import com.siiri.yc.mvp.contract.PermissionContract
import com.siiri.yc.ui.activity.MainActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

/**
 * @author: dinglei
 * @date: 2020/9/29 10:46
 */
@ActivityScope
class PermissionPresenter @Inject constructor(
    view: PermissionContract.View
) : BasePresenter<Nothing, PermissionContract.View>(view) {

    @Inject
    lateinit var mErrorHandler: RxErrorHandler

    @Inject
    lateinit var mRxPermissions: RxPermissions

    fun requestWritePermission() {
        requestPermission(
            MainActivity.REQUEST_CODE_UPDATE,
            PermissionConst.WRITE_EXTERNAL_STORAGE_PERMISSION
        )
    }

    fun requestCameraPermission(code: Int) {
        requestPermission(code, PermissionConst.CAMERA_PERMISSION)
    }

    fun requestWriteAndCameraPermission(code: Int) {
        requestPermission(
            code,
            PermissionConst.WRITE_EXTERNAL_STORAGE_PERMISSION,
            PermissionConst.CAMERA_PERMISSION
        )
    }

    fun requestPermission(code: Int, vararg permissionsArg: String) {
        PermissionUtil.requestPermission(
            object : PermissionUtil.RequestPermission {
                override fun onRequestPermissionFailureWithAskNeverAgain(permissions: MutableList<String>?) {
                    tipPermissions()
                }

                override fun onRequestPermissionSuccess() {
                    mRootView.requestPermissionSuccess(code)
                }

                override fun onRequestPermissionFailure(permissions: MutableList<String>?) {
                    if (permissions?.isNotEmpty() == true)
                        requestPermission(code, *(permissions.toTypedArray()))
                }
            },
            mRxPermissions,
            mErrorHandler,
            *permissionsArg
        )
    }

    private fun tipPermissions() {
        QMUIDialog.MessageDialogBuilder(mRootView.getActivity())
            .setCanceledOnTouchOutside(false)
            .setCancelable(false)
            .setTitle("权限申请")
            .setMessage("为了给您提供更好的服务，${AppUtils.getAppName()} 需要申请权限")
            .setSkinManager(QMUISkinManager.defaultInstance(mRootView.getActivity()))
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

}