package com.qcloud.qclib.materialdesign.dialogs

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import com.qcloud.qclib.materialdesign.enums.GravityEnum

/**
 * 类说明：主题配置
 * Author: Kuzan
 * Date: 2018/2/9 11:08.
 */
class ThemeSingleton {
    var darkTheme = false
    @ColorInt
    var titleColor = 0
    @ColorInt
    var contentColor = 0

    var positiveColor: ColorStateList? = null
    var neutralColor: ColorStateList? = null
    var negativeColor: ColorStateList? = null

    @ColorInt
    var widgetColor = 0
    @ColorInt
    var itemColor = 0

    var icon: Drawable? = null

    @ColorInt
    var backgroundColor = 0
    @ColorInt
    var dividerColor = 0
    var linkColor: ColorStateList? = null
    @DrawableRes
    var listSelector = 0
    @DrawableRes
    var btnSelectorStacked = 0
    @DrawableRes
    var btnSelectorPositive = 0
    @DrawableRes
    var btnSelectorNeutral = 0
    @DrawableRes

    var btnSelectorNegative = 0
    var titleGravity = GravityEnum.START
    var contentGravity = GravityEnum.START
    var btnStackedGravity = GravityEnum.END
    var itemsGravity = GravityEnum.START
    var buttonsGravity = GravityEnum.START

    companion object {
        private var singleton: ThemeSingleton? = null

        @JvmOverloads
        operator fun get(createIfNull: Boolean = true): ThemeSingleton? {
            if (singleton == null && createIfNull) {
                singleton = ThemeSingleton()
            }
            return singleton
        }
    }
}
