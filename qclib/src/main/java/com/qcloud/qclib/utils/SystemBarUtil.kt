package com.qcloud.qclib.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.RequiresApi
import android.support.v4.graphics.ColorUtils
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout

/**
 * 类说明：系统状态栏工具类
 * Author: Kuzan
 * Date: 2017/12/7 13:56.
 */
object SystemBarUtil {
    private const val SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar"

    /**
     * 修改状态栏为全透明，需要Android 4.4以上
     *
     * @param activity
     */
    fun transparencyStatusBar(activity: Activity) {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 防止系统栏隐藏时内容区域大小发生变化
            var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !OSUtil.isEmui3_1()) {
                // 初始化5.0以上，包含5.0
                uiFlags = uiFlags or initStatusBarAboveLOLLIPOP(uiFlags, window)
            } else {
                // 初始化5.0以下，4.4以上沉浸式
                initStatusBarBelowLOLLIPOP(activity)
            }
            window.decorView.systemUiVisibility = uiFlags
        }
    }

    /**
     * 初始化5.0以上，包含5.0的状态栏
     *
     * @param uiFlags
     * @param window
     * */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun initStatusBarAboveLOLLIPOP(uiFlags: Int, window: Window): Int {
        // Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态栏遮住。
        val newUiFlags = uiFlags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // 设置状态栏颜色为透明
        window.statusBarColor = (ColorUtils.blendARGB(Color.TRANSPARENT, Color.TRANSPARENT, 0.0f))

        return newUiFlags
    }


    /**
     * 初始化5.0以下Android 4.4以上的状态栏 和 Emui3.1状态栏
     *
     * @param activity
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun initStatusBarBelowLOLLIPOP(activity: Activity) {
        // 透明状态栏
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        createStatusBarView(activity)
    }

    /**
     * 设置一个可以自定义颜色的状态栏
     *
     * @param activity
     * @param backgroundColor 颜色值
     */
    fun createStatusBarView(activity: Activity, @ColorInt backgroundColor: Int = Color.TRANSPARENT) {
        val statusBarView = View(activity)
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenUtil.getStatusBarHeight(activity))
        params.gravity = Gravity.TOP

        statusBarView.layoutParams = params
        statusBarView.setBackgroundColor(ColorUtils.blendARGB(backgroundColor, backgroundColor, 0.0f))
        statusBarView.visibility = View.VISIBLE

        val viewGroup = statusBarView.parent as ViewGroup
        viewGroup.removeView(statusBarView)

        val decorView = activity.window.decorView as ViewGroup
        decorView.addView(statusBarView)
    }

    /**
     *  重新绘制标题栏高度，解决状态栏与顶部重叠问题
     *
     *  @param activity
     *  @param titleBarView 标题栏
     * */
    fun remeasureTitleBar(activity: Activity, titleBarView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            titleBarView.measure(width, height)

            val layoutParams = titleBarView.layoutParams
            val statusBarHeight = ScreenUtil.getStatusBarHeight(activity)
            val titleBarHeight = titleBarView.measuredHeight + statusBarHeight
            val titleBarPaddingTopHeight = titleBarView.paddingTop + statusBarHeight

            layoutParams.height = titleBarHeight
            titleBarView.setPadding(titleBarView.paddingLeft, titleBarPaddingTopHeight, titleBarView.paddingRight, titleBarView.paddingBottom)
            titleBarView.layoutParams = layoutParams
        }
    }

    /**
     * 修改底部导航栏为全透明
     *
     * @param activity
     */
    fun transparencyNavBar(activity: Activity) {
        if (hasNavBar(activity)) {
            val window = activity.window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 防止系统栏隐藏时内容区域大小发生变化
                var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !OSUtil.isEmui3_1()) {
                    uiFlags = uiFlags or initNavBarAboveLOLLIPOP(uiFlags, activity)
                } else {
                    // 初始化5.0以下，4.4以上沉浸式
                    initNavBarBelowLOLLIPOP(activity)
                }
                window.decorView.systemUiVisibility = uiFlags
            } else {
                System.out.print("当前设备没有导航栏或者低于4.4系统")
            }
        }
    }

    /**
     * 初始化5.0以上，包含5.0的导航栏
     *
     * @param uiFlags
     * @param activity
     * */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun initNavBarAboveLOLLIPOP(uiFlags: Int, activity: Activity): Int {
        // Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态栏遮住。
        var newUiFlags = uiFlags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        // Activity全屏显示，但导航栏不会被隐藏覆盖，导航栏依然可见，Activity底部布局部分会被导航栏遮住。
        newUiFlags = newUiFlags or  View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        val window = activity.window
        if (hasNavBar(activity)) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        // 设置导航栏颜色为透明
        window.navigationBarColor = ColorUtils.blendARGB(Color.TRANSPARENT, Color.TRANSPARENT, 0.0f)

        return newUiFlags
    }


    /**
     * 初始化Android 5.0 Android 4.4以上导航栏 和 Emui3.1导航栏
     *
     * @param activity
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun initNavBarBelowLOLLIPOP(activity: Activity) {
        // 透明状态栏
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        if (hasNavBar(activity)) {
            // 透明导航栏，设置这个，如果有导航栏，底部布局会被导航栏遮住
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            // 创建一个假的导航栏
            createNavBarView(activity)
        }
    }

    /**
     * 设置一个可以自定义颜色的导航栏
     *
     * @param activity
     * @param backgroundColor 背景颜色
     */
    fun createNavBarView(activity: Activity, @ColorInt backgroundColor: Int = Color.TRANSPARENT) {
        val navBarView = View(activity)

        val params: FrameLayout.LayoutParams
        if (isNavAtBottom(activity)) {
            params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenUtil.getNavBarHeight(activity))
            params.gravity = Gravity.BOTTOM
        } else {
            params = FrameLayout.LayoutParams(ScreenUtil.getNavBarWidth(activity), FrameLayout.LayoutParams.MATCH_PARENT)
            params.gravity = Gravity.END
        }
        navBarView.layoutParams = params
        navBarView.setBackgroundColor(ColorUtils.blendARGB(backgroundColor, backgroundColor, 0.0f))
        navBarView.visibility = View.VISIBLE

        val viewGroup = navBarView.parent as ViewGroup
        viewGroup.removeView(navBarView)

        val decorView = activity.window.decorView as ViewGroup
        decorView.addView(navBarView)
    }

    /**
     * 隐藏状态栏
     *
     * @param activity
     */
    fun hideStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = activity.window
            // 防止系统栏隐藏时内容区域大小发生变化
            var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            uiFlags = uiFlags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            uiFlags = uiFlags or View.INVISIBLE
            uiFlags = uiFlags or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            window.decorView.systemUiVisibility = uiFlags
        }
    }

    /**
     * 显示状态栏
     *
     * @param activity
     */
    fun showStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = activity.window
            // 防止系统栏隐藏时内容区域大小发生变化
            var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            uiFlags = uiFlags or View.SYSTEM_UI_FLAG_VISIBLE
            uiFlags = uiFlags or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            window.decorView.systemUiVisibility = uiFlags
        }
    }

    /**
     * 隐藏底部导航栏
     *
     * @param activity
     */
    fun hideNavBar(activity: Activity) {
        if (hasNavBar(activity) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = activity.window
            // 防止系统栏隐藏时内容区域大小发生变化
            var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            // 隐藏状态栏或者导航栏
            uiFlags = uiFlags or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            uiFlags = uiFlags or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            uiFlags = uiFlags or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

            window.decorView.systemUiVisibility = uiFlags
        } else {
            System.out.print("当前设备没有导航栏或者低于4.4系统")
        }
    }

    /**
     * 显示底部导航栏
     *
     * @param activity
     */
    fun showNavBar(activity: Activity) {
        if (hasNavBar(activity) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = activity.window
            // 防止系统栏隐藏时内容区域大小发生变化
            var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            uiFlags = uiFlags or View.SYSTEM_UI_FLAG_VISIBLE
            uiFlags = uiFlags or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

            window.decorView.systemUiVisibility = uiFlags
        } else {
            System.out.print("当前设备没有导航栏或者低于4.4系统")
        }
    }

    /**
     * 是否有底部导航栏
     *
     * @param activity
     * @return boolean
     * */
    fun hasNavBar(activity: Activity): Boolean {
        val manager = activity.windowManager
        val display = manager.defaultDisplay

        val realMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(realMetrics)
        }
        val realWidth = realMetrics.widthPixels
        val realHeight = realMetrics.heightPixels

        val metrics = DisplayMetrics()
        display.getMetrics(metrics)

        val width = metrics.widthPixels
        val height = metrics.heightPixels

        return (realWidth - width) > 0 || (realHeight - height) > 0
    }


    /**
     * 是否有底部导航栏
     *
     * @param context
     * @return boolean
     * */
    @SuppressLint("PrivateApi")
    @TargetApi(14)
    fun hasNavBar(context: Context): Boolean {
        var sNavBarOverride: String? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                val clazz = Class.forName("android.os.SystemProperties")
                val method = clazz.getDeclaredMethod("get", String::class.java)
                method.isAccessible = true
                sNavBarOverride = method.invoke(null, "qemu.hw.mainkeys") as String
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }

        val res = context.resources
        val resourceId = res.getIdentifier(SHOW_NAV_BAR_RES_NAME, "bool", "android")
        return if (resourceId != 0) {
            var hasNav = res.getBoolean(resourceId)
            if (sNavBarOverride == "1") {
                hasNav = false
            } else if (sNavBarOverride == "0") {
                hasNav = true
            }
            return hasNav
        } else {
            !ViewConfiguration.get(context).hasPermanentMenuKey()
        }
    }

    /**
     * 是否导航栏在底部(判断横竖屏)
     *
     * @param activity
     * */
    fun isNavAtBottom(activity: Activity): Boolean {
        val res = activity.resources
        val isInPortrait = res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        return ScreenUtil.getSmallestWidthDp(activity) >= 600 || isInPortrait
    }


    /**
     * 修改状态栏颜色，支持4.4以上版本,保持沉浸式状态
     *
     * @param activity
     * @param colorId
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun setStatusBarColorKeepFollow(activity: Activity, colorId: Int) = setStatusBarColor(activity, colorId, true, false)

    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param activity
     * @param colorId  直接使用资源ID，即(ContextCompat.getColor(context, R.color.xxx))
     * @param isFollow 是否保持沉浸式状态
     * @param isPadding 是否需要解决状态栏与标题栏重叠问题，主要用来解决小米系统
     */
    @SuppressLint("ObsoleteSdkInt")
    fun setStatusBarColor(activity: Activity, @ColorInt colorId: Int, isFollow: Boolean, isPadding: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!isFollow) {
                val window = activity.window
                window.statusBarColor = colorId
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 使用SystemBarTint库使4.4版本状态栏变色，需要先将状态栏设置为透明
            transparencyStatusBar(activity)
            val contentFrameLayout = activity.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
            val parentView = contentFrameLayout.getChildAt(0)
            if (parentView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                parentView.fitsSystemWindows = true
            }
            val tintManager = SystemBarTintManager(activity)
            tintManager.setStatusBarTintEnabled(true)
            tintManager.setStatusBarTintColor(colorId)

            val decorView = activity.window.decorView as ViewGroup
            val contentView = decorView.findViewById<ViewGroup>(android.R.id.content)

            if (isPadding) {
                contentView.setPadding(0, ScreenUtil.getStatusBarHeight(activity), 0, 0)
            } else {
                contentView.setPadding(0, 0, 0, 0)
            }
        }
    }

    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @param dark     是否把状态栏字体及图标颜色设置为深色
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    fun setStatusBarLightMode(activity: Activity, dark: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            when {
                OSUtil.isMIUI() -> {
                    setMIUIStatusBarLightMode(activity.window, dark)
                    // 解决MIUI9以上不改变字体颜色问题
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (dark) {
                            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        }
                    }
                }
                OSUtil.isFlyme() -> {
                    setFlymeStatusBarLightMode(activity.window, dark)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    // android 6.0以上
                    val uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    activity.window.decorView.systemUiVisibility = uiFlags
                }
                else -> {
                    val uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    activity.window.decorView.systemUiVisibility = uiFlags
                }
            }
        }
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    @SuppressLint("PrivateApi")
    fun setMIUIStatusBarLightMode(window: Window, dark: Boolean): Boolean {
        return try {
            val clazz = window.javaClass
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            val darkModeFlag = field.getInt(layoutParams)
            val method = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            if (dark) {
                // 状态栏透明且黑色字体
                method.invoke(window, darkModeFlag, darkModeFlag)
            } else {
                // 清除黑色字体
                method.invoke(window, 0, darkModeFlag)
            }
            return true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    fun setFlymeStatusBarLightMode(window: Window, dark: Boolean): Boolean {
        return try {
            val params = window.attributes as WindowManager.LayoutParams
            val darkFlag = WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            darkFlag.isAccessible = true

            val meizuFlag = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
            meizuFlag.isAccessible = true

            val bit = darkFlag.getInt(null)
            var value = meizuFlag.getInt(params)
            value = if (dark) {
                value or bit
            } else {
                value and bit.inv()
            }
            meizuFlag.setInt(params, value)
            window.attributes = params
            return true
        } catch (e: Exception) {
            false
        }
    }


    /**
     * 已知系统类型时，设置状态栏黑色字体图标。
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @param type     1:MIUUI 2:Flyme 3:android6.0
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun setStatusBarDarkMode(activity: Activity, type: Int) {
        when (type) {
            1 -> setMIUIStatusBarLightMode(activity.window, true)
            2 -> setFlymeStatusBarLightMode(activity.window, true)
            3 -> {
                val uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                activity.window.decorView.systemUiVisibility = uiFlags
            }
        }
    }

    /**
     * 清除MIUI或flyme或6.0以上版本状态栏黑色字体，即白色字体
     *
     * @param activity
     * @param type     1:MIUUI 2:Flyme 3:android6.0
     */
    fun setStatusBarLightMode(activity: Activity, type: Int) {
        when (type) {
            1 -> setMIUIStatusBarLightMode(activity.window, false)
            2 -> setFlymeStatusBarLightMode(activity.window, false)
            3 -> activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }


    /**
     * 设置状态栏
     *
     * @param activity
     * @param useThemeStatusBarColor   是否要状态栏的颜色，不设置则为透明色
     * @param withoutUseStatusBarColor 是否不需要使用状态栏为暗色调
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun setStatusBar(activity: Activity, useThemeStatusBarColor: Boolean, withoutUseStatusBarColor: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0及以上
            val decorView = activity.window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            if (useThemeStatusBarColor) {
                activity.window.statusBarColor = Color.WHITE
            } else {
                activity.window.statusBarColor = Color.TRANSPARENT
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 4.4到5.0
            val localLayoutParams = activity.window.attributes
            localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !withoutUseStatusBarColor) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    /**
     * 判断手机支不支持状态栏字体变色
     *
     * @return the boolean
     */
    fun isSupportStatusBarDarkFont(): Boolean = OSUtil.isMiui6Later() || OSUtil.isFlymeOS4Later() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}