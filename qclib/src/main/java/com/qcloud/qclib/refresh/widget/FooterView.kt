package com.qcloud.qclib.refresh.widget

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.qcloud.qclib.R
import com.qcloud.qclib.refresh.listener.OnFooterStateListener

/**
 * 类说明：上接加载更多底部View
 * Author: Kuzan
 * Date: 2018/1/12 16:46.
 */
class FooterView(@NonNull context: Context): LinearLayout(context), OnFooterStateListener {

    private var mIvFooterDownArrow: ImageView? = null
    private var mIvFooterLoading: ImageView? = null
    private var mTvState: TextView? = null

    private val animationDrawable: AnimationDrawable = ContextCompat.getDrawable(context, R.drawable.progress_round) as AnimationDrawable

    private var isReach: Boolean = false    // 是否达到指定高度
    private var isMore: Boolean = true      // 是否还有更多

    init {
        initView(context)
    }

    /**
     * 初始化头部
     * */
    private fun initView(context: Context) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.layout_refresh_view, this, false)
        this.addView(layout, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        findViewById(layout)
        this.setPadding(0, 20, 0, 30)
    }

    private fun findViewById(@NonNull view: View) {
        mIvFooterDownArrow = view.findViewById(R.id.iv_header_down_arrow)
        mIvFooterLoading = view.findViewById(R.id.iv_header_loading)
        mTvState = view.findViewById(R.id.tv_header_state)
        restore()
    }

    override fun onScrollChange(foot: View?, scrollOffset: Int, scrollRatio: Int) {
        if (isMore) {
            if (scrollRatio == 100 && !isReach) {
                mTvState?.setText(R.string.load_by_loosen)
                mIvFooterDownArrow?.rotation = 0f
                isReach = true
            } else if (scrollRatio != 100 && isReach) {
                mTvState?.setText(R.string.load_by_pull_up)
                mIvFooterDownArrow?.rotation = 180f
                isReach = false
            }
        }
    }

    override fun onRefreshFoot(foot: View?) {
        if (isMore) {
            mIvFooterDownArrow?.visibility = View.GONE
            mIvFooterLoading?.visibility = View.VISIBLE
            mIvFooterLoading?.setImageDrawable(animationDrawable)
            animationDrawable.start()
            mTvState?.setText(R.string.loading)
        }
    }

    override fun onRetractFoot(foot: View?) {
        if (isMore) {
            restore()
            animationDrawable.stop()
            isReach = false
        }
    }

    override fun onNotMore(foot: View?) {
        mIvFooterLoading?.visibility = View.GONE
        mIvFooterDownArrow?.visibility = View.GONE
        mTvState?.setText(R.string.has_loaded_finish)
        isMore = false
    }

    override fun onHasMore(foot: View?) {
        mIvFooterLoading?.visibility = View.GONE
        mIvFooterDownArrow?.visibility = View.VISIBLE
        mTvState?.setText(R.string.load_by_pull_up)
        isMore = true
    }

    /**
     * 重置状态
     * */
    private fun restore() {
        mIvFooterLoading?.visibility = View.GONE
        mIvFooterLoading?.setImageResource(R.drawable.loading1)
        mIvFooterDownArrow?.visibility = View.VISIBLE
        mIvFooterDownArrow?.setImageResource(R.drawable.icon_down_arrow)
        mIvFooterDownArrow?.rotation = 180f
        mTvState?.setText(R.string.load_by_pull_up)
    }
}