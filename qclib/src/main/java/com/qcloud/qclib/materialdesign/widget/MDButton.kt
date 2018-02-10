package com.qcloud.qclib.materialdesign.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.Gravity
import com.qcloud.qclib.R
import com.qcloud.qclib.materialdesign.enums.GravityEnum
import com.qcloud.qclib.utils.DialogUtil

/**
 * 类说明：自定义Material Design 效果Button
 * Author: Kuzan
 * Date: 2018/2/8 16:30.
 */
class MDButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    var isStacked = false
    var stackedGravity: GravityEnum = GravityEnum.END

    private val stackedEndPadding: Int = context.resources.getDimensionPixelSize(R.dimen.small_btn_height)
    private var stackedBackground: Drawable? = null
    private var defaultBackground: Drawable? = null

    fun setStacked(stacked: Boolean, force: Boolean) {
        if (isStacked != stacked || force) {
            gravity = if (stacked) Gravity.CENTER_VERTICAL or stackedGravity.getGravityInt() else Gravity.CENTER
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textAlignment = if (stacked) stackedGravity.getTextAlignment() else TEXT_ALIGNMENT_CENTER
            }
            DialogUtil.setBackgroundCompat(this, if (stacked) stackedBackground else defaultBackground)
            if (stacked) {
                setPadding(stackedEndPadding, paddingTop, stackedEndPadding, paddingBottom)
            }
            isStacked = stacked
        }
    }

    fun setStackedSelector(d: Drawable?) {
        stackedBackground = d
        if (isStacked) {
            setStacked(true, true)
        }
    }

    fun setDefaultSelector(d: Drawable?) {
        defaultBackground = d
        if (!isStacked) {
            setStacked(false, true)
        }
    }

    @SuppressLint("RestrictedApi")
    fun setAllCapsCompat(allCaps: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setAllCaps(allCaps)
        } else {
            transformationMethod = if (allCaps) {
                AllCapsTransformationMethod(context)
            } else {
                null
            }
        }
    }
}