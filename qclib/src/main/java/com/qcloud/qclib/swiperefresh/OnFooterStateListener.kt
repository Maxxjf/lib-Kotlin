package com.qcloud.qclib.swiperefresh

import android.view.View

/**
 * 类说明：底部状态监听器
 * Author: Kuzan
 * Date: 2017/8/3 16:37.
 */
interface OnFooterStateListener {
    /**
     * 底部滑动变化
     *
     * @param footer         底部View
     * @param scrollOffset  滑动距离
     * @param scrollRatio   从开始到触发阀值的滑动比率（0到100）如果滑动到达了阀值，就算在滑动，这个值也是100
     */
    fun onScrollChange(footer: View?, scrollOffset: Int, scrollRatio: Int)

    /**
     * 底部处于加载状态 （触发上拉加载的时候调用）
     *
     * @param footer 底部View
     */
    fun onLoading(footer: View?)

    /**
     * 底部收起
     *
     * @param footer 底部View
     */
    fun onRetract(footer: View?)

    /**
     * 没有更多
     *
     * @param footer
     */
    fun onNoMore(footer: View?)

    /**
     * 有更多
     *
     * @param footer
     */
    fun onHasMore(footer: View?)
}
