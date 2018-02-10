package com.qcloud.qclib.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.FloatRange
import android.support.annotation.RequiresApi
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout

/**
 * 类说明：系统状态栏管理工具
 * Author: Kuzan
 * Date: 2017/12/7 16:16.
 */
@SuppressLint("ResourceType")
class SystemBarTintManager constructor(activity: Activity) {
    // 当前设备配置的系统配置
    val mConfig: SystemBarConfig
    var mStatusBarAvailable: Boolean = false
    var mNavBarAvailable: Boolean = false
    var mStatusBarTintEnabled: Boolean = false
    var mNavBarTintEnabled: Boolean = false

    private var mStatusBarTintView: View? = null
    private var mNavBarTintView: View? = null

    init {
        val window = activity.window
        val decorViewGroup = window.decorView as ViewGroup

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val attrs = intArrayOf(android.R.attr.windowTranslucentStatus, android.R.attr.windowTranslucentNavigation)
            val ta = activity.obtainStyledAttributes(attrs)

            try {
                mStatusBarAvailable = ta.getBoolean(0, false)
                mNavBarAvailable = ta.getBoolean(1, false)
            } finally {
                ta.recycle()
            }

            val windowParams = window.attributes
            val statusBits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            if ((windowParams.flags and statusBits) != 0) {
                mStatusBarAvailable = true
            }
            val navBits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            if ((windowParams.flags and navBits) != 0) {
                mNavBarAvailable = true
            }
        }

        mConfig = SystemBarConfig(activity, mStatusBarAvailable, mNavBarAvailable)
        if (!SystemBarUtil.hasNavBar(activity)) {
            mNavBarAvailable = false
        }

        if (mStatusBarAvailable) {
            setupStatusBarView(activity, decorViewGroup)
        }

        if (mNavBarAvailable) {
            setupNavBarView(activity, decorViewGroup)
        }
    }

    /**
     * 使系统状态栏的着色。
     *
     * @param enabled
     */
    fun setStatusBarTintEnabled(enabled: Boolean) {
        mStatusBarTintEnabled = enabled
        if (mStatusBarAvailable) {
            mStatusBarTintView?.visibility = if (enabled) View.VISIBLE else View.GONE
        }
    }

    /**
     * 使系统导航栏的着色。
     *
     * @param enabled
     */
    fun setNavigationBarTintEnabled(enabled: Boolean) {
        mNavBarTintEnabled = enabled
        if (mNavBarAvailable) {
            mNavBarTintView?.visibility = if (enabled) View.VISIBLE else View.GONE
        }
    }

    /**
     * 修改状态栏和导航栏为指定的颜色
     *
     * @param color
     */
    fun setTintColor(@ColorInt color: Int) {
        setStatusBarTintColor(color)
        setNavigationBarTintColor(color)
    }

    /**
     * 修改状态栏和导航栏为指定的drawable
     *
     * @param res
     */
    fun setTintResource(@DrawableRes res: Int) {
        setStatusBarTintResource(res)
        setNavigationBarTintResource(res)
    }

    /**
     * 修改状态栏和导航栏为指定的drawable
     *
     * @param drawable
     */
    fun setTintDrawable(drawable: Drawable) {
        setStatusBarTintDrawable(drawable)
        setNavigationBarTintDrawable(drawable)
    }

    /**
     * 修改状态栏和导航栏的透明度
     *
     * @param alpha
     */
    fun setTintAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        setStatusBarAlpha(alpha)
        setNavigationBarAlpha(alpha)
    }

    /**
     * 修改状态栏为指定的颜色
     *
     * @param color
     */
    fun setStatusBarTintColor(@ColorInt color: Int) {
        if (mStatusBarAvailable) {
            mStatusBarTintView?.setBackgroundColor(color)
        }
    }

    /**
     * 修改状态栏为指定的drawable
     *
     * @param res
     */
    fun setStatusBarTintResource(@DrawableRes res: Int) {
        if (mStatusBarAvailable) {
            mStatusBarTintView?.setBackgroundResource(res)
        }
    }

    /**
     * 修改状态栏为指定的drawable
     *
     * @param drawable
     */
    @SuppressWarnings("deprecation")
    fun setStatusBarTintDrawable(drawable: Drawable) {
        if (mStatusBarAvailable) {
            mStatusBarTintView?.setBackgroundDrawable(drawable)
        }
    }


    /**
     * 修改状态栏的透明度
     *
     * @param alpha
     */
    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun setStatusBarAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        if (mStatusBarAvailable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mStatusBarTintView?.alpha = alpha
            }
        }
    }

    /**
     * 修改导航栏为指定的颜色
     *
     * @param color
     */
    fun setNavigationBarTintColor(@ColorInt color: Int) {
        if (mNavBarAvailable) {
            mNavBarTintView?.setBackgroundColor(color)
        }
    }

    /**
     * 修改导航栏为指定的drawable
     *
     * @param res
     */
    fun setNavigationBarTintResource(@DrawableRes res: Int) {
        if (mNavBarAvailable) {
            mNavBarTintView?.setBackgroundResource(res)
        }
    }

    /**
     * 修改导航栏为指定的drawable
     *
     * @param drawable
     */
    @SuppressWarnings("deprecation")
    fun setNavigationBarTintDrawable(drawable: Drawable) {
        if (mNavBarAvailable) {
            mNavBarTintView?.setBackgroundDrawable(drawable)
        }
    }


    /**
     * 修改导航栏的透明度
     *
     * @param alpha The alpha to use
     */
    @SuppressLint("ObsoleteSdkInt")
    fun setNavigationBarAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        if (mNavBarAvailable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mNavBarTintView?.alpha = alpha
            }
        }
    }

    fun setupStatusBarView(activity: Activity, decorViewGroup: ViewGroup) {
        mStatusBarTintView = View(activity)
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mConfig.mStatusBarHeight)
        params.gravity = Gravity.TOP
        if (mNavBarAvailable && !SystemBarUtil.isNavAtBottom(activity)) {
            params.rightMargin = mConfig.mNavigationBarWidth
        }
        mStatusBarTintView?.layoutParams = params
        mStatusBarTintView?.setBackgroundColor(Color.TRANSPARENT)
        mStatusBarTintView?.visibility = View.GONE
        decorViewGroup.addView(mStatusBarTintView)
    }

    fun setupNavBarView(activity: Activity, decorViewGroup: ViewGroup) {
        mNavBarTintView = View(activity)
        val params: FrameLayout.LayoutParams
        if (SystemBarUtil.isNavAtBottom(activity)) {
            params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mConfig.mNavigationBarHeight)
            params.gravity = Gravity.BOTTOM
        } else {
            params = FrameLayout.LayoutParams(mConfig.mNavigationBarWidth, FrameLayout.LayoutParams.MATCH_PARENT)
            params.gravity = Gravity.RIGHT
        }
        mNavBarTintView?.layoutParams = params
        mNavBarTintView?.setBackgroundColor(Color.TRANSPARENT)
        mNavBarTintView?.visibility = View.GONE
        decorViewGroup.addView(mNavBarTintView)
    }
}