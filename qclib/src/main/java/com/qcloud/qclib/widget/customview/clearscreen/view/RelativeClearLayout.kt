package com.qcloud.qclib.widget.customview.clearscreen.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout

import com.qcloud.qclib.widget.customview.clearscreen.ClearConstants
import com.qcloud.qclib.widget.customview.clearscreen.IClearEvent
import com.qcloud.qclib.widget.customview.clearscreen.IClearRootView
import com.qcloud.qclib.widget.customview.clearscreen.IPositionCallBack

/**
 * 类说明：封装相对布局
 * Author: Kuzan
 * Date: 2017/8/23 11:09.
 */
class RelativeClearLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle), IClearRootView {
    private val MIN_SCROLL_SIZE = 50
    private val LEFT_SIDE_X = 0
    private val RIGHT_SIDE_X = resources.displayMetrics.widthPixels

    private var mDownX: Int = 0
    private var mEndX: Int = 0
    private val mEndAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1.0f).setDuration(200)

    private var isCanScroll: Boolean = false
    private var isTouchWithAnimRunning: Boolean = false

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
        val offsetX = x - mDownX
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_MOVE -> {
                if (isGreaterThanMinSize(mDownX, x) && isCanScroll) {
                    mIPositionCallBack!!.onPositionChange(getPositionChangeX(offsetX), 0)
                    return true
                }
            }
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_UP -> {
                if (isGreaterThanMinSize(mDownX, x) && isCanScroll) {
                    mDownX = getPositionChangeX(offsetX)
                    fixPosition(offsetX)
                    mEndAnimator.start()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                isTouchWithAnimRunning = mEndAnimator.isRunning
                mDownX = x
            }
            MotionEvent.ACTION_MOVE -> {
                if (isGreaterThanMinSize(mDownX, x) && !isTouchWithAnimRunning) {
                    isCanScroll = true
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(event)
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

    private fun getPositionChangeX(offsetX: Int): Int {
        val absOffsetX = Math.abs(offsetX)
        return if (mOrientation == ClearConstants.Orientation.RIGHT) {
            absOffsetX - MIN_SCROLL_SIZE
        } else {
            RIGHT_SIDE_X - (absOffsetX - MIN_SCROLL_SIZE)
        }
    }

    private fun fixPosition(offsetX: Int) {
        val absOffsetX = Math.abs(offsetX)
        if (mOrientation == ClearConstants.Orientation.RIGHT && absOffsetX > RIGHT_SIDE_X / 3) {
            mEndX = RIGHT_SIDE_X
        } else if (mOrientation == ClearConstants.Orientation.LEFT && absOffsetX > RIGHT_SIDE_X / 3) {
            mEndX = LEFT_SIDE_X
        }
    }

    fun isGreaterThanMinSize(x1: Int, x2: Int): Boolean {
        return if (mOrientation == ClearConstants.Orientation.RIGHT) {
            x2 - x1 > MIN_SCROLL_SIZE
        } else {
            x1 - x2 > MIN_SCROLL_SIZE
        }
    }
}
