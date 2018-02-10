package com.qcloud.qclib.snackbar

import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.*
import android.support.annotation.IntRange
import android.support.design.widget.Snackbar
import android.support.v4.widget.Space
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.qcloud.qclib.R
import com.qcloud.qclib.utils.ColorUtil
import com.qcloud.qclib.utils.DensityUtil
import com.qcloud.qclib.utils.DrawableUtil
import com.qcloud.qclib.utils.ScreenUtil
import java.lang.ref.WeakReference








/**
 * 类说明：自定义SnackBar
 * Author: Kuzan
 * Date: 2018/2/7 9:51.
 */
class CustomSnackBar {

    /**
     * 获取 snackbar
     *
     * @return
     */
    private val snackBar: Snackbar?
        get() = if (sReference != null) {
            sReference!!.get()
        } else {
            null
        }

    /**
     * 获取snackBar View
     *
     * @return
     * */
    private val barView: View?
        get() = snackBar?.view

    private constructor() {
        throw RuntimeException("创建实例失败")
    }

    private constructor(reference: WeakReference<Snackbar>?) {
        sReference = reference
    }

    /**
     * 设置背景颜色
     *
     * @param backgroundColor 背景颜色
     * */
    fun setBackgroundColor(@ColorInt backgroundColor: Int = NORMAL_COLOR): CustomSnackBar {
        barView?.setBackgroundColor(backgroundColor)
        return this
    }

    /**
     * 设置背景
     *
     * @param backgroundColor 背景颜色
     * */
    fun setBackground(@ColorInt backgroundColor: Int = NORMAL_COLOR, @FloatRange(from = 0.0, to = 20.0) radius: Float = 0.0f): CustomSnackBar {
        if (barView != null) {
            barView!!.setBackgroundColor(backgroundColor)
            val background = DrawableUtil.getBackgroundDrawable(barView!!.background)
            if (background != null) {
                background.cornerRadii = floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f)
                DrawableUtil.setBackground(barView!!, background)
            }
        }
        return this
    }

    /**
     * 设置字体颜色
     *
     * @param textColor 字体颜色
     * */
    fun setTextColor(@ColorInt textColor: Int = DEFAULT_TEXT_COLOR): CustomSnackBar {
        val tvMessage: TextView? = barView?.findViewById(R.id.snackbar_text)
        tvMessage?.setTextColor(textColor)
        return this
    }

    /**
     * 设置操作按钮字体颜色
     *
     * @param actionColor 按钮字体颜色
     * */
    fun setAction(actionName: String, @ColorInt actionColor: Int = DEFAULT_ACTION_COLOR, listener: View.OnClickListener): CustomSnackBar {
        val btnAction: Button? = barView?.findViewById(R.id.snackbar_action)
        btnAction?.setTextColor(actionColor)
        snackBar?.setAction(actionName, listener)
        return this
    }

    /**
     * 设置透明度
     *
     * @param alpha
     * */
    fun setAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float = 0.3f): CustomSnackBar {
        barView?.alpha = alpha
        return this
    }

    /**
     * 设置圆角，只设置顶部圆角
     *
     * @param radius 圆角
     * */
    fun setRadius(@FloatRange(from = 0.0, to = 20.0) radius: Float = 0.0f): CustomSnackBar {
        if (barView != null) {
            val background = DrawableUtil.getBackgroundDrawable(barView!!.background)
            if (background != null) {
                background.cornerRadii = floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f)
                DrawableUtil.setBackground(barView!!, background)
            }
        }
        return this
    }

    /**
     * 设置字体显示位置
     *
     * @param gravity 显示位置，可传多个Gravity.CENTER_VERTICAL or Gravity.RIGHT
     * */
    fun setMessageGravity(gravity: Int = Gravity.CENTER_VERTICAL): CustomSnackBar {
        // View.setTextAlignment需要SDK>=17
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (barView != null) {
                val tvMessage: TextView? = barView?.findViewById(R.id.snackbar_text)

                tvMessage?.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
                tvMessage?.gravity = gravity
            }
        }
        return this
    }

    /**
     * 设置左图标
     *
     * @param iconId 图标id
     * */
    fun setLeftIcon(@DrawableRes iconId: Int): CustomSnackBar {
        if (barView != null) {
            var drawable: Drawable? = DrawableUtil.getDrawable(barView!!.context, iconId)
            if (drawable != null) {
                drawable = DrawableUtil.tintDrawble(drawable, DEFAULT_TEXT_COLOR)
            }
            return setDrawable(drawable)
        }
        return this
    }

    /**
     * 设置右图标
     *
     * @param iconId 图标id
     * */
    fun setRigthIcon(@DrawableRes iconId: Int): CustomSnackBar {
        if (barView != null) {
            var drawable: Drawable? = DrawableUtil.getDrawable(barView!!.context, iconId)
            if (drawable != null) {
                drawable = DrawableUtil.tintDrawble(drawable, DEFAULT_TEXT_COLOR)
            }
            return setDrawable(rightDrawable = drawable)
        }
        return this
    }

    /**
     * 设置图标
     *
     * @param leftIconId    左图标id
     * @param rightIconId   右图标id
     * */
    fun setIcon(@NonNull snackbar: Snackbar, @DrawableRes leftIconId: Int, @DrawableRes rightIconId: Int): CustomSnackBar {
        if (barView != null) {
            var leftDrawable: Drawable? = DrawableUtil.getDrawable(barView!!.context, leftIconId)
            if (leftDrawable != null) {
                leftDrawable = DrawableUtil.tintDrawble(leftDrawable, DEFAULT_TEXT_COLOR)
            }
            var rightDrawable: Drawable? = DrawableUtil.getDrawable(barView!!.context, rightIconId)
            if (rightDrawable != null) {
                rightDrawable = DrawableUtil.tintDrawble(rightDrawable, DEFAULT_TEXT_COLOR)
            }
            return setDrawable(leftDrawable, rightDrawable)
        }
        return this
    }

    /**
     * 设置TextView左右两侧的图片
     *
     * @param leftDrawable
     * @param rightDrawable
     */
    private fun setDrawable(leftDrawable: Drawable? = null, rightDrawable: Drawable? = null): CustomSnackBar {
        if (leftDrawable == null && rightDrawable == null) {
            return this
        }
        val tvMessage: TextView? = barView?.findViewById(R.id.snackbar_text)
        if (barView != null && tvMessage != null) {
            var params = tvMessage.layoutParams as LinearLayout.LayoutParams
            params = LinearLayout.LayoutParams(params.width, params.height, 0.0f)
            tvMessage.layoutParams = params
            tvMessage.compoundDrawablePadding = tvMessage.paddingLeft
            val textSize = tvMessage.textSize.toInt()
            leftDrawable?.setBounds(0, 0, textSize, textSize)
            rightDrawable?.setBounds(0, 0, textSize, textSize)
            tvMessage.setCompoundDrawables(leftDrawable, null, rightDrawable, null)
            val paramsSpace = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
            (barView as Snackbar.SnackbarLayout).addView(Space(barView!!.context), 1, paramsSpace)
        }
        return this
    }

    /**
     * 设置 snackbar 展示完成 及 隐藏完成 的监听
     *
     * @param callback
     * @return
     */
    fun setCallback(@NonNull callback: Snackbar.Callback): CustomSnackBar {
        snackBar?.setCallback(callback)
        return this
    }

    /**
     * 向Snackbar布局中添加View(Google不建议,复杂的布局应该使用Dialog进行展示)
     *
     * @param viewId 要添加的View的布局文件ID
     * @param index  要添加的位置
     * */
    fun addView(@LayoutRes viewId: Int, gravity: Int = Gravity.CENTER_VERTICAL, index: Int = 0): CustomSnackBar {
        if (barView != null) {
            val addView = LayoutInflater.from(barView!!.context).inflate(viewId, null)
            return addView(addView, gravity, index)
        }
        return this
    }

    /**
     * 向Snackbar布局中添加View(Google不建议,复杂的布局应该使用Dialog进行展示)
     *
     * @param addView 要添加的View的布局文件ID
     * @param index  要添加的位置
     * */
    fun addView(@NonNull addView: View, gravity: Int = Gravity.CENTER_VERTICAL, index: Int = 0): CustomSnackBar {
        if (barView != null) {
            // 设置新建布局参数
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            // 设置新建View在Snackbar内垂直居中显示
            params.gravity = gravity
            addView.layoutParams = params
            (barView as Snackbar.SnackbarLayout).addView(addView, index)
        }
        return this
    }

    /**
     * 设置Snackbar显示在指定View的上方
     *      注:暂时仅支持单行的Snackbar
     * @param targetView        指定View
     * @param contentViewTop    Activity中的View布局区域 距离屏幕顶端的距离
     * @param marginLeft        左边距
     * @param marginRight       右边距
     * @return
     */
    fun above(targetView: View, contentViewTop: Int = 0, @IntRange(from = 0) marginLeft: Int = 0, @IntRange(from = 0) marginRight: Int = 0): CustomSnackBar {
        if (barView != null) {
            val locations = IntArray(2)
            targetView.getLocationOnScreen(locations)
            val snackbarHeight = calculateSnackBarHeight()
            // 必须保证指定View的顶部可见 且 单行Snackbar可以完整的展示
            if (locations[1] >= contentViewTop + snackbarHeight) {
                gravityFrameLayout(Gravity.BOTTOM)
                val params = barView!!.layoutParams
                (params as ViewGroup.MarginLayoutParams).setMargins(marginLeft, 0, marginRight, barView!!.resources.displayMetrics.heightPixels - locations[1])
                barView!!.layoutParams = params
            }
        }
        return this
    }

    /**
     * 设置Snackbar显示在指定View的下方
     *      注:暂时仅支持单行的Snackbar
     *
     * @param targetView        指定View
     * @param contentViewTop    Activity中的View布局区域 距离屏幕顶端的距离
     * @param marginLeft        左边距
     * @param marginRight       右边距
     *
     * @return
     */
    fun bellow(targetView: View, contentViewTop: Int = 0, @IntRange(from = 0) marginLeft: Int = 0, @IntRange(from = 0) marginRight: Int = 0): CustomSnackBar {
        if (barView != null) {
            val locations = IntArray(2)
            targetView.getLocationOnScreen(locations)
            val snackbarHeight = calculateSnackBarHeight()
            val screenHeight = ScreenUtil.getScreenHeight(barView!!.context)

            // 必须保证指定View的底部可见 且 单行Snackbar可以完整的展示
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //为什么要'+2'? 因为在Android L(Build.VERSION_CODES.LOLLIPOP)以上,例如Button会有一定的'阴影(shadow)',阴影的大小由'高度(elevation)'决定.
                //为了在Android L以上的系统中展示的Snackbar不要覆盖targetView的阴影部分太大比例,所以人为减小2px的layout_marginBottom属性.
                if (locations[1] + targetView.height >= contentViewTop && locations[1] + targetView.height + snackbarHeight + 2 <= screenHeight) {
                    gravityFrameLayout(Gravity.BOTTOM)
                    val params = barView!!.layoutParams
                    (params as ViewGroup.MarginLayoutParams).setMargins(marginLeft, 0, marginRight, screenHeight - (locations[1] + targetView.height + snackbarHeight + 2))
                    barView!!.layoutParams = params
                }
            } else {
                if (locations[1] + targetView.height >= contentViewTop && locations[1] + targetView.height + snackbarHeight <= screenHeight) {
                    gravityFrameLayout(Gravity.BOTTOM)
                    val params = barView!!.layoutParams
                    (params as ViewGroup.MarginLayoutParams).setMargins(marginLeft, 0, marginRight, screenHeight - (locations[1] + targetView.height + snackbarHeight))
                    barView!!.layoutParams = params
                }
            }
        }
        return this
    }

    /**
     * 计算单行的Snackbar的高度值(单位 pix)
     *
     * @return
     */
    private fun calculateSnackBarHeight(): Int {
        //文字高度+paddingTop+paddingBottom : 14sp + 14dp*2
        return if (barView != null) {
            DensityUtil.dp2px(barView!!.context, 28f) + DensityUtil.sp2px(barView!!.context, 14f)
        } else {
            0
        }
    }

    /**
     * 设置Snackbar显示的位置
     *
     * @param gravity
     */
    fun gravityFrameLayout(gravity: Int): CustomSnackBar {
        if (barView!! != null) {
            val params = FrameLayout.LayoutParams(barView!!.layoutParams.width, barView!!.layoutParams.height)
            params.gravity = gravity
            barView!!.layoutParams = params
        }
        return this
    }

    /**
     * 显示Snackbar
     * */
    fun show() {
        snackBar?.show()
    }

    companion object {
        // 字体颜色 白色
        @ColorInt
        var DEFAULT_TEXT_COLOR = ColorUtil.parseColor("#FFFFFF")
        // 按钮字体颜色 绿色
        @ColorInt
        var DEFAULT_ACTION_COLOR = ColorUtil.parseColor("#25AB38")
        // 错误颜色 红色
        @ColorInt
        var ERROR_COLOR = ColorUtil.parseColor("#D50000")
        // 提醒颜色 蓝色
        @ColorInt
        var INFO_COLOR = ColorUtil.parseColor("#3F51B5")
        // 成功颜色 绿色
        @ColorInt
        var SUCCESS_COLOR = ColorUtil.parseColor("#388E3C")
        // 警告颜色 橙色
        @ColorInt
        var WARNING_COLOR = ColorUtil.parseColor("#FFA900")
        // 默认颜色 浅黑色
        @ColorInt
        var NORMAL_COLOR = ColorUtil.parseColor("#353A3E")
        // 成功图标
        @DrawableRes
        var SUCCESS_ICON = R.drawable.icon_toast_success;
        // 消息图标
        @DrawableRes
        var INFO_ICON = R.drawable.icon_toast_info
        // 警告图标
        @DrawableRes
        var WARNING_ICON = R.drawable.icon_toast_warning
        // 错误图标
        @DrawableRes
        var ERROR_ICON = R.drawable.icon_toast_error
        @FloatRange(from = 0.0, to = 1.0)
        var RADIUS_SIZE: Float = 0.0f

        // 短显示
        val LENGTH_SHORT: Int = 1500
        // 长显示
        val LENGTH_LONG: Int = 2500

        // 当前持有的Snackbar实例
        private var sReference: WeakReference<Snackbar>? = null

        /**
         * 创建Snackbar实例
         * */
        fun create(@NonNull view: View, message: String, duration: Int = LENGTH_LONG): CustomSnackBar {
            return CustomSnackBar(WeakReference(Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setDuration(duration)))
        }
    }
}