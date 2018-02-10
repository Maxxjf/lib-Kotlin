package com.qcloud.qclib.widget.customview.wheelview.entity

import android.os.Handler
import android.os.Message
import com.qcloud.qclib.widget.customview.wheelview.WheelView

/**
 * 类说明：处理事件
 * Author: Kuzan
 * Date: 2018/1/19 14:53.
 */
class MessageHandler(val view: WheelView): Handler() {

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            WHAT_INVALIDATE -> view.invalidate()
            WHAT_SMOOTH_SCROLL -> view.smoothScroll(WheelView.ACTION_FLING)
            WHAT_ITEM_SELECTED -> view.itemSelectedCallback()
        }
    }

    companion object {
        val WHAT_INVALIDATE: Int = 1000
        val WHAT_SMOOTH_SCROLL: Int = 2000
        val WHAT_ITEM_SELECTED: Int = 3000
    }
}