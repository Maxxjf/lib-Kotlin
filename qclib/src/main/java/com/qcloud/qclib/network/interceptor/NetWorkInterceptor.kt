package com.qcloud.qclib.network.interceptor

import com.qcloud.qclib.FrameConfig
import com.qcloud.qclib.utils.NetUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 类说明：网络缓存
 * Author: Kuzan
 * Date: 2018/3/27 18:28.
 */
class NetWorkInterceptor: Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (FrameConfig.isCache) {
            val maxStale = 60 * 60 * 24 * 28 // 无网络情况下，缓存时间为4周
            response.newBuilder().removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale").build()
        } else {
            if (NetUtil.isConnected(FrameConfig.appContext)) {
                // 有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                val cacheControl = request.cacheControl().toString()
                response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", cacheControl)
                        .build()
            } else {
                val maxStale = 60 * 60 * 24 * 28 // 无网络情况下，缓存时间为4周
                response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale").build()
            }
        }
        return response
    }

}