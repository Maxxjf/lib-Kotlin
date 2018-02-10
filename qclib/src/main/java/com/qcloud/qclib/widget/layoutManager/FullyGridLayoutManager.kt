package com.qcloud.qclib.widget.layoutManager

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * 类说明：解决嵌套之间的滚动冲突
 * Author: Kuzan
 * Date: 2018/1/18 10:08.
 */
class FullyGridLayoutManager: GridLayoutManager {
    var mWidth: Int = 0
    var mHeight: Int = 0

    private val mMeasuredDimension = IntArray(2)

    constructor(context: Context, spanCount: Int): super(context, spanCount)

    constructor(context: Context, spanCount: Int, orientation: Int, reverseLayout: Boolean): super(context, spanCount, orientation, reverseLayout)

    @SuppressLint("SwitchIntDef")
    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State?, widthSpec: Int, heightSpec: Int) {
        // 宽的mode + size
        val widthMode = View.MeasureSpec.getMode(widthSpec)
        val widthSize = View.MeasureSpec.getSize(widthSpec)
        // 高的mode + size
        val heightMode = View.MeasureSpec.getMode(heightSpec)
        val heightSize = View.MeasureSpec.getSize(heightSpec)

        // 自身宽高的初始值
        var xwidth = 0
        var xheight = 0
        // item的数目
        var count = itemCount
        // item的列数
        var span = spanCount

        for (i in 0 until count) {
            measureScrapChild(recycler, i, View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), mMeasuredDimension)
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                if (i % span == 0) {
                    xwidth += mMeasuredDimension[0]
                }
                if (i == 0) {
                    xheight = mMeasuredDimension[1]
                }
            } else {
                if (i % span == 0) {
                    xheight += mMeasuredDimension[1]
                }
                if (i == 0) {
                    xwidth = mMeasuredDimension[0]
                }
            }
        }

        xwidth = when (widthMode) {
            // 当控件宽是match_parent时，宽度就是父控件的宽度
            View.MeasureSpec.EXACTLY -> widthSize
            else -> xwidth
        }
        xheight = when (heightMode) {
            // 当控件高是match_parent时，高度就是父控件的高度
            View.MeasureSpec.EXACTLY -> heightSize
            else -> xheight
        }
        mWidth = xwidth
        mHeight = xheight
        setMeasuredDimension(xwidth, xheight)
    }

    private fun measureScrapChild(@NonNull recycler: RecyclerView.Recycler, position: Int, widthSpec: Int, heightSpec: Int, measuredDimension: IntArray) {
        if (position < itemCount) {
            try {
                val view = recycler.getViewForPosition(0)   // fix 动态添加时报IndexOutOfBoundsException
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
}