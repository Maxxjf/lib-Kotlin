package com.qcloud.qclib.widget.swipeback

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.qcloud.qclib.R
import com.qcloud.qclib.utils.SwipeBackUtil

/**
 * 类说明：侧滑返回工具
 * Author: Kuzan
 * Date: 2018/1/17 14:06.
 */
class SwipeBackHelper(private val mActivity: Activity) {
    /**
     * 注意在使用的时候加上
     * <activity android:name=".TestActivity"
     *      android:theme="@style/AppSwipeBackTheme"
     *      android:launchMode="singleTask"/>
     *
     * <style name="AppSwipeBackTheme" parent="@style/AppTheme" >
     *      <item name="android:windowBackground">@color/transparent</item>
     *      <item name="android:windowIsTranslucent">true</item>
     * </style>
     *
     * 要不然退出的时候会黑屏
     * */
    var swipeBackLayout: SwipeBackLayout? = null

    fun onActivityCreate() {
        mActivity.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mActivity.window.decorView.setBackgroundDrawable(null)
        swipeBackLayout = LayoutInflater.from(mActivity).inflate(R.layout.layout_swipe_back, null, false) as SwipeBackLayout
        swipeBackLayout?.addSwipeListener(object: SwipeListener {
            override fun onScrollStateChange(state: Int, scrollPercent: Float) {

            }

            override fun onEdgeTouch(edgeFlag: Int) {
                SwipeBackUtil.convertActivityToTranslucent(mActivity)
            }

            override fun onScrollOverThreshold() {
            }
        })
    }

    fun findViewById(viewId: Int): View? {
        return swipeBackLayout?.findViewById(viewId)
    }

    fun onPostCreate() {
        swipeBackLayout?.attachToActivity(mActivity)
    }
}