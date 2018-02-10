package com.qcloud.qclib.utils

import android.app.Activity

/**
 * 类说明：侧滑返回工具类
 * Author: Kuzan
 * Date: 2017/12/7 13:45.
 */
object SwipeBackUtil {
    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    fun convertActivityFromTranslucent(activity: Activity) {
        try {
            val method = Activity::class.java.getDeclaredMethod("convertFromTranslucent")
            method.isAccessible = true
            method.invoke(activity)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(Activity)} .
     * <p>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    fun convertActivityToTranslucent(activity: Activity) {
        try {
            val clazzs = Activity::class.java.declaredClasses
            val translucentConversionListenerClazz: Class<*>? = clazzs.lastOrNull { it.simpleName.contains("TranslucentConversionListener") }
            val method = Activity::class.java.getDeclaredMethod("convertToTranslucent")
            method.isAccessible = true
            method.invoke(activity, arrayOf<Any?>(null))
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}