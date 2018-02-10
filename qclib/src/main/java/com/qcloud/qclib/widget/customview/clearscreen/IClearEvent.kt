package com.qcloud.qclib.widget.customview.clearscreen

/**
 * 类说明：清除事件
 * Author: Kuzan
 * Date: 2017/8/23 10:56.
 */
interface IClearEvent {
    /**滑动结束 */
    fun onClearEnd()

    /**恢复 */
    fun onRecovery()
}
