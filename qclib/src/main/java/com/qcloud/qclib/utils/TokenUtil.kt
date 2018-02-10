package com.qcloud.qclib.utils

import android.util.Log
import com.qcloud.qclib.network.OkGoRequest

/**
 * 类说明：app token工具类
 * Author: Kuzan
 * Date: 2017/11/23 17:52.
 */
object TokenUtil {
    private val TOKEN = "app_token"

    /**
     * 保存token
     *
     * @param token
     * */
    fun saveToken(token: String) {
        val tokenStr = Base64Util.stringToBase64(token)
        if (StringUtil.isNotBlank(tokenStr)) {
            SharedUtil.writeString(TOKEN, tokenStr)
            OkGoRequest.setToken(token)
        }
    }

    /**
     * 清掉token
     * */
    fun clearToken() {
        Log.e("TokenUtil", "clearToken")
        SharedUtil.writeString(TOKEN, "")
        OkGoRequest.setToken("")
    }

    /**
     * 获取token
     * */
    fun getToken(): String {
        val token = Base64Util.base64ToString(SharedUtil.getString(TOKEN))
        return if (StringUtil.isBlank(token)) "" else token
    }
}