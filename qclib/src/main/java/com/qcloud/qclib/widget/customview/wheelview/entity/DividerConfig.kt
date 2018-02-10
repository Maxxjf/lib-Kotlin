package com.qcloud.qclib.widget.customview.wheelview.entity

import android.support.annotation.ColorInt
import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import com.qcloud.qclib.widget.customview.wheelview.WheelView

/**
 * 类说明：选中项的分割线
 * Author: Kuzan
 * Date: 2018/1/19 15:40.
 */
class DividerConfig {
    var visible: Boolean = true
    var shadowVisible: Boolean = false
    var color: Int = WheelView.DIVIDER_COLOR
    var shadowColor: Int = WheelView.TEXT_COLOR_NORMAL
    var shadowAlpha: Int = 100
    var alpha: Int = WheelView.DIVIDER_ALPHA
    var ratio: Float = 0.1f
    var thick: Float = WheelView.DIVIDER_THICK

    constructor() : super()

    constructor(@FloatRange(from = 0.0, to = 1.0) ratio: Float) {
        this.ratio = ratio
    }

    /**线是否可见*/
    fun setVisible(visible: Boolean): DividerConfig {
        this.visible = visible
        return this
    }

    /**阴影是否可见*/
    fun setShadowVisible(shadowVisible: Boolean): DividerConfig {
        this.shadowVisible = shadowVisible
        if (shadowVisible && color == WheelView.DIVIDER_COLOR) {
            color = shadowColor
            alpha = 255
        }
        return this
    }

    /**阴影颜色*/
    fun setShadowColor(@ColorInt color: Int): DividerConfig {
        shadowVisible = true
        shadowColor = color
        return this
    }

    /**阴影透明度*/
    fun setShadowAlpha(@IntRange(from = 1, to = 255) alpha: Int): DividerConfig {
        this.shadowAlpha = alpha
        return this
    }

    /**线颜色*/
    fun setColor(@ColorInt color: Int): DividerConfig {
        this.color = color
        return this
    }

    /**线透明度*/
    fun setAlpha(@IntRange(from = 1, to = 255) alpha: Int): DividerConfig {
        this.alpha = alpha
        return this
    }

    /**线比例，范围为0-1,0表示最长，1表示最短*/
    fun setRatio(@FloatRange(from = 0.0, to = 1.0) ratio: Float): DividerConfig {
        this.ratio = ratio
        return this
    }

    /** 线粗 */
    fun setThick(thick: Float): DividerConfig {
        this.thick = thick
        return this
    }

    override fun toString(): String {
        return "visible=$visible,color=$color,alpha=$alpha,thick=$thick"
    }

    companion object {
        val FILL = 0.0f
        val WRAP = 1.0f
    }
}