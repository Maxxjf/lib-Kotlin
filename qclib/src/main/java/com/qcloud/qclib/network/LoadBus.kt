package com.qcloud.qclib.network

import io.reactivex.Flowable
import io.reactivex.annotations.NonNull
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor

/**
 * 类说明：下载拦截发送事件
 * Author: Kuzan
 * Date: 2018/4/8 13:49.
 */
class LoadBus private constructor() {

    private val mBus: FlowableProcessor<Any> = PublishProcessor.create()

    /**
     * 发送一个新事件
     *
     * @param obj
     */
    fun post(@NonNull obj: Any) {
        mBus.onNext(obj)
    }

    fun <T> register(clz: Class<T>): Flowable<T>? {
        return mBus.ofType(clz)
    }

    fun register(): Flowable<Any> {
        return mBus
    }

    /**
     * 会将所有由mBus生成的Flowable都置completed状态后续的所有消息都收不到了
     * */
    fun unregisterAll() {
        mBus.onComplete()
    }

    fun hasSubscribers(): Boolean {
        return mBus.hasSubscribers()
    }

    /**
     * 静态内部类
     * 一个ClassLoader下同一个类只会加载一次，保证了并发时不会得到不同的对象
     * */
    object LoadHolder {
        var instance: LoadBus = LoadBus()
    }

    companion object {
        /**
         * 实现懒加载
         * 在调用getInstance()方法时才会去初始化mInstance
         */
        val instance: LoadBus
            get() = LoadHolder.instance
    }
}