package com.qcloud.qclib.base

/**
 * 类说明：View的通用方法
 * Author: Kuzan
 * Date: 2018/1/16 14:38.
 */
interface BaseView {
    /**
     * 错误提示
     *
     * @param errMsg
     * @param isShow 是否toast提示，默认提示
     * */
    fun loadErr(errMsg: String, isShow: Boolean = true)
}