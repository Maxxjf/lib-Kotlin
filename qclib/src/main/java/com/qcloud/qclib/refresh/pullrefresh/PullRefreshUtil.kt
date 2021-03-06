package com.qcloud.qclib.refresh.pullrefresh

import android.support.annotation.NonNull
import android.view.View
import com.qcloud.qclib.refresh.listener.OnFooterStateListener
import com.qcloud.qclib.refresh.listener.OnHeaderStateListener
import com.qcloud.qclib.refresh.widget.FooterView
import com.qcloud.qclib.refresh.widget.HeaderView

/**
 * 类说明：下拉刷新上拉加载更多工具类
 * Author: Kuzan
 * Date: 2018/1/15 14:26.
 */
object PullRefreshUtil {
    /**
     * 刷新控件的基本配件 (头部、尾部都用默认的)
     *
     * @param layout          刷新控件
     * @param isDownRefresh 是否开启下拉刷新，默认开启
     * @param isUpLoadMore  是否开启上拉加载，默认关闭
     */
    fun setRefresh(layout: PullRefreshLayout, isDownRefresh: Boolean = true, isUpLoadMore: Boolean = false) {
        val headView: HeaderView? = if (isDownRefresh) {
            HeaderView(layout.context)
        } else {
            null
        }
        val footView: FooterView? = if (isUpLoadMore) {
            FooterView(layout.context)
        } else {
            null
        }
        setRefresh(layout, isDownRefresh, isUpLoadMore, headView, footView, headView, footView)
    }

    /**
     * 刷新控件的基本配件 (自定义头部、尾部用默认的)
     *
     * @param layout                  刷新控件
     * @param isDownRefresh         是否开启下拉刷新
     * @param isUpLoadMore          是否开启上拉加载
     * @param headView              头部View
     * @param headerStateListener   头部监听器
     */
    fun setRefresh(@NonNull layout: PullRefreshLayout, isDownRefresh: Boolean, isUpLoadMore: Boolean, headView: View?, headerStateListener: OnHeaderStateListener?) {
        val footView: FooterView? = if (isUpLoadMore) {
            FooterView(layout.context)
        } else {
            null
        }
        setRefresh(layout, isDownRefresh, isUpLoadMore, headView, footView, headerStateListener, footView)
    }

    /**
     * 刷新控件的基本配件 (自定义尾部 、头部用默认的)
     *
     * @param layout                  刷新控件
     * @param isDownRefresh         是否开启下拉刷新
     * @param isUpLoadMore          是否开启上拉加载
     * @param footView              尾部View
     * @param footerStateListener   尾部监听器
     */
    fun setRefresh(@NonNull layout: PullRefreshLayout, isDownRefresh: Boolean, isUpLoadMore: Boolean, footView: View?, footerStateListener: OnFooterStateListener?) {
        val headView: HeaderView? = if (isDownRefresh) {
            HeaderView(layout.context)
        } else {
            null
        }
        setRefresh(layout, isDownRefresh, isUpLoadMore, headView, footView, headView, footerStateListener)
    }

    /**
     * 刷新控件的基本配件 （自定义头部、尾部）
     *
     * @param layout                  刷新控件
     * @param isDownRefresh         是否开启下拉刷新
     * @param isUpLoadMore          是否开启上拉加载
     * @param headView              头部View
     * @param footView              尾部View
     * @param headerStateListener   头部监听器
     * @param footerStateListener   尾部监听器
     */
    fun setRefresh(@NonNull layout: PullRefreshLayout, isDownRefresh: Boolean, isUpLoadMore: Boolean, headView: View?, footView: View?,
                   headerStateListener: OnHeaderStateListener?, footerStateListener: OnFooterStateListener?) {
        layout.setRefresh(isDownRefresh, isUpLoadMore)
        // 下拉刷新
        if (isDownRefresh && headView != null) {
            layout.setHead(headView)
            layout.onHeaderStateListener = headerStateListener
        }
        // 上拉加载
        if (isUpLoadMore && footView != null) {
            layout.setFoot(footView)
            layout.onFooterStateListener = footerStateListener
        }
    }
}