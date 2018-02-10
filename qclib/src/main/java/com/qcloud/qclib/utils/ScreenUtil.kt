package com.qcloud.qclib.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager

/**
 * 类说明：获得屏幕相关的工具类
 * Author: Kuzan
 * Date: 2017/12/5 20:08.
 */
object ScreenUtil {
    private const val STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height"
    private const val NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height"
    private const val NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape"
    private const val NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width"

    /**
     * 获取DisplayMetrics(像素)
     *      width = dm.widthPixels
     *      height = dm.heightPixels
     *
     * @param context 上下文
     */
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val dm = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(dm)
        return dm
    }

    /**
     * 获取Point(大小)
     *      width = point.x
     *      height = point.y
     *
     * @param context
     * */
    fun getPoint(context: Context): Point {
        val point = Point()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getSize(point)

        return point
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     */
    fun getScreenWidth(context: Context): Int = getDisplayMetrics(context).widthPixels

    /**
     * 获得屏幕高度
     *
     * @param context
     */
    fun getScreenHeight(context: Context) = getDisplayMetrics(context).heightPixels

    /**
     * 获得状态栏的高度，通过映射方法
     *
     * @param context
     */
    @SuppressLint("PrivateApi")
    fun getStatusBarHeight(context: Context): Int {
        return try {
            val clazz = Class.forName("com.android.internal.R\$dimen")
            val obj = clazz.newInstance()
            val height = Integer.parseInt(clazz.getField("status_bar_height").get(obj).toString())
            return context.resources.getDimensionPixelSize(height)
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 获得状态栏的高度，通过资源方法
     *
     * @param context
     */
    fun getStatusBarHeightByRes(context: Context): Int {
        val res = context.resources
        val resourceId = res.getIdentifier(STATUS_BAR_HEIGHT_RES_NAME, "dimen", "android")
        if (resourceId > 0) {
            return res.getDimensionPixelSize(resourceId)
        }
        return 0
    }

    /**
     * 获取导航栏高度
     *
     * @param activity
     * */
    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(14)
    fun getNavBarWidth(activity: Activity): Int {
        val res = activity.resources

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (SystemBarUtil.hasNavBar(activity)) {
                return getInternalDimensionSize(res, NAV_BAR_WIDTH_RES_NAME)
            }
        }
        return 0
    }

    /**
     * 获取导航栏高度
     *
     * @param activity
     * */
    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(14)
    fun getNavBarHeight(activity: Activity): Int {
        val res = activity.resources

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (SystemBarUtil.hasNavBar(activity)) {
                // 是否竖屏
                val isInPortrait = res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

                val key = if (isInPortrait) {
                    NAV_BAR_HEIGHT_RES_NAME
                } else {
                    NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME
                }
                return getInternalDimensionSize(res, key)
            }
        }
        return 0
    }

    /**
     * 获取内部尺寸
     *
     * @param res
     * @param key
     * */
    fun getInternalDimensionSize(res: Resources, key: String): Int {
        val resourceId = res.getIdentifier(key, "dimen", "android")
        return if (resourceId > 0) {
            res.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    /**
     * 可见屏幕高度
     *
     * @param paramActivity
     * */
    fun getAppHeight(paramActivity: Activity): Int {
        val localRect = Rect()
        paramActivity.window.decorView.getWindowVisibleDisplayFrame(localRect)
        return localRect.height()
    }

    /**
     * 内容高度
     *
     * @param paramActivity
     * */
    fun getAppContentHeight(paramActivity: Activity): Int =
            getScreenHeight(paramActivity) - getStatusBarHeight(paramActivity) -
                    getActionBarHeight(paramActivity) - KeyBoardUtil.getKeyboardHeight(paramActivity)

    /**
     * 获取标题栏高度
     *
     * @param paramActivity
     * */
    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(14)
    fun getActionBarHeight(paramActivity: Activity): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            val tv = TypedValue()
            paramActivity.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)
            return TypedValue.complexToDimensionPixelSize(tv.data, paramActivity.resources.displayMetrics)
        }
        return DensityUtil.dp2px(paramActivity, 48F)
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param paramActivity
     */
    fun snapShotWithStatusBar(paramActivity: Activity): Bitmap {
        val view = paramActivity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()

        val bmp = view.drawingCache
        val width = getScreenWidth(paramActivity)
        val height = getScreenHeight(paramActivity)

        val outBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height)
        view.destroyDrawingCache()
        bmp.recycle()
        return outBitmap
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param paramActivity
     */
    fun snapShotWithoutStatusBar(paramActivity: Activity): Bitmap {
        val view = paramActivity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()

        val bmp = view.drawingCache
        val rect = Rect()
        paramActivity.window.decorView.getWindowVisibleDisplayFrame(rect)
        val statusBarHeight = rect.top

        val width = getScreenWidth(paramActivity)
        val height = getScreenHeight(paramActivity)

        val outBitmap = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight)
        view.destroyDrawingCache()
        bmp.recycle()
        return outBitmap
    }

    /**
     * 获取屏幕密度
     *
     * @param paramActivity
     * */
    fun getScreenDensity(paramActivity: Activity): Float {
        val dm = DisplayMetrics()
        paramActivity.windowManager.defaultDisplay.getMetrics(dm)
        return dm.density
    }

    /**
     * 获取屏幕分辨率
     *
     * @param paramActivity
     * */
    fun getScreenDensityDpi(paramActivity: Activity): Int {
        val dm = DisplayMetrics()
        paramActivity.windowManager.defaultDisplay.getMetrics(dm)
        return dm.densityDpi
    }

    /**
     * 获取宽高的最小宽度dip值(主要用来区分横竖屏)
     *
     * @param activity
     * */
    @SuppressLint("NewApi")
    fun getSmallestWidthDp(activity: Activity): Float {
        val metrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.windowManager.defaultDisplay.getRealMetrics(metrics)
        } else {
            activity.windowManager.defaultDisplay.getMetrics(metrics)
        }
        val widthDp = metrics.widthPixels / metrics.density
        val heightDp = metrics.heightPixels / metrics.density

        return Math.min(widthDp, heightDp)
    }
}