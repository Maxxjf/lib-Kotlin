package com.qcloud.qclib.snackbar

import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.FloatRange
import com.qcloud.qclib.R
import com.qcloud.qclib.utils.ColorUtil

/**
 * 类说明：Snackbar全局属性设置，放到BaseApplication里设置
 * Author: Kuzan
 * Date: 2018/2/7 14:18.
 */
class SnackbarConfig private constructor() {
    // 字体颜色 白色
    @ColorInt
    private var defaultTextColor = ColorUtil.parseColor("#FFFFFF")
    // 字体颜色 白色
    @ColorInt
    private var defaultActionColor = ColorUtil.parseColor("#25AB38")
    // 错误颜色 红色
    @ColorInt
    private var errorColor = ColorUtil.parseColor("#D50000")
    // 提醒颜色 蓝色
    @ColorInt
    private var infoColor = ColorUtil.parseColor("#3F51B5")
    // 成功颜色 绿色
    @ColorInt
    private var successColor = ColorUtil.parseColor("#388E3C")
    // 警告颜色 橙色
    @ColorInt
    private var warningColor = ColorUtil.parseColor("#FFA900")
    // 默认颜色 浅黑色
    @ColorInt
    private var normalColor = ColorUtil.parseColor("#353A3E")
    // 成功图标
    @DrawableRes
    private var successIcon = R.drawable.icon_toast_success
    // 消息图标
    @DrawableRes
    private var infoIcon = R.drawable.icon_toast_info
    // 警告图标
    @DrawableRes
    private var warningIcon = R.drawable.icon_toast_warning
    // 错误图标
    @DrawableRes
    private var errorIcon = R.drawable.icon_toast_error
    // 圆角图标
    @FloatRange(from = 0.0, to = 20.0)
    private var radiusSize: Float = 0.0f

    fun setTextColor(@ColorInt textColor: Int): SnackbarConfig {
        this.defaultTextColor = textColor
        return this
    }

    fun setActionColor(@ColorInt actionColor: Int): SnackbarConfig {
        this.defaultActionColor = actionColor
        return this
    }

    fun setErrorColor(@ColorInt errorColor: Int): SnackbarConfig {
        this.errorColor = errorColor
        return this
    }

    fun setInfoColor(@ColorInt infoColor: Int): SnackbarConfig {
        this.infoColor = infoColor
        return this
    }

    fun setSuccessColor(@ColorInt successColor: Int): SnackbarConfig {
        this.successColor = successColor
        return this
    }

    fun setWarningColor(@ColorInt warningColor: Int): SnackbarConfig {
        this.warningColor = warningColor
        return this
    }

    fun setNormalColor(@ColorInt normalColor: Int): SnackbarConfig {
        this.normalColor = normalColor
        return this
    }

    fun setSuccessIcon(@DrawableRes successIcon: Int): SnackbarConfig {
        this.successIcon = successIcon
        return this
    }

    fun setInfoIcon(@DrawableRes infoIcon: Int): SnackbarConfig {
        this.infoIcon = infoIcon
        return this
    }

    fun setWarningIcon(@DrawableRes warningIcon: Int): SnackbarConfig {
        this.warningIcon = warningIcon
        return this
    }

    fun setErrorIcon(@DrawableRes errorIcon: Int): SnackbarConfig {
        this.errorIcon = errorIcon
        return this
    }

    fun setRadiusSize(@FloatRange(from = 0.0, to = 20.0) radius: Float = 0.0f): SnackbarConfig {
        this.radiusSize = radius
        return this
    }

    fun apply() {
        CustomSnackBar.DEFAULT_TEXT_COLOR = defaultTextColor
        CustomSnackBar.DEFAULT_ACTION_COLOR = defaultActionColor
        CustomSnackBar.ERROR_COLOR = errorColor
        CustomSnackBar.INFO_COLOR = infoColor
        CustomSnackBar.SUCCESS_COLOR = successColor
        CustomSnackBar.WARNING_COLOR = warningColor
        CustomSnackBar.NORMAL_COLOR = normalColor
        CustomSnackBar.SUCCESS_ICON = successIcon
        CustomSnackBar.INFO_ICON = infoIcon
        CustomSnackBar.WARNING_ICON = warningIcon
        CustomSnackBar.ERROR_ICON = errorIcon
        CustomSnackBar.RADIUS_SIZE = radiusSize
    }

    companion object {
        val instance: SnackbarConfig
            get() = SnackbarConfig()
    }
}