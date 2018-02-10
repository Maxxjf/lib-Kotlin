package com.qcloud.qclib.materialdesign.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.AppCompatEditText
import android.util.Log
import android.widget.*
import com.qcloud.qclib.R
import com.qcloud.qclib.utils.DialogUtil

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/2/9 10:52.
 */
object MDTintHelper {

    /**
     * 设置RadioButton 颜色
     * */
    fun setTint(radioButton: RadioButton, colors: ColorStateList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            radioButton.buttonTintList = colors
        } else {
            val radioDrawable = ContextCompat.getDrawable(radioButton.context, R.drawable.abc_btn_radio_material)
            val d = DrawableCompat.wrap(radioDrawable!!)
            DrawableCompat.setTintList(d, colors)
            radioButton.buttonDrawable = d
        }
    }

    /**
     * 设置RadioButton 颜色
     * */
    fun setTint(radioButton: RadioButton, @ColorInt color: Int) {
        val disabledColor = DialogUtil.getDisabledColor(radioButton.context)
        val sl = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_enabled, -android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_enabled, android.R.attr.state_checked)),
                intArrayOf(DialogUtil.resolveColor(radioButton.context, R.attr.colorControlNormal), color, disabledColor, disabledColor))
        setTint(radioButton, sl)
    }

    /**
     * 设置CheckBox 颜色
     * */
    fun setTint(box: CheckBox, colors: ColorStateList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            box.buttonTintList = colors
        } else {
            val checkDrawable = ContextCompat.getDrawable(box.context, R.drawable.abc_btn_check_material)
            val drawable = DrawableCompat.wrap(checkDrawable!!)
            DrawableCompat.setTintList(drawable, colors)
            box.buttonDrawable = drawable
        }
    }

    /**
     * 设置CheckBox 颜色
     * */
    fun setTint(box: CheckBox, @ColorInt color: Int) {
        val disabledColor = DialogUtil.getDisabledColor(box.context)
        val sl = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_enabled, -android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_enabled, android.R.attr.state_checked)),
                intArrayOf(DialogUtil.resolveColor(box.context, R.attr.colorControlNormal), color, disabledColor, disabledColor))
        setTint(box, sl)
    }

    fun setTint(seekBar: SeekBar, @ColorInt color: Int) {
        val s1 = ColorStateList.valueOf(color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            seekBar.thumbTintList = s1
            seekBar.progressTintList = s1
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            val progressDrawable = DrawableCompat.wrap(seekBar.progressDrawable)
            seekBar.progressDrawable = progressDrawable
            DrawableCompat.setTintList(progressDrawable, s1)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val thumbDrawable = DrawableCompat.wrap(seekBar.thumb)
                DrawableCompat.setTintList(thumbDrawable, s1)
                seekBar.thumb = thumbDrawable
            }
        } else {
            val mode = PorterDuff.Mode.SRC_IN
            if (seekBar.indeterminateDrawable != null) {
                seekBar.indeterminateDrawable.setColorFilter(color, mode)
            }
            if (seekBar.progressDrawable != null) {
                seekBar.progressDrawable.setColorFilter(color, mode)
            }
        }
    }

    fun setTint(progressBar: ProgressBar, @ColorInt color: Int) {
        setTint(progressBar, color, false)
    }

    private fun setTint(progressBar: ProgressBar, @ColorInt color: Int, skipIndeterminate: Boolean) {
        val sl = ColorStateList.valueOf(color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.progressTintList = sl
            progressBar.secondaryProgressTintList = sl
            if (!skipIndeterminate) {
                progressBar.indeterminateTintList = sl
            }
        } else {
            val mode = PorterDuff.Mode.SRC_IN
            if (!skipIndeterminate && progressBar.indeterminateDrawable != null) {
                progressBar.indeterminateDrawable.setColorFilter(color, mode)
            }
            if (progressBar.progressDrawable != null) {
                progressBar.progressDrawable.setColorFilter(color, mode)
            }
        }
    }

    private fun createEditTextColorStateList(context: Context, @ColorInt color: Int): ColorStateList {
        val states = arrayOfNulls<IntArray>(3)
        val colors = IntArray(3)
        var i = 0
        states[i] = intArrayOf(-android.R.attr.state_enabled)
        colors[i] = DialogUtil.resolveColor(context, R.attr.colorControlNormal)
        i++
        states[i] = intArrayOf(-android.R.attr.state_pressed, -android.R.attr.state_focused)
        colors[i] = DialogUtil.resolveColor(context, R.attr.colorControlNormal)
        i++
        states[i] = intArrayOf()
        colors[i] = color
        return ColorStateList(states, colors)
    }

    @SuppressLint("RestrictedApi")
    fun setTint(editText: EditText, @ColorInt color: Int) {
        val editTextColorStateList = createEditTextColorStateList(editText.context, color)
        if (editText is AppCompatEditText) {

            editText.supportBackgroundTintList = editTextColorStateList
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.backgroundTintList = editTextColorStateList
        }
        setCursorTint(editText, color)
    }

    private fun setCursorTint(editText: EditText, @ColorInt color: Int) {
        try {
            val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            fCursorDrawableRes.isAccessible = true
            val mCursorDrawableRes = fCursorDrawableRes.getInt(editText)
            val fEditor = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor = fEditor.get(editText)
            val clazz = editor.javaClass
            val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
            fCursorDrawable.isAccessible = true
            val drawables = arrayOfNulls<Drawable>(2)
            drawables[0] = ContextCompat.getDrawable(editText.context, mCursorDrawableRes)
            drawables[1] = ContextCompat.getDrawable(editText.context, mCursorDrawableRes)
            drawables[0]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            drawables[1]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            fCursorDrawable.set(editor, drawables)
        } catch (e1: NoSuchFieldException) {
            Log.d("MDTintHelper", "Device issue with cursor tinting: " + e1.message)
            e1.printStackTrace()
        } catch (e2: Exception) {
            e2.printStackTrace()
        }
    }
}