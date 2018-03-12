package com.qcloud.qclib.refresh.swiperefresh

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.View

import com.qcloud.qclib.refresh.listener.OnFooterStateListener
import com.qcloud.qclib.refresh.widget.FooterView

/**
 * 类说明：上拉加载更多工具类，下拉默认开启，若想不开启，使用PullRefreshView
 * Author: Kuzan
 * Date: 2017/8/03 16:42.
 */
object SwipeRefreshUtil {

    /**
     * 刷新控件的基本配件 (底部用默认的)
     *
     * @param view          刷新控件
     * @param isLoadMore   是否开启上拉加载
     */
    fun setLoadMore(view: SwipeRefreshLayout, isLoadMore: Boolean = true) {

        var footerView: FooterView? = null

        if (isLoadMore) {
            footerView = FooterView(view.context)
        }

        setLoadMore(view, isLoadMore, footerView, footerView)
    }

    /**
     * 刷新控件的基本配件 (自定义底部 )
     *
     * @param view              刷新控件
     * @param isLoadMore        是否开启上拉加载
     * @param footerView        尾部View
     * @param listener          尾部监听器
     */
    fun setLoadMore(view: SwipeRefreshLayout, isLoadMore: Boolean, footerView: View?, listener: OnFooterStateListener?) {
        view.setLoadMore(isLoadMore)
        if (isLoadMore) {
            view.setFooter(footerView!!)
            view.onFooterStateListener = listener
        }
    }

    /**
     * 设置下拉刷新动画的位置
     *
     * @param view          刷新控件
     * @param animPosition  刷新动画的位置 1左边 2中间
     */
    fun setAnimPosition(view: SwipeRefreshLayout, animPosition: Int) {
        view.mAnimPosition = animPosition
    }

    /**
     * 设置动画颜色
     *
     * @param colors 颜色值
     * */
    fun setColorScheme(view: SwipeRefreshLayout, @ColorInt vararg colors: Int) {
        view.setColorScheme(*colors)
    }

    /**
     * 设置动画颜色
     *
     * @param colorResIds 颜色值
     * */
    fun setColorSchemeResources(view: SwipeRefreshLayout, @ColorRes vararg colorResIds: Int) {
        view.setColorSchemeResources(*colorResIds)
    }

    /**
     * 设置动画颜色
     *
     * @param colors 颜色值
     * */
    fun setColorSchemeColors(view: SwipeRefreshLayout, colors: IntArray) {
        view.setColorSchemeColors(colors)
    }
}
