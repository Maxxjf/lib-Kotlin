package com.qcloud.qclib.rxtask.task

/**
 * 类说明：IO线程
 * Author: Kuzan
 * Date: 2017/12/21 9:15.
 */
interface IOTask<T> {
    fun doOnIOThread()
}