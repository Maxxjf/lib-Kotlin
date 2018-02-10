package com.qcloud.qclib.base

import android.app.Activity
import android.content.Context
import android.support.annotation.FloatRange
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import com.qcloud.qclib.R

/**
 * 类说明：PopupWindow基类
 * Author: Kuzan
 * Date: 2018/1/16 14:17.
 */
abstract class BasePopupWindow(protected val mContext: Context): PopupWindow(mContext) {
    val mView: View
        get() = LayoutInflater.from(mContext).inflate(viewId, null, false)
    var mActivity: Activity = mContext as Activity
    var onPopWindowViewClick: OnPopWindowViewClick? = null

    init {
        initWindow()
    }

    private fun initWindow() {
        initPop()
        initAfterViews()
    }

    /** 子类可重写 */
    open fun initPop() {
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        contentView = mView
        isFocusable = true
        animationStyle = animId
        isOutsideTouchable = true
        // 这个是为了点击"返回Back"也能使其消失，并且并不会影响背景
        setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.bg_shape_transparent))
    }

    /** 获取viewId */
    abstract val viewId: Int

    /** 获取弹窗动画 */
    abstract val animId: Int

    /** 初始化数据 */
    abstract fun initAfterViews()

    /**设置背影颜色透明度*/
    open fun setPopWindowBgAlpha(@FloatRange(from = 0.0, to = 1.0)alpha: Float) {
        val lp: WindowManager.LayoutParams = mActivity.window.attributes
        lp.alpha = alpha
        mActivity.window.attributes = lp
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        setPopWindowBgAlpha(0.3f)
    }

    override fun dismiss() {
        super.dismiss()
        setPopWindowBgAlpha(1.0f)
    }

    interface OnPopWindowViewClick {
        fun onViewClick(view: View)
    }
}