package com.qcloud.qclib.pullrefresh.listener

import android.view.View

/**
 * 类说明：头部状态监听器
 * Author: Kuzan
 * Date: 2018/1/12 15:57.
 */
interface OnHeaderStateListener {
    /**
     * 头部滑动变化
     *
     * @param head         头部View
     * @param scrollOffset 滑动距离
     * @param scrollRatio  从开始到触发阀值的滑动比率（0到100）如果滑动到达了阀值，就算在滑动，这个值也是100
     */
    fun onScrollChange(head: View?, scrollOffset: Int, scrollRatio: Int)

    /**
     * 头部处于刷新状态 （触发下拉刷新的时候调用）
     *
     * @param head 头部View
     */
    fun onRefreshHead(head: View?)

    /**
     * 头部收起
     *
     * @param head 头部View
     */
    fun onRetractHead(head: View?)
}