package com.qcloud.qclib.pullrefresh

import android.content.Context
import android.support.annotation.IntRange
import android.support.annotation.NonNull
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import com.qcloud.qclib.pullrefresh.listener.OnFooterStateListener
import com.qcloud.qclib.pullrefresh.listener.OnHeaderStateListener
import com.qcloud.qclib.pullrefresh.listener.OnPullDownRefreshListener
import com.qcloud.qclib.pullrefresh.listener.OnPullUpLoadMoreListener

/**
 * 类说明：自定义的上下拉刷新控件
 * Author: Kuzan
 * Date: 2018/1/12 16:58.
 */
open class PullRefreshView @JvmOverloads constructor(
        protected val mContext: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0): ViewGroup(mContext, attrs, defStyleAttr) {

    /**头部容器*/
    private var mHeadLayout: LinearLayout? = null
    /**头部View*/
    private var mHead: View? = null
    /**头部的高度*/
    private var mHeadHeight: Int = Companion.HEAD_DEFAULT_HEIGHT

    /**底部容器*/
    private var mFootLayout: LinearLayout? = null
    /**底部View*/
    private var mFoot: View? = null
    /**底部的高度*/
    private var mFootHeight: Int = Companion.FOOT_DEFAULT_HEIGHT

    /**滑动的偏移量*/
    private var mScrollOffset = 0

    /**标记 无状态（既不是上拉 也 不是下拉）*/
    private val STATE_NOT = -1
    /**标记 上拉状态*/
    private val STATE_UP = 1
    /**标记 下拉状态*/
    private val STATE_DOWN = 2
    /**当前状态*/
    private var mCurrentState = STATE_NOT

    /**是否处于下拉 正在更新状态*/
    var isPullDown = false
    /**是否处于上拉 正在加载状态*/
    var isPullUp = false

    /**是否启用下拉功能（默认开启）*/
    private var isDownRefresh = true
    /**是否启用上拉功能（默认不开启）*/
    private var isUpLoadMore = false
    /**加载状态*/
    private var isLoading = false

    /**阻力*/
    private var mDamp = 4

    /**头部状态监听器*/
    var onHeaderStateListener: OnHeaderStateListener? = null
    /**底部状态监听器*/
    var onFooterStateListener: OnFooterStateListener? = null
    /**上拉监听器*/
    var onPullUpLoadMoreListener: OnPullUpLoadMoreListener? = null
    /**下拉监听器*/
    var onPullDownRefreshListener: OnPullDownRefreshListener? = null

    /**是否还有更多数据*/
    private var isMore = true

    init {
        clipToPadding = false
        initHeadLayout()
        initFootLayout()
    }

    /**
     * 初始化头部
     */
    private fun initHeadLayout() {
        mHeadLayout = LinearLayout(mContext)
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        mHeadLayout?.gravity = Gravity.CENTER or Gravity.BOTTOM
        mHeadLayout?.layoutParams = lp
        addView(mHeadLayout)
    }

    /**
     * 设置头部View
     *
     * @param head
     */
    fun setHead(@NonNull head: View) {
        mHead = head
        mHeadLayout?.removeAllViews()
        mHeadLayout?.addView(mHead)
        mHeadHeight = measureViewHeight(mHead!!)
        log("mHeadHeight" + mHeadHeight)
        if (isPullDown) {
            scroll(-mHeadHeight)
        }
    }

    /**
     * 初始化底部
     */
    private fun initFootLayout() {
        mFootLayout = LinearLayout(mContext)
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        mFootLayout?.gravity = Gravity.CENTER or Gravity.BOTTOM
        mFootLayout?.layoutParams = lp
        addView(mFootLayout)
    }

    /**
     * 设置底部View
     *
     * @param foot
     */
    fun setFoot(@NonNull foot: View) {
        this.mFoot = foot
        mFootLayout?.removeAllViews()
        mFootLayout?.addView(mFoot)

        // 获取底部高度
        mFootHeight = measureViewHeight(mFootLayout!!)
        log("mFootHeight" + mFootHeight)
        if (isPullUp) {
            scroll(mFootHeight)
        }
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        // 布局头部
        val head = getChildAt(0)
        head.layout(paddingLeft, -mHeadHeight, paddingLeft + head.measuredWidth, 0)

        // 布局底部
        val foot = getChildAt(1)
        foot.layout(paddingLeft, measuredHeight, paddingLeft + foot.measuredWidth, measuredHeight + mFootHeight)

        // 布局内容容器
        val count = childCount
        if (count > 2) {
            val content = getChildAt(2)
            content.layout(paddingLeft, paddingTop, paddingLeft + content.measuredWidth, paddingTop + content.measuredHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 测量头部高度
        val head = getChildAt(0)
        measureChild(head, widthMeasureSpec, heightMeasureSpec)

        // 测量底部高度
        val foot = getChildAt(1)
        measureChild(foot, widthMeasureSpec, heightMeasureSpec)

        // 测量内容肉器宽高
        val count = childCount
        var contentHeight = 0
        var contentWidth = 0

        if (count > 2) {
            val content = getChildAt(2)
            measureChild(content, widthMeasureSpec, heightMeasureSpec)
            contentHeight = content.measuredHeight
            contentWidth = content.measuredWidth
        }
        // 设置PullRefreshView宽高
        setMeasuredDimension(measureWidth(widthMeasureSpec, contentWidth), measureHeight(heightMeasureSpec, contentHeight))
    }

    var mY = 0
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_MOVE -> {
                if (mY > y) {
                    if (mCurrentState == STATE_UP) {
                        scroll((mY - y) / mDamp)
                    }
                } else if (mCurrentState == STATE_DOWN) {
                    scroll((mY - y) / mDamp)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isPullDown && !isPullUp) {
                    if (mCurrentState == STATE_DOWN) {
                        if (mScrollOffset < mHeadHeight) {
                            restore()
                        } else {
                            triggerPullDownRefresh()
                        }
                    } else if (mCurrentState == STATE_UP) {
                        if (mScrollOffset < mFootHeight) {
                            restore()
                        } else {
                            triggerPullUpLoadMore()
                        }
                    } else {
                        restore()
                    }
                }
                mY = 0
            }
            else -> {}
        }
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val y = ev.y.toInt()

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mY = ev.y.toInt()
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                if (isLoading) {
                    return false
                }
                if (pullDown() && y - mY > 20) {
                    mCurrentState = STATE_DOWN
                    return true
                }
                if (pullUp() && mY - y > 20) {
                    mCurrentState = STATE_UP
                    return true
                }
                return false
            }
            MotionEvent.ACTION_UP -> return false
            else -> return false
        }
    }

    /**
     * 计算View高度
     * */
    private fun measureViewHeight(@NonNull view: View): Int {
        val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(width, height)
        return view.measuredHeight
    }

    /**
     * 计算宽度
     * */
    private fun measureWidth(measureSpec: Int, contentWidth: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        return if (specMode == MeasureSpec.EXACTLY) {
            specSize
        } else {
            val realWidth = contentWidth + paddingLeft + paddingRight
            if (specMode == MeasureSpec.AT_MOST) {
                Math.min(realWidth, specSize)
            } else {
                realWidth
            }
        }
    }

    /**
     * 计算高度
     * */
    private fun measureHeight(measureSpec: Int, contentHeight: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        return if (specMode == MeasureSpec.EXACTLY) {
            specSize
        } else {
            val realHeight = contentHeight + paddingTop + paddingBottom
            if (specMode == MeasureSpec.AT_MOST) {
                Math.min(realHeight, specSize)
            } else {
                realHeight
            }
        }
    }

    /**
     * 还原
     */
    private fun restore() {
        mCurrentState = STATE_NOT
        scroll(0)
    }

    /**
     * 滑动处理
     *
     * @param offset
     */
    private fun scroll(offset: Int = 0) {
        // 正在下拉
        if (offset < 0 && !isDownRefresh) {
            return
        }
        // 正在上拉
        if (offset > 0 && !isUpLoadMore) {
            return
        }
        scrollTo(0, offset)
        mScrollOffset = Math.abs(offset)

        if (mCurrentState == STATE_DOWN && onHeaderStateListener != null) {
            onHeaderStateListener?.onScrollChange(mHead, mScrollOffset,
                    if (mScrollOffset >= mHeadHeight) Companion.HEAD_DEFAULT_HEIGHT else mScrollOffset * Companion.HEAD_DEFAULT_HEIGHT / mHeadHeight)
        }
        if (mCurrentState == STATE_UP && onFooterStateListener != null && isMore) {
            onFooterStateListener?.onScrollChange(mFoot!!, mScrollOffset,
                    if (mScrollOffset >= mFootHeight) Companion.FOOT_DEFAULT_HEIGHT else mScrollOffset * Companion.FOOT_DEFAULT_HEIGHT / mFootHeight)
        }
    }

    /**
     * 设置是否启用上下拉功能
     *
     * @param isDownRefresh     是否开启下拉功能 默认开启
     * @param isUpLoadMore      是否开启上拉功能 默认不开启
     */
    fun setRefresh(isDownRefresh: Boolean = true, isUpLoadMore: Boolean = false) {
        this.isDownRefresh = isDownRefresh
        this.isUpLoadMore = isUpLoadMore
    }

    /**
     * 通知加载完成
     */
    fun loadedFinish() {
        restore()
        if (isLoading) {
            isLoading = false
            if (isPullUp) {
                isPullUp = false
                if (onFooterStateListener != null && isMore) {
                    onFooterStateListener?.onRetractFoot(mFoot)
                }
            } else if (isPullDown) {
                isPullDown = false
                onHeaderStateListener?.onRetractHead(mHead)
            }
        }
    }

    /**
     * 是否有更多
     * */
    fun isMore(isMore: Boolean) {
        this.isMore = isMore
        if (isMore) {
            onFooterStateListener?.onHasMore(mFoot)
        } else {
            onFooterStateListener?.onNotMore(mFoot)
        }
    }

    /**
     * 设置拉动阻力 （1到10）
     *
     * @param damp
     */
    fun setDamp(@IntRange(from = 1, to = 10) damp: Int) {
        if (damp < 1) {
            mDamp = 1
        } else if (damp > 10) {
            mDamp = 10
        } else {
            mDamp = damp
        }
    }

    /**
     * 触发下拉刷新
     */
    open fun triggerPullDownRefresh() {
        if (!isDownRefresh) {
            return
        }
        if (!isLoading) {
            isLoading = true
            isPullDown = true
            mCurrentState = STATE_NOT
            scroll(-mHeadHeight)

            onHeaderStateListener?.onRefreshHead(mHead)

            onPullDownRefreshListener?.onRefresh()
        }
    }

    /**
     * 触发上拉加载
     */
    open fun triggerPullUpLoadMore() {
        if (!isUpLoadMore) {
            return
        }
        if (!isLoading) {
            isLoading = true
            isPullUp = true
            mCurrentState = STATE_NOT
            scroll(mFootHeight)
            if (onFooterStateListener != null && isMore) {
                onFooterStateListener?.onRefreshFoot(mFoot)
            }
            if (isMore) {
                onPullUpLoadMoreListener?.onLoadMore()
            } else {
                loadedFinish()
            }
        }
    }

    /**
     * 是否下拉
     * */
    open fun pullDown(): Boolean {
        return mCurrentState != STATE_UP && isDownRefresh && isTop()
    }

    /**
     * 是否上拉
     * */
    open fun pullUp(): Boolean {
        return mCurrentState != STATE_DOWN && isUpLoadMore && isBottom()
    }

    /**
     * 是否在顶部
     * */
    open fun isTop(): Boolean {
        if (childCount < 2) {
            return true
        }
        val view = getChildAt(2)
        return if (view is ViewGroup) {
            if (view is ScrollView) {
                view.scrollY <= 0
            } else {
                isChildTop(view)
            }
        } else {
            true
        }
    }

    /**
     * 是否子布局在顶部
     * */
    open fun isChildTop(@NonNull viewGroup: ViewGroup): Boolean {
        var minY = 0
        val count = viewGroup.childCount
        for (i in 0 until count) {
            val view = viewGroup.getChildAt(i)
            val lp = view.layoutParams
            val topMargin = if (lp is MarginLayoutParams) {
                lp.topMargin
            } else {
                0
            }
            val top = view.top - topMargin
            minY = Math.min(minY, top)
        }
        return minY >= 0
    }

    /**
     * 是否在底部
     * */
    open fun isBottom(): Boolean {
        if (childCount < 2) {
            return false
        }
        val view = getChildAt(2)
        return if (view is ViewGroup) {
            if (view is ScrollView) {
                if (view.childCount > 0) {
                    view.scrollY >= view.getChildAt(0).height - view.height
                } else {
                    true
                }
            } else {
                isChildBottom(view)
            }
        } else {
            true
        }
    }

    /**
     * 是否子布局在底部
     * */
    open fun isChildBottom(@NonNull viewGroup: ViewGroup): Boolean {
        var maxY = 0
        val count = viewGroup.childCount
        if (count == 0) {
            return false
        }
        for (i in 0 until count) {
            val view = viewGroup.getChildAt(i)
            val lp = view.layoutParams
            val bottomMargin = if (lp is MarginLayoutParams) {
                lp.bottomMargin
            } else {
                0
            }
            val bottom = view.bottom + bottomMargin
            maxY = Math.max(maxY, bottom)
        }
        val h = viewGroup.measuredHeight - viewGroup.paddingBottom
        return maxY <= h
    }

    /**
     * 是志打印
     * */
    fun log(message: String) {
        Log.e(TAG, message)
    }

    /**
     * 静态变量
     * */
    companion object {
        private val TAG = PullRefreshView::class.java.simpleName

        /**头部下拉的最小高度*/
        private val HEAD_DEFAULT_HEIGHT: Int = 100
        /**底部上拉的最小高度*/
        private val FOOT_DEFAULT_HEIGHT: Int = 100
    }
}