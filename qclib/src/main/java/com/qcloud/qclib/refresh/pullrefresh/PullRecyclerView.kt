package com.qcloud.qclib.pullrefresh

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.qcloud.qclib.R

/**
 * 类说明：下拉刷新上接加载更多的RecyclerView
 * Author: Kuzan
 * Date: 2018/1/15 11:48.
 */
class PullRefreshRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0): PullRefreshView(context, attrs, defStyleAttr) {

    /**内置的RecyclerView*/
    var mRecyclerView: RecyclerView? = null
    /**是否自动上拉刷新*/
    var isAutomaticUp: Boolean = false
    /**可见的最后一个item*/
    private var lastVisibleItem: Int = 0
    /**可见的第一个item*/
    private var firstVisibleItem: Int = 0
    /**空数据提示布局容器*/
    private var mEmptyLayout: LinearLayout? = null

    init {
        mRecyclerView = LayoutInflater.from(mContext).inflate(R.layout.layout_recycler_view, null, false) as RecyclerView
        this.addView(mRecyclerView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        setScrollBarEnabled(false)
        initListener()
    }

    /**
     * 初始化监听事件
     * */
    private fun initListener() {
        mRecyclerView?.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (isAutomaticUp && newState == RecyclerView.SCROLL_STATE_IDLE && pullUp()) {
                    triggerPullUpLoadMore()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                getVisibleItem()
            }
        })
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> getVisibleItem()
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun isChildBottom(viewGroup: ViewGroup): Boolean {
        val childNum = viewGroup.childCount
        return if (childNum == 0 || mRecyclerView?.childCount == 0) {
            false
        } else {
            if (mRecyclerView == getChildAt(2) && lastVisibleItem != mRecyclerView?.adapter!!.itemCount - 1) {
                false
            } else {
                val view = viewGroup.getChildAt(childNum - 1)
                val bottomMargin = ((view.layoutParams) as ViewGroup.MarginLayoutParams).bottomMargin
                view.bottom + bottomMargin + viewGroup.paddingBottom <= height
            }
        }
    }

    override fun isChildTop(viewGroup: ViewGroup): Boolean {
        val childNum = viewGroup.childCount
        return if (childNum == 0) {
            true
        } else {
            if (mRecyclerView == getChildAt(2) && firstVisibleItem != 0) {
                false
            } else {
                val view = viewGroup.getChildAt(0)
                val topMargin = (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin
                view.top - topMargin - viewGroup.paddingTop >= 0
            }
        }
    }

    override fun triggerPullDownRefresh() {
        mRecyclerView?.scrollToPosition(0)
        super.triggerPullDownRefresh()
    }

    /**
     * 获取当前第一个显示的item 和 最后一个显示的item.
     */
    private fun getVisibleItem() {
        val manager = mRecyclerView?.layoutManager
        if (manager != null) {
            when (manager) {
                is LinearLayoutManager -> {
                    lastVisibleItem = manager.findLastVisibleItemPosition()
                    firstVisibleItem = manager.findFirstVisibleItemPosition()
                }
                is GridLayoutManager -> {
                    lastVisibleItem = manager.findLastVisibleItemPosition()
                    firstVisibleItem = manager.findFirstVisibleItemPosition()
                }
                is StaggeredGridLayoutManager -> {
                    val lastPositions = IntArray(manager.spanCount)
                    manager.findLastVisibleItemPositions(lastPositions)
                    lastVisibleItem = getMax(lastPositions)

                    val firstPositions = IntArray(manager.spanCount)
                    manager.findFirstVisibleItemPositions(firstPositions)
                    firstVisibleItem = getMin(firstPositions)
                }
            }
        }
    }

    private fun getMax(arrs: IntArray): Int {
        var max = arrs[0]
        (1 until arrs.size)
                .asSequence()
                .filter { arrs[it] > max }
                .forEach { max = arrs[it] }
        return max
    }

    private fun getMin(arrs: IntArray): Int {
        var min = arrs[0]
        (1 until arrs.size)
                .asSequence()
                .filter { arrs[it] < min }
                .forEach { min = arrs[it] }

        return min
    }

    /**
     * 初始化空列表布局
     * */
    private fun initEmptyLayout() {
        val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mEmptyLayout = LinearLayout(mContext)
        mEmptyLayout?.layoutParams = lp
        mEmptyLayout?.setOnTouchListener { _, _ -> true }
    }

    /**
     * 设置空布局
     *
     * @param emptyView
     * @param layoutGravity 空布局在父布局的方向
     */
    fun setEmptyView(emptyView: View, layoutGravity: Int) {
        if (mEmptyLayout == null) {
            initEmptyLayout()
        }
        mEmptyLayout?.gravity = layoutGravity
        mEmptyLayout?.addView(emptyView)
    }

    /**
     * 显示空布局
     */
    fun showEmptyView() {
        if (mEmptyLayout != null && mEmptyLayout?.parent == null) {
            addView(mEmptyLayout, 2)
        }
        if (mRecyclerView?.parent != null) {
            removeView(mRecyclerView)
        }
    }

    /**
     * 隐藏空布局
     */
    fun hideEmptyView() {
        if (mRecyclerView?.parent == null) {
            addView(mRecyclerView, 2)
        }
        if (mEmptyLayout != null && mEmptyLayout?.parent != null) {
            removeView(mEmptyLayout)
        }
    }

    /**
     * 移动到固定位置
     * */
    fun smoothScrollToPosition(position: Int) {
        if (mRecyclerView != null) {
            mRecyclerView?.smoothScrollToPosition(position)
        }
    }

    /*********** 提供一系列对内置RecyclerView的操作方法 **********/
    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        mRecyclerView?.layoutManager = layoutManager
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mRecyclerView?.adapter = adapter
    }

    fun setItemAnimator(animator: RecyclerView.ItemAnimator) {
        mRecyclerView?.itemAnimator = animator
    }

    fun addItemDecoration(decor: RecyclerView.ItemDecoration) {
        mRecyclerView?.addItemDecoration(decor)
    }

    fun addItemDecoration(decor: RecyclerView.ItemDecoration, index: Int) {
        mRecyclerView?.addItemDecoration(decor, index)
    }

    fun setViewPadding(left: Int, top: Int, right: Int, bottom: Int) {
        mRecyclerView?.setPadding(left, top, right, bottom)
    }

    fun setScrollBarEnabled(isEnabled: Boolean) {
        mRecyclerView?.isVerticalScrollBarEnabled = isEnabled
    }
}