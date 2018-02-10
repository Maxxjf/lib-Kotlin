package com.qcloud.qclib.utils

import android.util.Log
import com.qcloud.qclib.FrameConfig

/**
 * 类说明：网络请求域名
 * Author: Kuzan
 * Date: 2017/12/2 14:49.
 */
object BaseUrlUtil {
    // 保存服务器地址
    private const val BASE_URL = "base_url"
    // 保存是否开启后门的标志
    private const val PASTERN = "postern"

    var mBaseUrl: String? = null

    /**
     * 设置并保存baseUrl
     *
     * @param url
     */
    fun saveBaseUrl(url: String?) {
        if (StringUtil.isBlank(url)) {
            Log.e("BaseUrlUtil", "url is empty")
        } else {
            mBaseUrl = url
            if (isPastern()) {
                SharedUtil.writeString(BASE_URL, url)
            }
        }
    }

    /**
     * 获取baseUrl
     *
     * @return
     */
    fun getBaseUrl(): String? {
        if (StringUtil.isNotBlank(mBaseUrl)) {
            return mBaseUrl
        }
        if (isPastern()) {
            val url = SharedUtil.getString(BASE_URL)
            if (StringUtil.isNotBlank(url)) {
                mBaseUrl = url
                return url
            }
        }

        if (StringUtil.isNotBlank(FrameConfig.server)) {
            saveBaseUrl(FrameConfig.server)
            return FrameConfig.server
        }

        return null
    }

    /**
     * 保存是否开启后门的标志
     *
     * @param isPastern
     */
    fun savePastern(isPastern: Boolean) {
        if (FrameConfig.isPastern) {
            SharedUtil.writeBoolean(PASTERN, isPastern)
        }
    }

    /**
     * 获取是否开启后门的标志
     *
     * @return
     */
    fun isPastern(): Boolean = FrameConfig.isPastern && SharedUtil.getBoolean(PASTERN, false)
}