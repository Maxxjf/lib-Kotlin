package com.qcloud.qclib.widget.layoutManager

import android.annotation.SuppressLint
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.ViewGroup

/**
 * 类说明：瀑布流Manager
 * Author: Kuzan
 * Date: 2018/1/18 9:44.
 */
class ExStaggeredGridLayoutManager(spanCount: Int, orientation: Int): StaggeredGridLayoutManager(spanCount, orientation) {

    // 尺寸的数组，[0]是宽，[1]是高
    private val measuredDimension = IntArray(2)
    // 用来比较同行/列那个item罪宽/高
    private var dimension: IntArray? = null

    @SuppressLint("SwitchIntDef")
    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State?, widthSpec: Int, heightSpec: Int) {
        // 宽的mode + size
        val widthMode = View.MeasureSpec.getMode(widthSpec)
        val widthSize = View.MeasureSpec.getSize(widthSpec)
        // 高的mode + size
        val heightMode = View.MeasureSpec.getMode(heightSpec)
        val heightSize = View.MeasureSpec.getSize(heightSpec)

        // 自身宽高的初始值
        var width = 0
        var height = 0
        // item的数目
        var count = itemCount
        // item的列数
        var span = spanCount
        // 根据行数或列数来创建数组
        dimension = IntArray(span)

        for (i in 0 until count) {
            measureScrapChild(recycler, i,
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), measuredDimension)
            // 如果是竖直的列表，计算item的高，否则计算宽度
            if (orientation == VERTICAL) {
                dimension!![findMinIndex(dimension!!)] += measuredDimension[1]
            } else {
                dimension!![findMinIndex(dimension!!)] += measuredDimension[0]
            }
        }
        if (orientation == VERTICAL) {
            height = findMax(dimension!!)
        } else {
            width = findMax(dimension!!)
        }

        width = when (widthMode) {
            // 当控件宽是match_parent时，宽度就是父控件的宽度
            View.MeasureSpec.EXACTLY -> widthSize
            else -> width
        }
        height = when (heightMode) {
            // 当控件高是match_parent时，高度就是父控件的高度
            View.MeasureSpec.EXACTLY -> heightSize
            else -> height
        }
        setMeasuredDimension(width, height)
    }

    private fun measureScrapChild(@NonNull recycler: RecyclerView.Recycler, position: Int, widthSpec: Int, heightSpec: Int, measuredDimension: IntArray) {
        // 挨个遍历所有item
        if (position < itemCount) {
            try {
                val view = recycler.getViewForPosition(position)    // fix 动态添加时报IndexOutOfBoundsException
                if (view != null) {
                    val lp = view.layoutParams as RecyclerView.LayoutParams
                    val childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, paddingLeft + paddingRight, lp.width)
                    val childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, paddingTop + paddingBottom, lp.height)
                    // 子view进行测量，然后可以通过getMeasuredWidth()获得测量的宽，高类似
                    view.measure(childWidthSpec, childHeightSpec)
                    // 将item的宽高放入数组中
                    measuredDimension[0] = view.measuredWidth + lp.leftMargin + lp.rightMargin
                    measuredDimension[1] = view.measuredHeight + lp.topMargin + lp.bottomMargin
                    recycler.recycleView(view)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 得到最数组中最大元素
     *
     * @param array
     * @return
     */
    private fun findMax(array: IntArray): Int {
        return array.max() ?: array[0]
    }

    /**
     * 得到最数组中最小元素的下标
     *
     * @param array
     * @return
     */
    private fun findMinIndex(array: IntArray): Int {
        var index = 0
        var min = array[0]
        for (i in 0 until  array.size) {
            if (array[i] < min) {
                min = array[i]
                index = i
            }
        }
        return index
    }
}