package com.qcloud.qclib.swiperefresh

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.annotation.DrawableRes
import android.view.View

import com.qcloud.qclib.R

/**
 * 类说明：上拉加载更多工具类，下拉默认开启，若想不开启，使用PullRefreshView
 * Author: Kuzan
 * Date: 2017/8/03 16:42.
 */
object SwipeRefreshUtil {

    /**
     * 刷新控件的基本配件 (底部用默认的)
     *
     * @param view          刷新控件
     * @param isLoadMore   是否开启上拉加载
     */
    fun setLoadMore(view: CustomSwipeLayout, isLoadMore: Boolean) {

        var footerView: FooterView? = null

        if (isLoadMore) {
            footerView = FooterView(view.context)
        }

        setLoadMore(view, isLoadMore, footerView, footerView)
    }

    /**
     * 刷新控件的基本配件 (自定义底部 )
     *
     * @param view              刷新控件
     * @param isLoadMore        是否开启上拉加载
     * @param footerView        尾部View
     * @param listener          尾部监听器
     */
    fun setLoadMore(view: CustomSwipeLayout, isLoadMore: Boolean, footerView: View?, listener: OnFooterStateListener?) {
        view.setLoadMore(isLoadMore)
        if (isLoadMore) {
            view.setFooter(footerView!!)
            view.onFooterStateListener = listener
        }
    }

    /**
     * 设置下拉刷新加载动画
     *
     * @param view          刷新控件
     * @param res           刷新动画图片资源
     * @param animPosition  刷新动画的位置 1左边 2中间
     */
    fun setRefreshImage(context: Context, view: CustomSwipeLayout, @DrawableRes res: Int, animPosition: Int) {
        val drawable = CustomProgressDrawable(context, view)
        val bitmap: Bitmap = if (res > 0) {
            BitmapFactory.decodeResource(context.resources, res)
        } else {
            BitmapFactory.decodeResource(context.resources, R.drawable.default_refresh_icon)
        }
        drawable.setBitmap(bitmap)
        view.setProgressView(drawable)
        view.animPosition = animPosition
    }

    /**
     * 设置下拉刷新动画的位置
     *
     * @param view          刷新控件
     * @param animPosition  刷新动画的位置 1左边 2中间
     */
    fun setAnimPosition(view: CustomSwipeLayout, animPosition: Int) {
        view.animPosition = animPosition
    }
}
