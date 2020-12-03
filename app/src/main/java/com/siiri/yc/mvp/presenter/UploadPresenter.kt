package com.siiri.yc.mvp.presenter

import com.blankj.utilcode.util.FileUtils
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.mvp.BasePresenter
import com.siiri.yc.app.FileExitsException
import com.siiri.yc.app.applySchedulers
import com.siiri.yc.mvp.contract.UploadContract
import com.siiri.yc.mvp.model.entity.BaseResponse
import com.siiri.yc.mvp.model.entity.UploadResponseEntity
import com.siiri.yc.utils.CommonUtils
import com.siiri.yc.utils.MD5Util
import io.reactivex.Observable
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.math.ceil

@ActivityScope
class UploadPresenter @Inject constructor(
    model: UploadContract.Model,
    view: UploadContract.View
) :
    BasePresenter<UploadContract.Model, UploadContract.View>(model, view) {

    @Inject
    lateinit var mErrorHandler: RxErrorHandler

    fun uploadFiles(path: String, isDelete: Boolean = true) {
        val file = File(path)
        if (!FileUtils.isFileExists(file)) {
            mRootView.showMessage("文件不存在，请重新选择")
            return
        }
        mRootView.showUploadDialog(null)
        val md5File = MD5Util.fileToMD5(file)
        val fileName = file.name
        val type =
            fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(Locale.getDefault())
        val chunkLength = 2 * 1024 * 1024
        var totalChunk = ceil(file.length() / chunkLength.toFloat()).toInt()
        var uploadChunk = 0


        applySchedulers<BaseResponse<*>>(mRootView)
            .apply(
                mModel.check(fileName, md5File)
                    .flatMap { check ->
                        when (check.code) {
                            400 -> throw FileExitsException("${md5File}.${type}")
                            200 -> {
                                var currentChunk = 0
                                val fileList = mutableListOf<Pair<ByteArray, Int>>()
                                if (file.length() < chunkLength) {
                                    totalChunk = 1
                                    fileList.add(Pair(file.readBytes(), 0))
                                } else
                                    file.forEachBlock(chunkLength) { buffer, byteSize ->
                                        fileList.add(
                                            Pair(
                                                buffer.sliceArray(0 until byteSize),
                                                currentChunk
                                            )
                                        )
                                        currentChunk++
                                    }
                                Observable.just(fileList)
                            }
                            else -> throw RuntimeException(check.message)
                        }
                    }
                    .flatMap {
                        Observable.concat(it.map { data ->
                            mModel.chunk(md5File, data.second)
                                .flatMap { chunk ->
                                    when (chunk.code) {
                                        200 -> {
                                            mModel.upload(
                                                data.first,
                                                fileName,
                                                md5File,
                                                data.second
                                            )
                                        }
                                        else -> {
                                            throw RuntimeException(chunk.message)
                                        }
                                    }
                                }
                        })
                    }
                    .flatMap {
                        uploadChunk++
                        if (uploadChunk == totalChunk && it.code == 200) {
                            mModel.merge(fileName, md5File, totalChunk)
                                .flatMap { merge ->
                                    when (merge.code) {
                                        200 -> {
                                            merge.isSuccess = true
                                            Observable.just(merge)
                                        }
                                        else -> {
                                            throw RuntimeException(merge.message)
                                        }
                                    }
                                }
                        } else {
                            Observable.just(it)
                        }
                    }
            )
            .subscribe(object : ErrorHandleSubscriber<BaseResponse<*>>(mErrorHandler) {
                override fun onNext(t: BaseResponse<*>) {
                    if (t.isSuccess && t.data is UploadResponseEntity) {
                        uploadComplete(file, isDelete)
                        mRootView.uploadSuccess((t.data as UploadResponseEntity).fileName)
                    }
                }

                override fun onError(t: Throwable) {
                    super.onError(t)
                    uploadComplete(file, isDelete)
                    if (t is FileExitsException && t.message != null) {
                        mRootView.uploadSuccess(t.message!!)
                    } else {
                        mRootView.showMessage(t.message ?: "文件上传失败")
                    }
                }

            })
    }

    fun uploadComplete(file: File, delete: Boolean) {
        mRootView.hideUploadDialog()
        if (delete) {
            FileUtils.delete(file)
            CommonUtils.updateFileLibrary(mRootView.getActivity(), file)
        }
    }

}
