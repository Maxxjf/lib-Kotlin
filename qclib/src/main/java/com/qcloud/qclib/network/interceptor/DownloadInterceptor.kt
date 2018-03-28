package com.qcloud.qclib.network.interceptor

import android.os.Looper
import android.util.Log
import com.qcloud.qclib.callback.DownloadCallback
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
class DownloadInterceptor(private val callback: DownloadCallback?): Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val builder: Response.Builder = response.newBuilder()
                .body(ProgressResponseBody(response.body()!!, object : ProgressListener {

                    override fun onProgress(progress: Long, total: Long, done: Boolean) {
                        Log.e("DOWNLOAD", Looper.myLooper().toString())
                        Log.e("DOWNLOAD", "onProgress: " + "total ---->" + total + "done ---->" + progress );
                    }
                }))

        return builder.build()
    }
}