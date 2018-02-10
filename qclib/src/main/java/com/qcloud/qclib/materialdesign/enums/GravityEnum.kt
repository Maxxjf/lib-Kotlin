package com.qcloud.qclib.materialdesign.enums

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build
import android.view.Gravity
import android.view.View

/**
 * 类说明：显示位置枚举
 * Author: Kuzan
 * Date: 2018/2/8 16:33.
 */
enum class GravityEnum {
    START,
    CENTER,
    END;

    private val HAS_RTL: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1

    @SuppressLint("RtlHardcoded")
    fun getGravityInt(): Int {
        return when (this) {
            START -> if (HAS_RTL) Gravity.START else Gravity.LEFT
            CENTER -> Gravity.CENTER_HORIZONTAL
            END -> if (HAS_RTL) Gravity.END else Gravity.RIGHT
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getTextAlignment(): Int {
        return when (this) {
            CENTER -> View.TEXT_ALIGNMENT_CENTER
            END -> View.TEXT_ALIGNMENT_VIEW_END
            else -> View.TEXT_ALIGNMENT_VIEW_START
        }
    }
}