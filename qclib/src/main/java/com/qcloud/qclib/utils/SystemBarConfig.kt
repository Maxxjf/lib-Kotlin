package com.qcloud.qclib.utils

import android.app.Activity
import android.content.res.Configuration

/**
 * 类说明：系统栏的配置
 * Author: Kuzan
 * Date: 2017/12/7 16:21.
 */
class SystemBarConfig constructor(activity: Activity, translucentStatusBar: Boolean, translucentNavBar: Boolean) {
    private val mTranslucentStatusBar: Boolean
    private val mTranslucentNavBar: Boolean
    private val mHasNavigationBar: Boolean
    private val mInPortrait: Boolean

    val mStatusBarHeight: Int
    val mActionBarHeight: Int
    val mNavigationBarHeight: Int
    val mNavigationBarWidth: Int

    private val mSmallestWidthDp: Float

    init {
        val res = activity.resources
        mInPortrait = res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        mSmallestWidthDp = ScreenUtil.getSmallestWidthDp(activity)
        mStatusBarHeight = ScreenUtil.getStatusBarHeightByRes(activity)
        mActionBarHeight = ScreenUtil.getActionBarHeight(activity)
        mNavigationBarWidth = ScreenUtil.getNavBarWidth(activity)
        mNavigationBarHeight = ScreenUtil.getNavBarHeight(activity)
        mHasNavigationBar = mNavigationBarHeight > 0
        mTranslucentStatusBar = translucentStatusBar
        mTranslucentNavBar = translucentNavBar
    }

    /**
     * 获取出现在屏幕顶部的任何系统UI的布局插图高度
     *
     * @param withActionBar True 包括ActionBar的高度.
     * @return
     */
    fun getPixelInsetTop(withActionBar: Boolean): Int {
        var value = 0
        if (mTranslucentStatusBar) {
            value += mStatusBarHeight
        }
        if (withActionBar) {
            value += mActionBarHeight
        }
        return value
    }

    /**
     * 获取屏幕底部出现的任何系统UI的布局插图。
     *
     * @return
     */
    fun getPixelInsetBottom(activity: Activity): Int {
        return if (mTranslucentNavBar && SystemBarUtil.isNavAtBottom(activity)) {
            mNavigationBarHeight
        } else {
            0
        }
    }

    /**
     * 获取屏幕右侧出现的任何系统UI的布局插图。
     *
     * @return
     */
    fun getPixelInsetRight(activity: Activity): Int {
        return if (mTranslucentNavBar && !SystemBarUtil.isNavAtBottom(activity)) {
            mNavigationBarWidth
        } else {
            0
        }
    }
}