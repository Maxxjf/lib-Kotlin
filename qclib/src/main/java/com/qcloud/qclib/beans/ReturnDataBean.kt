package com.qcloud.qclib.beans

import java.io.Serializable

/**
 * 类说明：返回列表数据data
 * Author: Kuzan
 * Date: 2018/1/16 10:11.
 */
class ReturnDataBean<T>: Serializable {
    var list: List<T>? = null
    var totalRow: Int = 0   // 数据总条数

    fun isNext(pageSize: Int): Boolean {
        if (list == null) {
            return false
        }
        return list!!.size >= pageSize
    }

    override fun toString(): String {
        return "ReturnDataBean(list=$list, totalRow=$totalRow)"
    }
}