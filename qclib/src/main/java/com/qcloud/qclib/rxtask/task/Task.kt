package com.qcloud.qclib.rxtask.task

/**
 * 类说明：主线程和子线程有交互执行
 * Author: Kuzan
 * Date: 2017/12/21 9:20.
 */
abstract class Task<T> (var t: T) {
    // T表示子线程和主线程需要调用的对象

    abstract fun doOnUIThread()

    abstract fun doOnIOThread()
}