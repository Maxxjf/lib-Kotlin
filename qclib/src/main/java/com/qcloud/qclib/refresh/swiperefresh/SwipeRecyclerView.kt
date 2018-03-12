package com.qcloud.qclib.refresh.swiperefresh

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.qcloud.qclib.R

/**
 * 类说明：SwipeRecyclerView
 *          SwipeRefreshLayout封装RecyclerView
 * Author: Kuzan
 * Date: 2018/1/22 15:48.
 */
class SwipeRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : SwipeRefreshLayout(context, attrs, defStyleAttr) {

    /**内置的RecyclerView*/
    val recyclerView = LayoutInflater.from(context).inflate(R.layout.layout_recycler_view, null, false) as RecyclerView

    /**空数据提示布局容器*/
    private var mEmptyLayout: LinearLayout? = null
    /**是否显示空布局*/
    var isShowEmpty: Boolean = false
    /** 是否自动上拉刷新 */
    var isAutomaticUp: Boolean = false
    /**可见的最后一个item*/
    private var lastVisibleItem: Int = 0
    /**可见的第一个item*/
    private var firstVisibleItem: Int = 0

    /** 获取RecyclerView所在的位置 */
    private val recyclerViewIndex: Int
        get() {
            var index = -1
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child == recyclerView) {
                    index = i
                    break
                }
            }
            return index
        }

    /** 获取EmptyView所在的位置 */
    private val emptyViewIndex: Int
        get() {
            var index = -1
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child == mEmptyLayout) {
                    index = i
                    break
                }
            }
            return index
        }

    init {
        this.addView(recyclerView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        setScrollBarEnabled(false)
        initListener()
    }

    /**
     * 自动上拉更新
     * */
    private fun initListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (isAutomaticUp && newState == RecyclerView.SCROLL_STATE_IDLE && pullUp() && isBottom()) {
                    autoLoadMore(SwipeRefreshLayout.FOOTER_DEFAULT_HEIGHT)
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

    private fun isBottom(): Boolean {
        return if (recyclerView == getTarget() && lastVisibleItem != recyclerView.adapter.itemCount - 1) {
                false
            } else {
                val view: View? = getFooter()
                if (view == null) {
                    false
                } else {
                    val bottomMargin = ((view.layoutParams) as MarginLayoutParams).bottomMargin
                    view.bottom  + bottomMargin + view.paddingBottom <= height
                }
            }
    }

    /**
     * 获取当前第一个显示的item 和 最后一个显示的item.
     */
    private fun getVisibleItem() {
        val manager = recyclerView?.layoutManager
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
     * 设置空布局
     *
     * @param emptyView
     * @param layoutGravity 空布局在父布局的方向
     */
    fun setEmptyView(emptyView: View, layoutGravity: Int) {
        if (mEmptyLayout == null) {
            initEmptyLayout()
        }
        mEmptyLayout!!.gravity = layoutGravity
        mEmptyLayout!!.addView(emptyView)
    }

    /**
     * 显示空布局
     */
    fun showEmptyView() {
        if (mEmptyLayout != null && mEmptyLayout!!.parent == null) {
            val index = recyclerViewIndex
            if (index > 0) {
                resetTarget()
                isShowEmpty = true
                this.addView(mEmptyLayout, index)

                if (recyclerView.parent != null) {
                    removeView(recyclerView)
                }
            }
        }
    }

    /**
     * 隐藏空布局
     */
    fun hideEmptyView() {
        if (recyclerView.parent == null) {
            val index = emptyViewIndex
            if (index > 0) {
                resetTarget()
                isShowEmpty = false
                addView(recyclerView, index)

                if (mEmptyLayout != null && mEmptyLayout!!.parent != null) {
                    removeView(mEmptyLayout)
                }
            }
        }
    }

    /**
     * 初始化空布局
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initEmptyLayout() {
        mEmptyLayout = LinearLayout(mContext)
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        mEmptyLayout!!.layoutParams = lp
        mEmptyLayout!!.setOnTouchListener { _, _ -> true }
    }

    /********* 提供一系列对内置RecyclerView的操作方法 *********/
    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        recyclerView.layoutManager = layoutManager
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
    }

    fun setItemAnimator(animator: RecyclerView.ItemAnimator) {
        recyclerView.itemAnimator = animator
    }

    fun addItemDecoration(decor: RecyclerView.ItemDecoration) {
        recyclerView.addItemDecoration(decor)
    }

    fun addItemDecoration(decor: RecyclerView.ItemDecoration, index: Int) {
        recyclerView.addItemDecoration(decor, index)
    }

    fun setViewPadding(left: Int, top: Int, right: Int, bottom: Int) {
        recyclerView.setPadding(left, top, right, bottom)
    }

    fun setScrollBarEnabled(isEnabled: Boolean) {
        recyclerView.isVerticalScrollBarEnabled = isEnabled
    }
}