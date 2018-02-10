package com.qcloud.qclib.widget.customview.clearscreen

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.qcloud.qclib.widget.customview.clearscreen.view.ScreenSideView
import java.util.*

/**
 * 类说明：清除屏幕功能
 * Author: Kuzan
 * Date: 2018/1/19 13:47.
 */
class ClearScreenHelper(context: Context, rootView: IClearRootView? = null) {

    private var mScreenSideView: IClearRootView? = null
    private var mIClearEvent: IClearEvent? = null
    private val mClearList: LinkedList<View> = LinkedList()

    init {
        initView(context, rootView)
        setOrientation(ClearConstants.Orientation.RIGHT)
        initCallback()
    }

    private fun initView(context: Context, rootView: IClearRootView?) {
        if (rootView == null) {
            val decorView = (context as Activity).window.decorView as ViewGroup
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            mScreenSideView = ScreenSideView(context)
            decorView.addView(mScreenSideView as View?, params)
        } else {
            mScreenSideView = rootView
            val imgV = View(context)
            imgV.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imgV.isClickable = true
            rootView.addView(imgV, 0)
        }
    }

    private fun initCallback() {
        mScreenSideView!!.setIPositionCallBack(object : IPositionCallBack {
            override fun onPositionChange(offsetX: Int, offsetY: Int) {
                for (i in mClearList.indices) {
                    mClearList[i].translationX = offsetX.toFloat()
                    mClearList[i].translationY = offsetY.toFloat()
                }
            }
        })

        mScreenSideView?.setIClearEvent(object : IClearEvent {
            override fun onClearEnd() {
                if (mIClearEvent != null) {
                    mIClearEvent!!.onClearEnd()
                }
            }

            override fun onRecovery() {
                if (mIClearEvent != null) {
                    mIClearEvent!!.onRecovery()
                }
            }
        })
    }

    fun setIClearEvent(l: IClearEvent) {
        mIClearEvent = l
    }

    fun setOrientation(orientation: ClearConstants.Orientation) {
        mScreenSideView?.setClearSide(orientation)
    }

    /**绑定随滑动布局而隐藏的控件 */
    fun bind(vararg cellList: View) {
        cellList
                .filterNot { mClearList.contains(it) }
                .forEach { mClearList.add(it) }
    }

    fun unbind(vararg cellList: View) {
        cellList
                .filter { mClearList.contains(it) }
                .forEach { mClearList.remove(it) }
    }

    fun unbindAllCell() {
        mClearList.clear()
    }
}