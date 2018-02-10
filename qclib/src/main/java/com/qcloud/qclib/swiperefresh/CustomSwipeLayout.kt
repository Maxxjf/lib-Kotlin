package com.qcloud.qclib.swiperefresh

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.*
import android.util.AttributeSet
import android.view.*
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.view.animation.TranslateAnimation
import android.widget.AbsListView
import android.widget.LinearLayout
import com.qcloud.qclib.R
import com.qcloud.qclib.utils.ColorUtil
import com.qcloud.qclib.utils.DensityUtil
import timber.log.Timber

/**
 * 类说明：自定义SwipeRefreshLayout
 *      注：若嵌套ScrollView，建议使用NestedScrollView,若里面还嵌套RecyclerView，添加以下属性
 *      layoutManager.setSmoothScrollbarEnabled(true);
 *      layoutManager.setAutoMeasureEnabled(true);
 *      mRecyclerView.setNestedScrollingEnabled(false);
 * Author: Kuzan
 * Date: 2018/1/22 11:50.
 */
open class CustomSwipeLayout @JvmOverloads constructor(
        protected val mContext: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : ViewGroup(mContext, attrs, defStyleAttr), NestedScrollingParent, NestedScrollingChild {

    /**动画所在位置*/
    var animPosition: Int = ANIM_IN_LEFT

    /**动画偏移左边的距离*/
    private val animPaddingLeft = DensityUtil.dp2px(mContext, 20f)

    /**手势目标布局，整个布局*/
    private var mTarget: View? = null

    /**下拉刷新事件监听*/
    var onRefreshListener: OnRefreshListener? = null
    /**上拉加载更多事件监听*/
    var onLoadMoreListener: OnLoadMoreListener? = null
    /**底部状态监听器*/
    var onFooterStateListener: OnFooterStateListener? = null

    /**是否在刷新*/
    private var isRefreshing: Boolean = false

    private val mTouchSlop: Int = ViewConfiguration.get(mContext).scaledTouchSlop
    private val mMediumAnimationDuration: Int = resources.getInteger(android.R.integer.config_mediumAnimTime)

    private var mTotalDragDistance: Float = -1f

    /**嵌套滚动,用于在触摸事件处理程序去确定OverScroll的地方*/
    private var mTotalUnconsumed: Float = 0.0f
    private val mNestedScrollingParentHelper: NestedScrollingParentHelper = NestedScrollingParentHelper(this)
    private var mNestedScrollingChildHelper: NestedScrollingChildHelper = NestedScrollingChildHelper(this)
    private val mParentOffsetInWindow = IntArray(2)
    private var mNestedScrollInProgress: Boolean = false

    private var mCurrentTargetOffsetTop: Int = 0

    /**是否已确定起始偏移量*/
    private var mOriginalOffsetCalculated: Boolean = false

    private var mInitialMotionY: Float = 0f
    private var mInitialDownY: Float = 0f
    private var mIsBeingDragged: Boolean = false
    private var mActivePointerId: Int = INVALID_POINTER
    private var mScale: Boolean = false

    /**目标将返回到起始偏移量，在它被取消或刷新时触发*/
    private var mReturningToStart: Boolean = false
    private val mDecelerateInterpolator: DecelerateInterpolator = DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)
    private val layoutArrays = intArrayOf(android.R.attr.enabled)

    /**加载动画有关*/
    private var mCircleView: CircleImageView? = null
    private var mCircleViewIndex: Int = -1

    var mFrom: Int = 0
    var mOriginalOffsetTop: Int = 0
    private var mStartingScale: Float = 0f
    private var mProgress: MaterialProgressDrawable? = null
    private var mScaleAnimation: Animation? = null
    private var mScaleDownAnimation: Animation? = null
    private var mAlphaStartAnimation: Animation? = null
    private var mAlphaMaxAnimation: Animation? = null
    private var mScaleDownToStartAnimation: Animation? = null

    private var mSpinnerFinalOffset: Float = 0f
    private var mNotify: Boolean = false
    private var mCircleWidth: Int = 0
    private var mCircleHeight: Int = 0

    /**是否设置了自定义起始位置*/
    private var mUsingCustomStart: Boolean = false

    /**尾部容器*/
    private var mFooterLayout: FooterLayout? = null
    /**尾部View*/
    private var mFooter: View? = null
    /**尾部的高度*/
    private var mFooterHeight: Int = FOOTER_DEFAULT_HEIGHT

    /**滑动的偏移量*/
    private var mScrollOffset: Int = 0
    private var mPullRefreshEnable: Boolean = true
    /**是否可以上拉加载更多*/
    private var mPullLoadEnable: Boolean = false

    /**标记 无状态（既不是上拉 也 不是下拉）*/
    private val ACTION_NOT: Int = -1
    /**标记 下拉状态*/
    private val ACTION_DOWN: Int = 0
    /**标记 上拉状态*/
    private val ACTION_UP: Int = 1
    private var mCurrentAction: Int = ACTION_NOT

    /**是否已完成*/
    private var isConfirm: Boolean = false

    /**是否还有更多数据*/
    var isMore: Boolean = true

    /**Api11之前，alpha用于使进度循环代替规模出现*/
    private val isAlphaUsedForScale: Boolean
        @SuppressLint("ObsoleteSdkInt")
        get() = android.os.Build.VERSION.SDK_INT < 11

    /**获取显示为"刷新"布局的一部分的进度圆的直径。*/
    val progressCircleDiameter: Int
        get() = if (mCircleView != null) mCircleView!!.measuredHeight else 0

    private val mAnimateToCorrectPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val targetTop: Int
            val endTarget: Int = if (!mUsingCustomStart) {
                (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop)).toInt()
            } else {
                mSpinnerFinalOffset.toInt()
            }
            targetTop = mFrom + ((endTarget - mFrom) * interpolatedTime).toInt()
            val offset = targetTop - mCircleView!!.top
            setTargetOffsetTopAndBottom(offset, false)
            mProgress!!.setArrowScale(1 - interpolatedTime)
        }
    }

    private val mAnimateToStartPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            moveToStart(interpolatedTime)
        }
    }

    /**  刷新监听 */
    private val mRefreshingListener = object : MyAnimationListener {
        override fun onAnimationStart(var1: Animation?) {}

        override fun onAnimationRepeat(var1: Animation?) {}

        override fun onAnimationEnd(var1: Animation?) {
            if (isRefreshing) {
                // Make sure the progress view is fully visible
                mProgress!!.alpha = MAX_ALPHA
                mProgress!!.start()
                if (mNotify) {
                    onRefreshListener?.onRefresh()
                }
                mCurrentTargetOffsetTop = mCircleView!!.top
            } else {
                reset()
            }
        }
    }

    init {
        setWillNotDraw(false)

        val a = context.obtainStyledAttributes(attrs, layoutArrays)
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
        mNestedScrollingChildHelper

        isNestedScrollingEnabled = true
    }

    /**
     * 初始化底部
     */
    private fun createFooterLayout() {
        mFooterLayout = FooterLayout(mContext)
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
        mFooterLayout?.gravity = Gravity.CENTER or Gravity.BOTTOM
        mFooterLayout?.layoutParams = lp

        mFooterLayout?.setText(mContext.resources.getString(R.string.load_by_pull_up))
        mFooterLayout?.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrayDark))
        addView(mFooterLayout)
    }

    /**
     * 设置尾部
     * */
    fun setFooter(footer: View) {
        mFooter = footer

        mFooterLayout!!.setFooterView(mFooter)

        //获取尾部高度
        mFooterHeight = measureViewHeight(mFooterLayout!!)
    }

    /**
     * 计算尾部高度
     * */
    private fun measureViewHeight(view: View): Int {
        val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(width, height)
        return view.measuredHeight
    }

    /**
     * 重置动画
     * */
    private fun reset() {
        mCircleView?.clearAnimation()
        mProgress?.stop()
        mCircleView?.visibility = View.GONE
        setColorViewAlpha(MAX_ALPHA)
        if (mScale) {
            // 动画完成和视图隐藏
            setAnimationProgress(0f)
        } else {
            // 需要更新
            setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop, true)
        }
        mCurrentTargetOffsetTop = mCircleView!!.top
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        reset()
    }

    private fun setColorViewAlpha(targetAlpha: Int) {
        mCircleView!!.background.alpha = targetAlpha
        mProgress!!.alpha = targetAlpha
    }

    fun setProgressViewOffset(scale: Boolean, start: Int, end: Int) {
        mScale = scale
        mCircleView!!.visibility = View.GONE
        mCurrentTargetOffsetTop = start
        mOriginalOffsetTop = mCurrentTargetOffsetTop
        mSpinnerFinalOffset = end.toFloat()
        mUsingCustomStart = true
        mCircleView!!.invalidate()
    }

    fun setProgressViewEndTarget(scale: Boolean, end: Int) {
        mSpinnerFinalOffset = end.toFloat()
        mScale = scale
        mCircleView!!.invalidate()
    }

    /**
     * 设置旋转动画大小
     */
    fun setSize(size: Int) {
        if (size != LARGE && size != DEFAULT) {
            return
        }
        val metrics = resources.displayMetrics
        if (size == LARGE) {
            mCircleWidth = (CIRCLE_DIAMETER_LARGE * metrics.density).toInt()
            mCircleHeight = mCircleWidth
        } else {
            mCircleWidth = (CIRCLE_DIAMETER * metrics.density).toInt()
            mCircleHeight = mCircleWidth
        }

        mCircleView!!.setImageDrawable(null)
        mProgress!!.updateSizes(size)
        mCircleView!!.setImageDrawable(mProgress)
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

    /**
     * 加载动画
     * */
    fun setProgressView(mProgress: MaterialProgressDrawable) {
        this.mProgress = mProgress
        mCircleView!!.setImageDrawable(mProgress)
    }

    /**
     * 创建加载动画
     * */
    private fun createProgressView() {
        mCircleView = CircleImageView(mContext, CIRCLE_BG_LIGHT, (CIRCLE_DIAMETER / 2).toFloat())

        val drawable = CustomProgressDrawable(mContext, this)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.default_refresh_icon)
        drawable.setBitmap(bitmap)
        mProgress = drawable

        mCircleView?.setImageDrawable(mProgress)
        mCircleView?.visibility = View.GONE

        addView(mCircleView)
    }

    /**
     * 设置是否启用上拉功能
     *
     * @param isLoadMore   是否开启上拉功能 默认不开启
     */
    fun setLoadMore(isLoadMore: Boolean) {
        mPullLoadEnable = isLoadMore
    }

    private fun startScaleUpAnimation(listener: MyAnimationListener?) {
        mCircleView!!.visibility = View.VISIBLE
        if (!isAlphaUsedForScale) {
            mProgress!!.alpha = MAX_ALPHA
        }
        mScaleAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationProgress(interpolatedTime)
            }
        }
        mScaleAnimation!!.duration = mMediumAnimationDuration.toLong()
        if (listener != null) {
            mCircleView!!.animationListener = listener
        }
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(mScaleAnimation)
    }

    /**
     * Pre API 11
     * @param progress
     */
    private fun setAnimationProgress(progress: Float) {
        if (isAlphaUsedForScale) {
            setColorViewAlpha((progress * MAX_ALPHA).toInt())
        } else {
            ViewCompat.setScaleX(mCircleView, progress)
            ViewCompat.setScaleY(mCircleView, progress)
        }
    }

    /**
     * 通知窗口，刷新状态改变
     *
     * @param refreshing 视图是否应该显示刷新进度。
     */
    private fun setRefreshing(refreshing: Boolean) {
        Timber.e("======>>>refreshing = $refreshing")
        if (refreshing && isRefreshing != refreshing) {
            isRefreshing = refreshing
            val endTarget: Int = if (!mUsingCustomStart) {
                (mSpinnerFinalOffset + mOriginalOffsetTop).toInt()
            } else {
                mSpinnerFinalOffset.toInt()
            }
            // 更新状态
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop, true)
            mNotify = false
            startScaleUpAnimation(mRefreshingListener)
        } else {
            // 刷新
            setRefreshing(refreshing, false)
        }
    }


    /**
     * 刷新状态
     * */
    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (isRefreshing != refreshing) {
            mNotify = notify
            ensureTarget()
            isRefreshing = refreshing
            if (isRefreshing) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshingListener)
            } else {
                startScaleDownAnimation(mRefreshingListener)
            }
        }
    }

    private fun startScaleDownAnimation(listener: MyAnimationListener?) {
        //  最终的偏移量就是mCircleView距离顶部的高度
        val deltaY = -mCircleView!!.bottom
        mScaleDownAnimation = TranslateAnimation(0f, 0f, 0f, deltaY.toFloat())
        mScaleDownAnimation!!.duration = 500
        mCircleView!!.animationListener = listener
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(mScaleDownAnimation)
    }

    private fun startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress!!.alpha, STARTING_PROGRESS_ALPHA)
    }

    private fun startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress!!.alpha, MAX_ALPHA)
    }

    private fun startAlphaAnimation(startingAlpha: Int, endingAlpha: Int): Animation? {
        if (mScale && isAlphaUsedForScale) {
            return null
        }
        val alpha = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                mProgress!!.alpha = (startingAlpha + (endingAlpha - startingAlpha) * interpolatedTime).toInt()
            }
        }
        alpha.duration = ALPHA_ANIMATION_DURATION.toLong()

        mCircleView!!.animationListener = null
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(alpha)

        return alpha
    }

    @Deprecated("")
    fun setProgressBackgroundColor(colorRes: Int) {
        setProgressBackgroundColorSchemeResource(colorRes)
    }

    /**
     * 设置加载动画的背景颜色。
     *
     * @param colorRes Resource id of the color.
     */
    fun setProgressBackgroundColorSchemeResource(@ColorRes colorRes: Int) {
        setProgressBackgroundColorSchemeColor(ContextCompat.getColor(mContext, colorRes))
    }

    /**
     * 设置加载动画的背景颜色。
     *
     * @param color
     */
    fun setProgressBackgroundColorSchemeColor(@ColorInt color: Int) {
        mCircleView?.setBackgroundColor(color)
        mProgress?.setBackgroundColor(color)
    }

    /**
     * 设置用于从色彩资源进度动画色彩资源。第一种颜色也将是根据用户刷卡手势而增长的颜色。
     *
     * @param colorResIds
     */
    fun setColorSchemeResources(@ColorRes vararg colorResIds: Int) {
        val res = resources
        val colorRes = IntArray(colorResIds.size)
        for (i in colorResIds.indices) {
            colorRes[i] = res.getColor(colorResIds[i])
        }
        setColorSchemeColors(*colorRes)
    }

    /**
     * 设置用于从色彩资源进度动画色彩资源。第一种颜色也将是根据用户刷卡手势而增长的颜色。
     *
     * @param colors
     */
    @SuppressLint("SupportAnnotationUsage")
    @ColorInt
    fun setColorSchemeColors(vararg colors: Int) {
        ensureTarget()
        mProgress!!.setColorSchemeColors(*colors)
    }

    /**
     * 返回是否正在刷新
     */
    fun isRefreshing(): Boolean {
        return isRefreshing
    }

    /**
     * 获取内容模块
     * */
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

    fun setDistanceToTriggerSync(distance: Int) {
        mTotalDragDistance = distance.toFloat()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = measuredWidth
        val height = measuredHeight
        if (childCount == 0) {
            return
        }
        // 添加了空布局，要重新获取target
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

        // 内容
        child!!.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)

        val circleWidth = mCircleView!!.measuredWidth
        val circleHeight = mCircleView!!.measuredHeight
        if (animPosition == ANIM_IN_CENTER) {
            // 加载动画在中间
            mCircleView!!.layout(width / 2 - circleWidth / 2, mCurrentTargetOffsetTop,
                    width / 2 + circleWidth / 2, mCurrentTargetOffsetTop + circleHeight)
        } else {
            // 加载动画在左边
            mCircleView!!.layout(animPaddingLeft, mCurrentTargetOffsetTop,
                    animPaddingLeft + circleWidth, mCurrentTargetOffsetTop + circleHeight)
        }

        // 底部
        mFooterLayout!!.layout(paddingLeft, measuredHeight, paddingLeft + width, childHeight + mFooterHeight)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
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
        // 获取加载动画所在的地方
        mCircleViewIndex = (0 until childCount).firstOrNull { getChildAt(it) === mCircleView } ?: -1
    }

    /**
     * @return 此布局的子视图是否可以向上滚动。重写此如果孩子的观点是一个自定义视图
     */
    @SuppressLint("ObsoleteSdkInt")
    fun canChildScrollDown(): Boolean {
        if (mTarget == null) {
            return false
        }
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (mTarget is AbsListView) {
                val absListView = mTarget as AbsListView?
                absListView!!.childCount > 0 && (absListView.firstVisiblePosition > 0 || absListView.getChildAt(0)
                        .top < absListView.paddingTop)
            } else {
                ViewCompat.canScrollVertically(mTarget, -1) || mTarget!!.scrollY > 0
            }
        } else {
            ViewCompat.canScrollVertically(mTarget, -1)
        }
    }

    /**
     * 判断是否可以上拉
     * */
    @SuppressLint("ObsoleteSdkInt")
    fun canChildScrollUp(): Boolean {
        return if (mTarget == null) {
            false
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.canScrollVertically(mTarget, 1)
        } else if (mTarget is AbsListView) {
            val absListView = mTarget as AbsListView?
            if (absListView!!.childCount <= 0) {
                false
            } else {
                val lastChildBottom = absListView.getChildAt(absListView.childCount - 1).bottom
                absListView.lastVisiblePosition == absListView.adapter.count - 1 && lastChildBottom <= absListView.measuredHeight
            }
        } else {
            ViewCompat.canScrollVertically(mTarget, 1) || mTarget!!.scrollY > 0
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        ensureTarget()

        val action = MotionEventCompat.getActionMasked(ev)

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }

        if (!isEnabled || mReturningToStart || canChildScrollDown()
                || isRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView!!.top, true)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
                val initialDownY = getMotionEventY(ev, mActivePointerId)
                if (initialDownY == -1f) {
                    return false
                }
                mInitialDownY = initialDownY
            }

            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_POINTER) {
                    return false
                }

                val y = getMotionEventY(ev, mActivePointerId)
                if (y == -1f) {
                    return false
                }
                val yDiff = y - mInitialDownY
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mInitialMotionY = mInitialDownY + mTouchSlop
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

    @SuppressLint("NewApi")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(ev)
        val pointerIndex: Int

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }

        if (!isEnabled || mReturningToStart || canChildScrollDown() || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
            }

            MotionEvent.ACTION_MOVE -> {
                // 下拉
                pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                if (pointerIndex < 0) {
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
                pointerIndex = MotionEventCompat.getActionIndex(ev)
                if (pointerIndex < 0) {
                    return false
                }
                mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
            }

            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP -> {
                // 手指松开时启动动画回到头部
                if (mTarget != null) {
                    mTarget!!.animate().translationY(0f).setDuration(200).start()
                }
                pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                if (pointerIndex < 0) {
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

    private fun getMotionEventY(ev: MotionEvent, activePointerId: Int): Float {
        val index = MotionEventCompat.findPointerIndex(ev, activePointerId)
        return if (index < 0) {
            -1f
        } else {
            MotionEventCompat.getY(ev, index)
        }
    }

    override fun requestDisallowInterceptTouchEvent(b: Boolean) {
        if (android.os.Build.VERSION.SDK_INT < 21 && mTarget is AbsListView || mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget!!)) {
        } else {
            super.requestDisallowInterceptTouchEvent(b)
        }
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return (isEnabled && canChildScrollDown() && !mReturningToStart && !isRefreshing
                && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
        startNestedScroll(axes and ViewCompat.SCROLL_AXIS_VERTICAL)
        mTotalUnconsumed = 0f
        mNestedScrollInProgress = true
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (mPullRefreshEnable || mPullLoadEnable) {
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

    /**
     * 移动布局
     * */
    private fun moveGuidanceView(distanceY: Float): Boolean {
        if (isRefreshing) {
            return false
        } else {
            val lp: ViewGroup.LayoutParams

            if (!canChildScrollDown() && mPullRefreshEnable && mCurrentAction == ACTION_DOWN) {
                // 下拉
                return true
            } else if (!canChildScrollUp() && mPullLoadEnable && mCurrentAction == ACTION_UP) {
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
     * */
    private fun moveTargetView(offset: Int) {
        if (offset > 0 && mCurrentAction != ACTION_UP) {
            return
        }

        mScrollOffset = Math.abs(offset)

        scrollTo(0, offset)

        if (isMore) {
            onFooterStateListener?.onScrollChange(mFooter, mScrollOffset,
                    if (mScrollOffset >= mFooterHeight) FOOTER_DEFAULT_HEIGHT else mScrollOffset * FOOTER_DEFAULT_HEIGHT / mFooterHeight)
        }
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

    /**
     * 处理事件
     */
    private fun handlerAction() {
        if (!isRefreshing) {
            isConfirm = false
            val lp: ViewGroup.LayoutParams
            // 下拉
            if (mPullRefreshEnable && mCurrentAction == ACTION_DOWN) {
                // 不处理
            }
            // 上拉
            if (mPullLoadEnable && mCurrentAction == ACTION_UP) {
                lp = mFooterLayout!!.layoutParams
                if (lp.height.toFloat() >= mFooterHeight) {
                    // 开始执行加载更多
                    if (isMore) {
                        startLoadMore(lp.height)
                    }

                    // 刷新状态
                    if (isMore) {
                        onFooterStateListener?.onLoading(mFooter)
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
     * */
    open fun startLoadMore(footerViewHeight: Int) {
        this.isRefreshing = true
        val animator = ValueAnimator.ofFloat(footerViewHeight.toFloat(), FOOTER_DEFAULT_HEIGHT.toFloat())
        animator.addUpdateListener { animation ->
            val lp = mFooterLayout!!.layoutParams
            lp.height = (animation.animatedValue as Float).toFloat().toInt()
            mFooterLayout!!.layoutParams = lp
            moveTargetView(lp.height)
        }

        animator.addListener(object : LoadMoreAnimatorListener() {
            override fun onAnimationEnd(var1: Animator) {
                super.onAnimationEnd(var1)
                onLoadMoreListener?.onLoadMore()
            }
        })
        animator.duration = 300L
        animator.start()
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
     * 重置加载状态
     * */
    private fun resetLoadMoreState() {
        isRefreshing = false
        isConfirm = false
        mCurrentAction = ACTION_NOT

        moveTargetView(0)
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

    private fun isAnimationRunning(animation: Animation?): Boolean {
        return animation != null && animation.hasStarted() && !animation.hasEnded()
    }

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
        if (!mScale) {
            ViewCompat.setScaleX(mCircleView, 1f)
            ViewCompat.setScaleY(mCircleView, 1f)
        }

        if (mScale) {
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
        // 经过一堆数学处理后的rotation
        val rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f
        mProgress!!.setProgressRotation(rotation)
        // 最终刷新的位置
        val endTarget: Int = if (!mUsingCustomStart) {
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
            isRefreshing = false
            mProgress!!.setStartEndTrim(0f, 0f)
            var listener: MyAnimationListener? = null
            if (!mScale) {
                listener = object : MyAnimationListener {

                    override fun onAnimationStart(var1: Animation?) {}

                    override fun onAnimationEnd(var1: Animation?) {
                        if (!mScale) {
                            startScaleDownAnimation(null)
                        }
                    }

                    override fun onAnimationRepeat(var1: Animation?) {}
                }
            }
            animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener)
            mProgress!!.showArrow(false)
        }
    }

    private fun animateOffsetToCorrectPosition(from: Int, listener: MyAnimationListener?) {
        mFrom = from
        mAnimateToCorrectPosition.reset()
        mAnimateToCorrectPosition.duration = ANIMATE_TO_TRIGGER_DURATION.toLong()
        mAnimateToCorrectPosition.interpolator = mDecelerateInterpolator
        if (listener != null) {
            mCircleView!!.animationListener = listener
        }
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(mAnimateToCorrectPosition)
    }

    private fun animateOffsetToStartPosition(from: Int, listener: MyAnimationListener?) {
        if (mScale) {
            // Scale the item back down
            startScaleDownReturnToStartAnimation(from, listener)
        } else {
            mFrom = from
            mAnimateToStartPosition.reset()
            mAnimateToStartPosition.duration = ANIMATE_TO_START_DURATION.toLong()
            mAnimateToStartPosition.interpolator = mDecelerateInterpolator
            if (listener != null) {
                mCircleView!!.animationListener = listener
            }
            mCircleView!!.clearAnimation()
            mCircleView!!.startAnimation(mAnimateToStartPosition)
        }
    }

    private fun moveToStart(interpolatedTime: Float) {
        val targetTop = mFrom + ((mOriginalOffsetTop - mFrom) * interpolatedTime).toInt()
        val offset = targetTop - mCircleView!!.top
        setTargetOffsetTopAndBottom(offset, false)
    }

    private fun startScaleDownReturnToStartAnimation(from: Int, listener: MyAnimationListener?) {
        mFrom = from
        mStartingScale = if (isAlphaUsedForScale) {
            mProgress!!.alpha.toFloat()
        } else {
            ViewCompat.getScaleX(mCircleView)
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
            mCircleView!!.animationListener = listener
        }
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(mScaleDownToStartAnimation)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setTargetOffsetTopAndBottom(offset: Int, requiresUpdate: Boolean) {
        mCircleView!!.bringToFront()
        mCircleView!!.offsetTopAndBottom(offset)
        mCurrentTargetOffsetTop = mCircleView!!.top
        if (requiresUpdate && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            invalidate()
        }
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = MotionEventCompat.getActionIndex(ev)
        val pointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex)
        }
    }

    /**
     * 是否上拉加载更多
     * */
    open fun pullUp(): Boolean {
        return mCurrentAction != ACTION_DOWN && mPullLoadEnable
    }

    /**
     * 通知加载完成
     */
    fun loadedFinish() {
        if (isRefreshing) {
            if (mCurrentAction == ACTION_UP) {
                loadMoreFinish()
                isRefreshing = false
            } else {
                setRefreshing(false)
            }
        }
    }

    /**
     * 完成加载更多
     * */
    private fun loadMoreFinish() {
        if (mCurrentAction == ACTION_UP) {
            resetFootView(if (mFooterLayout == null) 0 else mFooterLayout!!.measuredHeight)

            if (isMore) {
                onFooterStateListener?.onRetract(mFooter)
            }
        }
    }

    fun isMore(isMore: Boolean) {
        this.isMore = isMore
        if (isMore) {
            onFooterStateListener?.onHasMore(mFooter)
        } else {
            onFooterStateListener?.onNoMore(mFooter)
        }
    }

    open fun resetTarget() {
        mTarget = null
    }

    /**
     * 下拉刷新
     * */
    interface OnRefreshListener {
        fun onRefresh()
    }

    /**
     * 上拉加载更多
     * */
    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    companion object {
        /**加载动画大图*/
        val LARGE = MaterialProgressDrawable.LARGE
        val CIRCLE_DIAMETER_LARGE = MaterialProgressDrawable.CIRCLE_DIAMETER_LARGE
        /**加载动画默认图*/
        val DEFAULT = MaterialProgressDrawable.DEFAULT
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
        private val CIRCLE_BG_LIGHT: Int = ColorUtil.parseColor("0xFFFAFAFA")
        /**从视图顶部到进度旋转器应该停止的位置的默认偏移量*/
        private val DEFAULT_CIRCLE_TARGET = 64
    }
}