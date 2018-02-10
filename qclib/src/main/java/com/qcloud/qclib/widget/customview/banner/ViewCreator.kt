package com.qcloud.qclib.widget.customview.banner

import android.content.Context
import android.view.View

/**
 * 类说明：创建和更新轮播图View的接口
 * Author: Kuzan
 * Date: 2018/1/19 11:25.
 */
interface ViewCreator<in T> {

    fun createView(context: Context, position: Int): View

    fun updateUi(context: Context, view: View, position: Int, data: T)
}