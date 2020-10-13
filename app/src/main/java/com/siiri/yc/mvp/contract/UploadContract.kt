package com.siiri.yc.mvp.contract

import android.app.Activity
import android.content.Context
import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView
import com.siiri.yc.mvp.model.entity.BaseResponse
import com.siiri.yc.mvp.model.entity.UploadResponseEntity
import io.reactivex.Observable
import java.io.File

/**
 * @author: dinglei
 * @date: 2020/9/29 14:56
 */
interface UploadContract {
    interface View : IView {
        fun uploadSuccess(fileName: String)
        fun showUploadDialog()
        fun hideUploadDialog()
        fun getActivity(): Activity
    }

    interface Model : IModel {

        fun upload(
            buffer: ByteArray,
            fileName: String,
            md5File: String,
            chunk: Int
        ): Observable<BaseResponse<String>>

        fun check(fileName: String, md5File: String): Observable<BaseResponse<String>>

        fun merge(
            fileName: String,
            md5File: String,
            chunks: Int
        ): Observable<BaseResponse<UploadResponseEntity>>

        fun chunk(md5File: String, chunk: Int): Observable<BaseResponse<Boolean>>
    }

}