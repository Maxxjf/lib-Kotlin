package com.qcloud.qclib.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
import android.view.View
import android.text.Html



/**
 * Description: api过时替换工具类
 * Author: gaobaiqiang
 * 2018/3/13 下午4:24.
 */
object ApiReplaceUtil {
    /**
     * 获取颜色值
     *
     * @param context
     * @param colorRes 颜色资源id
     *
     * @return 颜色值
     * */
    fun getColor(@NonNull context: Context, @ColorRes colorRes: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.getColor(context, colorRes)
        } else {
            context.resources.getColor(colorRes)
        }
    }

    /**
     * 获取Drawable
     *
     * @param context
     * @param drawableRes 图片资源id
     *
     * @return 图片Drawable
     * */
    fun getDrawable(@NonNull context: Context, @DrawableRes drawableRes: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.getDrawable(context, drawableRes)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.getDrawable(drawableRes)
            } else {
                context.resources.getDrawable(drawableRes)
            }
        }
    }

    /**
     * 给控件设置背景
     *
     * @param view 控件
     * @param drawable 背景
     * */
    fun setBackground(@NonNull view: View, drawable: Drawable) {
        view.setBackgroundResource(0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.background = drawable
        } else {
            view.setBackgroundDrawable(drawable)
        }
    }

    /**
     * Html实现
     *
     * @param content
     * */
    fun fromHtml(content: String?): CharSequence {
        if (StringUtil.isEmpty(content)) {
            return ""
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(content)
        }
    }
}