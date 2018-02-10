package com.qcloud.qclib.widget.customview

import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import com.qcloud.qclib.R
import com.qcloud.qclib.utils.ScreenUtil

/**
 * 类说明：一个能保持比例的 ImageView
 *      TODO 暂时只支持维持宽度适应高度
 *      需在application节点添加
 *      ========================================
 *      <meta-data
 *           android:name="targer_screen_width"
 *           android:value="720"/>
 *      ========================================
 * Author: Kuzan
 * Date: 2018/1/20 11:19.
 */
class RatioImageView: AppCompatImageView {

    private var mRatio: Float = 0f
    var ratio: Float
        get() = mRatio
        set(ratio) {
            mRatio = ratio
            invalidate()
        }
    private var mWidth: Int = 0

    private val mContext: Context = context

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        getAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        getAttrs(context, attrs)
    }

    private fun getAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView)
            mRatio = a.getFloat(R.styleable.RatioImageView_ratio, 0f)
            val width = a.getInteger(R.styleable.RatioImageView_width, 0)
            calculateWidth(width)
            a.recycle()
        }
    }

    private fun calculateWidth(width: Int) {
        var targetScreenWidth = 0
        try {
            val info = mContext.packageManager.getApplicationInfo(mContext.packageName,
                    PackageManager.GET_META_DATA)
            targetScreenWidth = info.metaData.getInt(RatioImageView.TARGET_SCREEN_WIDTH)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (width > 0) {
            mWidth = if (targetScreenWidth > 0) {
                width * ScreenUtil.getScreenWidth(mContext) / targetScreenWidth
            } else {
                width
            }
        }
    }

    fun setWidth(width: Int) {
        calculateWidth(width)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mRatio > 0 || mWidth > 0) {
            var width = MeasureSpec.getSize(widthMeasureSpec)
            var height = MeasureSpec.getSize(heightMeasureSpec)

            if (mWidth > 0) {
                width = mWidth
            }
            if (width > 0 && mRatio > 0) {
                height = (width * mRatio).toInt()
            }
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    companion object {
        private val TARGET_SCREEN_WIDTH = "targer_screen_width"
    }
}