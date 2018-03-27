package com.qcloud.qclib.network.interceptor

import com.qcloud.qclib.FrameConfig
import com.qcloud.qclib.utils.NetUtil
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 类说明：缓存策略拦截器
 * Author: Kuzan
 * Date: 2018/3/27 18:14.
 */
class CacheInterceptor: Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (FrameConfig.isCache) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
        }
        if (!NetUtil.isConnected(FrameConfig.appContext)) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
        }
        return chain.proceed(request)
    }
}