package com.qcloud.qclib.network

import android.os.Environment
import android.support.annotation.NonNull
import android.util.Log
import com.google.gson.JsonParseException
import com.qcloud.qclib.beans.BaseResponse
import com.qcloud.qclib.beans.RxBusEvent
import com.qcloud.qclib.callback.DataCallback
import com.qcloud.qclib.callback.DownloadCallback
import com.qcloud.qclib.enums.RequestStatusEnum
import com.qcloud.qclib.rxbus.BusProvider
import com.qcloud.qclib.utils.TokenUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * 类说明：网络请求返回数据处理
 * Author: Kuzan
 * Date: 2018/1/16 13:39.
 */
object BaseApi {
    /**
     * 网络请求数据回来后，对数据进行进一步的解析，获取我们真正需要的数据。
     *
     * @time 2018/1/16 13:40
     */
    fun <T> dispose(@NonNull observable: Observable<BaseResponse<T>>, callback: DataCallback<T>?) {
        observable.map { tBaseResponse: BaseResponse<T> ->
            Log.e("BaseApi", "status = ${tBaseResponse.status}")
            Log.e("BaseApi", "message = ${tBaseResponse.message}")
            tBaseResponse
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<BaseResponse<T>> {
                    override fun onNext(tBaseResponse: BaseResponse<T>) {
                        Log.e("BaseApi", "onNext, BaseResponse = $tBaseResponse")
                        when (tBaseResponse.status) {
                            RequestStatusEnum.SUCCESS.status -> callback?.onSuccess(tBaseResponse.data, tBaseResponse.message)
                            RequestStatusEnum.NO_ROOT.status -> {
                                // 清空登录token
                                TokenUtil.clearToken()
                                // 抛登录异常
                                callback?.onError(RequestStatusEnum.NO_ROOT.status, "登录过时，请重新登录")
                                // 发送未登录的全局通知
                                BusProvider.instance.post(RxBusEvent.newBuilder(RequestStatusEnum.NO_ROOT.status).build())
                            }
                            RequestStatusEnum.ERROR.status, RequestStatusEnum.INVALID.status -> {
                                callback?.onError(tBaseResponse.status, tBaseResponse.message ?: "服务器请求出错!")
                            }
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.e("BaseApi", "onSubscribe")
                    }

                    override fun onError(e: Throwable) {
                        Log.e("BaseApi", "onError = ${e.message}")
                        Log.e("BaseApi", "onError = ${e.javaClass.name}")
                        e.printStackTrace()

                        when (e) {
                            is IOException, is HttpException -> callback?.onError(RequestStatusEnum.ERROR.status, "网络请求失败")
                            is JsonParseException -> callback?.onError(RequestStatusEnum.INVALID.status, "数据解析出错")
                            else -> callback?.onError(RequestStatusEnum.INVALID.status, e.message ?: "未知错误")
                        }
                    }

                    override fun onComplete() {
                        Log.e("BaseApi", "onComplete")
                    }
                })

    }

    /**
     * 下载请求
     *
     * @param call 文件下载请求返回
     * @param fileName 文件名称 jiuhua.apk
     * @param callback
     * */
    fun disposeDownload(call: Call<ResponseBody>, fileName: String, callback: DownloadCallback?) {
        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                try {
                    val iStream = response.body()!!.byteStream()
                    val file = File(Environment.getExternalStorageDirectory(), fileName)
                    val fos = FileOutputStream(file)
                    val bis = BufferedInputStream(iStream)
                    val buffer = ByteArray(1024)
                    var len: Int = bis.read(buffer)
                    while (len != -1) {
                        fos.write(buffer, 0, len)
                        fos.flush()
                        len = bis.read(buffer)
                    }
                    fos.close()
                    bis.close()
                    iStream.close()
                    callback?.onSuccess(file)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Log.e("DOWNLOAD", "onFailure: " + t?.message)
            }
        })
    }
}