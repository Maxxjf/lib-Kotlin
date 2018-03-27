package com.qcloud.qclib.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


/**
 * 类说明：请求地址拦截器，打印网络请求的实际url
 * Author: Kuzan
 * Date: 2018/3/27 18:20.
 */
class LoggingInterceptor: Interceptor {
    private val TAG = "logger"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val response = chain.proceed(request)
        Log.e(TAG, "url：" + request.url())

        return response
    }
}