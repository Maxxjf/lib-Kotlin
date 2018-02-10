package com.qcloud.qclib.widget.customview

import android.content.Context
import android.graphics.Paint
import android.support.annotation.IntRange
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

/**
 * 类说明：带有下划线和中间线的TextView
 *          下划线一般用来协议
 *          中划线一般用来原价
 * Author: Kuzan
 * Date: 2018/1/20 11:14.
 */
class LineTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    fun setText(text: CharSequence, @IntRange(from = 0, to = 1) linePosition: Int = 0) {
        super.setText(text)
        paint.isAntiAlias = true
        when (linePosition) {
            MIDDLE -> paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
            BOTTOM -> paint.flags = Paint.UNDERLINE_TEXT_FLAG
        }
    }

    companion object {
        val MIDDLE: Int = 0 // 中划线
        val BOTTOM: Int = 1 // 下划线
    }
}