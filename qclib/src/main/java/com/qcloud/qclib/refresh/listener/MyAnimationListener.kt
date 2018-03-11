package com.qcloud.qclib.swiperefresh

import android.view.animation.Animation

/**
 * 类说明：下拉动画监听
 * Author: Kuzan
 * Date: 2018/2/2 9:09.
 */
interface MyAnimationListener {
    fun onAnimationStart(var1: Animation?)

    fun onAnimationEnd(var1: Animation?)

    fun onAnimationRepeat(var1: Animation?)
}