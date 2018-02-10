package com.qcloud.qclib.widget.swipeback

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.FloatRange
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.qcloud.qclib.R
import com.qcloud.qclib.utils.ColorUtil

/**
 * 类说明：侧滑返回
 * Author: Kuzan
 * Date: 2018/1/17 17:35.
 */
class SwipeBackLayout @JvmOverloads constructor(
        private val mContext: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = R.attr.SwipeBackLayoutStyle): FrameLayout(mContext, attrs) {

    private var mEdgeFlag: Int = 0
    private var mScrollThreshold = DEFAULT_SCROLL_THRESHOLD

    private var mActivity: Activity? = null
    private var mDragHelper: SwipeViewDragHelper? = null

    var isEnableGesture = true
    var mContentView: View? = null

    private var mScrollPercent: Float = 0.toFloat()
    private var mContentLeft: Int = 0
    private var mContentTop: Int = 0

    private val mListeners: MutableList<SwipeListener> = ArrayList()

    private var mShadowLeft: Drawable? = null
    private var mShadowRight: Drawable? = null
    private var mShadowBottom: Drawable? = null

    private var mScrimOpacity: Float = 0.toFloat()
    private var mScrimColor = DEFAULT_SCRIM_COLOR

    private var mInLayout = false

    private val mTmpRect = Rect()

    private var mTrackingEdge: Int = 0

    init {
        mDragHelper = SwipeViewDragHelper.Companion.create(this, ViewDragCallback())
        val a = context.obtainStyledAttributes(attrs, R.styleable.SwipeBackLayout, defStyle, R.style.SwipeBackLayout)
        val edgeSize = a.getDimensionPixelSize(R.styleable.SwipeBackLayout_edge_size, -1)
        if (edgeSize > 0)
            setEdgeSize(edgeSize)
        val mode = EDGE_FLAGS[a.getInt(R.styleable.SwipeBackLayout_edge_flag, 0)]
        setEdgeTrackingEnabled(mode)

        val shadowLeft = a.getResourceId(R.styleable.SwipeBackLayout_shadow_left, R.drawable.shadow_left)
        val shadowRight = a.getResourceId(R.styleable.SwipeBackLayout_shadow_right, R.drawable.shadow_right)
        val shadowBottom = a.getResourceId(R.styleable.SwipeBackLayout_shadow_bottom, R.drawable.shadow_bottom)
        setShadow(shadowLeft, EDGE_LEFT)
        setShadow(shadowRight, EDGE_RIGHT)
        setShadow(shadowBottom, EDGE_BOTTOM)
        a.recycle()
        val density = resources.displayMetrics.density
        val minVel = MIN_FLING_VELOCITY * density
        mDragHelper!!.mMinVelocity = minVel
        mDragHelper!!.mMaxVelocity = minVel * 2f
    }

    fun setSensitivity(context: Context, sensitivity: Float) {
        mDragHelper!!.setSensitivity(context, sensitivity)
    }

    fun setEdgeTrackingEnabled(edgeFlags: Int) {
        mEdgeFlag = edgeFlags
        mDragHelper!!.mTrackingEdges = mEdgeFlag
    }

    fun setScrimColor(color: Int) {
        mScrimColor = color
        invalidate()
    }

    fun setEdgeSize(size: Int) {
        mDragHelper!!.mEdgeSize = size
    }

    fun setSwipeListener(listener: SwipeListener) {
        addSwipeListener(listener)
    }

    fun addSwipeListener(listener: SwipeListener) {
        mListeners.add(listener)
    }

    fun removeSwipeListener(listener: SwipeListener) {
        mListeners.remove(listener)
    }

    fun setScrollThresHold(@FloatRange(from = 0.0, to = 1.0)threshold: Float) {
        if (threshold >= 1.0f || threshold <= 0) {
            throw IllegalArgumentException("Threshold value should be between 0 and 1.0")
        }
        mScrollThreshold = threshold
    }

    fun setShadow(shadow: Drawable?, edgeFlag: Int) {
        when {
            edgeFlag and EDGE_LEFT != 0 -> mShadowLeft = shadow
            edgeFlag and EDGE_RIGHT != 0 -> mShadowRight = shadow
            edgeFlag and EDGE_BOTTOM != 0 -> mShadowBottom = shadow
        }
        invalidate()
    }

    fun setShadow(resId: Int, edgeFlag: Int) {
        setShadow(ContextCompat.getDrawable(mContext, resId), edgeFlag)
    }

    fun scrollToFinishActivity() {
        val childWidth = mContentView!!.width
        val childHeight = mContentView!!.height

        var left = 0
        var top = 0
        if (mEdgeFlag and EDGE_LEFT != 0) {
            left = childWidth + mShadowLeft!!.intrinsicWidth + OVERSCROLL_DISTANCE
            mTrackingEdge = EDGE_LEFT
        } else if (mEdgeFlag and EDGE_RIGHT != 0) {
            left = -childWidth - mShadowRight!!.intrinsicWidth - OVERSCROLL_DISTANCE
            mTrackingEdge = EDGE_RIGHT
        } else if (mEdgeFlag and EDGE_BOTTOM != 0) {
            top = -childHeight - mShadowBottom!!.intrinsicHeight - OVERSCROLL_DISTANCE
            mTrackingEdge = EDGE_BOTTOM
        }

        mDragHelper!!.smoothSlideViewTo(mContentView!!, left, top)
        invalidate()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (!isEnableGesture) {
            return false
        }
        try {
            return mDragHelper!!.shouldInterceptTouchEvent(event)
        } catch (e: ArrayIndexOutOfBoundsException) {
            // FIXME: handle exception
            // issues #9
            return false
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnableGesture) {
            return false
        }
        mDragHelper!!.processTouchEvent(event)
        return true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        mInLayout = true
        if (mContentView != null)
            mContentView!!.layout(mContentLeft, mContentTop,
                    mContentLeft + mContentView!!.measuredWidth,
                    mContentTop + mContentView!!.measuredHeight)
        mInLayout = false
    }

    override fun requestLayout() {
        if (!mInLayout) {
            super.requestLayout()
        }
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val drawContent = child === mContentView

        val ret = super.drawChild(canvas, child, drawingTime)
        if (mScrimOpacity > 0 && drawContent
                && mDragHelper!!.mDragState != SwipeViewDragHelper.STATE_IDLE) {
            drawShadow(canvas, child)
            drawScrim(canvas, child)
        }
        return ret
    }

    private fun drawScrim(canvas: Canvas, child: View) {
        val baseAlpha = (mScrimColor and -0x1000000).ushr(24)
        val alpha = (baseAlpha * mScrimOpacity).toInt()
        val color = alpha shl 24 or (mScrimColor and 0xffffff)

        if (mTrackingEdge and EDGE_LEFT != 0) {
            canvas.clipRect(0, 0, child.left, height)
        } else if (mTrackingEdge and EDGE_RIGHT != 0) {
            canvas.clipRect(child.right, 0, right, height)
        } else if (mTrackingEdge and EDGE_BOTTOM != 0) {
            canvas.clipRect(child.left, child.bottom, right, height)
        }
        canvas.drawColor(color)
    }

    private fun drawShadow(canvas: Canvas, child: View) {
        val childRect = mTmpRect
        child.getHitRect(childRect)

        if (mEdgeFlag and EDGE_LEFT != 0) {
            mShadowLeft!!.setBounds(childRect.left - mShadowLeft!!.intrinsicWidth, childRect.top,
                    childRect.left, childRect.bottom)
            mShadowLeft!!.alpha = (mScrimOpacity * FULL_ALPHA).toInt()
            mShadowLeft!!.draw(canvas)
        }

        if (mEdgeFlag and EDGE_RIGHT != 0) {
            mShadowRight!!.setBounds(childRect.right, childRect.top,
                    childRect.right + mShadowRight!!.intrinsicWidth, childRect.bottom)
            mShadowRight!!.alpha = (mScrimOpacity * FULL_ALPHA).toInt()
            mShadowRight!!.draw(canvas)
        }

        if (mEdgeFlag and EDGE_BOTTOM != 0) {
            mShadowBottom!!.setBounds(childRect.left, childRect.bottom, childRect.right,
                    childRect.bottom + mShadowBottom!!.intrinsicHeight)
            mShadowBottom!!.alpha = (mScrimOpacity * FULL_ALPHA).toInt()
            mShadowBottom!!.draw(canvas)
        }
    }

    fun attachToActivity(activity: Activity) {
        mActivity = activity
        val a = activity.theme.obtainStyledAttributes(intArrayOf(android.R.attr.windowBackground))
        val background = a.getResourceId(0, 0)
        a.recycle()

        val decor = activity.window.decorView as ViewGroup
        val decorChild = decor.getChildAt(0) as ViewGroup
        decorChild.setBackgroundResource(background)
        decor.removeView(decorChild)
        addView(decorChild)
        mContentView = decorChild
        decor.addView(this)
    }

    override fun computeScroll() {
        mScrimOpacity = 1 - mScrollPercent
        if (mDragHelper!!.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private inner class ViewDragCallback : SwipeCallback() {
        private var mIsScrollOverValid: Boolean = false

        override fun tryCaptureView(child: View, i: Int): Boolean {
            val ret = mDragHelper!!.isEdgeTouched(mEdgeFlag, i)
            if (ret) {
                when {
                    mDragHelper!!.isEdgeTouched(EDGE_LEFT, i) -> mTrackingEdge = EDGE_LEFT
                    mDragHelper!!.isEdgeTouched(EDGE_RIGHT, i) -> mTrackingEdge = EDGE_RIGHT
                    mDragHelper!!.isEdgeTouched(EDGE_BOTTOM, i) -> mTrackingEdge = EDGE_BOTTOM
                }
                if (mListeners.isNotEmpty()) {
                    for (listener in mListeners) {
                        listener.onEdgeTouch(mTrackingEdge)
                    }
                }
                mIsScrollOverValid = true
            }
            return ret
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return mEdgeFlag and (EDGE_LEFT or EDGE_RIGHT)
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return mEdgeFlag and EDGE_BOTTOM
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            if (mTrackingEdge and EDGE_LEFT != 0) {
                mScrollPercent = Math.abs(left.toFloat() / (mContentView!!.width + mShadowLeft!!.intrinsicWidth))
            } else if (mTrackingEdge and EDGE_RIGHT != 0) {
                mScrollPercent = Math.abs(left.toFloat() / (mContentView!!.width + mShadowRight!!.intrinsicWidth))
            } else if (mTrackingEdge and EDGE_BOTTOM != 0) {
                mScrollPercent = Math.abs(top.toFloat() / (mContentView!!.height + mShadowBottom!!.intrinsicHeight))
            }
            mContentLeft = left
            mContentTop = top
            invalidate()
            if (mScrollPercent < mScrollThreshold && !mIsScrollOverValid) {
                mIsScrollOverValid = true
            }
            if (mListeners.isNotEmpty()
                    && mDragHelper!!.mDragState == Companion.STATE_DRAGGING
                    && mScrollPercent >= mScrollThreshold && mIsScrollOverValid) {
                mIsScrollOverValid = false
                for (listener in mListeners) {
                    listener.onScrollOverThreshold()
                }
            }

            if (mScrollPercent >= 1) {
                if (!mActivity!!.isFinishing)
                    mActivity!!.finish()
            }
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val childWidth = releasedChild.width
            val childHeight = releasedChild.height

            var left = 0
            var top = 0

            if (mTrackingEdge and EDGE_LEFT != 0) {
                left = if (xvel > 0f || xvel == 0f && mScrollPercent > mScrollThreshold)
                    childWidth + mShadowLeft!!.intrinsicWidth + OVERSCROLL_DISTANCE
                else
                    0
            } else if (mTrackingEdge and EDGE_RIGHT != 0) {
                left = if (xvel < 0 || xvel == 0f && mScrollPercent > mScrollThreshold)
                    -(childWidth + mShadowLeft!!.intrinsicWidth + OVERSCROLL_DISTANCE)
                else
                    0
            } else if (mTrackingEdge and EDGE_BOTTOM != 0) {
                top = if (yvel < 0 || yvel == 0f && mScrollPercent > mScrollThreshold)
                    -(childHeight + mShadowBottom!!.intrinsicHeight + OVERSCROLL_DISTANCE)
                 else
                    0
            }

            mDragHelper!!.settleCapturedViewAt(left, top)
            invalidate()
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            var ret = 0
            if (mTrackingEdge and EDGE_LEFT != 0) {
                ret = Math.min(child.width, Math.max(left, 0))
            } else if (mTrackingEdge and EDGE_RIGHT != 0) {
                ret = Math.min(0, Math.max(left, -child.width))
            }
            return ret
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            var ret = 0
            if (mTrackingEdge and EDGE_BOTTOM != 0) {
                ret = Math.min(0, Math.max(top, -child.height))
            }
            return ret
        }

        override fun onViewDragStateChanged(state: Int) {
            super.onViewDragStateChanged(state)
            if (mListeners.isNotEmpty()) {
                for (listener in mListeners) {
                    listener.onScrollStateChange(state, mScrollPercent)
                }
            }
        }
    }

    companion object {
        /** 最小触摸速度 */
        private val MIN_FLING_VELOCITY = 400    // dp
        private val DEFAULT_SCRIM_COLOR = ColorUtil.parseColor("0x99000000")
        private val FULL_ALPHA = 255

        private val EDGE_LEFT = SwipeViewDragHelper.EDGE_LEFT
        private val EDGE_RIGHT = SwipeViewDragHelper.EDGE_RIGHT
        private val EDGE_BOTTOM = SwipeViewDragHelper.EDGE_BOTTOM
        private val EDGE_ALL = Companion.EDGE_LEFT or Companion.EDGE_RIGHT or Companion.EDGE_BOTTOM

        private val STATE_IDLE = SwipeViewDragHelper.STATE_IDLE
        private val STATE_DRAGGING = SwipeViewDragHelper.STATE_DRAGGING
        private val STATE_SETTLING = SwipeViewDragHelper.STATE_SETTLING

        private val DEFAULT_SCROLL_THRESHOLD = 0.3f
        private val OVERSCROLL_DISTANCE = 10

        private val EDGE_FLAGS = intArrayOf(EDGE_LEFT, EDGE_RIGHT, EDGE_BOTTOM, EDGE_ALL)
    }
}