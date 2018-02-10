package com.qcloud.qclib.callback

/**
 * 类说明：数据解析回调
 * Author: Kuzan
 * Date: 2018/1/16 11:26.
 */
interface DataCallback<in T> {
    /** 成功 */
    fun onSuccess(t: T?, message: String?)

    /** 失败 */
    fun onError(status: Int, message: String)
}