package com.qcloud.qclib.utils

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import com.readystatesoftware.chuck.internal.support.JsonConvertor
import java.io.Reader
import java.lang.reflect.Type

/**
 * 类说明：Json数据转换 用于网络请求数据返回处理
 * Author: Kuzan
 * Date: 2017/12/6 18:31.
 */
object JsonConvertUtil {

    object GsonHolder {
        val gson = Gson()
    }

    fun create(): Gson = GsonHolder.gson

    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(json: String?, clazz: Class<T>): T = create().fromJson(json, clazz)

    fun <T> fromJson(json: String?, type: Type): T = create().fromJson(json, type)

    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(reader: JsonReader, type: Type): T = create().fromJson(reader, type)

    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(reader: Reader, clazz: Class<T>): T = create().fromJson(reader, clazz)

    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(reader: Reader, type: Type): T = create().fromJson(reader, type)

    fun toJson(src: Any?): String? = create().toJson(src)

    fun toJson(src: Any?, type: Type): String? = create().toJson(src, type)

    fun formatJson(json: String?): String? {
        return try {
            val jp = JsonParser()
            val je = jp.parse(json)
            return JsonConvertor.getInstance().toJson(je)
        } catch (e: Exception) {
            json
        }
    }

    fun formatJson(src: Any?): String? {
        return try {
            val jp = JsonParser()
            val je = jp.parse(toJson(src))
            return JsonConvertor.getInstance().toJson(je)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}