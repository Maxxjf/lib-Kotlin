package com.qcloud.qclib.widget.indicator.transition

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import com.qcloud.qclib.utils.ColorUtil
import com.shizhefei.view.indicator.Indicator

/**
 * 类说明：tab滑动变化的转换监听，图标
 *      基于shizhefei的ViewPage进行二次封装
 * Author: Kuzan
 * Date: 2018/1/18 14:01.
 */
class OnTransitionImageListener: Indicator.OnTransitionListener {
    private var selectImgColor: Int = Color.BLACK
    private var unSelectImgColor: Int = Color.GRAY

    constructor(): super()

    constructor(selectImgColor: Int, unSelectImgColor: Int): super() {
        setColor(selectImgColor, unSelectImgColor)
    }

    override fun onTransition(view: View, position: Int, selectPercent: Float) {
        val selectImageView = getImageView(view)
        selectImageView.setColorFilter(ColorUtil.getColorGradient(unSelectImgColor, selectImgColor, 100, (selectPercent * 100).toInt()))
    }

    fun setColor(@ColorInt selectImgColor: Int, @ColorInt unSelectImgColor: Int): OnTransitionImageListener {
        this.selectImgColor = selectImgColor
        this.unSelectImgColor = unSelectImgColor
        return this
    }

    fun setColorId(context: Context, @ColorRes selectColorId: Int, @ColorRes unSelectColorId: Int): OnTransitionImageListener {
        setColor(ContextCompat.getColor(context, selectColorId), ContextCompat.getColor(context, unSelectColorId))
        return this
    }

    /**
     * 如果tabItemView 不是目标的ImageView，那么你可以重写该方法返回实际要变化的ImageView
     *
     * @param tabItemView
     *            Indicator的每一项的view
     * @return
     */
    fun getImageView(tabItemView: View): ImageView {
        return tabItemView as ImageView
    }
}