package com.qcloud.qclib.widget.customview.clearscreen

import android.view.View

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2017/8/23 10:51.
 */
interface IClearRootView {
    /**设置竖屏或横屏 */
    fun setClearSide(orientation: ClearConstants.Orientation)

    /**滑动回调 */
    fun setIPositionCallBack(callBack: IPositionCallBack)

    /**滑动事件 */
    fun setIClearEvent(event: IClearEvent)

    /**添加布局 */
    fun addView(child: View, index: Int)
}
