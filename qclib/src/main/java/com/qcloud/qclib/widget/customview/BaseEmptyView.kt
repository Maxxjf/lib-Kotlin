package com.qcloud.qclib.widget.customview

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.qcloud.qclib.R
import com.qcloud.qclib.base.BaseLinearLayout
import com.qcloud.qclib.utils.StringUtil

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/1/20 10:26.
 */
abstract class BaseEmptyView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : BaseLinearLayout(context, attrs, defStyleAttr) {

    private var mImageIcon: ImageView? = null
    private var mTvTip: TextView? = null
    private var mBtnRefresh: TextView? = null
    private var mLayoutEmpty: LinearLayout? = null

    var onRefreshListener: OnRefreshListener? = null

    override val viewId: Int
        get() = R.layout.layout_base_empty

    override fun initViewAndData() {
        mImageIcon = mView?.findViewById(R.id.image_icon)
        mTvTip = mView?.findViewById(R.id.tv_tip)
        mBtnRefresh = mView?.findViewById(R.id.btn_refresh)
        mLayoutEmpty = mView?.findViewById(R.id.layout_empty)

        mImageIcon?.setImageResource(imageIcon)

        mBtnRefresh?.setBackgroundResource(btnBg)
        mBtnRefresh?.setTextColor(btnColor)
        mBtnRefresh?.visibility = if (isShowBtn) View.VISIBLE else View.GONE
        mBtnRefresh?.text = btnName

        mTvTip?.text = defaultTip
        mTvTip?.setTextColor(tipColor)
        mTvTip?.visibility = if (isShowTip) View.VISIBLE else View.GONE

        mLayoutEmpty?.setBackgroundColor(bgColor)
        mLayoutEmpty?.setPadding(padLeft, padTop, padRight, padBottom)

        mImageIcon?.setOnClickListener {
            onRefreshListener?.onRefresh()
        }
        mBtnRefresh!!.setOnClickListener {
            onRefreshListener?.onRefresh()
        }
    }

    /** 没有数据 */
    fun noData(tip: String) {
        if (StringUtil.isNotBlank(tip)) {
            mTvTip?.text = tip
        } else {
            mTvTip?.setText(R.string.tip_no_data)
        }
    }

    /** 没有数据 */
    fun noData(res: Int) {
        if (res > 0) {
            mTvTip?.setText(res)
        } else {
            mTvTip?.setText(R.string.tip_no_data)
        }
    }

    /** 没有网络了 */
    fun noNetWork() {
        mTvTip?.setText(R.string.tip_no_net)
    }

    /** 图标 */
    protected open val imageIcon: Int
        get() = R.drawable.icon_no_data

    /** 按钮背景 */
    protected open val btnBg: Int
        get() = R.drawable.btn_green_radius_selector

    /** 是否显示按钮 */
    protected open val isShowBtn: Boolean
        get() = false

    /** 设置按钮名称 */
    protected open val btnName: String
        get() = mContext.resources.getString(R.string.btn_refresh)

    /** 按钮字体颜色 */
    protected open val btnColor: Int
        get() = ContextCompat.getColor(mContext, R.color.white)

    /** 提示文字 */
    protected open val defaultTip: String
        get() = mContext.resources.getString(R.string.tip_no_data)

    /** 提示字体颜色 */
    protected open val tipColor: Int
        get() = ContextCompat.getColor(mContext, R.color.colorDark)

    /** 设置是否显示提示文字 */
    protected open val isShowTip: Boolean
        get() = true

    /** 设置背景颜色 */
    protected open val bgColor: Int
        get() = ContextCompat.getColor(mContext, R.color.transparent)

    /** 设置左偏移 */
    protected open val padLeft: Int
        get() = 0

    /** 设置顶偏移 */
    protected open val padTop: Int
        get() = 0

    /** 设置右偏移 */
    protected open val padRight: Int
        get() = 0

    /** 设置底偏移 */
    protected open val padBottom: Int
        get() = 0

    interface OnRefreshListener {
        fun onRefresh()
    }
}