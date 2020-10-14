package com.siiri.yc.config

import android.content.Context
import com.google.gson.GsonBuilder
import com.jess.arms.di.module.GlobalConfigModule
import com.siiri.lib_core.app.GlobalConfiguration
import com.siiri.lib_core.app.GlobalHttpHandlerImpl
import com.siiri.lib_core.app.ResponseErrorListenerImpl
import com.siiri.yc.mvp.model.api.Api
import io.rx_cache2.internal.RxCache
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * @author: dinglei
 * @date: 2020/9/29 11:05
 */
class MyGlobalModule : GlobalConfiguration() {
    override fun applyOptions(context: Context, builder: GlobalConfigModule.Builder) {
        builder.baseurl(Api.BASE_URL)
            .globalHttpHandler(GlobalHttpHandlerImpl(context))
            .responseErrorListener(ResponseErrorListenerImpl())
            .gsonConfiguration { _: Context?, gsonBuilder: GsonBuilder ->
                gsonBuilder
                    .serializeNulls()
                    .enableComplexMapKeySerialization()
            }
            .retrofitConfiguration { _: Context?, _: Retrofit.Builder? -> }
            .okhttpConfiguration { _: Context?, okhttpBuilder: OkHttpClient.Builder ->
                okhttpBuilder.writeTimeout(10, TimeUnit.SECONDS)
                RetrofitUrlManager.getInstance().with(okhttpBuilder)
            }
            .rxCacheConfiguration { _: Context?, rxCacheBuilder: RxCache.Builder ->
                rxCacheBuilder.useExpiredDataIfLoaderNotAvailable(true)
                null
            }
    }
}