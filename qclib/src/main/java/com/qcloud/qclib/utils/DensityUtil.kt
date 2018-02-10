package com.qcloud.qclib.utils

import android.content.Context
import android.util.TypedValue

/**
 * 类说明：常用单位转换工具类
 * Author: Kuzan
 * Date: 2017/12/4 11:37.
 */
object DensityUtil {

    /**
     * dp转px
     */
    fun dp2px(context: Context, dpValue: Float): Int =
            (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    dpValue, context.resources.displayMetrics) + 0.5).toInt()

    /**
     * sp转px
     */
    fun sp2px(context: Context, spValue: Float): Int =
            (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    spValue, context.resources.displayMetrics)).toInt()

    /**
     * px转dp
     */
    fun px2dp(context: Context, pxValue: Float): Float = pxValue / (context.resources.displayMetrics.density)

    /**
     * px转sp
     */
    fun px2sp(context: Context, pxValue: Float): Float = pxValue / (context.resources.displayMetrics.scaledDensity)
}