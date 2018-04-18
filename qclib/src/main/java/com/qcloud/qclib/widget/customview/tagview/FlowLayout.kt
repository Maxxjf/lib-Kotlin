package com.qcloud.qclib.widget.customview.tagview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.qcloud.qclib.R
import java.util.ArrayList

/**
 * Description: 流水布局
 * Author: gaobaiqiang
 * 2018/4/17 下午8:48.
 */
open class FlowLayout @JvmOverloads constructor(
        private val mContext: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : ViewGroup(mContext, attrs, defStyle) {

    protected open var mAllViews: MutableList<MutableList<View>> = ArrayList()
    protected open var mLineHeight: MutableList<Int> = ArrayList()
    protected open var mLineWidth: MutableList<Int> = ArrayList()

    private var mGravity: Int = LEFT
    private var lineViews: MutableList<View> = ArrayList()

    init {
        parseAttrs(attrs)
    }

    private fun parseAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = mContext.obtainStyledAttributes(attrs, R.styleable.TagLayout)
            try {
                mGravity = a.getInt(R.styleable.TagLayout_gravity, LEFT)
            } finally {
                a.recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sizeWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val modeWidth = View.MeasureSpec.getMode(widthMeasureSpec)
        val sizeHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        val modeHeight = View.MeasureSpec.getMode(heightMeasureSpec)

        // wrap_content
        var width = 0
        var height = 0

        var lineWidth = 0
        var lineHeight = 0

        val cCount = childCount

        for (i in 0 until cCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                if (i == cCount - 1) {
                    width = Math.max(lineWidth, width)
                    height += lineHeight
                }
                continue
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child.layoutParams as ViewGroup.MarginLayoutParams

            val childWidth = (child.measuredWidth + lp.leftMargin + lp.rightMargin)
            val childHeight = (child.measuredHeight + lp.topMargin + lp.bottomMargin)

            if (lineWidth + childWidth > sizeWidth - paddingLeft - paddingRight) {
                width = Math.max(width, lineWidth)
                lineWidth = childWidth
                height += lineHeight
                lineHeight = childHeight
            } else {
                lineWidth += childWidth
                lineHeight = Math.max(lineHeight, childHeight)
            }
            if (i == cCount - 1) {
                width = Math.max(lineWidth, width)
                height += lineHeight
            }
        }
        setMeasuredDimension(
                if (modeWidth == View.MeasureSpec.EXACTLY) sizeWidth else width + paddingLeft + paddingRight,
                if (modeHeight == View.MeasureSpec.EXACTLY) sizeHeight else height + paddingTop + paddingBottom
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mAllViews.clear()
        mLineHeight.clear()
        mLineWidth.clear()
        lineViews.clear()

        val width = width

        var lineWidth = 0
        var lineHeight = 0

        val cCount = childCount

        for (i in 0 until cCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue
            val lp = child.layoutParams as ViewGroup.MarginLayoutParams

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - paddingLeft - paddingRight) {
                mLineHeight.add(lineHeight)
                mAllViews.add(lineViews)
                mLineWidth.add(lineWidth)

                lineWidth = 0
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin
                lineViews = ArrayList()
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin)
            lineViews.add(child)
        }

        mLineHeight.add(lineHeight)
        mLineWidth.add(lineWidth)
        mAllViews.add(lineViews)

        var left = paddingLeft
        var top = paddingTop

        val lineNum = mAllViews.size

        for (i in 0 until lineNum) {
            lineViews = mAllViews[i]
            lineHeight = mLineHeight[i]

            // set gravity
            val currentLineWidth = this.mLineWidth[i]
            when (this.mGravity) {
                LEFT -> left = paddingLeft
                CENTER -> left = (width - currentLineWidth) / 2 + paddingLeft
                RIGHT -> left = width - currentLineWidth + paddingLeft
            }

            for (j in lineViews.indices) {
                val child = lineViews[j]
                if (child.visibility == View.GONE) {
                    continue
                }

                val lp = child.layoutParams as ViewGroup.MarginLayoutParams

                val lc = left + lp.leftMargin
                val tc = top + lp.topMargin
                val rc = lc + child.measuredWidth
                val bc = tc + child.measuredHeight

                child.layout(lc, tc, rc, bc)

                left += (child.measuredWidth + lp.leftMargin + lp.rightMargin)
            }
            top += lineHeight
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(p)
    }

    companion object {
        private val LEFT = -1
        private val CENTER = 0
        private val RIGHT = 1
    }
}