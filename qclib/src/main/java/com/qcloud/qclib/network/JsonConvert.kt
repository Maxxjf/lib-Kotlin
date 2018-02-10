package com.qcloud.qclib.network

import android.support.annotation.NonNull
import com.google.gson.stream.JsonReader
import com.lzy.okgo.convert.Converter
import com.qcloud.qclib.beans.BaseResponse
import com.qcloud.qclib.beans.SimpleResponse
import com.qcloud.qclib.utils.JsonConvertUtil
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.ParameterizedType

import java.lang.reflect.Type

/**
 * 类说明：Json转换器，处理网络请求返回数据
 * Author: Kuzan
 * Date: 2018/1/16 8:53.
 */
class JsonConvert<T>: Converter<T> {

    private var mType: Type? = null
    private var mClazz: Class<T>? = null

    constructor() {

    }

    constructor(@NonNull type: Type) {
        this.mType = type
    }

    constructor(@NonNull clazz: Class<T>) {
        this.mClazz = clazz
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象，生成onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    override fun convertResponse(@NonNull response: Response): T? {
        if (mType == null) {
            if (mClazz == null) {
                // 如果没有通过构造函数传进来，就自动解析父类泛型的真实类型（有局限性，继承后就无法解析到）
                val genType = javaClass.genericSuperclass
                mType = (genType as ParameterizedType).actualTypeArguments[0]
            } else {
                return parseClass<T>(response, mClazz)
            }
        }
        return when (mType) {
            is ParameterizedType -> parseParameterizedType<T>(response, mType as ParameterizedType?)
            is Class<*> -> parseClass(response, mType as Class<*>?)
            else -> parseType(response, mType)
        }
    }

    /**
     * 解析class
     * */
    @Suppress("UNCHECKED_CAST")
    private fun <T> parseClass(@NonNull response: Response, clazz: Class<*>?): T? {
        if (clazz == null) return null
        val body = response.body() ?: return null

        val jsonReader = JsonReader(body.charStream())
        return when (clazz) {
            String::class.java -> body.string() as T
            JSONObject::class.java -> JSONObject(body.string()) as T
            JSONArray::class.java -> JSONArray(body.string()) as T
            else -> {
                val t = JsonConvertUtil.fromJson<T>(jsonReader, clazz)
                response.close()
                return t
            }
        }
    }

    /**
     * 解析Type
     * */
    @Throws(Exception::class)
    private fun parseType(@NonNull response: Response, type: Type?): T? {
        if (type == null) return null
        val body = response.body() ?: return null

        val jsonReader = JsonReader(body.charStream())
        // 泛型格式如下： new JsonCallback<任意JavaBean>(this)
        val t = JsonConvertUtil.fromJson<T>(jsonReader, type)
        response.close()

        return t
    }

    /**
     * 解析ParameterizedType
     * */
    @Suppress("UNCHECKED_CAST")
    private fun <T> parseParameterizedType(@NonNull response: Response, type: ParameterizedType?): T? {
        if (type == null) return null
        val body = response.body() ?: return null

        val jsonReader = JsonReader(body.charStream())

        // 泛型的实际类型
        val rawType = type.rawType
        // 泛型的参数
        val typeArgument = type.actualTypeArguments[0]

        return if (rawType != BaseResponse::class.java) {
            // 泛型格式如下： new JsonCallback<外层BaseBean<内层JavaBean>>(this)
            val t = JsonConvertUtil.fromJson<T>(jsonReader, type)
            response.close()
            t
        } else {
            if (typeArgument == Void::class.java) {
                // 泛型格式如下： new JsonCallback<LzyResponse<Void>>(this)
                val simpleResponse = JsonConvertUtil.fromJson<SimpleResponse>(jsonReader, SimpleResponse::class.java)
                response.close()
                simpleResponse.toBaseResponse<T>() as T
            } else {
                // 泛型格式如下： new JsonCallback<LzyResponse<内层JavaBean>>(this)
                val lzyResponse = JsonConvertUtil.fromJson<T>(jsonReader, type)
                response.close()
                lzyResponse
            }
        }
    }
}