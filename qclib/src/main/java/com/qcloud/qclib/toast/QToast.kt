package com.qcloud.qclib.toast

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.support.annotation.NonNull

/**
 * 类说明：显示自定义Toast
 * Author: Kuzan
 * Date: 2017/12/20 10:48.
 */
object QToast {
    @SuppressLint("StaticFieldLeak")
    @Volatile private var mToast: CustomToast? = null

    /**
     * 常规toast
     *
     * @param context   上下文
     * @param obj       提示信息
     * @param duration  显示时长，可不传，默认为2500ms
     * */
    fun show(context: Context?, obj: Any?, duration: Long = CustomToast.LENGTH_LONG) {
        if (context == null) {
            return
        }
        if (mToast == null) {
            initToast(context)
        }

        if (obj is Int) {
            val value = try {
                context.getString(obj)
            } catch (e: Resources.NotFoundException) {
                obj.toString()
            }
            mToast?.setText(value)
        } else {
            mToast?.setText(obj.toString())
        }

        mToast?.setDuration(duration)
        mToast?.showIcon(false)
        mToast?.setBackground()
        mToast?.show()
    }

    /**
     * 成功toast
     *
     * @param context   上下文
     * @param obj       提示信息
     * @param withIcon  是否显示图标，可不传，默认显示
     * @param duration  显示时长，可不传，默认为2500ms
     * */
    fun success(context: Context?, obj: Any?, withIcon: Boolean = true, duration: Long = CustomToast.LENGTH_LONG) {
        if (context == null) {
            return
        }
        if (mToast == null) {
            initToast(context)
        }

        if (obj is Int) {
            val value = try {
                context.getString(obj)
            } catch (e: Resources.NotFoundException) {
                obj.toString()
            }
            mToast?.setText(value)
        } else {
            mToast?.setText(obj.toString())
        }

        mToast?.setDuration(duration)
        mToast?.setBackgroundColor(CustomToast.SUCCESS_COLOR)
        mToast?.setIcon(CustomToast.SUCCESS_ICON, withIcon)
        mToast?.show()
    }

    /**
     * 提示toast
     *
     * @param context   上下文
     * @param obj       提示信息
     * @param withIcon  是否显示图标，可不传，默认显示
     * @param duration  显示时长，可不传，默认为2500ms
     * */
    fun info(context: Context?, obj: Any?, withIcon: Boolean = true, duration: Long = CustomToast.LENGTH_LONG) {
        if (context == null) {
            return
        }
        if (mToast == null) {
            initToast(context)
        }

        if (obj is Int) {
            val value = try {
                context.getString(obj)
            } catch (e: Resources.NotFoundException) {
                obj.toString()
            }
            mToast?.setText(value)
        } else {
            mToast?.setText(obj.toString())
        }

        mToast?.setDuration(duration)
        mToast?.setBackgroundColor(CustomToast.INFO_COLOR)
        mToast?.setIcon(CustomToast.INFO_ICON, withIcon)
        mToast?.show()
    }

    /**
     * 警告toast
     *
     * @param context   上下文
     * @param obj       提示信息
     * @param withIcon  是否显示图标，可不传，默认显示
     * @param duration  显示时长，可不传，默认为2500ms
     * */
    fun warning(context: Context?, obj: Any?, withIcon: Boolean = true, duration: Long = CustomToast.LENGTH_LONG) {
        if (context == null) {
            return
        }
        if (mToast == null) {
            initToast(context)
        }

        if (obj is Int) {
            val value = try {
                context.getString(obj)
            } catch (e: Resources.NotFoundException) {
                obj.toString()
            }
            mToast?.setText(value)
        } else {
            mToast?.setText(obj.toString())
        }

        mToast?.setDuration(duration)
        mToast?.setBackgroundColor(CustomToast.WARNING_COLOR)
        mToast?.setIcon(CustomToast.WARNING_ICON, withIcon)
        mToast?.show()
    }

    /**
     * 错误toast
     *
     * @param context   上下文
     * @param obj       提示信息
     * @param withIcon  是否显示图标，可不传，默认显示
     * @param duration  显示时长，可不传，默认为2500ms
     * */
    fun error(context: Context?, obj: Any?, withIcon: Boolean = true, duration: Long = CustomToast.LENGTH_LONG) {
        if (context == null) {
            return
        }
        if (mToast == null) {
            initToast(context)
        }

        if (obj is Int) {
            val value = try {
                context.getString(obj)
            } catch (e: Resources.NotFoundException) {
                obj.toString()
            }
            mToast?.setText(value)
        } else {
            mToast?.setText(obj.toString())
        }

        mToast?.setDuration(duration)
        mToast?.setBackgroundColor(CustomToast.ERROR_COLOR)
        mToast?.setIcon(CustomToast.ERROR_ICON, withIcon)
        mToast?.show()
    }

    /**
     * 初始化toast
     * */
    private fun initToast(@NonNull context: Context) {
        synchronized(QToast::class.java) {
            if (mToast == null) {
                mToast = CustomToast.getInstance(context.applicationContext)
            }
        }
    }
}