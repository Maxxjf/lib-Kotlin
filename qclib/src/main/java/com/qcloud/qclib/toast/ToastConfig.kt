package com.qcloud.qclib.toast

import android.graphics.Color
import android.support.annotation.CheckResult
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import com.qcloud.qclib.R

/**
 * 类说明：Toast全局属性设置，放到BaseApplication里设置
 * Author: Kuzan
 * Date: 2017/12/20 13:51.
 */
class ToastConfig constructor() {
    // 字体大小
    var textSize: Float = 12.0f
    // 字体颜色 白色
    @ColorInt private var defaultTextColor = Color.parseColor("#FFFFFF")
    // 错误颜色 红色
    @ColorInt private var errorColor = Color.parseColor("#D50000")
    // 提醒颜色 蓝色
    @ColorInt private var infoColor = Color.parseColor("#3F51B5")
    // 成功颜色 绿色
    @ColorInt private var successColor = Color.parseColor("#388E3C")
    // 警告颜色 橙色
    @ColorInt private var warningColor = Color.parseColor("#FFA900")
    // 默认颜色 浅黑色
    @ColorInt private var normalColor = Color.parseColor("#353A3E")
    // 默认背景
    @DrawableRes private var toastBg = R.drawable.toast_bg
    // 成功图标
    @DrawableRes private var successIcon = R.drawable.icon_toast_success
    // 消息图标
    @DrawableRes private var infoIcon = R.drawable.icon_toast_info
    // 警告图标
    @DrawableRes private var warningIcon = R.drawable.icon_toast_warning
    // 错误图标
    @DrawableRes private var errorIcon = R.drawable.icon_toast_error

    fun setTextSize(textSize: Float): ToastConfig {
        this.textSize = textSize
        return this
    }

    @CheckResult
    fun setTextColor(@ColorInt textColor: Int): ToastConfig {
        this.defaultTextColor = textColor
        return this
    }

    @CheckResult
    fun setErrorColor(@ColorInt errorColor: Int): ToastConfig {
        this.errorColor = errorColor
        return this
    }

    @CheckResult
    fun setInfoColor(@ColorInt infoColor: Int): ToastConfig {
        this.infoColor = infoColor
        return this
    }

    @CheckResult
    fun setSuccessColor(@ColorInt successColor: Int): ToastConfig {
        this.successColor = successColor
        return this
    }

    @CheckResult
    fun setWarningColor(@ColorInt warningColor: Int): ToastConfig {
        this.warningColor = warningColor
        return this
    }

    @CheckResult
    fun setNormalColor(@ColorInt normalColor: Int): ToastConfig {
        this.normalColor = normalColor
        return this
    }

    @CheckResult
    fun setToastBg(@DrawableRes toastBg: Int): ToastConfig {
        this.toastBg = toastBg
        return this
    }

    @CheckResult
    fun setSuccessIcon(@DrawableRes successIcon: Int): ToastConfig {
        this.successIcon = successIcon
        return this
    }

    @CheckResult
    fun setInfoIcon(@DrawableRes infoIcon: Int): ToastConfig {
        this.infoIcon = infoIcon
        return this
    }

    @CheckResult
    fun setWarningIcon(@DrawableRes warningIcon: Int): ToastConfig {
        this.warningIcon = warningIcon
        return this
    }

    @CheckResult
    fun setErrorIcon(@DrawableRes errorIcon: Int): ToastConfig {
        this.errorIcon = errorIcon
        return this
    }

    fun apply() {
        CustomToast.TEXT_SIZE = textSize
        CustomToast.DEFAULT_TEXT_COLOR = defaultTextColor
        CustomToast.ERROR_COLOR = errorColor
        CustomToast.INFO_COLOR = infoColor
        CustomToast.SUCCESS_COLOR = successColor
        CustomToast.WARNING_COLOR = warningColor
        CustomToast.NORMAL_COLOR = normalColor
        CustomToast.TOAST_BG = toastBg
        CustomToast.SUCCESS_ICON = successIcon
        CustomToast.INFO_ICON = infoIcon
        CustomToast.WARNING_ICON = warningIcon
        CustomToast.ERROR_ICON = errorIcon
    }

    companion object {
        val instance: ToastConfig
            @CheckResult
            get() = ToastConfig()
    }
}