package com.qcloud.qclib.rxbus

import io.reactivex.annotations.NonNull
import java.lang.reflect.Method

/**
 * 类说明：注释Subscriber
 * Author: Kuzan
 * Date: 2018/1/9 11:59.
 */
class AnnotatedSubscriber<T: Any> internal constructor(
        @NonNull private var observer: Any,
        @NonNull private var method: Method): AbstractSubscriber<T>() {

    private val hashCode: Int = 31 * observer.hashCode() + method.hashCode()

    override fun acceptEvent(t: T) {
        method.invoke(observer, t)
    }

    override fun release() {

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as AnnotatedSubscriber<*>?

        return observer == that!!.observer && method == that.method
    }

    override fun hashCode(): Int {
        return hashCode
    }
}