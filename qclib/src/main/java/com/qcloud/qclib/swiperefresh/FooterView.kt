package com.qcloud.qclib.swiperefresh

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.qcloud.qclib.R

/**
 * 类说明：上拉加载更多底部View
 * Author: Kuzan
 * Date: 2018/1/22 9:23.
 */
class FooterView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), OnFooterStateListener {

    private var ivFooterDownArrow: ImageView? = null
    private var ivFooterLoading: ImageView? = null
    private var tvTip: TextView? = null

    private var isReach: Boolean = false
    private var isMore: Boolean = true

    private var animationDrawable: AnimationDrawable = ContextCompat.getDrawable(context, R.drawable.progress_round) as AnimationDrawable

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        val layout = LayoutInflater.from(context).inflate(R.layout.layout_refresh_view, this, false)
        this.addView(layout, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        findViewById(layout)
        setPadding(0, 20, 0, 30)
    }

    private fun findViewById(@NonNull view: View) {
        ivFooterDownArrow = view.findViewById(R.id.iv_header_down_arrow)
        ivFooterLoading = view.findViewById(R.id.iv_header_loading)
        tvTip = view.findViewById(R.id.tv_header_state)
        restore()
    }

    override fun onScrollChange(footer: View?, scrollOffset: Int, scrollRatio: Int) {
        if (isMore) {
            if (scrollRatio == CustomSwipeLayout.FOOTER_DEFAULT_HEIGHT && !isReach) {
                tvTip?.setText(R.string.load_by_loosen)
                ivFooterDownArrow?.rotation = 0f
                isReach = true
            } else if (scrollRatio != CustomSwipeLayout.FOOTER_DEFAULT_HEIGHT && isReach) {
                tvTip?.setText(R.string.load_by_pull_up)
                ivFooterDownArrow?.rotation = 180f
                isReach = false
            }
        }
    }

    override fun onLoading(footer: View?) {
        if (isMore) {
            ivFooterLoading?.visibility = View.VISIBLE
            ivFooterDownArrow?.visibility = View.GONE
            ivFooterLoading?.setImageDrawable(animationDrawable)
            animationDrawable?.start()
            tvTip?.setText(R.string.loading)
        }
    }

    override fun onRetract(footer: View?) {
        if (isMore) {
            restore()
            animationDrawable?.stop()
            isReach = false
        }
    }

    override fun onNoMore(footer: View?) {
        ivFooterLoading?.visibility = View.GONE
        ivFooterDownArrow?.visibility = View.GONE
        tvTip?.setText(R.string.has_loaded_finish)
        isMore = false
    }

    override fun onHasMore(tail: View?) {
        ivFooterLoading?.visibility = View.GONE
        ivFooterDownArrow?.visibility = View.VISIBLE
        tvTip?.setText(R.string.load_by_pull_up)
        isMore = true
    }

    private fun restore() {
        ivFooterLoading?.visibility = View.GONE
        ivFooterDownArrow?.visibility = View.VISIBLE
        ivFooterLoading?.setImageResource(R.drawable.loading1)
        ivFooterDownArrow?.setImageResource(R.drawable.icon_down_arrow)
        ivFooterDownArrow?.rotation = 180f
        tvTip?.setText(R.string.load_by_pull_up)
    }
}