package com.qcloud.qclib.widget.swipeback

/**
 * 类说明：侧滑返回
 *          在使用的Activity直接继承这个就可以
 * Author: Kuzan
 * Date: 2018/1/17 14:04.
 */
interface ISwipeBack {
    /** 返回与对应activity相关的swipeBackLayout */
    fun getSwipeBackLayout(): SwipeBackLayout?

    /** 是否启用SwipeBack功能 */
    fun setSwipeBackEnable(enable: Boolean)

    /** 右滑退出activity */
    fun scrollToFinishActivity()
}