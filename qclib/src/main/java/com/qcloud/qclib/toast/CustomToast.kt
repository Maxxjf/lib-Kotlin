package com.qcloud.qclib.toast

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.support.annotation.CheckResult
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.NonNull
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.qcloud.qclib.R
import com.qcloud.qclib.utils.DrawableUtil


/**
 * 类说明：自定义弹窗
 * Author: Kuzan
 * Date: 2017/12/19 17:26.
 */
class CustomToast constructor(private val mContext: Context) {
    private val currentToast: Toast = Toast(mContext)

    private val toastLayout: View = (mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.layout_toast, null)
    private val toastIcon: ImageView
    private val toastText: TextView

    private var isShow = false
    private var mDuration: Long = LENGTH_LONG

    private val mHandler = Handler()
    private val mRunnable = Runnable { cancel() }

    init {
        toastIcon = toastLayout.findViewById(R.id.toast_icon)
        toastText = toastLayout.findViewById(R.id.toast_text)

        toastText.setTextColor(DEFAULT_TEXT_COLOR)
        toastText.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE)

        toastLayout.setPadding(40, 20, 40, 20)
        toastLayout.setBackgroundResource(TOAST_BG)

        currentToast.view = toastLayout
    }

    /**
     * 设置背景/恢复默认背景
     *
     * @param resId 背景
     * */
    fun setBackground(@DrawableRes resId: Int = TOAST_BG) {
        toastLayout.setBackgroundResource(0)
        toastLayout.setBackgroundResource(resId)
    }

    /**
     * 设置背景颜色
     *
     * @param tintColor 背景颜色
     * */
    fun setBackgroundColor(@ColorInt tintColor: Int) {
        var drawable = toastLayout.background
        drawable = DrawableUtil.tintDrawble(drawable, tintColor)

        DrawableUtil.setBackground(toastLayout, drawable)
    }

    /**
     * 设置是否显示icon
     *
     * @param withIcon  是否显示图标，默认显示
     * */
    fun showIcon(withIcon: Boolean = true) {
        toastIcon.visibility = if (withIcon) View.VISIBLE else View.GONE
    }

    /**
     * 设置图标
     *
     * @param iconId    图标id
     * @param withIcon  是否显示图标，默认显示
     * */
    fun setIcon(@DrawableRes iconId: Int, withIcon: Boolean = true) {
        if (withIcon) {
            toastIcon.visibility = View.VISIBLE
            var icon = DrawableUtil.getDrawable(mContext, iconId)
            if (icon != null) {
                icon = DrawableUtil.tintDrawble(icon, DEFAULT_TEXT_COLOR)
                DrawableUtil.setBackground(toastIcon, icon)
            }
        } else {
            toastIcon.visibility = View.GONE
        }
    }

    /**
     * 设置字体颜色
     *
     * @param textColor 颜色
     * */
    fun setTextColor(@ColorInt textColor: Int) {
        toastText.setTextColor(textColor)
    }

    /**
     * 设置提示消息
     *
     * @param text 提示消息
     * */
    fun setText(text: CharSequence) {
        toastText.text = text
    }

    /**
     * 设置显示时长
     *
     * @param duration 显示时长
     * */
    fun setDuration(duration: Long) {
        mDuration = duration
    }

    /**
     * 显示toast
     * */
    fun show() {
        if (!isShow) {
            mHandler.removeCallbacks(mRunnable)
            isShow = true
            currentToast.show()
            mHandler.postDelayed(mRunnable, mDuration)
        }
    }

    /**
     * 隐藏toast
     * */
    private fun cancel() {
        if (isShow) {
            isShow = false
            currentToast.cancel()
        }
    }

    companion object {
        // 字体大小
        var TEXT_SIZE: Float = 12.0f
        // 字体颜色 白色
        @ColorInt var DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF")
        // 错误颜色 红色
        @ColorInt var ERROR_COLOR = Color.parseColor("#D50000")
        // 提醒颜色 蓝色
        @ColorInt var INFO_COLOR = Color.parseColor("#3F51B5")
        // 成功颜色 绿色
        @ColorInt var SUCCESS_COLOR = Color.parseColor("#388E3C")
        // 警告颜色 橙色
        @ColorInt var WARNING_COLOR = Color.parseColor("#FFA900")
        // 默认颜色 浅黑色
        @ColorInt var NORMAL_COLOR = Color.parseColor("#353A3E")
        // 默认背景
        @DrawableRes var TOAST_BG = R.drawable.toast_bg
        // 成功图标
        @DrawableRes var SUCCESS_ICON = R.drawable.icon_toast_success;
        // 消息图标
        @DrawableRes var INFO_ICON = R.drawable.icon_toast_info
        // 警告图标
        @DrawableRes var WARNING_ICON = R.drawable.icon_toast_warning
        // 错误图标
        @DrawableRes var ERROR_ICON = R.drawable.icon_toast_error

        // 短显示
        val LENGTH_SHORT: Long = 1500L
        // 长显示
        val LENGTH_LONG: Long = 2500L

        @CheckResult
        fun getInstance(@NonNull context: Context): CustomToast = CustomToast(context)
    }
}
