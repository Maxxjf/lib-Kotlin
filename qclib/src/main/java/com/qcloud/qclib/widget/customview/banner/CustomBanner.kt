package com.qcloud.qclib.widget.customview.banner

import android.content.Context
import android.os.Handler
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.qcloud.qclib.R
import com.qcloud.qclib.utils.DensityUtil

/**
 * 类说明：自定义轮播图
 * Author: Kuzan
 * Date: 2018/1/19 11:20.
 */
class CustomBanner<T>: FrameLayout {

    private val mContext: Context = context

    private var mBannerViewPager: ViewPager? = null
    private var mIndicatorLayout: LinearLayout? = null
    private var mAdapter: BannerPagerAdapter<T>? = null
    private var mScroller: ViewPagerScroller? = null

    private var mIndicatorSelectRes: Int = 0
    private var mIndicatorUnSelectRes: Int = 0

    var autoTurningTime: Long = 0L
    var isTurning: Boolean = false

    var onPageClickListener: OnPageClickListener<T>? = null
    var onPageChangeListener: ViewPager.OnPageChangeListener? = null

    private val mTimeHandler: Handler = Handler()
    private val mTurningTask: Runnable = Runnable {
        if (isTurning && mBannerViewPager != null) {
            val page = mBannerViewPager!!.currentItem + 1
            mBannerViewPager!!.currentItem = page
        }
    }

    /** 指示器的位置  */
    enum class IndicatorGravity {
        LEFT, RIGHT, CENTER_HORIZONTAL
    }

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        getAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        getAttrs(context, attrs)
    }

    init {
        addBannerViewPager(context)
        addIndicatorLayout(context)
    }

    /**
     * 获取布局属性
     * */
    private fun getAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.custom_banner)
            val gravity = mTypedArray.getInt(R.styleable.custom_banner_indicatorGravity, 0)

            when (gravity) {
                1 -> setIndicatorGravity(IndicatorGravity.LEFT)
                2 -> setIndicatorGravity(IndicatorGravity.RIGHT)
                3 -> setIndicatorGravity(IndicatorGravity.CENTER_HORIZONTAL)
            }
            mIndicatorSelectRes = mTypedArray.getResourceId(R.styleable.custom_banner_indicatorSelectRes, 0)
            mIndicatorUnSelectRes = mTypedArray.getResourceId(R.styleable.custom_banner_indicatorUnSelectRes, 0)
            mTypedArray.recycle()
        }
    }

    private fun addBannerViewPager(context: Context) {
        mBannerViewPager = ViewPager(context)
        mBannerViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetP: Int) {
                if (!isMarginal(position) && onPageChangeListener != null) {
                    onPageChangeListener!!.onPageScrolled(getActualPosition(position), positionOffset, positionOffsetP)
                }
            }

            override fun onPageSelected(position: Int) {
                if (!isMarginal(position) && onPageChangeListener != null) {
                    onPageChangeListener!!.onPageSelected(getActualPosition(position))
                }

                if (isTurning && !isMarginal(position)) {
                    mTimeHandler.removeCallbacksAndMessages(null)
                    mTimeHandler.postDelayed(mTurningTask, autoTurningTime)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                val position = mBannerViewPager!!.currentItem
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    when (position) {
                        0 -> {
                            mScroller!!.isZero = true
                            mBannerViewPager!!.setCurrentItem(mAdapter!!.count - 2, true)//如果为false，就不会刷新视图，也就出现第一次加载的时候往前滚，会有空白View。
                            mScroller!!.isZero = false
                        }
                        mAdapter!!.count - 1 -> {
                            mScroller!!.isZero = true
                            mBannerViewPager!!.setCurrentItem(1, true)//如果为false，就不会刷新视图，也就出现第一次加载的时候往前滚，会有空白View。
                            mScroller!!.isZero = false
                        }
                        else -> updateIndicator()
                    }
                }

                if (!isMarginal(position) && onPageChangeListener != null) {
                    onPageChangeListener!!.onPageScrollStateChanged(state)
                }
            }
        })

        initViewPagerScroll()
        this.addView(mBannerViewPager)
    }

    /**
     * 添加指示器
     * */
    private fun addIndicatorLayout(context: Context) {
        mIndicatorLayout = LinearLayout(context)
        val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        lp.setMargins(0, 0, 0, DensityUtil.dp2px(context, 8f))
        mIndicatorLayout!!.gravity = Gravity.CENTER
        this.addView(mIndicatorLayout, lp)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (isTurning) {
            if (hasWindowFocus) {
                startTurning(autoTurningTime)
            } else {
                stopTurning()
                isTurning = true
            }
        }
    }

    private fun isMarginal(position: Int): Boolean {
        return position == 0 || position == getCount() + 1
    }

    /**
     * 设置轮播图数据
     *
     * @param creator 创建和更新轮播图View的接口
     * @return
     */
    fun setPages(creator: ViewCreator<T>): CustomBanner<*> {
        mAdapter = BannerPagerAdapter(mContext, creator)
        if (onPageClickListener != null) {
            mAdapter!!.mOnPageClickListener = onPageClickListener
        }
        mBannerViewPager!!.adapter = mAdapter

        return this
    }

    /**
     * 设置指示器资源
     *
     * @param selectRes   选中的效果资源
     * @param unSelectRes 未选中的效果资源
     * @return
     */
    fun setIndicatorRes(selectRes: Int, unSelectRes: Int): CustomBanner<*> {
        mIndicatorSelectRes = selectRes
        mIndicatorUnSelectRes = unSelectRes
        updateIndicator()
        return this
    }

    /**
     * 设置指示器方向
     *
     * @param gravity 指示器方向 左、中、右三种
     * @return
     */
    fun setIndicatorGravity(gravity: IndicatorGravity): CustomBanner<*> {
        val lp = mIndicatorLayout!!.layoutParams as FrameLayout.LayoutParams
        when (gravity) {
            CustomBanner.IndicatorGravity.LEFT -> lp.gravity = Gravity.BOTTOM or Gravity.LEFT
            CustomBanner.IndicatorGravity.RIGHT -> lp.gravity = Gravity.BOTTOM or Gravity.RIGHT
            CustomBanner.IndicatorGravity.CENTER_HORIZONTAL -> lp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        }
        mIndicatorLayout!!.layoutParams = lp
        return this
    }

    /**
     * 启动轮播
     *
     * @param autoTurningTime 轮播间隔时间
     * @return
     */
    fun startTurning(autoTurningTime: Long): CustomBanner<*> {
        if (isTurning) {
            stopTurning()
        }
        isTurning = true
        this.autoTurningTime = autoTurningTime
        mTimeHandler.postDelayed(mTurningTask, this.autoTurningTime)
        return this
    }

    /**
     * 停止轮播
     *
     * @return
     */
    fun stopTurning(): CustomBanner<*> {
        isTurning = false
        mTimeHandler.removeCallbacksAndMessages(null)
        return this
    }

    /**
     * 替换数据
     * */
    fun replaceData(list: List<T>?) {
        if (list == null || list.isEmpty()) {
            mIndicatorLayout!!.removeAllViews()
        } else {
            mAdapter!!.replaceList(list)
            initIndicator(list.size)

            setCurrentItem(0)
            updateIndicator()
        }
    }

    /** 获取总轮播条数 */
    fun getCount(): Int {
        if (mAdapter == null || mAdapter!!.count == 0) {
            return 0
        }
        return mAdapter!!.count - 2
    }

    /** 获取当前轮播图 */
    fun setCurrentItem(position: Int): CustomBanner<*> {
        if (position >= 0 && position < mAdapter!!.count) {
            mBannerViewPager!!.currentItem = position + 1
        }
        return this
    }

    fun getCurrentItem(): Int {
        return if (mBannerViewPager == null) 0 else getActualPosition(mBannerViewPager!!.currentItem)
    }

    private fun getActualPosition(position: Int): Int {
        if (mAdapter == null || mAdapter!!.count == 0) {
            return -1
        }

        return when (position) {
            0 -> getCount() - 1
            getCount() + 1 -> 0
            else -> position - 1
        }
    }

    private fun initIndicator(count: Int) {
        mIndicatorLayout!!.removeAllViews()
        if (count > 0) {
            for (i in 0 until count) {
                val imageView = ImageView(mContext)
                val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.setMargins(DensityUtil.dp2px(mContext, 2f), 0, DensityUtil.dp2px(mContext, 2f), 0)
                mIndicatorLayout!!.addView(imageView, lp)
            }
        }
    }

    /** 更新指示器 */
    private fun updateIndicator() {
        val count = mIndicatorLayout!!.childCount
        val currentPage = getCurrentItem()
        if (count > 0) {
            for (i in 0 until count) {
                val view = mIndicatorLayout!!.getChildAt(i) as ImageView
                if (i == currentPage) {
                    if (mIndicatorSelectRes != 0) {
                        view.setImageResource(mIndicatorSelectRes)
                    } else {
                        view.setImageBitmap(null)
                    }
                } else {
                    if (mIndicatorUnSelectRes != 0) {
                        view.setImageResource(mIndicatorUnSelectRes)
                    } else {
                        view.setImageBitmap(null)
                    }
                }
            }
        }
    }

    /** 设置ViewPager的滑动速度 */
    private fun initViewPagerScroll() {
        try {
            val field = ViewPager::class.java.getDeclaredField("mScroller")
            field.isAccessible = true
            mScroller = ViewPagerScroller(mContext, AccelerateInterpolator())
            field.set(mBannerViewPager, mScroller)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** 设置ViewPager的滚动速度 */
    fun setScrollDuration(scrollDuration: Int): CustomBanner<*> {
        mScroller!!.mScrollDuration = scrollDuration
        return this
    }

    fun getScrollDuration(): Int {
        return mScroller!!.mScrollDuration
    }

    fun setOnPageChangeListener(listener: ViewPager.OnPageChangeListener): CustomBanner<*> {
        onPageChangeListener = listener
        return this
    }

    fun setOnPageClickListener(listener: OnPageClickListener<T>): CustomBanner<*> {
        mAdapter?.mOnPageClickListener = listener
        onPageClickListener = listener
        return this
    }
}