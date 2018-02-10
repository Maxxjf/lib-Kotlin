package com.qcloud.qclib.adapter.abslistview

/**
 * 类说明：多种Item类型支持接口
 * Author: Kuzan
 * Date: 2018/1/17 11:43.
 */
interface MultiItemTypeSupport<in T> {
    fun getLayoutId(position: Int, t: T): Int

    fun getViewTypeCount(): Int

    fun getItemViewType(position: Int, t: T): Int
}