package com.qcloud.qclib.rxtask.task

/**
 * 类说明：新线程
 * Author: Kuzan
 * Date: 2017/12/21 9:16.
 */
interface NewTask<T> {
    fun doOnNewThread()
}