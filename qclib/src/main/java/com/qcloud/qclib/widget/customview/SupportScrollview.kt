package com.qcloud.qclib.widget.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.ScrollView

/**
 * 类说明：适配5.0以上ScrollView嵌套RecyclerView 滑动不顺畅的问题。
 * Author: Kuzan
 * Date: 2018/1/20 11:31.
 */
class SupportScrollview @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : ScrollView(context, attrs, defStyleAttr) {

    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

    private var downX: Int = 0
    private var downY: Int = 0

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = (ev.rawX).toInt()
                downY = (ev.rawY).toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val moveY = (ev.rawY).toInt()
                if (Math.abs(moveY - downY) > mTouchSlop) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}