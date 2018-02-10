package com.qcloud.qclib.widget.customview.verticalviewpager

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup

/**
 * 类说明：ViewPager布局参数
 * Author: Kuzan
 * Date: 2018/1/20 14:39.
 */
class VerticalLayoutParams : ViewGroup.LayoutParams {

    /** 如果该视图不是适配器提供的视图，则为true。 */
    var isDecor: Boolean = false

    /** as [android.view.Gravity] */
    var gravity: Int = 0

    /** 0到1 */
    var heightFactor: Float = 0f

    /** 如果在布局期间添加此视图并需要测量，则为true。 */
    var needsMeasure: Boolean = false

    var position: Int = 0

    var childIndex: Int = 0

    constructor() : super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.layout_gravity))
        gravity = a.getInteger(0, Gravity.TOP)
        a.recycle()
    }
}