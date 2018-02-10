package com.qcloud.qclib.base

import com.qcloud.qclib.rxbus.Bus
import com.qcloud.qclib.rxbus.BusProvider

/**
 * 类说明：Presenter基类
 * Author: Kuzan
 * Date: 2018/1/17 11:26.
 */
abstract class BasePresenter<T> {
    var mView: T? = null
    var mEventBus: Bus? = BusProvider.instance

    /** 绑定view */
    fun attach(view: T) {
        this.mView = view

        if (mEventBus == null) {
            mEventBus = BusProvider.instance
        }
        try {
            mEventBus?.register(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** 解绑，资源回收 */
    fun detach() {
        mView?.let {
            mView = null
        }

        mEventBus?.let {
            try {
                mEventBus?.unregister(this)
                mEventBus = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}