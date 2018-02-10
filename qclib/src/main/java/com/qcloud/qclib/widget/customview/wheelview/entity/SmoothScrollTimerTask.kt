package com.qcloud.qclib.widget.customview.wheelview.entity

import com.qcloud.qclib.widget.customview.wheelview.WheelView
import java.util.*

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/1/19 15:53.
 */
class SmoothScrollTimerTask(val view: WheelView, val offset: Int): TimerTask() {
    var realTotalOffset: Int = Integer.MAX_VALUE
    var realOffset: Int = 0

    override fun run() {
        if (realTotalOffset == Integer.MAX_VALUE) {
            realTotalOffset = offset
        }
        //把要滚动的范围细分成10小份，按10小份单位来重绘
        realOffset = (realTotalOffset.toFloat() * 0.1f).toInt()
        if (realOffset == 0) {
            realOffset = if (realTotalOffset < 0) {
                -1
            } else {
                1
            }
        }
        if (Math.abs(realTotalOffset) <= 1) {
            view.cancelFuture()
            view.handler.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED)
        } else {
            view.totalScrollY = view.totalScrollY + realOffset
            //这里如果不是循环模式，则点击空白位置需要回滚，不然就会出现选到－1 item的情况
            if (!view.isLoop) {
                val itemHeight = view.itemHeight
                val top = (-view.initPosition).toFloat() * itemHeight
                val bottom = (view.getItemCount() - 1 - view.initPosition).toFloat() * itemHeight
                if (view.totalScrollY <= top || view.totalScrollY >= bottom) {
                    view.totalScrollY = view.totalScrollY - realOffset
                    view.cancelFuture()
                    view.handler!!.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED)
                    return
                }
            }
            view.handler!!.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE)
            realTotalOffset -= realOffset
        }
    }
}