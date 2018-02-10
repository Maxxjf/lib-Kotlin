package com.qcloud.qclib.widget.customview.wheelview.entity

import com.qcloud.qclib.widget.customview.wheelview.WheelView
import java.util.*

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/1/19 15:11.
 */
class InertiaTimerTask constructor(val view: WheelView, val velocityY: Float): TimerTask() {
    var value: Float = Integer.MAX_VALUE.toFloat()

    override fun run() {
        if (value == Integer.MAX_VALUE.toFloat()) {
            value = if (Math.abs(velocityY) > 2000f) {
                if (velocityY > 0.0f) {
                    2000f
                } else {
                    -2000f
                }
            } else {
                velocityY
            }
        }
        if (Math.abs(value) in 0.0f..20f) {
            view.cancelFuture()
            view.handler.sendEmptyMessage(MessageHandler.WHAT_SMOOTH_SCROLL)
            return
        }
        val i = (value * 10f / 1000f).toInt()
        view.totalScrollY = view.totalScrollY - i
        if (!view.isLoop) {
            val itemHeight = view.itemHeight
            var top = -view.initPosition * itemHeight
            var bottom = (view.getItemCount() - 1 - view.initPosition) * itemHeight
            if (view.totalScrollY - itemHeight * 0.25 < top) {
                top = view.totalScrollY + i
            } else if (view.totalScrollY + itemHeight * 0.25 > bottom) {
                bottom = view.totalScrollY + i
            }
            if (view.totalScrollY <= top) {
                value = 40f
                view.totalScrollY = top.toInt().toFloat()
            } else if (view.totalScrollY >= bottom) {
                view.totalScrollY = bottom.toInt().toFloat()
                value = -40f
            }
        }
        value = if (value < 0.0f) {
            value + 20f
        } else {
            value - 20f
        }
        view.handler!!.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE)
    }
}