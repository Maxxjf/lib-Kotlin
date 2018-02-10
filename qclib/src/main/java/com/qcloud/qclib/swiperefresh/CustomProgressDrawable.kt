package com.qcloud.qclib.swiperefresh

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation

/**
 * 类说明：下拉更新旋转动画
 * Author: Kuzan
 * Date: 2018/1/22 10:23.
 */
class CustomProgressDrawable(context: Context, private val mParent: View): MaterialProgressDrawable(context, mParent) {

    /**加载时的动画 */
    private var mAnimation: Animation? = null
    private var mBitmap: Bitmap? = null
    /**旋转角度 */
    private var rotation: Float = 0.toFloat()
    private val paint: Paint = Paint()

    init {
        setupAnimation()
        setBackgroundColor(Color.WHITE)
    }

    /**
     * 初始化旋转动画
     * */
    private fun setupAnimation() {
        mAnimation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setProgressRotation(-interpolatedTime)
            }
        }
        mAnimation!!.duration = 5000
        // 无限重复
        mAnimation!!.repeatCount = Animation.INFINITE
        mAnimation!!.repeatMode = Animation.RESTART
        // 均匀转速
        mAnimation!!.interpolator = LinearInterpolator()
    }

    override fun start() {
        mParent.startAnimation(mAnimation)
    }

    fun setBitmap(bitmap: Bitmap) {
        this.mBitmap = bitmap
    }

    override fun setProgressRotation(rotation: Float) {
        //取负号是为了和微信保持一致，下拉时逆时针转加载时顺时针转，旋转因子是为了调整转的速度。
        this.rotation = -rotation * ROTATION_FACTOR
        invalidateSelf()
    }

    override fun draw(c: Canvas) {
        val bound = bounds
        c.rotate(rotation, bound.exactCenterX(), bound.exactCenterY())
        val src = Rect(0, 0, mBitmap!!.width, mBitmap!!.height)
        c.drawBitmap(mBitmap!!, src, bound, paint)
    }

    companion object {
        /**旋转因子，调整旋转速度 */
        private val ROTATION_FACTOR = 5 * 360
    }
}