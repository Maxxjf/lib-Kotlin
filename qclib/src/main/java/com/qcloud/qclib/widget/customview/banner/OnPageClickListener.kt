package com.qcloud.qclib.widget.customview.banner

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/1/19 11:28.
 */
interface OnPageClickListener<in T> {
    fun onPageClick(position: Int, t: T)
}