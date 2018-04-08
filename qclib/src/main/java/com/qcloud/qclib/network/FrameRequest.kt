package com.qcloud.qclib.network

import android.content.Context
import android.os.Environment
import android.util.Log
import com.qcloud.qclib.FrameConfig
import com.qcloud.qclib.FrameConfig.Companion.cachePath
import com.qcloud.qclib.network.interceptor.CacheInterceptor
import com.qcloud.qclib.network.interceptor.DownloadInterceptor
import com.qcloud.qclib.network.interceptor.LoggingInterceptor
import com.qcloud.qclib.network.interceptor.NetWorkInterceptor
import com.qcloud.qclib.utils.BaseUrlUtil
import com.qcloud.qclib.utils.QCloudAppSignUtil
import com.qcloud.qclib.utils.StringUtil
import com.qcloud.qclib.utils.TokenUtil
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * 类说明：网络请求
 * Author: Kuzan
 * Date: 2018/3/27 18:32.
 */
class FrameRequest private constructor() {

    private var httpCacheDirectory: File? = null
    // okhttp网络请求
    private val httpClient: OkHttpClient

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        // 缓存路径
        if (FrameConfig.appContext != null) {
            httpCacheDirectory = File(getDiskCacheDir(FrameConfig.appContext!!), cachePath)
        }
        Log.e("Cache", "缓存路径：$httpCacheDirectory")
        val cache: Cache? = if (httpCacheDirectory != null) {
            Cache(httpCacheDirectory!!, FrameConfig.cacheSize.toLong())
        } else {
            null
        }

        httpClient = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(LoggingInterceptor())           // 自定义日志打印拦截器，打印请求地址
                .addInterceptor(CacheInterceptor())             // 自定义缓存拦截器
                .addNetworkInterceptor(NetWorkInterceptor())    // 自定义网络缓存
                .connectTimeout(20, TimeUnit.SECONDS)   // 连接超时时间，单位：秒
                .readTimeout(20, TimeUnit.SECONDS)      // 读取超时时间，单位：秒
                .writeTimeout(20, TimeUnit.SECONDS)     // 写超时时间，单位：秒
                .build()
    }

    /**
     * 创建请求服务
     * */
    fun <T> createRequest(clazz: Class<T>): T {
        val retrofit = Retrofit.Builder()
        /**请求URL前缀，例：http://www.qi-cloud.com/  BaseUrl:总是以/结尾 @Url:不要以/开头*/
        retrofit.baseUrl(BaseUrlUtil.getBaseUrl() ?: "")
        /**请求服务*/
        retrofit.client(httpClient)
        /**增加 RxJava2 适配器*/
        retrofit.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        /**增加json 转换器*/
        retrofit.addConverterFactory(GsonConverterFactory.create())
        return retrofit.build().create(clazz)
    }

    /**
     * 下载请求
     *
     * @param clazz
     * */
    fun <T> createDownloadRequest(clazz: Class<T>): T {
        val downloadClient: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)           // 连接超时时间，单位：秒
                .readTimeout(20, TimeUnit.SECONDS)              // 读取超时时间，单位：秒
                .writeTimeout(20, TimeUnit.SECONDS)             // 写超时时间，单位：秒
                .addInterceptor(LoggingInterceptor())                   // 自定义日志打印拦截器，打印请求地址
                .addNetworkInterceptor(DownloadInterceptor())           // 自定义拦截下载进度
                .build()

        val retrofit = Retrofit.Builder()
        /**请求URL前缀，例：http://www.qi-cloud.com/  BaseUrl:总是以/结尾 @Url:不要以/开头*/
        retrofit.baseUrl(BaseUrlUtil.getBaseUrl() ?: "")
        /**请求服务*/
        retrofit.client(downloadClient)
        /**增加 RxJava2 适配器*/
        retrofit.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        /**增加json 转换器*/
        retrofit.addConverterFactory(GsonConverterFactory.create())

        return retrofit.build().create(clazz)
    }

    /**
     * 获取缓存路径
     * */
    private fun getDiskCacheDir(context: Context): File? {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
            context.externalCacheDir
        } else {
            context.cacheDir
        }
    }

    /**
     * 静态内部类
     * 一个ClassLoader下同一个类只会加载一次，保证了并发时不会得到不同的对象
     * */
    object RequestHolder {
        var instance: FrameRequest = FrameRequest()
    }

    companion object {
        /**
         * 实现懒加载
         * 在调用getInstance()方法时才会去初始化mInstance
         */
        val instance: FrameRequest
            get() = RequestHolder.instance

        private val CLIENT_TYPE_KEY = "qc_client_type"
        private val CLIENT_TYPE = "android"
        private val FORMAT = "format"
        private val APP_STR_KEY = "qc_app_str"
        private val APP_SIGN_KEY = "qc_app_sign"
        private val APP_TOKEN_KEY = "qc_app_token"

        private val qc_app_str = QCloudAppSignUtil.encryptCharStr()
        private val qc_app_sign = QCloudAppSignUtil.signParamStr(qc_app_str, FrameConfig.appSign)

        private var qc_app_token: String? = null

        /**
         * 普通的请求数据
         */
        fun getBaseParams(): HashMap<String, Any> {
            val params = HashMap<String, Any>()
            params[FORMAT] = true
            params[CLIENT_TYPE_KEY] = CLIENT_TYPE
            params[APP_STR_KEY] = qc_app_str
            params[APP_SIGN_KEY] = qc_app_sign

            return params
        }

        /**
         * 请求基础参数
         */
        fun getAppParams(): HashMap<String, Any> {
            val params = getBaseParams()
            if (StringUtil.isBlank(qc_app_token)) {
                qc_app_token = TokenUtil.getToken()
            }
            if (StringUtil.isNotBlank(qc_app_token)) {
                params[APP_TOKEN_KEY] = qc_app_token!!
            }
            return params
        }

        /**
         * 保存token到请求参数里去
         * */
        fun setToken(token: String?) {
            qc_app_token = token
            Log.e("TOKEN", "token = $qc_app_token")
        }
    }
}