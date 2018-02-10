package com.qcloud.qclib.network

import android.support.annotation.NonNull
import android.util.Log
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.*
import com.qcloud.qclib.FrameConfig
import com.qcloud.qclib.beans.UploadFileBean
import com.qcloud.qclib.callback.DownloadCallback
import com.qcloud.qclib.callback.UploadCallback
import com.qcloud.qclib.callback.UploadFileCallback
import com.qcloud.qclib.utils.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.io.File
import java.lang.reflect.Type

/**
 * 类说明：网络请求封装
 * Author: Kuzan
 * Date: 2018/1/16 10:44.
 */
class OkGoRequest private constructor() {

    init {
        baseUrl = BaseUrlUtil.getBaseUrl()
        baseType = object : TypeToken<JSONObject>() {

        }.type
    }

    /*********** GET请求 *************/
    fun <T> getRequest(@NonNull url: String, tag: Any? = null): Observable<T> {
        return getRequest(url, OkGoRequest.Companion.baseType, null, null, null, tag)
    }

    fun <T> getRequest(@NonNull url: String, @NonNull type: Type, tag: Any? = null): Observable<T> {
        return getRequest(url, type, null, null, null, tag)
    }

    fun <T> getRequest(@NonNull url: String, @NonNull type: Type, @NonNull params: HttpParams, tag: Any? = null): Observable<T> {
        return getRequest(url, type, null, params, null, tag)
    }

    fun <T> getRequest(@NonNull url: String, @NonNull type: Type, @NonNull headers: HttpHeaders, tag: Any? = null): Observable<T> {
        return getRequest(url, type, null, null, headers, tag)
    }

    fun <T> getRequest(@NonNull url: String, @NonNull type: Type, @NonNull params: HttpParams, @NonNull headers: HttpHeaders, tag: Any? = null): Observable<T> {
        return getRequest(url, type, null, params, headers, tag)
    }

    fun <T> getRequest(@NonNull url: String, @NonNull clazz: Class<T>, tag: Any? = null): Observable<T> {
        return getRequest(url, null, clazz, null, null, tag)
    }

    fun <T> getRequest(@NonNull url: String, @NonNull clazz: Class<T>, @NonNull params: HttpParams, tag: Any? = null): Observable<T> {
        return getRequest(url, null, clazz, params, null, tag)
    }

    fun <T> getRequest(@NonNull url: String, @NonNull clazz: Class<T>, @NonNull headers: HttpHeaders, tag: Any? = null): Observable<T> {
        return getRequest(url, null, clazz, null, headers, tag)
    }

    fun <T> getRequest(@NonNull url: String, @NonNull clazz: Class<T>, @NonNull params: HttpParams, @NonNull headers: HttpHeaders, tag: Any? = null): Observable<T> {
        return getRequest(url, null, clazz, params, headers, tag)
    }

    /**
     * GET请求
     *
     * @param url 请求链接
     * @param type 返回对象类型，可不传，默认为空
     * @param clazz 返回对象类型，可不传，默认为空
     * @param params 请求参数，可不传，默认为空
     * @param headers 请求头，可不传，默认为空
     * @param tag 请求标签，可不传，默认为空
     * */
    fun <T> getRequest(@NonNull url: String, type: Type? = null, clazz: Class<T>? = null, params: HttpParams? = null, headers: HttpHeaders? = null, tag: Any? = null): Observable<T> {
        return RxRequest.request(HttpMethod.GET, baseUrl+url, type, clazz, params, headers, tag)
    }

    /*********** POST请求 *************/
    fun <T> postRequest(@NonNull url: String, tag: Any? = null): Observable<T> {
        return postRequest(url, OkGoRequest.Companion.baseType, null, null, null, tag)
    }

    fun <T> postRequest(@NonNull url: String, @NonNull type: Type, tag: Any? = null): Observable<T> {
        return postRequest(url, type, null, null, null, tag)
    }

    fun <T> postRequest(@NonNull url: String, @NonNull type: Type, @NonNull params: HttpParams, tag: Any? = null): Observable<T> {
        return postRequest(url, type, null, params, null, tag)
    }

    fun <T> postRequest(@NonNull url: String, @NonNull type: Type, @NonNull headers: HttpHeaders, tag: Any? = null): Observable<T> {
        return postRequest(url, type, null, null, headers, tag)
    }

    fun <T> postRequest(@NonNull url: String, @NonNull type: Type, @NonNull params: HttpParams, @NonNull headers: HttpHeaders, tag: Any? = null): Observable<T> {
        return postRequest(url, type, null, params, headers, tag)
    }

    fun <T> postRequest(@NonNull url: String, @NonNull clazz: Class<T>, tag: Any? = null): Observable<T> {
        return postRequest(url, null, clazz, null, null, tag)
    }

    fun <T> postRequest(@NonNull url: String, @NonNull clazz: Class<T>, @NonNull params: HttpParams, tag: Any? = null): Observable<T> {
        return postRequest(url, null, clazz, params, null, tag)
    }

    fun <T> postRequest(@NonNull url: String, @NonNull clazz: Class<T>, @NonNull headers: HttpHeaders, tag: Any? = null): Observable<T> {
        return postRequest(url, null, clazz, null, headers, tag)
    }

    fun <T> postRequest(@NonNull url: String, @NonNull clazz: Class<T>, @NonNull params: HttpParams, @NonNull headers: HttpHeaders, tag: Any? = null): Observable<T> {
        return postRequest(url, null, clazz, params, headers, tag)
    }

    /**
     * POST请求
     *
     * @param url 请求链接
     * @param type 返回对象类型，可不传，默认为空
     * @param clazz 返回对象类型，可不传，默认为空
     * @param params 请求参数，可不传，默认为空
     * @param headers 请求头，可不传，默认为空
     * @param tag 请求标签，可不传，默认为空
     * */
    fun <T> postRequest(@NonNull url: String, type: Type? = null, clazz: Class<T>? = null, params: HttpParams? = null, headers: HttpHeaders? = null, tag: Any? = null): Observable<T> {
        return RxRequest.request(HttpMethod.POST, baseUrl+url, type, clazz, params, headers, tag)
    }

    /**
     * 文件下载数据请求
     *
     * @param url 请求地址
     * @param params 请求参数
     * @param tag 请求标签，方便区分是哪个请求在运行，以便取消请求
     * @param callback 回调
     * */
    fun downloadRequest(@NonNull url: String, params: HttpParams?, tag: Any?, callback: DownloadCallback?) {
        Observable.create<Progress> { e ->
            OkGo.get<File>(baseUrl + url)
                    .params(params)
                    .tag(tag)
                    .execute(object: FileCallback(){
                        override fun onSuccess(response: Response<File>) {
                            callback?.onSuccess(response.body())
                        }

                        override fun onError(response: Response<File>) {
                            e.onError(response.exception)
                        }

                        override fun downloadProgress(progress: Progress) {
                            e.onNext(progress)
                        }
                    })
        }.doOnSubscribe {
            callback?.onAccept("正在下载中...");
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<Progress> {
                    override fun onSubscribe(d: Disposable) {
                        // 添加到订阅管理，在activity的onDestroy里添加CompositeDisposableUtil.disposable();
                        CompositeDisposableUtil().addDisposable(d)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        callback?.onError("下载出错");
                    }

                    override fun onComplete() {
                        callback?.onComplete("下载完成");
                    }

                    override fun onNext(progress: Progress) {
                        Log.d("download", "progress = $progress")
                        // 文件下载了多少
                        //val downloadLength = Formatter.formatFileSize(context, progress.currentSize)
                        // 文件大小
                        //val totalLength = Formatter.formatFileSize(context, progress.totalSize)
                        // 下载速度100 kb/s
                        //val speed = Formatter.formatFileSize(context, progress.speed)
                        // 已下载的占总大小的百分比
                        //val percent = NumberFormat.getPercentInstance().format(progress.fraction.toDouble())
                        // 已下载的文件大小
                        //val hasProgress = (progress.fraction * 1000).toInt()

                        callback?.onProgress(progress)
                    }

                })
    }

    /**
     * 文件上传
     *
     * @param url 请求地址
     * @param params 请求参数
     * @param files 文件列表
     * @param tag 请求标签，方便区分是哪个请求在运行，以便取消请求
     * @param callback 回调
     * */
    fun uploadRequest(@NonNull url: String, params: HttpParams?, files: List<File>, tag: Any?, callback: UploadCallback?) {
        Observable.create<Progress> {e ->
            OkGo.post<UploadFileBean>(baseUrl + url)
                    .tag(tag)
                    .params(params)
                    .addFileParams("file", files)
                    .execute(object: UploadFileCallback() {
                        override fun onSuccess(response: Response<UploadFileBean>) {
                            callback?.onSuccess(response.body())
                        }

                        override fun onError(response: Response<UploadFileBean>) {
                            e.onError(response.exception)
                        }

                        override fun uploadProgress(progress: Progress) {
                            e.onNext(progress)
                        }
                    })
        }.doOnSubscribe { callback?.onAccept("正在上传中..."); }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<Progress> {

                    override fun onSubscribe(d: Disposable) {
                        // 添加到订阅管理，在activity的onDestroy里添加CompositeDisposableUtil.disposable();
                        CompositeDisposableUtil().addDisposable(d)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        callback?.onError("上传出错");
                    }

                    override fun onComplete() {
                        callback?.onComplete("上传完成");
                    }

                    override fun onNext(progress: Progress) {
                        Log.d("upload", "progress = $progress")
                        callback?.onProgress(progress)
                    }

                })
    }

    /**
     * 静态内部类
     * 一个ClassLoader下同一个类只会加载一次，保证了并发时不会得到不同的对象
     */
    object RequestHolder {
        var instance: OkGoRequest = OkGoRequest()
    }

    companion object {
        private val CLIENT_TYPE_KEY = "qc_client_type"
        private val CLIENT_TYPE = "android"
        private val FORMAT = "format"
        private val APP_STR_KEY = "qc_app_str"
        private val APP_SIGN_KEY = "qc_app_sign"
        private val APP_TOKEN_KEY = "qc_app_token"

        private val qc_app_str = QCloudAppSignUtil.encryptCharStr()
        private val qc_app_sign = QCloudAppSignUtil.signParamStr(qc_app_str, FrameConfig.appSign)

        private var qc_app_token: String? = null
        private var baseUrl: String? = null
        private var baseType: Type? = null

        val instance: OkGoRequest
            get() = RequestHolder.instance

        /**
         * 普通的请求数据
         */
        fun getDefaultParams(): HttpParams {
            val params = HttpParams()
            params.put(FORMAT, true)
            params.put(CLIENT_TYPE_KEY, CLIENT_TYPE)
            params.put(APP_STR_KEY, qc_app_str)
            params.put(APP_SIGN_KEY, qc_app_sign)

            return params
        }

        /**
         * 请求基础参数
         */
        fun getAppParams(): HttpParams {
            val params = getDefaultParams()
            if (StringUtil.isBlank(qc_app_token)) {
                qc_app_token = TokenUtil.getToken()
            }
            params.put(APP_TOKEN_KEY, qc_app_token)
            return params
        }

        /**
         * 保存token到请求参数里去
         * */
        fun setToken(token: String?) {
            OkGoRequest.Companion.qc_app_token = token
            Log.e("TOKEN", "token = $qc_app_token")
        }
    }
}