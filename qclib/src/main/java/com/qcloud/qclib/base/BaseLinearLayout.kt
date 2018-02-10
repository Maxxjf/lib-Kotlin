package com.qcloud.qclib.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

/**
 * 类说明：LinearLayout基类
 * Author: Kuzan
 * Date: 2017/12/11 14:26.
 */
abstract class BaseLinearLayout @JvmOverloads constructor (
        protected var mContext: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0): LinearLayout(mContext, attrs, defStyleAttr) {

    var mView: View? = null

    var onViewClickListener: OnViewClickListener? = null

    init {
        initLayout()
    }

    private fun initLayout() {
        mView = LayoutInflater.from(mContext).inflate(viewId, null, false)
        addView(mView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        initViewAndData()
    }

    /** 获取布局id */
    abstract val viewId: Int

    /** 初始化界面和数据 */
    abstract fun initViewAndData()

    interface OnViewClickListener {
        fun onViewClick(view: View)
    }
}