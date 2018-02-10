package com.qcloud.qclib.rxbus

/**
 * 类说明：Bus实例
 * Author: Kuzan
 * Date: 2018/1/9 11:53.
 */
class BusProvider internal constructor() {
    /**
     * 静态内部类
     * 一个ClassLoader下同一个类只会加载一次，保证了并发时不会得到不同的对象
     */
    private object BusHolder {
        val instance: Bus = RxBus()
    }

    companion object {
        val instance: Bus
            get() = BusHolder.instance
    }
}