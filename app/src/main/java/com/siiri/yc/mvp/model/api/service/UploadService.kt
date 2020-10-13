package com.siiri.yc.mvp.model.api.service

import com.siiri.yc.mvp.model.entity.BaseResponse
import com.siiri.yc.mvp.model.entity.UploadResponseEntity
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * @author: dinglei
 * @date: 2020/9/29 15:02
 */
interface UploadService {

    @POST("/api/file/check")
    fun check(@Query("md5File") md5File: String, @Body body: RequestBody)
            : Observable<BaseResponse<String>>

    @POST("/api/file/chunk")
    fun chunk(@Query("md5File") md5File: String, @Query("chunk") chunk: Int)
            : Observable<BaseResponse<Boolean>>

    @POST("/api/file/upload")
    @Multipart
    fun upload(
        @Query("md5File") md5File: String,
        @Query("chunk") chunk: Int,
        @Part part: MultipartBody.Part
    )
            : Observable<BaseResponse<String>>

    @POST("/api/file/merge")
    fun merge(
        @Query("md5File") md5File: String,
        @Query("chunks") chunks: Int,
        @Body body: RequestBody
    )
            : Observable<BaseResponse<UploadResponseEntity>>

}