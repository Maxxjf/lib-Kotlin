package com.qcloud.qclib.utils

import com.google.gson.Gson

/**
 * 类说明：Gson工具
 * Author: Kuzan
 * Date: 2017/12/4 11:44.
 */
object GsonUtil {
    /**
     * 将Map转化为Json
     *
     * @param map
     * @return String
     */
    fun <T> map2Json(map: Map<String, T>): String = Gson().toJson(map)
}