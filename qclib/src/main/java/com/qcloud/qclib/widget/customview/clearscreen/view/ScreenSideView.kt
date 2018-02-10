package com.qcloud.qclib.widget.customview.clearscreen.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import com.qcloud.qclib.widget.customview.clearscreen.ClearConstants
import com.qcloud.qclib.widget.customview.clearscreen.IClearEvent
import com.qcloud.qclib.widget.customview.clearscreen.IClearRootView
import com.qcloud.qclib.widget.customview.clearscreen.IPositionCallBack

/**
 * 类说明：滑动布局
 * Author: Kuzan
 * Date: 2017/8/23 10:58.
 */
class ScreenSideView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : LinearLayout(context, attrs, defStyle), IClearRootView {
    private val MIN_SCROLL_SIZE = 30
    private val LEFT_SIDE_X = 0
    private val RIGHT_SIDE_X = resources.displayMetrics.widthPixels

    private var mDownX: Int = 0
    private var mEndX: Int = 0
    private val mEndAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1.0f).setDuration(200)

    private var isCanScroll: Boolean = false

    private var mOrientation: ClearConstants.Orientation? = null

    private var mIPositionCallBack: IPositionCallBack? = null
    private var mIClearEvent: IClearEvent? = null

    init {
        initEndAnimator()
    }

    private fun initEndAnimator() {
        mEndAnimator.addUpdateListener { valueAnimator ->
            val factor = valueAnimator.animatedValue as Float
            val diffX = mEndX - mDownX
            mIPositionCallBack!!.onPositionChange((mDownX + diffX * factor).toInt(), 0)
        }
        mEndAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (mOrientation == ClearConstants.Orientation.RIGHT && mEndX == RIGHT_SIDE_X) {
                    mIClearEvent!!.onClearEnd()
                    mOrientation = ClearConstants.Orientation.LEFT
                } else if (mOrientation == ClearConstants.Orientation.LEFT && mEndX == LEFT_SIDE_X) {
                    mIClearEvent!!.onRecovery()
                    mOrientation = ClearConstants.Orientation.RIGHT
                }
                mDownX = mEndX
                isCanScroll = false
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isScrollFromSide(x)) {
                    isCanScroll = true
                    return true
                }
                if (isGreaterThanMinSize(x) && isCanScroll) {
                    mIPositionCallBack!!.onPositionChange(getRealTimeX(x), 0)
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isGreaterThanMinSize(x) && isCanScroll) {
                    mIPositionCallBack!!.onPositionChange(getRealTimeX(x), 0)
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isGreaterThanMinSize(x) && isCanScroll) {
                    mDownX = getRealTimeX(x)
                    fixPosition()
                    mEndAnimator.start()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun setClearSide(orientation: ClearConstants.Orientation) {
        mOrientation = orientation
    }

    override fun setIPositionCallBack(callBack: IPositionCallBack) {
        mIPositionCallBack = callBack
    }

    override fun setIClearEvent(event: IClearEvent) {
        mIClearEvent = event
    }

    private fun getRealTimeX(x: Int): Int {
        return if (mOrientation == ClearConstants.Orientation.RIGHT && mDownX > RIGHT_SIDE_X / 3 || mOrientation == ClearConstants.Orientation.LEFT && mDownX > RIGHT_SIDE_X * 2 / 3) {
            x + MIN_SCROLL_SIZE
        } else {
            x - MIN_SCROLL_SIZE
        }
    }

    private fun fixPosition() {
        if (mOrientation == ClearConstants.Orientation.RIGHT && mDownX > RIGHT_SIDE_X / 3) {
            mEndX = RIGHT_SIDE_X
        } else if (mOrientation == ClearConstants.Orientation.LEFT && mDownX < RIGHT_SIDE_X * 2 / 3) {
            mEndX = LEFT_SIDE_X
        }
    }

    private fun isGreaterThanMinSize(x: Int): Boolean {
        val absX = Math.abs(mDownX - x)
        return absX > MIN_SCROLL_SIZE
    }

    private fun isScrollFromSide(x: Int): Boolean {
        return x <= LEFT_SIDE_X + MIN_SCROLL_SIZE && mOrientation == ClearConstants.Orientation.RIGHT || x > RIGHT_SIDE_X - MIN_SCROLL_SIZE && mOrientation == ClearConstants.Orientation.LEFT
    }
}
