package com.qcloud.qclib.swiperefresh

import android.content.Context
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * 类说明：底部加载更多布局
 * Author: Kuzan
 * Date: 2017/8/4 9:26.
 */
class FooterLayout @JvmOverloads constructor(
        private val mContext: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : LinearLayout(mContext, attrs, defStyleAttr) {
    private var tvLoad: TextView? = null

    init {
        setupViews()
    }

    private fun setupViews() {
        tvLoad = TextView(mContext)
        addView(tvLoad)
    }

    fun setFooterView(view: View?) {
        if (view != null) {
            removeAllViews()
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            addView(view, lp)
        }
    }

    fun setFooterView(@LayoutRes layoutResID: Int) {
        val inflater = LayoutInflater.from(this.context)
        val view = inflater.inflate(layoutResID, null)
        if (view != null) {
            removeAllViews()
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            addView(view, lp)
        }
    }

    fun setText(loadText: String) {
        if (tvLoad != null) {
            tvLoad!!.text = loadText
        }
    }

    fun setTextColor(color: Int) {
        if (tvLoad != null) {
            tvLoad!!.setTextColor(color)
        }
    }
}
