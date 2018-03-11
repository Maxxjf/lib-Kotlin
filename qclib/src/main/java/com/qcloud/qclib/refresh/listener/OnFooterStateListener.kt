package com.qcloud.qclib.pullrefresh.listener

import android.view.View

/**
 * 类说明：底部状态监听器
 * Author: Kuzan
 * Date: 2018/1/12 16:00.
 */
interface OnFooterStateListener {
    /**
     * 底部滑动变化
     *
     * @param foot         尾部View
     * @param scrollOffset 滑动距离
     * @param scrollRatio  从开始到触发阀值的滑动比率（0到100）如果滑动到达了阀值，就算在滑动，这个值也是100
     */
    fun onScrollChange(foot: View?, scrollOffset: Int, scrollRatio: Int)

    /**
     * 底部处于加载状态 （触发上拉加载的时候调用）
     *
     * @param foot 底部View
     */
    fun onRefreshFoot(foot: View?)

    /**
     * 底部收起
     *
     * @param foot 底部View
     */
    fun onRetractFoot(foot: View?)

    /**
     * 没有更多
     *
     * @param foot
     */
    fun onNotMore(foot: View?)

    /**
     * 有更多
     *
     * @param foot
     */
    fun onHasMore(foot: View?)
}