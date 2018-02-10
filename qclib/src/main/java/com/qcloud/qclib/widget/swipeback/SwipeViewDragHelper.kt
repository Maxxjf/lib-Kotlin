package com.qcloud.qclib.widget.swipeback

import android.content.Context
import android.support.annotation.NonNull
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.VelocityTrackerCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ScrollerCompat
import android.view.*
import android.view.animation.Interpolator
import java.util.*

/**
 * 类说明：写入自定义ViewGroup实用类
 *          提供了许多有用的操作和状态
 *          允许用户拖动视图重新定位在它们的父ViewGroup
 * Author: Kuzan
 * Date: 2018/1/17 14:28.
 */
class SwipeViewDragHelper private constructor(
        private val mContext: Context,
        private val mParentView: ViewGroup?,
        private val mCallback: SwipeCallback?) {

    /** 当前拖动状态 */
    var mDragState = STATE_IDLE
    /** 开始拖动前的距离 */
    var mTouchSlop = 0
    /** 跟踪最后的位置 */
    /** 拖动最大速度 */
    var mMaxVelocity: Float = 0.toFloat()
    /** 拖动最小速度 */
    var mMinVelocity: Float = 0.toFloat()
    /**
     * 是否启用对父视图的选定边缘跟踪。
     * @see #EDGE_LEFT
     * @see #EDGE_TOP
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     * */
    var mTrackingEdges: Int = EDGE_ALL
    /** 边缘大小 */
    var mEdgeSize: Int = 0
    /** 当前捕获的视图 */
    var mCapturedView: View? = null
    /**当前拖动捕获视图的的id*/
    private var mActivePointerId = INVALID_POINTER

    private var mInitialMotionX: FloatArray? = null
    private var mInitialMotionY: FloatArray? = null
    private var mLastMotionX: FloatArray? = null
    private var mLastMotionY: FloatArray? = null
    private var mInitialEdgeTouched: IntArray? = null
    private var mEdgeDragsInProgress: IntArray? = null
    private var mEdgeDragsLocked: IntArray? = null

    private var mPointersDown: Int = 0
    private var mVelocityTracker: VelocityTracker? = null

    private var mScroller: ScrollerCompat? = null

    private var mReleaseInProgress: Boolean = false

    private val mSetIdleRunnable = Runnable {
        setDragState(STATE_IDLE)
    }

    init {
        if (mParentView == null) {
            throw IllegalArgumentException("Parent view may not be null")
        }
        if (mCallback == null) {
            throw IllegalArgumentException("Callback may not be null")
        }
        val vc = ViewConfiguration.get(mContext)
        val density = mContext.resources.displayMetrics.density
        mEdgeSize = (EDGE_SIZE * density + 0.5f).toInt()

        mTouchSlop = vc.scaledTouchSlop
        mMaxVelocity = vc.scaledMaximumFlingVelocity.toFloat()
        mMinVelocity = vc.scaledMinimumFlingVelocity.toFloat()
        mScroller = ScrollerCompat.create(mContext, sInterpolator)
    }

    /**
     * 设置灵敏度
     *
     * @param context
     * @param sensitivity
     * */
    fun setSensitivity(context: Context, sensitivity: Float) {
        val s = Math.max(0f, Math.min(1.0f, sensitivity))
        val viewConfiguration = ViewConfiguration.get(context)
        mTouchSlop = (viewConfiguration.scaledTouchSlop * (1 / s)).toInt()
    }

    /**
     * 捕获在父视图中拖动的特定子视图
     * */
    fun captureChildView(@NonNull childView: View, activePointerId: Int) {
        if (childView.parent != mParentView) {
            throw IllegalArgumentException("captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view $mParentView ")
        }
        mCapturedView = childView
        mActivePointerId = activePointerId
        mCallback?.onViewCaptured(childView, activePointerId)
        setDragState(STATE_DRAGGING)
    }

    fun cancel() {
        mActivePointerId = INVALID_POINTER
        clearMotionHistory()

        mVelocityTracker?.recycle()
        mVelocityTracker = null
    }

    fun abort() {
        cancel()
        if (mDragState == STATE_SETTLING && mScroller != null) {
            val oldX = mScroller!!.currX
            val oldY = mScroller!!.currY
            mScroller?.abortAnimation()
            val newX = mScroller!!.currX
            val newY = mScroller!!.currY
            mCallback?.onViewPositionChanged(mCapturedView!!, newX, newY, newX - oldX, newY - oldY)
        }
        setDragState(STATE_IDLE)
    }

    fun smoothSlideViewTo(@NonNull child: View, finalLeft: Int, finalTop: Int): Boolean {
        mCapturedView = child
        mActivePointerId = INVALID_POINTER
        return forceSettleCapturedViewAt(finalLeft, finalTop, 0, 0)
    }

    fun settleCapturedViewAt(finalLeft: Int, finalTop: Int): Boolean {
        if (!mReleaseInProgress) {
            throw IllegalStateException("Cannot settleCapturedViewAt outside of a call to " + "Callback#onViewReleased")
        }

        return forceSettleCapturedViewAt(finalLeft, finalTop,
                VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId).toInt(),
                VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId).toInt())
    }

    /**
     * 在给定（左，顶）位置解决捕获的视图。
     * */
    private fun forceSettleCapturedViewAt(finalLeft: Int, finalTop: Int, xvel: Int, yvel: Int): Boolean {
        val startLeft = mCapturedView!!.left
        val startTop = mCapturedView!!.top
        val dx = finalLeft - startLeft
        val dy = finalTop - startTop

        if (dx == 0 && dy == 0) {
            // Nothing to do. Send callbacks, be done.
            mScroller?.abortAnimation()
            setDragState(STATE_IDLE)
            return false
        }

        val duration = computeSettleDuration(mCapturedView!!, dx, dy, xvel, yvel)
        mScroller?.startScroll(startLeft, startTop, dx, dy, duration)
        setDragState(STATE_SETTLING)

        return true
    }

    private fun computeSettleDuration(@NonNull child: View, dx: Int, dy: Int, xvel: Int, yvel: Int): Int {
        val xVel = clampMag(xvel, mMinVelocity.toInt(), mMaxVelocity.toInt())
        val yVel = clampMag(yvel, mMinVelocity.toInt(), mMaxVelocity.toInt())

        val absDx = Math.abs(dx)
        val absDy = Math.abs(dy)
        val absXVel = Math.abs(xVel)
        val absYVel = Math.abs(yVel)
        val addedVel = absXVel + absYVel
        val addedDistance = absDx + absDy

        val xweight = if (xvel != 0)
            absXVel.toFloat() / addedVel
        else
            absDx.toFloat() / addedDistance

        val yweight = if (yvel != 0)
            absYVel.toFloat() / addedVel
        else
            absDy.toFloat() / addedDistance

        val xduration = computeAxisDuration(dx, xvel, mCallback!!.getViewHorizontalDragRange(child))
        val yduration = computeAxisDuration(dy, yvel, mCallback!!.getViewVerticalDragRange(child))

        return (xduration * xweight + yduration * yweight).toInt()
    }

    private fun computeAxisDuration(delta: Int, velocity: Int, motionRange: Int): Int {
        var velocity = velocity
        if (delta == 0) {
            return 0
        }

        val width = mParentView!!.width
        val halfWidth = width / 2
        val distanceRatio = Math.min(1f, Math.abs(delta).toFloat() / width)
        val distance = halfWidth + halfWidth * distanceInfluenceForSnapDuration(distanceRatio)

        velocity = Math.abs(velocity)
        val duration = if (velocity > 0) {
            4 * Math.round(1000 * Math.abs(distance / velocity))
        } else {
            val range = Math.abs(delta).toFloat() / motionRange
            ((range + 1) * BASE_SETTLE_DURATION).toInt()
        }
        return Math.min(duration, MAX_SETTLE_DURATION)
    }

    private fun clampMag(value: Int, absMin: Int, absMax: Int): Int {
        val absValue = Math.abs(value)

        if (absValue < absMin) {
            return 0
        }
        if (absValue > absMax) {
            return if (value > 0) absMax else -absMax
        }
        return value
    }

    private fun distanceInfluenceForSnapDuration(f: Float): Float {
        var f2 = f
        f2 -= 0.5f // center the values about 0.
        f2 *= (0.3f * Math.PI / 2.0f).toFloat()
        return Math.sin(f.toDouble()).toFloat()
    }

    fun flingCapturedView(minLeft: Int, minTop: Int, maxLeft: Int, maxTop: Int) {
        if (!mReleaseInProgress) {
            throw IllegalStateException("Cannot flingCapturedView outside of a call to Callback#onViewReleased")
        }

        mScroller?.fling(mCapturedView!!.left, mCapturedView!!.top,
                VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId).toInt(),
                VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId).toInt(),
                minLeft, maxLeft, minTop, maxTop)

        setDragState(STATE_SETTLING)
    }

    fun continueSettling(deferCallbacks: Boolean): Boolean {
        if (mDragState == STATE_SETTLING) {
            var keepGoing = mScroller!!.computeScrollOffset()
            val x = mScroller!!.currX
            val y = mScroller!!.currY
            val dx = x - mCapturedView!!.left
            val dy = y - mCapturedView!!.top

            if (dx != 0) {
                mCapturedView!!.offsetLeftAndRight(dx)
            }
            if (dy != 0) {
                mCapturedView!!.offsetTopAndBottom(dy)
            }

            if (dx != 0 || dy != 0) {
                mCallback?.onViewPositionChanged(mCapturedView!!, x, y, dx, dy)
            }

            if (keepGoing && x == mScroller!!.finalX && y == mScroller!!.finalY) {
                mScroller!!.abortAnimation()
                keepGoing = mScroller!!.isFinished
            }

            if (!keepGoing) {
                if (deferCallbacks) {
                    mParentView?.post(mSetIdleRunnable)
                } else {
                    setDragState(STATE_IDLE)
                }
            }
        }

        return mDragState == STATE_SETTLING
    }

    private fun dispatchViewReleased(xvel: Float, yvel: Float) {
        mReleaseInProgress = true
        mCallback?.onViewReleased(mCapturedView!!, xvel, yvel)
        mReleaseInProgress = false

        if (mDragState == STATE_DRAGGING) {
            setDragState(STATE_IDLE)
        }
    }

    private fun clearMotionHistory() {
        if (mInitialMotionX == null) {
            return
        }
        Arrays.fill(mInitialMotionX, 0f)
        Arrays.fill(mInitialMotionY, 0f)
        Arrays.fill(mLastMotionX, 0f)
        Arrays.fill(mLastMotionY, 0f)
        Arrays.fill(mInitialEdgeTouched, 0)
        Arrays.fill(mEdgeDragsInProgress, 0)
        Arrays.fill(mEdgeDragsLocked, 0)
        mPointersDown = 0
    }

    private fun clearMotionHistory(pointerId: Int) {
        if (mInitialMotionX == null) {
            return
        }
        mInitialMotionX!![pointerId] = 0f
        mInitialMotionY!![pointerId] = 0f
        mLastMotionX!![pointerId] = 0f
        mLastMotionY!![pointerId] = 0f
        mInitialEdgeTouched!![pointerId] = 0
        mEdgeDragsInProgress!![pointerId] = 0
        mEdgeDragsLocked!![pointerId] = 0

        mPointersDown = mPointersDown and (1 shl pointerId).inv()
    }

    private fun ensureMotionHistorySizeForId(pointerId: Int) {
        if (mInitialMotionX == null || mInitialMotionX!!.size <= pointerId) {
            val imx = FloatArray(pointerId + 1)
            val imy = FloatArray(pointerId + 1)
            val lmx = FloatArray(pointerId + 1)
            val lmy = FloatArray(pointerId + 1)
            val iit = IntArray(pointerId + 1)
            val edip = IntArray(pointerId + 1)
            val edl = IntArray(pointerId + 1)

            if (mInitialMotionX != null) {
                System.arraycopy(mInitialMotionX, 0, imx, 0, mInitialMotionX!!.size)
                System.arraycopy(mInitialMotionY, 0, imy, 0, mInitialMotionY!!.size)
                System.arraycopy(mLastMotionX, 0, lmx, 0, mLastMotionX!!.size)
                System.arraycopy(mLastMotionY, 0, lmy, 0, mLastMotionY!!.size)
                System.arraycopy(mInitialEdgeTouched, 0, iit, 0, mInitialEdgeTouched!!.size)
                System.arraycopy(mEdgeDragsInProgress, 0, edip, 0, mEdgeDragsInProgress!!.size)
                System.arraycopy(mEdgeDragsLocked, 0, edl, 0, mEdgeDragsLocked!!.size)
            }
            mInitialMotionX = imx
            mInitialMotionY = imy
            mLastMotionX = lmx
            mLastMotionY = lmy
            mInitialEdgeTouched = iit
            mEdgeDragsInProgress = edip
            mEdgeDragsLocked = edl
        }
    }

    fun saveInitialMotion(x: Float, y: Float, pointerId: Int) {
        ensureMotionHistorySizeForId(pointerId)
        mInitialMotionX!![pointerId] = x
        mInitialMotionY!![pointerId] = x
        mLastMotionX!![pointerId] = x
        mLastMotionY!![pointerId] = x
        mInitialEdgeTouched!![pointerId] = getEdgeTouched(x.toInt(), y.toInt())

        mPointersDown = mPointersDown or (1 shl pointerId)
    }

    private fun saveLastMotion(ev: MotionEvent) {
        val pointerCount = MotionEventCompat.getPointerCount(ev)
        for (i in 0 until pointerCount) {
            val pointerId = MotionEventCompat.getPointerId(ev, i)
            val x = MotionEventCompat.getX(ev, i)
            val y = MotionEventCompat.getY(ev, i)
            mLastMotionX!![pointerId] = x
            mLastMotionY!![pointerId] = y
        }
    }

    fun isPointerDown(pointerId: Int): Boolean {
        return mPointersDown and (1 shl pointerId) != 0
    }

    private fun setDragState(state: Int) {
        if (mDragState != state) {
            mDragState = state
            mCallback?.onViewDragStateChanged(state)
            if (state == STATE_IDLE) {
                mCapturedView = null
            }
        }
    }

    internal fun tryCaptureViewForDrag(toCapture: View?, pointerId: Int): Boolean {
        if (toCapture === mCapturedView && mActivePointerId == pointerId) {
            // Already done!
            return true
        }
        if (toCapture != null && mCallback!!.tryCaptureView(toCapture, pointerId)) {
            mActivePointerId = pointerId
            captureChildView(toCapture, pointerId)
            return true
        }
        return false
    }

    protected fun canScroll(v: View, checkV: Boolean, dx: Int, dy: Int, x: Int, y: Int): Boolean {
        if (v is ViewGroup) {
            val scrollX = v.getScrollX()
            val scrollY = v.getScrollY()
            val count = v.childCount
            // Count backwards - let topmost views consume scroll distance
            // first.
            (count - 1 downTo 0)
                    .map { v.getChildAt(it) }
                    .filter { x + scrollX >= it.left && x + scrollX < it.right && y + scrollY >= it.top && y + scrollY < it.bottom && canScroll(it, true, dx, dy, x + scrollX - it.left, y + scrollY - it.top) }
                    .forEach { return true }
        }

        return checkV && (ViewCompat.canScrollHorizontally(v, -dx) || ViewCompat.canScrollVertically(v, -dy))
    }

    fun shouldInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(ev)
        val actionIndex = MotionEventCompat.getActionIndex(ev)

        if (action == MotionEvent.ACTION_DOWN) {
            // Reset things for a new event stream, just in case we didn't get
            // the whole previous stream.
            cancel()
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(ev)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x
                val y = ev.y
                val pointerId = MotionEventCompat.getPointerId(ev, 0)
                saveInitialMotion(x, y, pointerId)

                val toCapture = findTopChildUnder(x.toInt(), y.toInt())

                // Catch a settling view if possible.
                if (toCapture === mCapturedView && mDragState == STATE_SETTLING) {
                    tryCaptureViewForDrag(toCapture, pointerId)
                }

                val edgesTouched = mInitialEdgeTouched!![pointerId]
                if (edgesTouched and mTrackingEdges != 0) {
                    mCallback?.onEdgeTouched(edgesTouched and mTrackingEdges, pointerId)
                }
            }

            MotionEventCompat.ACTION_POINTER_DOWN -> {
                val pointerId = MotionEventCompat.getPointerId(ev, actionIndex)
                val x = MotionEventCompat.getX(ev, actionIndex)
                val y = MotionEventCompat.getY(ev, actionIndex)

                saveInitialMotion(x, y, pointerId)

                // A ViewDragHelper can only manipulate one view at a time.
                if (mDragState == STATE_IDLE) {
                    val edgesTouched = mInitialEdgeTouched!![pointerId]
                    if (edgesTouched and mTrackingEdges != 0) {
                        mCallback?.onEdgeTouched(edgesTouched and mTrackingEdges, pointerId)
                    }
                } else if (mDragState == STATE_SETTLING) {
                    // Catch a settling view if possible.
                    val toCapture = findTopChildUnder(x.toInt(), y.toInt())
                    if (toCapture === mCapturedView) {
                        tryCaptureViewForDrag(toCapture, pointerId)
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                // First to cross a touch slop over a draggable view wins. Also
                // report edge drags.
                val pointerCount = MotionEventCompat.getPointerCount(ev)
                for (i in 0 until pointerCount) {
                    val pointerId = MotionEventCompat.getPointerId(ev, i)
                    val x = MotionEventCompat.getX(ev, i)
                    val y = MotionEventCompat.getY(ev, i)
                    val dx = x - mInitialMotionX!![pointerId]
                    val dy = y - mInitialMotionY!![pointerId]

                    reportNewEdgeDrags(dx, dy, pointerId)
                    if (mDragState == STATE_DRAGGING) {
                        // Callback might have started an edge drag
                        break
                    }

                    val toCapture = findTopChildUnder(x.toInt(), y.toInt())
                    if (toCapture != null && checkTouchSlop(toCapture, dx, dy)
                            && tryCaptureViewForDrag(toCapture, pointerId)) {
                        break
                    }
                }
                saveLastMotion(ev)
            }

            MotionEventCompat.ACTION_POINTER_UP -> {
                val pointerId = MotionEventCompat.getPointerId(ev, actionIndex)
                clearMotionHistory(pointerId)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                cancel()
            }
        }

        return mDragState == STATE_DRAGGING
    }

    fun processTouchEvent(ev: MotionEvent) {
        val action = MotionEventCompat.getActionMasked(ev)
        val actionIndex = MotionEventCompat.getActionIndex(ev)

        if (action == MotionEvent.ACTION_DOWN) {
            // Reset things for a new event stream, just in case we didn't get
            // the whole previous stream.
            cancel()
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(ev)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x
                val y = ev.y
                val pointerId = MotionEventCompat.getPointerId(ev, 0)
                val toCapture = findTopChildUnder(x.toInt(), y.toInt())

                saveInitialMotion(x, y, pointerId)

                // Since the parent is already directly processing this touch
                // event,
                // there is no reason to delay for a slop before dragging.
                // Start immediately if possible.
                tryCaptureViewForDrag(toCapture, pointerId)

                val edgesTouched = mInitialEdgeTouched!![pointerId]
                if (edgesTouched and mTrackingEdges != 0) {
                    mCallback?.onEdgeTouched(edgesTouched and mTrackingEdges, pointerId)
                }
            }

            MotionEventCompat.ACTION_POINTER_DOWN -> {
                val pointerId = MotionEventCompat.getPointerId(ev, actionIndex)
                val x = MotionEventCompat.getX(ev, actionIndex)
                val y = MotionEventCompat.getY(ev, actionIndex)

                saveInitialMotion(x, y, pointerId)

                // A ViewDragHelper can only manipulate one view at a time.
                if (mDragState == STATE_IDLE) {
                    // If we're idle we can do anything! Treat it like a normal
                    // down event.

                    val toCapture = findTopChildUnder(x.toInt(), y.toInt())
                    tryCaptureViewForDrag(toCapture, pointerId)

                    val edgesTouched = mInitialEdgeTouched!![pointerId]
                    if (edgesTouched and mTrackingEdges != 0) {
                        mCallback?.onEdgeTouched(edgesTouched and mTrackingEdges, pointerId)
                    }
                } else if (isCapturedViewUnder(x.toInt(), y.toInt())) {
                    // We're still tracking a captured view. If the same view is
                    // under this
                    // point, we'll swap to controlling it with this pointer
                    // instead.
                    // (This will still work if we're "catching" a settling
                    // view.)

                    tryCaptureViewForDrag(mCapturedView, pointerId)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (mDragState == STATE_DRAGGING) {
                    val index = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                    val x = MotionEventCompat.getX(ev, index)
                    val y = MotionEventCompat.getY(ev, index)
                    val idx = (x - mLastMotionX!![mActivePointerId]).toInt()
                    val idy = (y - mLastMotionY!![mActivePointerId]).toInt()

                    dragTo(mCapturedView!!.left + idx, mCapturedView!!.top + idy, idx, idy)

                    saveLastMotion(ev)
                } else {
                    // Check to see if any pointer is now over a draggable view.
                    val pointerCount = MotionEventCompat.getPointerCount(ev)
                    for (i in 0 until pointerCount) {
                        val pointerId = MotionEventCompat.getPointerId(ev, i)
                        val x = MotionEventCompat.getX(ev, i)
                        val y = MotionEventCompat.getY(ev, i)
                        val dx = x - mInitialMotionX!![pointerId]
                        val dy = y - mInitialMotionY!![pointerId]

                        reportNewEdgeDrags(dx, dy, pointerId)
                        if (mDragState == STATE_DRAGGING) {
                            // Callback might have started an edge drag.
                            break
                        }

                        val toCapture = findTopChildUnder(x.toInt(), y.toInt())
                        if (checkTouchSlop(toCapture, dx, dy) && tryCaptureViewForDrag(toCapture, pointerId)) {
                            break
                        }
                    }
                    saveLastMotion(ev)
                }
            }

            MotionEventCompat.ACTION_POINTER_UP -> {
                val pointerId = MotionEventCompat.getPointerId(ev, actionIndex)
                if (mDragState == STATE_DRAGGING && pointerId == mActivePointerId) {
                    // Try to find another pointer that's still holding on to
                    // the captured view.
                    var newActivePointer = INVALID_POINTER
                    val pointerCount = MotionEventCompat.getPointerCount(ev)
                    for (i in 0 until pointerCount) {
                        val id = MotionEventCompat.getPointerId(ev, i)
                        if (id == mActivePointerId) {
                            // This one's going away, skip.
                            continue
                        }

                        val x = MotionEventCompat.getX(ev, i)
                        val y = MotionEventCompat.getY(ev, i)
                        if (findTopChildUnder(x.toInt(), y.toInt()) === mCapturedView && tryCaptureViewForDrag(mCapturedView, id)) {
                            newActivePointer = mActivePointerId
                            break
                        }
                    }

                    if (newActivePointer == INVALID_POINTER) {
                        // We didn't find another pointer still touching the
                        // view, release it.
                        releaseViewForPointerUp()
                    }
                }
                clearMotionHistory(pointerId)
            }

            MotionEvent.ACTION_UP -> {
                if (mDragState == STATE_DRAGGING) {
                    releaseViewForPointerUp()
                }
                cancel()
            }

            MotionEvent.ACTION_CANCEL -> {
                if (mDragState == STATE_DRAGGING) {
                    dispatchViewReleased(0f, 0f)
                }
                cancel()
            }
        }
    }

    private fun reportNewEdgeDrags(dx: Float, dy: Float, pointerId: Int) {
        var dragsStarted = 0
        if (checkNewEdgeDrag(dx, dy, pointerId, EDGE_LEFT)) {
            dragsStarted = dragsStarted or EDGE_LEFT
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, EDGE_TOP)) {
            dragsStarted = dragsStarted or EDGE_TOP
        }
        if (checkNewEdgeDrag(dx, dy, pointerId, EDGE_RIGHT)) {
            dragsStarted = dragsStarted or EDGE_RIGHT
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, EDGE_BOTTOM)) {
            dragsStarted = dragsStarted or EDGE_BOTTOM
        }

        if (dragsStarted != 0) {
            mEdgeDragsInProgress!![pointerId] = mEdgeDragsInProgress!![pointerId] or dragsStarted
            mCallback?.onEdgeDragStarted(dragsStarted, pointerId)
        }
    }

    private fun checkNewEdgeDrag(delta: Float, odelta: Float, pointerId: Int, edge: Int): Boolean {
        val absDelta = Math.abs(delta)
        val absODelta = Math.abs(odelta)

        if (mInitialEdgeTouched!![pointerId] and edge != edge || mTrackingEdges and edge == 0
                || mEdgeDragsLocked!![pointerId] and edge == edge
                || mEdgeDragsInProgress!![pointerId] and edge == edge
                || absDelta <= mTouchSlop && absODelta <= mTouchSlop) {
            return false
        }
        if (absDelta < absODelta * 0.5f && mCallback!!.onEdgeLock(edge)) {
            mEdgeDragsLocked!![pointerId] = mEdgeDragsLocked!![pointerId] or edge
            return false
        }
        return mEdgeDragsInProgress!![pointerId] and edge == 0 && absDelta > mTouchSlop
    }

    private fun checkTouchSlop(child: View?, dx: Float, dy: Float): Boolean {
        if (child == null) {
            return false
        }
        val checkHorizontal = mCallback!!.getViewHorizontalDragRange(child) > 0
        val checkVertical = mCallback.getViewVerticalDragRange(child) > 0

        if (checkHorizontal && checkVertical) {
            return dx * dx + dy * dy > mTouchSlop * mTouchSlop
        } else if (checkHorizontal) {
            return Math.abs(dx) > mTouchSlop
        } else if (checkVertical) {
            return Math.abs(dy) > mTouchSlop
        }
        return false
    }

    fun checkTouchSlop(directions: Int): Boolean {
        val count = mInitialMotionX!!.size
        return (0 until count).any { checkTouchSlop(directions, it) }
    }

    fun checkTouchSlop(directions: Int, pointerId: Int): Boolean {
        if (!isPointerDown(pointerId)) {
            return false
        }

        val checkHorizontal = directions and DIRECTION_HORIZONTAL == DIRECTION_HORIZONTAL
        val checkVertical = directions and DIRECTION_VERTICAL == DIRECTION_VERTICAL

        val dx = mLastMotionX!![pointerId] - mInitialMotionX!![pointerId]
        val dy = mLastMotionY!![pointerId] - mInitialMotionY!![pointerId]

        if (checkHorizontal && checkVertical) {
            return dx * dx + dy * dy > mTouchSlop * mTouchSlop
        } else if (checkHorizontal) {
            return Math.abs(dx) > mTouchSlop
        } else if (checkVertical) {
            return Math.abs(dy) > mTouchSlop
        }
        return false
    }

    fun isEdgeTouched(edges: Int): Boolean {
        val count = mInitialEdgeTouched!!.size
        for (i in 0 until count) {
            if (isEdgeTouched(edges, i)) {
                return true
            }
        }
        return false
    }

    fun isEdgeTouched(edges: Int, pointerId: Int): Boolean {
        return isPointerDown(pointerId) && mInitialEdgeTouched!![pointerId] and edges != 0
    }

    private fun releaseViewForPointerUp() {
        mVelocityTracker!!.computeCurrentVelocity(1000, mMaxVelocity)
        val xvel = clampMag(
                VelocityTrackerCompat.getXVelocity(mVelocityTracker!!, mActivePointerId).toInt(),
                mMinVelocity.toInt(), mMaxVelocity.toInt())
        val yvel = clampMag(
                VelocityTrackerCompat.getYVelocity(mVelocityTracker!!, mActivePointerId).toInt(),
                mMinVelocity.toInt(), mMaxVelocity.toInt())
        dispatchViewReleased(xvel.toFloat(), yvel.toFloat())
    }

    private fun dragTo(left: Int, top: Int, dx: Int, dy: Int) {
        var clampedX = left
        var clampedY = top
        val oldLeft = mCapturedView!!.left
        val oldTop = mCapturedView!!.top
        if (dx != 0) {
            clampedX = mCallback!!.clampViewPositionHorizontal(mCapturedView!!, left, dx)
            mCapturedView!!.offsetLeftAndRight(clampedX - oldLeft)
        }
        if (dy != 0) {
            clampedY = mCallback!!.clampViewPositionVertical(mCapturedView!!, top, dy)
            mCapturedView!!.offsetTopAndBottom(clampedY - oldTop)
        }

        if (dx != 0 || dy != 0) {
            val clampedDx = clampedX - oldLeft
            val clampedDy = clampedY - oldTop
            mCallback!!.onViewPositionChanged(mCapturedView!!, clampedX, clampedY, clampedDx, clampedDy)
        }
    }

    fun isCapturedViewUnder(x: Int, y: Int): Boolean {
        return isViewUnder(mCapturedView, x, y)
    }

    fun isViewUnder(view: View?, x: Int, y: Int): Boolean {
        return if (view == null) {
            false
        } else x >= view.left && x < view.right && y >= view.top
                && y < view.bottom
    }

    fun findTopChildUnder(x: Int, y: Int): View? {
        val childCount = mParentView!!.childCount
        return (childCount - 1 downTo 0)
                .map { mParentView.getChildAt(mCallback!!.getOrderedChildIndex(it)) }
                .firstOrNull { x >= it.left && x < it.right && y >= it.top && y < it.bottom }
    }

    private fun getEdgeTouched(x: Int, y: Int): Int {
        return when {
            x < mParentView!!.left + mEdgeSize -> EDGE_LEFT
            y < mParentView.top + mEdgeSize -> EDGE_TOP
            x > mParentView.right - mEdgeSize -> EDGE_RIGHT
            y > mParentView.bottom - mEdgeSize -> EDGE_BOTTOM
            else -> 0
        }
    }

    companion object {
        /** 一个无用的 ID，用来标识SwipeViewDragHelper */
        val INVALID_POINTER = -1

        /** 空闲状态 */
        val STATE_IDLE = 0
        /** 拖动状态 */
        val STATE_DRAGGING = 1
        /** 被占用状态 */
        val STATE_SETTLING = 2

        /** 左边缘 */
        val EDGE_LEFT = 1 shl 0
        /** 右边缘 */
        val EDGE_RIGHT = 1 shl 1
        /** 上边缘 */
        val EDGE_TOP = 1 shl 2
        /** 底边缘 */
        val EDGE_BOTTOM = 1 shl 3
        /** 所有边缘 */
        val EDGE_ALL: Int = EDGE_LEFT or EDGE_TOP or EDGE_RIGHT or EDGE_BOTTOM


        /** 水平方向 */
        val DIRECTION_HORIZONTAL = 1 shl 0
        /** 垂直方向 */
        val DIRECTION_VERTICAL = 1 shl 1
        /** 水平方向 与 垂直方向 */
        val DIRECTION_ALL = DIRECTION_HORIZONTAL or DIRECTION_VERTICAL

        val EDGE_SIZE = 20  // dp
        val BASE_SETTLE_DURATION = 256  // ms
        val MAX_SETTLE_DURATION = 600   // ms

        /** 滑动动画曲线的插补器定义 */
        private val sInterpolator = Interpolator { t ->
            val t2 = t - 1.0f
            t2 * t2 * t2 * t2 * t2 + 1.0f
        }

        /**
         * 创建一个新的ViewDragHelper
         *
         * @param forParent 监视的父视图
         * @param callback  回调来提供信息和接收事件
         *
         * @return 一个新的viewDragHelper实例
         */
        fun create(@NonNull forParent: ViewGroup, callback: SwipeCallback): SwipeViewDragHelper {
            return SwipeViewDragHelper(forParent.context, forParent, callback)
        }

        /**
         * 创建一个新的ViewDragHelper
         *
         * @param forParent 监视的父视图
         * @param sensitivity 拖动敏感，1.0最大
         * @param callback  回调来提供信息和接收事件
         *
         * @return 一个新的viewDragHelper实例
         */
        fun create(@NonNull forParent: ViewGroup, sensitivity: Float, callback: SwipeCallback): SwipeViewDragHelper {
            val helper = create(forParent, callback)
            helper.mTouchSlop = (helper.mTouchSlop * (1 / sensitivity)).toInt()
            return helper
        }
    }
}