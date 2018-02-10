package com.qcloud.qclib.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * 类说明：软键盘工具类
 * Author: Kuzan
 * Date: 2017/12/5 20:03.
 */
object KeyBoardUtil {
    private const val KEY_BOARD_HEIGHT = "KeyboardHeight"

    /**
     * 打卡软键盘
     *
     * @param context  上下文
     * @param editText 输入框
     */
    fun showKeybord(context: Context, editText: EditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    /**
     * 关闭软键盘
     *
     * @param context  上下文
     * @param editText 输入框
     */
    fun hideKeybord(context: Context, editText: EditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    /**
     * 获取输入框高度
     *
     * @param paramActivity activity
     * */
    fun getKeyboardHeight(paramActivity: Activity): Int {
        val height = ScreenUtil.getScreenHeight(paramActivity) - ScreenUtil.getStatusBarHeight(paramActivity) -
                ScreenUtil.getAppHeight(paramActivity)
        return if (height == 0) {
            //787为默认软键盘高度 基本差不离
            SharedUtil.getInt(KEY_BOARD_HEIGHT, 787)
        } else {
            SharedUtil.getInt(KEY_BOARD_HEIGHT, height)
            return height
        }
    }

    /**
     * 键盘是否在显示
     * */
    fun isKeyBoardShow(paramActivity: Activity): Boolean {
        val height = ScreenUtil.getScreenHeight(paramActivity) - ScreenUtil.getStatusBarHeight(paramActivity) -
                ScreenUtil.getAppHeight(paramActivity)
        return height != 0
    }
}