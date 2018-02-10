package com.qcloud.qclib.rxbus

import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * 类说明：封装Subscriber
 * Author: Kuzan
 * Date: 2018/1/9 11:12.
 */
abstract class AbstractSubscriber<T : Any>: Consumer<T>, Disposable {
    @Volatile private var isDisposed: Boolean = false

    override fun accept(t: T) {
        try {
            acceptEvent(t)
        } catch (e: Exception) {
            throw RuntimeException("Could not dispatch event: " + t::class.java, e)
        }
    }

    override fun dispose() {
        if (!isDisposed) {
            isDisposed = true
            release()
        }
    }

    override fun isDisposed(): Boolean {
        return isDisposed
    }

    @Throws(Exception::class)
    protected abstract fun acceptEvent(t: T)

    protected abstract fun release()
}