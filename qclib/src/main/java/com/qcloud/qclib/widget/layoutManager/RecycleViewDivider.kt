package com.qcloud.qclib.widget.layoutManager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * 类说明：RecyclerView 分隔线
 *      recyclerView.addItemDecoration(
 *          new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL,// 横向
 *              10,
 *              Color.WHITE);
 * Author: Kuzan
 * Date: 2018/1/18 10:39.
 */
class RecycleViewDivider: RecyclerView.ItemDecoration {
    // 延时加载，第一次使用时才执行初始化操作
    private val mPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }
    private var mDivider: Drawable? = null
    private var mDividerHeight: Int = 2     // 分割线高度，默认为1px
    // 列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
    private var mOrientation: Int = LinearLayoutManager.HORIZONTAL
    private val ATTRS = intArrayOf(android.R.attr.listDivider)

    // Grid有关
    private var spanCount = 0       // 列表个数
    private var spacing = 0     // 宽度
    private var includeEdge: Boolean = false    // 是否包括边缘
    private var isGrid: Boolean = false

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param orientation 列表方向
     */
    constructor(context: Context, orientation: Int) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw IllegalArgumentException("invalid orientation")
        }
        this.mOrientation = orientation
        isGrid = false

        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()
    }

    /**
     * 自定义分割线
     *
     * @param orientation 列表方向
     * @param drawableId 分割线图片
     */
    constructor(context: Context, orientation: Int, drawableId: Int): this(context, orientation) {
        mDivider = ContextCompat.getDrawable(context, drawableId)
        mDividerHeight = mDivider!!.intrinsicHeight
    }

    /**
     * 自定义分割线
     *
     * @param orientation 列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor 分割线颜色
     */
    constructor(context: Context, orientation: Int, dividerHeight: Int, dividerColor: Int): this(context, orientation) {
        mDividerHeight = dividerHeight
        mPaint.color = dividerColor
        mPaint.style = Paint.Style.FILL
    }

    /**
     * 自定义Grid分割线
     *
     * @param spanCount 列表个数
     * @param spacing 宽度
     * @param includeEdge 是否包括边缘
     * */
    constructor(spanCount: Int, spacing: Int, includeEdge: Boolean) {
        this.spanCount = spanCount
        this.spacing = spacing
        this.includeEdge = includeEdge
        isGrid = true
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        if (isGrid) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column + 1) * spacing / spanCount
                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        } else {
            outRect.set(0, 0, 0, mDividerHeight)
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        super.onDraw(c, parent, state)
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    /**
     * 绘制横向 item 分割线
     * */
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.measuredWidth - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + mDividerHeight
            mDivider?.setBounds(left, top, right, bottom)
            mDivider?.draw(canvas)
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }
    }

    /**
     * 绘制纵向 item 分割线
     * */
    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.measuredHeight - parent.paddingBottom

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + mDividerHeight
            mDivider?.setBounds(left, top, right, bottom)
            mDivider?.draw(canvas)
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }
    }
}