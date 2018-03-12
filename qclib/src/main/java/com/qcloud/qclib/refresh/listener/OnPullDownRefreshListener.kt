package com.qcloud.qclib.refresh.listener

/**
 * 类说明：下拉更新监听器
 * Author: Kuzan
 * Date: 2018/1/12 16:05.
 */
interface OnPullDownRefreshListener {
    /**
     * 下拉刷新
     * */
    fun onRefresh()
}