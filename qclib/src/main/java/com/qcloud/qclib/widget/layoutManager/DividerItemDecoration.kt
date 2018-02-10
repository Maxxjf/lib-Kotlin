package com.qcloud.qclib.widget.layoutManager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.NonNull
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/1/18 9:13.
 */
class DividerItemDecoration(context: Context, orientation: Int): RecyclerView.ItemDecoration() {

    private val ATTRS = intArrayOf(android.R.attr.listDivider)

    private var mDivider: Drawable? = null
    private var mOrientation: Int = HORIZONTAL_LIST

    init {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()
        setOrientation(orientation)
    }

    fun setOrientation(orientation: Int) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw IllegalArgumentException("invalid orientation")
        }
        mOrientation = orientation
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    fun drawVertical(@NonNull c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider!!.intrinsicHeight
            mDivider?.setBounds(left, top, right, bottom)
            mDivider?.draw(c)
        }
    }

    fun drawHorizontal(@NonNull c: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + mDivider!!.intrinsicHeight
            mDivider?.setBounds(left, top, right, bottom)
            mDivider?.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        if (mOrientation == VERTICAL_LIST) {
            outRect?.set(0, 0, 0, mDivider!!.intrinsicHeight)
        } else {
            outRect?.set(0, 0, mDivider!!.intrinsicWidth, 0)
        }
    }

    companion object {
        /**水平方向*/
        val HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL
        /**垂直方向*/
        val VERTICAL_LIST = LinearLayoutManager.VERTICAL
    }
}