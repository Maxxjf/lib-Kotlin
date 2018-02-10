package com.qcloud.qclib.rxbus

import io.reactivex.Scheduler
import io.reactivex.annotations.NonNull
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate
import io.reactivex.internal.functions.ObjectHelper


/**
 * 类说明：自定义Subscriber
 * Author: Kuzan
 * Date: 2018/1/9 11:22.
 */
class CustomSubscriber<T: Any> internal constructor(
        @NonNull eventClass: Class<T>,
        @NonNull private var receiver: Consumer<T>): AbstractSubscriber<T>() {
    private val hashCode: Int

    internal var eventClass: Class<T>? = null
        private set
    internal var filter: Predicate<T>? = null
        private set
    internal var scheduler: Scheduler? = null
        private set

    init {
        this.eventClass = eventClass
        hashCode = receiver.hashCode()
    }

    @SuppressWarnings("WeakerAccess")
    fun withFilter(@NonNull filter: Predicate<T>): CustomSubscriber<T> {
        ObjectHelper.requireNonNull(filter, "Filter must not be null.")
        this.filter = filter
        return this
    }

    @SuppressWarnings("WeakerAccess")
    fun withScheduler(@NonNull scheduler: Scheduler): CustomSubscriber<T> {
        ObjectHelper.requireNonNull(scheduler, "Scheduler must not be null.")
        this.scheduler = scheduler
        return this
    }

    @Throws(Exception::class)
    override fun acceptEvent(t: T) {
        receiver.accept(t)
    }

    override fun release() {
        eventClass = null
        filter = null
        scheduler = null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as CustomSubscriber<*>?

        return receiver == that!!.receiver
    }

    override fun hashCode(): Int {
        return hashCode
    }
}