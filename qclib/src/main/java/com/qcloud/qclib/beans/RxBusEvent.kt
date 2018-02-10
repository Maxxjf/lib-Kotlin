package com.qcloud.qclib.beans

import java.io.Serializable

/**
 * 类说明：事件总线对象
 * Author: Kuzan
 * Date: 2018/1/16 9:54.
 */
class RxBusEvent internal constructor(builder: RxBusEventBuilder): Serializable {

    var type: Int = 0
    var obj: Any? = null
    var clickId: Int = 0

    init {
        this.type = builder.type
        this.obj = builder.obj
        this.clickId = clickId
    }

    companion object {
        private const val serialVersionUID = 1L

        fun newBuilder(type: Int): RxBusEventBuilder {
            return RxBusEventBuilder(type)
        }
    }

    override fun toString(): String {
        return "RxBusEvent(type=$type, obj=$obj, clickId=$clickId)"
    }
}