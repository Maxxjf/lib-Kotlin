package com.qcloud.qclib.beans

/**
 * 类说明：RxBus事件
 * Author: Kuzan
 * Date: 2018/1/16 9:57.
 */
class RxBusEventBuilder(internal var type: Int) {
    var obj: Any? = null
    var clickId: Int = 0

    fun clickId(clickId: Int): RxBusEventBuilder {
        this.clickId = clickId
        return this
    }

    fun setObj(obj: Any?): RxBusEventBuilder {
        this.obj = obj
        return this
    }

    fun build(): RxBusEvent {
        return RxBusEvent(this)
    }
}