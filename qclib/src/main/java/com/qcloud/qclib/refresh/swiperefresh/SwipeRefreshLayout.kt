package com.qcloud.qclib.refresh.swiperefresh

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.view.*
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.AbsListView
import android.widget.LinearLayout
import com.qcloud.qclib.R
import com.qcloud.qclib.refresh.listener.LoadMoreAnimatorListener
import com.qcloud.qclib.refresh.listener.MyAnimationListener
import com.qcloud.qclib.refresh.listener.OnFooterStateListener
import com.qcloud.qclib.refresh.widget.FooterLayout
import com.qcloud.qclib.utils.ColorUtil
import com.qcloud.qclib.utils.DensityUtil

/**
 * Description: 自定义Material design下拉刷新控件
 * Author: gaobaiqiang
 * 2018/3/11 下午5:07.
 */
open class SwipeRefreshLayout @JvmOverloads constructor(
        protected val mContext: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : ViewGroup(mContext, attrs, defStyleAttr), NestedScrollingParent, NestedScrollingChild {

    /**加载动画的位置*/
    var mAnimPosition = ANIM_IN_CENTER
        set(value) {
            field = if (value != ANIM_IN_CENTER && value != ANIM_IN_LEFT) {
                ANIM_IN_CENTER
            } else {
                value
            }
        }

    /**动画偏移左边的距离*/
    private val animPaddingLeft = DensityUtil.dp2px(mContext, 20f)

    /**内容布局 */
    private var mTarget: View? = null
    /**下拉刷新监听 */
    var onRefreshListener: OnRefreshListener? = null
    /**是否在刷新 */
    private var mRefreshing = false

    private var mTouchSlop: Int = 0
    private var mTotalDragDistance = -1f
    private val mMediumAnimationDuration: Int
    private var mCurrentTargetOffsetTop: Int = 0
    private var mOriginalOffsetCalculated = false

    private var mInitialMotionY: Float = 0.toFloat()
    private var mIsBeingDragged: Boolean = false
    private var mActivePointerId = INVALID_POINTER

    /**是否扩大还是裁剪*/
    private var isScale: Boolean = false

    /**当被取消或刷新被触发时是否将返回到起始偏移量*/
    private var isReturningToStart: Boolean = false

    private val mDecelerateInterpolator: DecelerateInterpolator

    private var mStartingScale: Float = 0f
    private var mFrom: Int = 0
    private var mOriginalOffsetTop: Int = 0

    /**下拉加载动画 */
    private var mCircleView: CircleImageView? = null
    private var mCircleViewIndex = -1
    private var mCircleWidth: Int = 0
    private var mCircleHeight: Int = 0

    private var mProgress: MaterialProgressDrawable? = null
    private var mScaleAnimation: Animation? = null
    private var mScaleDownAnimation: Animation? = null
    private var mAlphaStartAnimation: Animation? = null
    private var mAlphaMaxAnimation: Animation? = null
    private var mScaleDownToStartAnimation: Animation? = null

    private var mSpinnerFinalOffset: Float = 0f
    private var isNotify: Boolean = false

    /**尾部容器 */
    private var mFooterLayout: FooterLayout? = null
    /**尾部View */
    private var mFooter: View? = null
    /**尾部的高度 */
    private var mFooterHeight = FOOTER_DEFAULT_HEIGHT
    /**是否可以上拉加载更多 */
    private var isPullLoadEnable = false

    /**上拉加载更多事件监听 */
    var onLoadMoreListener: OnLoadMoreListener? = null
    /**底部状态监听器 */
    var onFooterStateListener: OnFooterStateListener? = null

    /**嵌套滚动,用于在触摸事件处理程序去确定OverScroll的地方 */
    private var mTotalUnconsumed: Float = 0.toFloat()
    private val mNestedScrollingParentHelper: NestedScrollingParentHelper
    private val mNestedScrollingChildHelper: NestedScrollingChildHelper
    private val mParentScrollConsumed = IntArray(2)
    private val mParentOffsetInWindow = IntArray(2)
    private var mNestedScrollInProgress: Boolean = false

    /**当前滑动状态*/
    private var mCurrentAction = ACTION_NOT

    /**滑动的偏移量 */
    private var mScrollOffset = 0

    /**是否已完成 */
    private var isConfirm = false

    /**是否还有更多数据 */
    private var isMore = true

    /**是否设置了自定义起始位置*/
    private var mUsingCustomStart: Boolean = false

    /**
     * Api11之前，alpha用于使进度循环代替规模出现
     */
    private val isAlphaUsedForScale: Boolean
        get() = Build.VERSION.SDK_INT < 11

    /**
     * 加载动画监听
     * */
    private val mRefreshListener = object : MyAnimationListener {
        override fun onAnimationStart(animation: Animation?) {}

        override fun onAnimationRepeat(animation: Animation?) {}

        override fun onAnimationEnd(animation: Animation?) {
            if (mRefreshing) {
                // 确保进度视图是完全可见的
                mProgress?.alpha = MAX_ALPHA
                mProgress?.start()
                if (isNotify) {
                    onRefreshListener?.onRefresh()
                }
            } else {
                mProgress?.stop()
                mCircleView?.visibility = View.GONE
                setColorViewAlpha(MAX_ALPHA)
                // 将返回起始位置
                if (isScale) {
                    setAnimationProgress(0f)
                } else {
                    setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop, true)
                }
            }
            mCurrentTargetOffsetTop = mCircleView!!.top
        }
    }

    private val mAnimateToCorrectPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            var endTarget = if (!mUsingCustomStart) {
                (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop)).toInt()
            } else {
                mSpinnerFinalOffset.toInt()
            }
            val targetTop = mFrom + ((endTarget - mFrom) * interpolatedTime).toInt()
            val offset = targetTop - mCircleView!!.top
            setTargetOffsetTopAndBottom(offset, false /* requires update */)
        }
    }

    private val mAnimateToStartPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            moveToStart(interpolatedTime)
        }
    }

    init {
        mTouchSlop = ViewConfiguration.get(mContext).scaledTouchSlop

        mMediumAnimationDuration = resources.getInteger(android.R.integer.config_mediumAnimTime)

        setWillNotDraw(false)
        mDecelerateInterpolator = DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)

        val a = mContext.obtainStyledAttributes(attrs, LAYOUT_ATTRS)
        isEnabled = a.getBoolean(0, true)
        a.recycle()

        // 获取加载动画的宽高
        val metrics = resources.displayMetrics
        mCircleWidth = (CIRCLE_DIAMETER * metrics.density).toInt()
        mCircleHeight = (CIRCLE_DIAMETER * metrics.density).toInt()

        clipToPadding = false

        createProgressView()
        createFooterLayout()
        ViewCompat.setChildrenDrawingOrderEnabled(this, true)

        mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density
        mTotalDragDistance = mSpinnerFinalOffset

        mNestedScrollingParentHelper = NestedScrollingParentHelper(this)

        mNestedScrollingChildHelper = NestedScrollingChildHelper(this)

        isNestedScrollingEnabled = true
    }

    /**
     * 创建加载动画
     * */
    private fun createProgressView() {
        mCircleView = CircleImageView(context, CIRCLE_BG_LIGHT, (CIRCLE_DIAMETER / 2).toFloat())
        mProgress = MaterialProgressDrawable(context, this)
        mProgress!!.setBackgroundColor(CIRCLE_BG_LIGHT)
        mCircleView!!.setImageDrawable(mProgress)
        mCircleView!!.visibility = View.GONE
        addView(mCircleView)
    }

    /**
     * 初始化底部
     */
    private fun createFooterLayout() {
        mFooterLayout = FooterLayout(mContext)
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
        mFooterLayout!!.gravity = Gravity.CENTER or Gravity.BOTTOM
        mFooterLayout!!.layoutParams = lp

        mFooterLayout!!.setText(mContext.resources.getString(com.qcloud.qclib.R.string.load_by_pull_up))
        mFooterLayout!!.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrayDark))
        addView(mFooterLayout)
    }

    /**
     * 设置尾部
     */
    fun setFooter(footer: View) {
        mFooter = footer

        mFooterLayout?.setFooterView(mFooter)

        //获取尾部高度
        mFooterHeight = measureViewHeight(mFooterLayout!!)
        Log.e(LOG_TAG, "mFooterHeight = $mFooterHeight")
    }

    /**
     * 计算尾部高度
     */
    private fun measureViewHeight(view: View): Int {
        val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(width, height)
        return view.measuredHeight
    }

    /**
     * 设置颜色背景透明度
     * */
    private fun setColorViewAlpha(targetAlpha: Int) {
        mCircleView?.background?.alpha = targetAlpha
        mProgress?.alpha = targetAlpha
    }

    /**
     * 设置动画进度
     * */
    private fun setAnimationProgress(progress: Float) {
        if (isAlphaUsedForScale) {
            setColorViewAlpha((progress * MAX_ALPHA).toInt())
        } else {
            ViewCompat.setScaleX(mCircleView!!, progress)
            ViewCompat.setScaleY(mCircleView!!, progress)
        }
    }

    private fun setTargetOffsetTopAndBottom(offset: Int, requiresUpdate: Boolean) {
        mCircleView!!.bringToFront()
        mCircleView!!.offsetTopAndBottom(offset)
        mCurrentTargetOffsetTop = mCircleView!!.top
        if (requiresUpdate && Build.VERSION.SDK_INT < 11) {
            invalidate()
        }
    }

    private fun ensureTarget() {
        if (mTarget == null) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child != mCircleView && child != mFooterLayout) {
                    mTarget = child
                    break
                }
            }
        }
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        return when {
            mCircleViewIndex < 0 -> i
            i == childCount - 1 -> {
                // 最后绘制的view
                mCircleViewIndex
            }
            i >= mCircleViewIndex -> {
                // 在选定view之前移动view
                i + 1
            }
            else -> {
                // 保持view与选定的view一样
                i
            }
        }
    }

    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (mRefreshing != refreshing) {
            isNotify = notify
            ensureTarget()
            mRefreshing = refreshing
            if (mRefreshing) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener)
            } else {
                startScaleDownAnimation(mRefreshListener)
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun startScaleUpAnimation(listener: MyAnimationListener?) {
        mCircleView!!.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= 11) {
            mProgress!!.alpha = MAX_ALPHA
        }
        mScaleAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationProgress(interpolatedTime)
            }
        }
        mScaleAnimation!!.duration = mMediumAnimationDuration.toLong()
        if (listener != null) {
            mCircleView?.setAnimationListener(listener)
        }
        mCircleView?.clearAnimation()
        mCircleView?.startAnimation(mScaleAnimation)
    }

    private fun startScaleDownAnimation(listener: MyAnimationListener?) {
        mScaleDownAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationProgress(1 - interpolatedTime)
            }
        }
        mScaleDownAnimation!!.duration = SCALE_DOWN_DURATION.toLong()
        mCircleView?.setAnimationListener(listener)
        mCircleView?.clearAnimation()
        mCircleView?.startAnimation(mScaleDownAnimation)
    }

    private fun animateOffsetToCorrectPosition(from: Int, listener: MyAnimationListener?) {
        mFrom = from
        mAnimateToCorrectPosition.reset()
        mAnimateToCorrectPosition.duration = ANIMATE_TO_TRIGGER_DURATION.toLong()
        mAnimateToCorrectPosition.interpolator = mDecelerateInterpolator
        if (listener != null) {
            mCircleView?.setAnimationListener(listener)
        }
        mCircleView?.clearAnimation()
        mCircleView?.startAnimation(mAnimateToCorrectPosition)
    }

    private fun startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress!!.alpha, STARTING_PROGRESS_ALPHA)
    }

    private fun startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress!!.alpha, MAX_ALPHA)
    }

    private fun startAlphaAnimation(startingAlpha: Int, endingAlpha: Int): Animation? {
        if (isScale && isAlphaUsedForScale) {
            return null
        }
        val alpha = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                mProgress!!.alpha = (startingAlpha + (endingAlpha - startingAlpha) * interpolatedTime).toInt()
            }
        }
        alpha.duration = ALPHA_ANIMATION_DURATION.toLong()

        mCircleView?.setAnimationListener(null)
        mCircleView?.clearAnimation()
        mCircleView?.startAnimation(alpha)
        return alpha
    }

    /**
     * 通知窗口，刷新状态改变
     *
     * @param refreshing 是否显示刷新动画
     */
    fun setRefreshing(refreshing: Boolean = false) {
        if (refreshing && mRefreshing != refreshing) {
            mRefreshing = refreshing
            val endTarget = if (!mUsingCustomStart) {
                (mSpinnerFinalOffset + mOriginalOffsetTop).toInt()
            } else {
                mSpinnerFinalOffset.toInt()
            }
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop, true)
            isNotify = false
            startScaleUpAnimation(mRefreshListener)
        } else {
            setRefreshing(refreshing, false)
        }
    }

    fun isRefreshing(): Boolean = mRefreshing

    /**
     * 设置下拉动画大小
     */
    fun setSize(size: Int) {
        if (size != MaterialProgressDrawable.LARGE && size != MaterialProgressDrawable.DEFAULT) {
            return
        }
        val metrics = resources.displayMetrics
        if (size == MaterialProgressDrawable.LARGE) {
            mCircleWidth = (CIRCLE_DIAMETER_LARGE * metrics.density).toInt()
            mCircleHeight = mCircleWidth
        } else {
            mCircleWidth = (CIRCLE_DIAMETER * metrics.density).toInt()
            mCircleHeight = mCircleWidth
        }

        mCircleView?.setImageDrawable(null)
        mProgress?.updateSizes(size)
        mCircleView?.setImageDrawable(mProgress)
    }

    /**
     * 设置是否启用上拉功能
     *
     * @param isLoadMore   是否开启上拉功能 默认不开启
     */
    fun setLoadMore(isLoadMore: Boolean) {
        isPullLoadEnable = isLoadMore
    }

    /**
     * 设置背景颜色
     * */
    fun setProgressBackgroundColor(colorRes: Int) {
        mCircleView?.setBackgroundColor(colorRes)
        mProgress?.setBackgroundColor(ContextCompat.getColor(mContext, colorRes))
    }

    fun setColorScheme(vararg colors: Int) {
        setColorSchemeResources(*colors)
    }

    fun setColorSchemeResources(vararg colorResIds: Int) {
        val res = resources
        val colorRes = IntArray(colorResIds.size)
        for (i in colorResIds.indices) {
            colorRes[i] = res.getColor(colorResIds[i])
        }
        setColorSchemeColors(colorRes)
    }

    fun setColorSchemeColors(colors: IntArray) {
        ensureTarget()
        mProgress?.setColorSchemeColors(*colors)
    }

    fun setDistanceToTriggerSync(distance: Int) {
        mTotalDragDistance = distance.toFloat()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = measuredWidth
        val height = measuredHeight
        if (childCount == 0) {
            return
        }
        if (mTarget == null) {
            ensureTarget()
        }
        if (mTarget == null) {
            return
        }
        val child = mTarget
        val childLeft = paddingLeft
        val childTop = paddingTop
        val childWidth = width - paddingLeft - paddingRight
        val childHeight = height - paddingTop - paddingBottom
        child!!.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        val circleWidth = mCircleView!!.measuredWidth
        val circleHeight = mCircleView!!.measuredHeight
        if (mAnimPosition == ANIM_IN_CENTER) {
            // 加载动画在中间
            mCircleView?.layout(width / 2 - circleWidth / 2, mCurrentTargetOffsetTop,
                    width / 2 + circleWidth / 2, mCurrentTargetOffsetTop + circleHeight)
        } else {
            // 加载动画在左边
            mCircleView?.layout(animPaddingLeft, mCurrentTargetOffsetTop,
                    animPaddingLeft + circleWidth, mCurrentTargetOffsetTop + circleHeight)
        }

        // 底部
        mFooterLayout!!.layout(paddingLeft, measuredHeight, paddingLeft + width, childHeight + mFooterHeight)
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mTarget == null) {
            ensureTarget()
        }
        if (mTarget == null) {
            return
        }
        mTarget!!.measure(View.MeasureSpec.makeMeasureSpec(
                measuredWidth - paddingLeft - paddingRight,
                View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                measuredHeight - paddingTop - paddingBottom, View.MeasureSpec.EXACTLY))
        mCircleView!!.measure(View.MeasureSpec.makeMeasureSpec(mCircleWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(mCircleHeight, View.MeasureSpec.EXACTLY))

        // 测量底部高度
        measureChild(mFooterLayout, widthMeasureSpec, heightMeasureSpec)

        if (!mUsingCustomStart && !mOriginalOffsetCalculated) {
            mOriginalOffsetCalculated = true
            mOriginalOffsetTop = -mCircleView!!.measuredHeight
            mCurrentTargetOffsetTop = mOriginalOffsetTop
        }
        mCircleViewIndex = -1
        for (index in 0 until childCount) {
            if (getChildAt(index) == mCircleView) {
                mCircleViewIndex = index
                break
            }
        }
    }

    fun canChildScrollUp(): Boolean {
        return if (mTarget == null) {
            false
        } else if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.canScrollVertically(mTarget!!, 1)
        } else if (mTarget is AbsListView) {
            val absListView = mTarget as AbsListView?
            if (absListView!!.childCount <= 0) {
                false
            } else {
                val lastChildBottom = absListView.getChildAt(absListView.childCount - 1).bottom
                absListView.lastVisiblePosition == absListView.adapter.count - 1 && lastChildBottom <= absListView.measuredHeight
            }
        } else {
            ViewCompat.canScrollVertically(mTarget!!, 1) || mTarget!!.scrollY > 0
        }
    }

    fun canChildScrollDown(): Boolean {
        if (mTarget == null) {
            return false
        }
        return if (mTarget == null) {
            false
        } else if (Build.VERSION.SDK_INT < 14) {
            if (mTarget is AbsListView) {
                val absListView = mTarget as AbsListView?
                absListView!!.childCount > 0 && (absListView.firstVisiblePosition > 0 || absListView.getChildAt(0)
                        .top < absListView.paddingTop)
            } else {
                ViewCompat.canScrollVertically(mTarget!!, -1) || mTarget!!.scrollY > 0
            }
        } else {
            ViewCompat.canScrollVertically(mTarget!!, -1)
        }
    }

    private fun getMotionEventY(ev: MotionEvent, activePointerId: Int): Float {
        val index = MotionEventCompat.findPointerIndex(ev, activePointerId)
        return if (index < 0) {
            -1f
        } else MotionEventCompat.getY(ev, index)
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = MotionEventCompat.getActionIndex(ev)
        val pointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        ensureTarget()

        val action = MotionEventCompat.getActionMasked(ev)

        if (isReturningToStart && action == MotionEvent.ACTION_DOWN) {
            isReturningToStart = false
        }

        if (!isEnabled || isReturningToStart || canChildScrollDown()
                || mRefreshing || mNestedScrollInProgress) {
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView!!.top, true)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
                val initialMotionY = getMotionEventY(ev, mActivePointerId)
                if (initialMotionY == -1f) {
                    return false
                }
                mInitialMotionY = initialMotionY
            }

            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.")
                    return false
                }

                val y = getMotionEventY(ev, mActivePointerId)
                if (y == -1f) {
                    return false
                }
                val yDiff = y - mInitialMotionY
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mInitialMotionY += mTouchSlop
                    mIsBeingDragged = true
                    mProgress!!.alpha = STARTING_PROGRESS_ALPHA
                }
            }

            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
                mActivePointerId = INVALID_POINTER
            }
        }

        return mIsBeingDragged
    }


    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(ev)

        if (isReturningToStart && action == MotionEvent.ACTION_DOWN) {
            isReturningToStart = false
        }

        if (!isEnabled || isReturningToStart || canChildScrollDown() || mNestedScrollInProgress) {
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
            }

            MotionEvent.ACTION_MOVE -> {
                // 下拉
                val pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.")
                    return false
                }

                val y = MotionEventCompat.getY(ev, pointerIndex)
                // 记录手指移动的距离,mInitialMotionY是初始的位置，DRAG_RATE是拖拽因子。
                val overScrollTop = (y - mInitialMotionY) * DRAG_RATE
                // 赋值给mTarget的top使之产生拖动效果
                if (mTarget != null) {
                    mTarget!!.translationY = overScrollTop
                }
                if (mIsBeingDragged) {
                    if (overScrollTop > 0) {
                        moveSpinner(overScrollTop)
                    } else {
                        return false
                    }
                }
            }
            MotionEventCompat.ACTION_POINTER_DOWN -> {
                val index = MotionEventCompat.getActionIndex(ev)
                if (index < 0) {
                    Log.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.")
                    return false
                }
                mActivePointerId = MotionEventCompat.getPointerId(ev, index)
            }

            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP -> {
                if (mTarget != null) {
                    mTarget!!.animate().translationY(0f).setDuration(200).start()
                }
                val pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.")
                    return false
                }

                val y = MotionEventCompat.getY(ev, pointerIndex)
                val overScrollTop = (y - mInitialMotionY) * DRAG_RATE
                mIsBeingDragged = false
                finishSpinner(overScrollTop)
                mActivePointerId = INVALID_POINTER
                return false
            }

            MotionEvent.ACTION_CANCEL -> return false
        }

        return true
    }

    /**
     * 移动
     * */
    private fun moveSpinner(overScrollTop: Float) {
        mProgress!!.showArrow(true)
        val originalDragPercent = overScrollTop / mTotalDragDistance

        val dragPercent = Math.min(1f, Math.abs(originalDragPercent))
        val adjustedPercent = Math.max(dragPercent - .4, 0.0).toFloat() * 5 / 3
        val extraOS = Math.abs(overScrollTop) - mTotalDragDistance
        val slingshotDist = if (mUsingCustomStart) mSpinnerFinalOffset - mOriginalOffsetTop else mSpinnerFinalOffset
        val tensionSlingshotPercent = Math.max(0f, Math.min(extraOS, slingshotDist * 2) / slingshotDist)
        val tensionPercent = (tensionSlingshotPercent / 4 - Math.pow((tensionSlingshotPercent / 4).toDouble(), 2.0)).toFloat() * 2f
        val extraMove = slingshotDist * tensionPercent * 2f

        val targetY = mOriginalOffsetTop + (slingshotDist * dragPercent + extraMove).toInt()
        // where 1.0f is a full circle
        if (mCircleView!!.visibility != View.VISIBLE) {
            mCircleView!!.visibility = View.VISIBLE
        }
        if (!isScale) {
            ViewCompat.setScaleX(mCircleView!!, 1f)
            ViewCompat.setScaleY(mCircleView!!, 1f)
        }

        if (isScale) {
            setAnimationProgress(Math.min(1f, overScrollTop / mTotalDragDistance))
        }
        if (overScrollTop < mTotalDragDistance) {
            if (mProgress!!.alpha > STARTING_PROGRESS_ALPHA && !isAnimationRunning(mAlphaStartAnimation)) {
                // Animate the alpha
                startProgressAlphaStartAnimation()
            }
        } else {
            if (mProgress!!.alpha < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
                // Animate the alpha
                startProgressAlphaMaxAnimation()
            }
        }
        val strokeStart = adjustedPercent * .8f
        mProgress!!.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart))
        mProgress!!.setArrowScale(Math.min(1f, adjustedPercent))

        val rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f
        mProgress!!.setProgressRotation(rotation)
        // 最终刷新的位置
        val endTarget = if (!mUsingCustomStart) {
            // 没有修改使用默认的值
            (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop)).toInt()
        } else {
            // 否则使用定义的值
            mSpinnerFinalOffset.toInt()
        }
        if (targetY >= endTarget) {
            // 下移的位置超过最终位置后就不再下移，第一个参数为偏移量
            setTargetOffsetTopAndBottom(0, true)
        } else {
            // 否则继续继续下移
            setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true /* requires update */)
        }
    }

    /**
     * 完成动画旋转
     * */
    private fun finishSpinner(overScrollTop: Float) {
        if (overScrollTop > mTotalDragDistance) {
            // 刷新
            setRefreshing(true, true)
        } else {
            // cancel refresh
            mRefreshing = false
            mProgress!!.setStartEndTrim(0f, 0f)
            var listener: MyAnimationListener? = null
            if (!isScale) {
                listener = object : MyAnimationListener {

                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        if (!isScale) {
                            startScaleDownAnimation(null)
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                }
            }
            animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener)
            mProgress!!.showArrow(false)
        }
    }

    private fun isAnimationRunning(animation: Animation?): Boolean {
        return animation != null && animation.hasStarted() && !animation.hasEnded()
    }

    private fun animateOffsetToStartPosition(from: Int, listener: MyAnimationListener?) {
        if (isScale) {
            // Scale the item back down
            startScaleDownReturnToStartAnimation(from, listener)
        } else {
            mFrom = from
            mAnimateToStartPosition.reset()
            mAnimateToStartPosition.duration = ANIMATE_TO_START_DURATION.toLong()
            mAnimateToStartPosition.interpolator = mDecelerateInterpolator
            if (listener != null) {
                mCircleView?.setAnimationListener(listener)
            }
            mCircleView?.clearAnimation()
            mCircleView?.startAnimation(mAnimateToStartPosition)
        }
    }

    private fun startScaleDownReturnToStartAnimation(from: Int, listener: MyAnimationListener?) {
        mFrom = from
        mStartingScale = if (isAlphaUsedForScale) {
            mProgress!!.alpha.toFloat()
        } else {
            ViewCompat.getScaleX(mCircleView!!)
        }
        mScaleDownToStartAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val targetScale = mStartingScale + -mStartingScale * interpolatedTime
                setAnimationProgress(targetScale)
                moveToStart(interpolatedTime)
            }
        }
        mScaleDownToStartAnimation!!.duration = SCALE_DOWN_DURATION.toLong()
        if (listener != null) {
            mCircleView!!.setAnimationListener(listener)
        }
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(mScaleDownToStartAnimation)
    }

    private fun moveToStart(interpolatedTime: Float) {
        val targetTop = mFrom + ((mOriginalOffsetTop - mFrom) * interpolatedTime).toInt()
        val offset = targetTop - mCircleView!!.top
        setTargetOffsetTopAndBottom(offset, false)
    }

    override fun requestDisallowInterceptTouchEvent(b: Boolean) {
        if (Build.VERSION.SDK_INT < 21 && mTarget is AbsListView || mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget!!)) {

        } else {
            super.requestDisallowInterceptTouchEvent(b)
        }
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return (isEnabled && canChildScrollDown() && !isReturningToStart && !mRefreshing
                && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
        startNestedScroll(axes and ViewCompat.SCROLL_AXIS_VERTICAL)
        mTotalUnconsumed = 0f
        mNestedScrollInProgress = true
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (isPullLoadEnable) {
            if (Math.abs(dy) <= FOOTER_DEFAULT_HEIGHT) {
                if (!isConfirm) {
                    if (dy < 0 && !canChildScrollDown()) {
                        mCurrentAction = ACTION_DOWN
                        isConfirm = true
                    } else if (dy > 0 && !canChildScrollUp()) {
                        mCurrentAction = ACTION_UP
                        isConfirm = true
                    } else {
                        mCurrentAction = ACTION_NOT
                        isConfirm = false
                    }
                }

                if (moveGuidanceView((-dy).toFloat())) {
                    consumed[1] += dy
                }
            }
        }
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        // 首先嵌套到父级
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow)

        val dy = dyUnconsumed + mParentOffsetInWindow[1]
        if (dy < 0) {
            mTotalUnconsumed += Math.abs(dy).toFloat()
            moveSpinner(mTotalUnconsumed)
        }
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mNestedScrollingChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mNestedScrollingChildHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mNestedScrollingChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun getNestedScrollAxes(): Int {
        return mNestedScrollingParentHelper.nestedScrollAxes
    }

    override fun onStopNestedScroll(target: View) {
        mNestedScrollingParentHelper.onStopNestedScroll(target)
        mNestedScrollInProgress = false
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            finishSpinner(mTotalUnconsumed)
            mTotalUnconsumed = 0f
        }
        // Dispatch up our nested parent
        stopNestedScroll()
        handlerAction()
    }

    private fun moveGuidanceView(distanceY: Float): Boolean {
        if (mRefreshing) {
            return false
        } else {
            val lp: ViewGroup.LayoutParams

            if (!canChildScrollDown() && mCurrentAction == ACTION_DOWN) {
                // 下拉
                return true
            } else if (!canChildScrollUp() && isPullLoadEnable && mCurrentAction == ACTION_UP) {
                // 上拉
                lp = mFooterLayout!!.layoutParams
                lp.height = (lp.height.toFloat() - distanceY).toInt()
                if (lp.height < 0) {
                    lp.height = 0
                }

                if (lp.height.toFloat() > 300) {
                    lp.height = 300
                }

                if (lp.height == 0) {
                    isConfirm = false
                    mCurrentAction = ACTION_NOT
                }

                mFooterLayout!!.layoutParams = lp
                moveTargetView(lp.height)

                return true
            } else {
                resetLoadMoreState()
                return false
            }
        }
    }

    /**
     * 移动布局，用来显示底部
     */
    private fun moveTargetView(offset: Int) {
        if (offset > 0 && mCurrentAction != ACTION_UP) {
            return
        }

        mScrollOffset = Math.abs(offset)

        scrollTo(0, offset)

        if (onFooterStateListener != null && isMore) {
            onFooterStateListener!!.onScrollChange(mFooter, mScrollOffset,
                    if (mScrollOffset >= mFooterHeight) FOOTER_DEFAULT_HEIGHT else mScrollOffset * FOOTER_DEFAULT_HEIGHT / mFooterHeight)
        }
    }

    /**
     * 自动加载更多
     */
    protected fun autoLoadMore(offset: Int) {
        if (mRefreshing || mCurrentAction == ACTION_DOWN || mCurrentAction == ACTION_UP) {
            return
        }
        mCurrentAction = ACTION_UP
        mScrollOffset = Math.abs(offset)

        scrollTo(0, offset)

        if (onFooterStateListener != null && isMore) {
            onFooterStateListener!!.onRefreshFoot(mFooter)
        }
        startLoadMore(offset)
    }

    /**
     * 重置加载状态
     */
    private fun resetLoadMoreState() {
        mRefreshing = false
        isConfirm = false
        mCurrentAction = ACTION_NOT

        moveTargetView(0)
    }

    /**
     * 处理事件
     */
    private fun handlerAction() {
        if (!mRefreshing) {
            isConfirm = false
            val lp: ViewGroup.LayoutParams
            // 下拉
            if (mCurrentAction == ACTION_DOWN) {
                // 不处理
            }
            // 上拉
            if (isPullLoadEnable && mCurrentAction == ACTION_UP) {
                lp = mFooterLayout!!.layoutParams
                if (lp.height.toFloat() >= mFooterHeight) {
                    // 开始执行加载更多
                    if (isMore) {
                        startLoadMore(lp.height)
                    }

                    // 刷新状态
                    if (onFooterStateListener != null && isMore) {
                        onFooterStateListener!!.onRefreshFoot(mFooter)
                    } else {
                        loadMoreFinish()
                    }
                } else if (lp.height > 0) {
                    resetFootView(lp.height)
                } else {
                    resetLoadMoreState()
                }
            }
        }
    }

    /**
     * 开始加载更多
     */
    protected fun startLoadMore(footerViewHeight: Int) {
        this.mRefreshing = true
        val animator = ValueAnimator.ofFloat(footerViewHeight.toFloat(), FOOTER_DEFAULT_HEIGHT.toFloat())
        animator.addUpdateListener { animation ->
            val lp = mFooterLayout!!.layoutParams
            lp.height = (animation.animatedValue as Float).toFloat().toInt()
            mFooterLayout!!.layoutParams = lp
            moveTargetView(lp.height)
        }

        animator.addListener(object : LoadMoreAnimatorListener() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                onLoadMoreListener?.onLoadMore()
            }
        })
        animator.duration = 300L
        animator.start()
    }

    /**
     * 完成加载更多
     */
    fun loadMoreFinish() {
        if (mCurrentAction == ACTION_UP) {
            resetFootView(if (mFooterLayout == null) 0 else mFooterLayout!!.measuredHeight)

            if (onFooterStateListener != null && isMore) {
                onFooterStateListener!!.onRetractFoot(mFooter)
            }

            ensureTarget()
            if (mTarget != null && (mTarget is RecyclerView || mTarget is AbsListView)) {
                mTarget?.scrollBy(0, FOOTER_DEFAULT_HEIGHT)
            }
        }
    }

    private fun resetFootView(footerViewHeight: Int) {
        val animator = ValueAnimator.ofFloat(footerViewHeight.toFloat(), 0.0f)
        animator.addUpdateListener { animation ->
            val lp = mFooterLayout!!.layoutParams
            lp.height = (animation.animatedValue as Float).toFloat().toInt()
            mFooterLayout!!.layoutParams = lp
            moveTargetView(lp.height)
        }
        animator.addListener(object : LoadMoreAnimatorListener() {
            override fun onAnimationEnd(animation: Animator) {
                resetLoadMoreState()
            }
        })
        animator.duration = 300L
        animator.start()
    }

    /**
     * 是否上拉加载更多
     */
    protected fun pullUp(): Boolean {
        return mCurrentAction != ACTION_DOWN && isPullLoadEnable
    }

    /**
     * 通知加载完成
     */
    fun loadedFinish() {
        if (mRefreshing) {
            if (mCurrentAction == ACTION_UP) {
                loadMoreFinish()
                mRefreshing = false
            } else {
                setRefreshing(false)
            }
        }
    }

    fun isMore(isMore: Boolean) {
        this.isMore = isMore
        if (onFooterStateListener != null) {
            if (isMore) {
                onFooterStateListener!!.onHasMore(mFooter)
            } else {
                onFooterStateListener!!.onNotMore(mFooter)
            }
        }
    }

    protected fun resetTarget() {
        mTarget = null
    }

    fun getTarget(): View? {
        ensureTarget()
        return mTarget
    }

    fun getFooter(): View? {
        return mFooter
    }

    /**
     * 下拉刷新
     * */
    interface OnRefreshListener {
        fun onRefresh()
    }

    /**
     * 上拉加载更多
     */
    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    companion object {
        /**加载动画大图*/
        val LARGE = MaterialProgressDrawable.LARGE
        val CIRCLE_DIAMETER_LARGE = MaterialProgressDrawable.CIRCLE_DIAMETER_LARGE
        /**加载动画默认图*/
        val DEFAULT = MaterialProgressDrawableKotlin.DEFAULT
        val CIRCLE_DIAMETER = MaterialProgressDrawable.CIRCLE_DIAMETER

        /**加载动画在左边*/
        val ANIM_IN_LEFT = 1
        /**加载动画在右边*/
        val ANIM_IN_CENTER = 2

        /**底部上拉的最小高度*/
        val FOOTER_DEFAULT_HEIGHT = 200

        private val MAX_ALPHA = 255
        private val STARTING_PROGRESS_ALPHA: Int = (0.3f * MAX_ALPHA).toInt()

        private val DECELERATE_INTERPOLATION_FACTOR = 2f
        private val INVALID_POINTER = -1
        private val DRAG_RATE = 0.5f

        /**最大角度*/
        private val MAX_PROGRESS_ANGLE = 0.8f
        /**缩小时间*/
        private val SCALE_DOWN_DURATION = 150
        /**动画时间*/
        private val ALPHA_ANIMATION_DURATION = 300
        /**激活触发时间*/
        private val ANIMATE_TO_TRIGGER_DURATION = 200
        /**动画开始时间*/
        private val ANIMATE_TO_START_DURATION = 200
        /**默认的旋转动画背景颜色*/
        private val CIRCLE_BG_LIGHT: Int = ColorUtil.parseColor("#FFFAFAFA")
        /**从视图顶部到进度旋转器应该停止的位置的默认偏移量*/
        private val DEFAULT_CIRCLE_TARGET = 64
        private val LAYOUT_ATTRS = intArrayOf(android.R.attr.enabled)

        /**标记 无状态（既不是上拉 也 不是下拉） */
        private val ACTION_NOT = -1
        /**标记 下拉状态 */
        private val ACTION_DOWN = 0
        /**标记 上拉状态 */
        private val ACTION_UP = 1

        private val LOG_TAG = SwipeRefreshLayout::class.java.simpleName
    }
}