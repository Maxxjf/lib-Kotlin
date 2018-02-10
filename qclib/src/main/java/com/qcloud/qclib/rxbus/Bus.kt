package com.qcloud.qclib.rxbus

import io.reactivex.annotations.NonNull
import io.reactivex.functions.Consumer

/**
 * 类说明：事件接口
 * Author: Kuzan
 * Date: 2018/1/9 11:10.
 */
interface Bus {
    /** 注册事件 */
    fun register(@NonNull observer: Any)

    /** 获得监听者 */
    fun <T: Any> obtainSubscriber(@NonNull eventClass: Class<T>, @NonNull receiver: Consumer<T>): CustomSubscriber<T>

    /** 接收事件 */
    fun <T: Any> registerSubscriber(@NonNull observer: Any, @NonNull subscriber: CustomSubscriber<T>)

    /** 解除注册 */
    fun unregister(@NonNull observer: Any)

    /** 发送事件 */
    fun post(event: Any)
}