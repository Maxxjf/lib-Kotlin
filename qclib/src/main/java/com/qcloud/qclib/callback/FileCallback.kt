package com.qcloud.qclib.callback

import android.os.Environment
import com.qcloud.qclib.beans.ProgressBean
import com.qcloud.qclib.network.LoadBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream

/**
 * 类说明：文件下载/上传回调
 * Author: Kuzan
 * Date: 2018/4/8 14:13.
 */
abstract class FileCallback(
        private val destFileName: String): Callback<ResponseBody> {

    init {
        subscribeLoadProgress()
    }

    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
        try {
            saveFile(response)
        } catch (e: Exception) {
            onError(e.message ?: "保存文件出错")
        }
    }

    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
        onError(t?.message ?: "下载出错了")
    }

    /**
     * 订阅文件下载进度
     */
    private fun subscribeLoadProgress() {
        LoadBus.instance.register(ProgressBean::class.java)
                ?.doOnSubscribe { onAccept("开始下载") }
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    onLoading(it)
                }, {
                    onError(it.message ?: "下载出错")
                }, {
                    onComplete("下载完成")
                })
    }

    /**
     * 保存文件
     * */
    @Throws(Exception::class)
    private fun saveFile(response: Response<ResponseBody>?) {
        val iStream = response!!.body()!!.byteStream()
        val file = File(Environment.getExternalStorageDirectory(), destFileName)
        val fos = FileOutputStream(file)
        val bis = BufferedInputStream(iStream)
        try {
            val buffer = ByteArray(1024)
            var len: Int = bis.read(buffer)
            while (len != -1) {
                fos.write(buffer, 0, len)
                fos.flush()
                len = bis.read(buffer)
            }
            onSuccess(file)
            LoadBus.instance.unregisterAll()
        } finally {
            fos.close()
            bis.close()
            iStream.close()
        }
    }

    /** 开始下载 */
    abstract fun onAccept(acceptStr: String)

    /** 下载过程回调 */
    abstract fun onLoading(progress: ProgressBean)

    /** 下载出错 */
    abstract fun onError(errorMsg: String)

    /** 下载完成 */
    abstract fun onComplete(message: String)

    /** 成功后回调 */
    abstract fun onSuccess(file: File)
}