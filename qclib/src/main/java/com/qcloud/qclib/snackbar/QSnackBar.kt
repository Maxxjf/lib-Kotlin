package com.qcloud.qclib.snackbar

import android.content.Context
import android.content.res.Resources
import android.support.annotation.LayoutRes
import android.support.annotation.NonNull
import android.support.design.widget.Snackbar
import android.view.Gravity
import android.view.View

/**
 * 类说明：显示Snackbar
 * Author: Kuzan
 * Date: 2018/2/7 19:04.
 */
object QSnackBar {
    /**
     * 显示常规的
     * */
    fun show(view: View?, obj: Any?, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.NORMAL_COLOR, 15f)
                .show()
    }

    /**
     * 成功提示
     * */
    fun success(view: View?, obj: Any?, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.SUCCESS_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.SUCCESS_ICON)
                .show()
    }

    /**
     * 信息提示
     * */
    fun info(view: View?, obj: Any?, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.INFO_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.INFO_ICON)
                .show()
    }

    /**
     * 警告提示
     * */
    fun warning(view: View?, obj: Any?, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.WARNING_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.WARNING_ICON)
                .show()
    }

    /**
     * 错误提示
     * */
    fun error(view: View?, obj: Any?, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.ERROR_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.ERROR_ICON)
                .show()
    }

    /**
     * 显示常规的
     * */
    fun showCallback(view: View?, obj: Any?, @NonNull callback: Snackbar.Callback, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.NORMAL_COLOR, 15f)
                .setCallback(callback)
                .show()
    }

    /**
     * 成功提示
     * */
    fun successCallback(view: View?, obj: Any?, @NonNull callback: Snackbar.Callback, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.SUCCESS_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.SUCCESS_ICON)
                .setCallback(callback)
                .show()
    }

    /**
     * 信息提示
     * */
    fun infoCallback(view: View?, obj: Any?, @NonNull callback: Snackbar.Callback, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.INFO_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.INFO_ICON)
                .setCallback(callback)
                .show()
    }

    /**
     * 警告提示
     * */
    fun warningCallback(view: View?, obj: Any?, @NonNull callback: Snackbar.Callback, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.WARNING_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.WARNING_ICON)
                .setCallback(callback)
                .show()
    }

    /**
     * 错误提示
     * */
    fun errorCallback(view: View?, obj: Any?, @NonNull callback: Snackbar.Callback, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.ERROR_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.ERROR_ICON)
                .setCallback(callback)
                .show()
    }

    /**
     * 显示常规的
     * */
    fun showAction(view: View?, obj: Any?, actionName: String, actionColor: Int = CustomSnackBar.DEFAULT_ACTION_COLOR, listener: View.OnClickListener, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.NORMAL_COLOR, 15f)
                .setAction(actionName, actionColor, listener)
                .show()
    }

    /**
     * 成功提示
     * */
    fun successAction(view: View?, obj: Any?, actionName: String, actionColor: Int = CustomSnackBar.DEFAULT_ACTION_COLOR, listener: View.OnClickListener, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.SUCCESS_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.SUCCESS_ICON)
                .setAction(actionName, actionColor, listener)
                .show()
    }

    /**
     * 信息提示
     * */
    fun infoAction(view: View?, obj: Any?, actionName: String, actionColor: Int = CustomSnackBar.DEFAULT_ACTION_COLOR, listener: View.OnClickListener, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.INFO_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.INFO_ICON)
                .setAction(actionName, actionColor, listener)
                .show()
    }

    /**
     * 警告提示
     * */
    fun warningAction(view: View?, obj: Any?, actionName: String, actionColor: Int = CustomSnackBar.DEFAULT_ACTION_COLOR, listener: View.OnClickListener, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.WARNING_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.WARNING_ICON)
                .setAction(actionName, actionColor, listener)
                .show()
    }

    /**
     * 错误提示
     * */
    fun errorAction(view: View?, obj: Any?, actionName: String, actionColor: Int = CustomSnackBar.DEFAULT_ACTION_COLOR, listener: View.OnClickListener, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.ERROR_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.ERROR_ICON)
                .setAction(actionName, actionColor, listener)
                .show()
    }

    /**
     * 显示常规的
     * */
    fun showAbove(view: View?, obj: Any?, targetView: View, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.NORMAL_COLOR, 15f)
                .above(targetView)
                .show()
    }

    /**
     * 成功提示
     * */
    fun successAbove(view: View?, obj: Any?, targetView: View, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.SUCCESS_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.SUCCESS_ICON)
                .above(targetView)
                .show()
    }

    /**
     * 信息提示
     * */
    fun infoAbove(view: View?, obj: Any?, targetView: View, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.INFO_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.INFO_ICON)
                .above(targetView)
                .show()
    }

    /**
     * 警告提示
     * */
    fun warningAbove(view: View?, obj: Any?, targetView: View, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.WARNING_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.WARNING_ICON)
                .above(targetView)
                .show()
    }

    /**
     * 错误提示
     * */
    fun errorAbove(view: View?, obj: Any?, targetView: View, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.ERROR_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.ERROR_ICON)
                .above(targetView)
                .show()
    }

    /**
     * 显示常规的
     * */
    fun showBellow(view: View?, obj: Any?, targetView: View, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.NORMAL_COLOR, 15f)
                .bellow(targetView)
                .show()
    }

    /**
     * 成功提示
     * */
    fun successBellow(view: View?, obj: Any?, targetView: View, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.SUCCESS_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.SUCCESS_ICON)
                .bellow(targetView)
                .show()
    }

    /**
     * 信息提示
     * */
    fun infoBellow(view: View?, obj: Any?, targetView: View, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.INFO_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.INFO_ICON)
                .bellow(targetView)
                .show()
    }

    /**
     * 警告提示
     * */
    fun warningBellow(view: View?, obj: Any?, targetView: View, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.WARNING_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.WARNING_ICON)
                .bellow(targetView)
                .show()
    }

    /**
     * 错误提示
     *
     * @param targetView 指定View
     * */
    fun errorBellow(view: View?, obj: Any?, targetView: View, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .setBackground(CustomSnackBar.ERROR_COLOR, 15f)
                .setLeftIcon(CustomSnackBar.ERROR_ICON)
                .bellow(targetView)
                .show()
    }

    /**
     * 自定义布局
     * */
    fun custom(view: View?, obj: Any?, @LayoutRes viewId: Int, duration: Int = CustomSnackBar.LENGTH_SHORT) {
        if (view == null) {
            return
        }
        val message: String = initMessage(view.context, obj)

        CustomSnackBar.create(view, message, duration)
                .addView(viewId, Gravity.CENTER, 0)
                .show()
    }

    /**
     * 初始化打印信息
     * */
    private fun initMessage(@NonNull context: Context, obj: Any?): String {
        return if (obj is Int) {
            try {
                context.getString(obj)
            } catch (e: Resources.NotFoundException) {
                obj.toString()
            }
        } else {
            obj.toString()
        }
    }
}