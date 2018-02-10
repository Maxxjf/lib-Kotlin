package com.qcloud.qclib.materialdesign.widget

import android.annotation.TargetApi
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.support.annotation.ColorInt

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/2/9 16:49.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
object RippleHelper {

    fun applyColor(d: Drawable?, @ColorInt color: Int) {
        (d as? RippleDrawable)?.setColor(ColorStateList.valueOf(color))
    }
}
