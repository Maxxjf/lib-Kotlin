package com.qcloud.qclib.rxtask.task

/**
 * 类说明：UI线程，主线程
 * Author: Kuzan
 * Date: 2017/12/21 9:17.
 */
interface UITask<T> {
    fun doOnUIThread()
}