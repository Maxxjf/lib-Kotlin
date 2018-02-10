package com.qcloud.qclib.pullrefresh

import android.support.annotation.NonNull
import android.view.View
import com.qcloud.qclib.pullrefresh.listener.OnFooterStateListener
import com.qcloud.qclib.pullrefresh.listener.OnHeaderStateListener

/**
 * 类说明：下拉刷新上拉加载更多工具类
 * Author: Kuzan
 * Date: 2018/1/15 14:26.
 */
object PullRefreshUtil {
    /**
     * 刷新控件的基本配件 (头部、尾部都用默认的)
     *
     * @param view          刷新控件
     * @param isDownRefresh 是否开启下拉刷新，默认开启
     * @param isUpLoadMore  是否开启上拉加载，默认关闭
     */
    fun setRefresh(view: PullRefreshView, isDownRefresh: Boolean = true, isUpLoadMore: Boolean = false) {
        val headView: HeadView? = if (isDownRefresh) {
            HeadView(view.context)
        } else {
            null
        }
        val footView: FootView? = if (isUpLoadMore) {
            FootView(view.context)
        } else {
            null
        }
        setRefresh(view, isDownRefresh, isUpLoadMore, headView, footView, headView, footView)
    }

    /**
     * 刷新控件的基本配件 (自定义头部、尾部用默认的)
     *
     * @param view                  刷新控件
     * @param isDownRefresh         是否开启下拉刷新
     * @param isUpLoadMore          是否开启上拉加载
     * @param headView              头部View
     * @param headerStateListener   头部监听器
     */
    fun setRefresh(@NonNull view: PullRefreshView, isDownRefresh: Boolean, isUpLoadMore: Boolean, headView: View?, headerStateListener: OnHeaderStateListener?) {
        val footView: FootView? = if (isUpLoadMore) {
            FootView(view.context)
        } else {
            null
        }
        setRefresh(view, isDownRefresh, isUpLoadMore, headView, footView, headerStateListener, footView)
    }

    /**
     * 刷新控件的基本配件 (自定义尾部 、头部用默认的)
     *
     * @param view                  刷新控件
     * @param isDownRefresh         是否开启下拉刷新
     * @param isUpLoadMore          是否开启上拉加载
     * @param footView              尾部View
     * @param footerStateListener   尾部监听器
     */
    fun setRefresh(@NonNull view: PullRefreshView, isDownRefresh: Boolean, isUpLoadMore: Boolean, footView: View?, footerStateListener: OnFooterStateListener?) {
        val headView: HeadView? = if (isDownRefresh) {
            HeadView(view.context)
        } else {
            null
        }
        setRefresh(view, isDownRefresh, isUpLoadMore, headView, footView, headView, footerStateListener)
    }

    /**
     * 刷新控件的基本配件 （自定义头部、尾部）
     *
     * @param view                  刷新控件
     * @param isDownRefresh         是否开启下拉刷新
     * @param isUpLoadMore          是否开启上拉加载
     * @param headView              头部View
     * @param footView              尾部View
     * @param headerStateListener   头部监听器
     * @param footerStateListener   尾部监听器
     */
    fun setRefresh(@NonNull view: PullRefreshView, isDownRefresh: Boolean, isUpLoadMore: Boolean, headView: View?, footView: View?,
                   headerStateListener: OnHeaderStateListener?, footerStateListener: OnFooterStateListener?) {
        view.setRefresh(isDownRefresh, isUpLoadMore)
        // 下拉刷新
        if (isDownRefresh && headView != null) {
            view.setHead(headView)
            view.onHeaderStateListener = headerStateListener
        }
        // 上拉加载
        if (isUpLoadMore && footView != null) {
            view.setFoot(footView)
            view.onFooterStateListener = footerStateListener
        }
    }
}