package com.qcloud.qclib.network

import android.support.annotation.NonNull
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.model.HttpMethod
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.request.base.Request
import com.lzy.okrx2.adapter.ObservableBody
import io.reactivex.Observable
import java.lang.reflect.Type

/**
 * 类说明：Rx网络请求数据
 * Author: Kuzan
 * Date: 2018/1/16 8:42.
 */
object RxRequest {
    fun <T> request(@NonNull method: HttpMethod, @NonNull url: String, @NonNull type: Type, tag: Any? = null): Observable<T> {
        return request(method, url, type, null, null, null, tag)
    }

    fun <T> request(@NonNull method: HttpMethod, @NonNull url: String, @NonNull type: Type, @NonNull params: HttpParams, tag: Any? = null): Observable<T> {
        return request(method, url, type, null, params, null, tag)
    }

    fun <T> request(@NonNull method: HttpMethod, @NonNull url: String, @NonNull type: Type, @NonNull params: HttpParams, @NonNull headers: HttpHeaders, tag: Any? = null): Observable<T> {
        return request(method, url, type, null, params, headers, tag)
    }

    fun <T> request(@NonNull method: HttpMethod, @NonNull url: String, @NonNull clazz: Class<T>, tag: Any? = null): Observable<T> {
        return request(method, url, null, clazz, null, null, tag)
    }

    fun <T> request(@NonNull method: HttpMethod, @NonNull url: String, @NonNull clazz: Class<T>, @NonNull params: HttpParams, tag: Any? = null): Observable<T> {
        return request(method, url, null, clazz, params, null, tag)
    }

    fun <T> request(@NonNull method: HttpMethod, @NonNull url: String, @NonNull clazz: Class<T>, @NonNull params: HttpParams, @NonNull headers: HttpHeaders, tag: Any? = null): Observable<T> {
        return request(method, url, null, clazz, params, headers, tag)
    }

    /**
     * 网络请求
     *
     * @param method 请求方法，必须传
     * @param url 请求链接，必须传
     * @param type 返回数据对象的类型，可不传，默认为null
     * @param clazz 返回数据对象有class类型，可不传，默认为null
     * @param params 请求参数，可不传，默认为null
     * @param headers 请求头，可不传，默认为null
     * @param tag 请求标签，可不传，默认为null
     *
     * @time 2017/7/1 16:03
     */
    fun <T> request(@NonNull method: HttpMethod, @NonNull url: String, type: Type? = null, clazz: Class<T>? = null, params: HttpParams? = null, headers: HttpHeaders? = null, tag: Any? = null): Observable<T> {
        val request: Request<T, out Request<*, *>> = when (method) {
            HttpMethod.GET -> OkGo.get(url)
            HttpMethod.POST -> OkGo.post(url)
            HttpMethod.PUT -> OkGo.put(url)
            HttpMethod.DELETE -> OkGo.delete(url)
            HttpMethod.HEAD -> OkGo.head(url)
            HttpMethod.PATCH -> OkGo.patch(url)
            HttpMethod.OPTIONS -> OkGo.options(url)
            HttpMethod.TRACE -> OkGo.trace(url)
            else -> OkGo.get(url)
        }
        if (tag != null) {
            request.tag(tag)
        }
        request.headers(headers)
        request.params(params)
        when {
            type != null -> request.converter(JsonConvert<T>(type))
            clazz != null -> request.converter(JsonConvert(clazz))
            else -> request.converter(JsonConvert<T>())
        }
        return request.adapt(ObservableBody<T>())
    }
}