package com.qcloud.qclib.base

import android.support.annotation.IdRes

/**
 * 类说明：页面按钮点击
 * Author: Kuzan
 * Date: 2018/1/16 14:39.
 */
interface BtnClickPresenter {
    fun onBtnClick(@IdRes viewId: Int)
}