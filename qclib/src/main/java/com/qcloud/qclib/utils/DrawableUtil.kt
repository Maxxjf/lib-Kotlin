package com.qcloud.qclib.utils

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.NinePatchDrawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
import android.view.View

/**
 * 类说明：Drawable工具类
 * Author: Kuzan
 * Date: 2017/12/20 12:03.
 */
object DrawableUtil {
    /**
     * 设置drawable颜色
     *
     * @param drawable
     * @param tintColor
     * */
    fun tintDrawble(@NonNull drawable: Drawable, @ColorInt tintColor: Int): Drawable {
        drawable.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
        return drawable
    }

    /**
     * 设置背景
     *
     * @param view
     * @param drawable
     * */
    fun setBackground(@NonNull view: View, @NonNull drawable: Drawable) {
        view.setBackgroundResource(0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.background = drawable
        } else {
            view.setBackgroundDrawable(drawable)
        }
    }

    /**
     * 根据res获取drawable
     *
     * @param context
     * @param resId
     * */
    fun getDrawable(@NonNull context: Context, @DrawableRes resId: Int): Drawable? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.getDrawable(resId)
            } else {
                ContextCompat.getDrawable(context, resId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 根据res获取.9图片
     *
     * @param context
     * @param resId
     * */
    fun getNinePatchDrawable(@NonNull context: Context, @DrawableRes resId: Int): NinePatchDrawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(resId) as NinePatchDrawable
        } else {
            ContextCompat.getDrawable(context, resId) as NinePatchDrawable
        }
    }

    /**
     * 通过现在的背景,获取其GradientDrawable实例
     *
     * @param backgroundOri
     * @return
     */
    fun getBackgroundDrawable(backgroundOri: Drawable): GradientDrawable? {
        return when (backgroundOri) {
            is GradientDrawable -> backgroundOri
            is ColorDrawable -> {
                val backgroundColor = backgroundOri.color
                val background2 = GradientDrawable()
                background2.setColor(backgroundColor)
                background2
            }
            else -> null
        }
    }
}