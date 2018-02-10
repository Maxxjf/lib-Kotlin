package com.qcloud.qclib.widget.customview.banner

import android.content.Context
import android.view.animation.Interpolator
import android.widget.Scroller

/**
 * 类说明：自定义轮播图
 * Author: Kuzan
 * Date: 2018/1/19 11:13.
 */
open class ViewPagerScroller: Scroller {

    var mScrollDuration: Int = 550
    var isZero: Boolean = false

    constructor(context: Context): super(context)

    constructor(context: Context, interpolator: Interpolator): super(context, interpolator)

    constructor(context: Context, interpolator: Interpolator, flywheel: Boolean): super(context, interpolator, flywheel)

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy, if (isZero) 0 else mScrollDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, if (isZero) 0 else mScrollDuration)
    }
}