package com.siiri.yc.mvp.model

import com.blankj.utilcode.util.GsonUtils
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.siiri.yc.mvp.contract.UploadContract
import com.siiri.yc.mvp.model.api.service.UploadService
import com.siiri.yc.mvp.model.entity.BaseResponse
import com.siiri.yc.mvp.model.entity.UploadRequestBody
import com.siiri.yc.mvp.model.entity.UploadResponseEntity
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

/**
 * @author: dinglei
 * @date: 2020/9/29 14:57
 */
@ActivityScope
class UploadModel @Inject constructor(repositoryManager: IRepositoryManager) :
    BaseModel(repositoryManager), UploadContract.Model {

    private val mUploadService by lazy { repositoryManager.obtainRetrofitService(UploadService::class.java) }
    override fun upload(
        buffer: ByteArray,
        fileName: String,
        md5File: String,
        chunk: Int
    ): Observable<BaseResponse<String>> {
        return mUploadService.upload(
            md5File, chunk, MultipartBody.Part.createFormData(
                "file",
                fileName,
                RequestBody.create(MediaType.parse("*/*"), buffer)
            )
        )
    }

    override fun check(
        fileName: String,
        md5File: String
    ): Observable<BaseResponse<String>> {
        return mUploadService.check(
            md5File,
            RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                GsonUtils.toJson(UploadRequestBody(fileName = fileName))
            )
        )
    }

    override fun merge(
        fileName: String,
        md5File: String,
        chunks: Int
    ): Observable<BaseResponse<UploadResponseEntity>> {
        return mUploadService.merge(
            md5File, chunks, RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                GsonUtils.toJson(UploadRequestBody(fileName = fileName))
            )
        )
    }

    override fun chunk(md5File: String, chunk: Int): Observable<BaseResponse<Boolean>> {
        return mUploadService.chunk(md5File, chunk)
    }


}