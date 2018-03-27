package com.qcloud.qclib.network.interceptor

import com.qcloud.qclib.callback.ProgressListener
import com.qcloud.qclib.network.ProgressResponseBody
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


/**
 * 类说明：文件下载
 * Author: Kuzan
 * Date: 2018/3/27 20:43.
 */
class DownloadInterceptor: Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val orginalResponse = chain.proceed(chain.request())

        return orginalResponse.newBuilder()
                .body(ProgressResponseBody(orginalResponse.body()!!, object : ProgressListener {

                    override fun onProgress(progress: Long, total: Long, done: Boolean) {

                    }
                }))
                .build()
    }
}