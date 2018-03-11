package com.qcloud.qclib.pullrefresh

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
import com.qcloud.qclib.pullrefresh.listener.OnHeaderStateListener
import timber.log.Timber

/**
 * 类说明：下拉刷新头部View
 * Author: Kuzan
 * Date: 2018/1/12 15:47.
 */
class HeadView(@NonNull context: Context): LinearLayout(context), OnHeaderStateListener {

    private var mIvHeaderDownArrow: ImageView? = null
    private var mIvHeaderLoading: ImageView? = null
    private var mTvState: TextView? = null

    private val animationDrawable: AnimationDrawable = ContextCompat.getDrawable(context, R.drawable.progress_round) as AnimationDrawable

    private var isReach: Boolean = false    // 是否达到指定高度

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
        this.setPadding(0, 30, 0, 20)
    }

    private fun findViewById(@NonNull view: View) {
        mIvHeaderDownArrow = view.findViewById(R.id.iv_header_down_arrow)
        mIvHeaderLoading = view.findViewById(R.id.iv_header_loading)
        mTvState = view.findViewById(R.id.tv_header_state)
        restore()
    }

    override fun onScrollChange(head: View?, scrollOffset: Int, scrollRatio: Int) {
        if (scrollRatio == 100 && !isReach) {
            mTvState?.setText(R.string.refresh_by_loosen)
            mIvHeaderDownArrow?.rotation = 180f
            isReach = true
        } else if (scrollRatio != 100 && isReach) {
            mTvState?.setText(R.string.refresh_by_drop_down)
            mIvHeaderDownArrow?.rotation = 0f
            isReach = false
        }
    }

    override fun onRefreshHead(head: View?) {
        Timber.e("=======>>>onRefreshHead")
        mIvHeaderDownArrow?.visibility = View.GONE
        mIvHeaderLoading?.visibility = View.VISIBLE
        mIvHeaderLoading?.setImageDrawable(animationDrawable)
        animationDrawable.start()
        mTvState?.setText(R.string.refreshing)
    }

    override fun onRetractHead(head: View?) {
        restore()
        animationDrawable.stop()
        isReach = false
    }

    /**
     * 重置状态
     * */
    private fun restore() {
        mIvHeaderDownArrow?.visibility = View.VISIBLE
        mIvHeaderDownArrow?.setImageResource(R.drawable.icon_down_arrow)
        mIvHeaderDownArrow?.rotation = 0f
        mIvHeaderLoading?.visibility = View.GONE
        mIvHeaderLoading?.setImageResource(R.drawable.loading1)
        mTvState?.setText(R.string.refresh_by_drop_down)
    }
}