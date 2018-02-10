package com.qcloud.qclib.utils

import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.IBinder
import android.support.annotation.ArrayRes
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.qcloud.qclib.materialdesign.dialogs.MaterialDialog
import com.qcloud.qclib.materialdesign.enums.GravityEnum

/**
 * 类说明：弹窗有关工具类
 * Author: Kuzan
 * Date: 2018/2/8 16:56.
 */
object DialogUtil {

    @ColorInt
    fun getDisabledColor(context: Context): Int {
        val primaryColor = resolveColor(context, android.R.attr.textColorPrimary)
        val disabledColor = if (isColorDark(primaryColor)) Color.BLACK else Color.WHITE
        return adjustAlpha(disabledColor, 0.3f)
    }

    @ColorInt
    fun adjustAlpha(@ColorInt color: Int, @SuppressWarnings("SameParameterValue") factor: Float): Int {
        val alpha = Math.round(Color.alpha(color) * factor)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        return Color.argb(alpha, red, green, blue)
    }

    /**
     * 分解颜色
     * */
    @ColorInt
    fun resolveColor(context: Context, @AttrRes attr: Int): Int {
        return resolveColor(context, attr, 0)
    }

    /**
     * 分解颜色
     * */
    @ColorInt
    fun resolveColor(context: Context, attr: Int, fallback: Int): Int {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            return a.getColor(0, fallback)
        } finally {
            a.recycle()
        }
    }

    fun resolveActionTextColorStateList(context: Context, @AttrRes colorAttr: Int, fallback: ColorStateList?): ColorStateList? {
        val a = context.theme.obtainStyledAttributes(intArrayOf(colorAttr))

        try {
            val value = a.peekValue(0) ?: return fallback
            return if (value.type >= TypedValue.TYPE_LAST_COLOR_INT) {
                getActionTextStateList(context, value.data)
            } else {
                val stateList: ColorStateList? = a.getColorStateList(0)
                stateList ?: fallback
            }
        } finally {
            a.recycle()
        }
    }

    fun getActionTextColorStateList(context: Context, @ColorRes colorId: Int): ColorStateList? {
        val value = TypedValue()
        context.resources.getValue(colorId, value, true)
        return if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            getActionTextStateList(context, value.data)
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                context.resources.getColorStateList(colorId)
            } else {
                context.getColorStateList(colorId)
            }
        }
    }

    @ColorInt
    fun getColor(context: Context, @ColorRes colorId: Int): Int {
        return ContextCompat.getColor(context, colorId)
    }

    fun resolveString(context: Context, @AttrRes attr: Int): String {
        val v = TypedValue()
        context.theme.resolveAttribute(attr, v, true)
        return if (v.string == null) {
            ""
        } else {
            v.string as String
        }
    }

    private fun gravityEnumToAttrInt(value: GravityEnum): Int {
        return when (value) {
            GravityEnum.CENTER -> 1
            GravityEnum.END -> 2
            else -> 0
        }
    }

    fun resolveGravityEnum(context: Context, @AttrRes attr: Int, defaultGravity: GravityEnum): GravityEnum {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            return when (a.getInt(0, gravityEnumToAttrInt(defaultGravity))) {
                1 -> GravityEnum.CENTER
                2 -> GravityEnum.END
                else -> GravityEnum.START
            }
        } finally {
            a.recycle()
        }
    }

    fun resolveDrawable(context: Context, @AttrRes attr: Int): Drawable? {
        return resolveDrawable(context, attr, null)
    }

    private fun resolveDrawable(context: Context, @AttrRes attr: Int, fallback: Drawable?): Drawable? {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            var d = a.getDrawable(0)
            if (d == null && fallback != null) {
                d = fallback
            }
            return d
        } finally {
            a.recycle()
        }
    }

    fun resolveDimension(context: Context, @AttrRes attr: Int): Int {
        return resolveDimension(context, attr, -1)
    }

    private fun resolveDimension(context: Context, @AttrRes attr: Int, fallback: Int): Int {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            return a.getDimensionPixelSize(0, fallback)
        } finally {
            a.recycle()
        }
    }

    fun resolveBoolean(context: Context, @AttrRes attr: Int, fallback: Boolean): Boolean {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            return a.getBoolean(0, fallback)
        } finally {
            a.recycle()
        }
    }

    fun resolveBoolean(context: Context, @AttrRes attr: Int): Boolean {
        return resolveBoolean(context, attr, false)
    }

    fun isColorDark(@ColorInt color: Int): Boolean {
        val darkness: Double = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

    fun setBackgroundCompat(view: View, d: Drawable?) {
        if (d == null) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(d)
        } else {
            view.background = d
        }
    }

    fun showKeyboard(di: DialogInterface) {
        val dialog = di as MaterialDialog
        if (dialog.inputEditText == null) {
            return
        }
        dialog.inputEditText!!.post(
                Runnable {
                    dialog.inputEditText!!.requestFocus()
                    val imm = dialog.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.showSoftInput(dialog.inputEditText, InputMethodManager.SHOW_IMPLICIT)
                })
    }

    fun hideKeyboard(di: DialogInterface) {
        val dialog = di as MaterialDialog
        if (dialog.inputEditText == null) {
            return
        }
        val imm = dialog.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        if (imm != null) {
            val currentFocus = dialog.currentFocus
            var windowToken: IBinder? = null
            if (currentFocus != null) {
                windowToken = currentFocus.windowToken
            } else if (dialog.mView != null) {
                windowToken = dialog.mView!!.windowToken
            }
            if (windowToken != null) {
                imm.hideSoftInputFromWindow(windowToken, 0)
            }
        }
    }

    fun getActionTextStateList(context: Context, primaryColor: Int): ColorStateList {
        var newPrimaryColor = primaryColor
        val fallBackButtonColor = resolveColor(context, android.R.attr.textColorPrimary)
        if (newPrimaryColor == 0) {
            newPrimaryColor = fallBackButtonColor
        }
        val states = arrayOf(
                intArrayOf(-android.R.attr.state_enabled), // disabled
                intArrayOf() // enabled
        )
        val colors = intArrayOf(adjustAlpha(newPrimaryColor, 0.4f), newPrimaryColor)
        return ColorStateList(states, colors)
    }

    fun getColorArray(context: Context, @ArrayRes array: Int): IntArray? {
        if (array == 0) {
            return null
        }
        val ta = context.resources.obtainTypedArray(array)
        val colors = IntArray(ta.length())
        for (i in 0 until ta.length()) {
            colors[i] = ta.getColor(i, 0)
        }
        ta.recycle()
        return colors
    }

    fun <T> isIn(find: T, ary: Array<T>?): Boolean {
        if (ary == null || ary.isEmpty()) {
            return false
        }
        for (item in ary) {
            if (item == find) {
                return true
            }
        }
        return false
    }

    fun <T> checkNotNull(value: T?, name: String): T {
        if (value == null) {
            throw IllegalStateException(name + " == null")
        }
        return value
    }
}