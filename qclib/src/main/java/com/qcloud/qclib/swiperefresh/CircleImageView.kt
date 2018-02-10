package com.qcloud.qclib.swiperefresh

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.AppCompatImageView
import com.qcloud.qclib.utils.ColorUtil

/**
 * 类说明：下拉加载动画
 * Author: Kuzan
 * Date: 2018/1/22 9:36.
 */
class CircleImageView (context: Context, @ColorInt color: Int, radius: Float) : AppCompatImageView(context) {

    var animationListener: MyAnimationListener? = null
    private var shadowRadius: Int = 0

    init {
        val density: Float = context.resources.displayMetrics.density
        val diameter: Int = (radius * density * 2).toInt()
        val shadowXOffset: Int = (density * X_OFFSET).toInt()
        val shadowYOffset: Int = (density * Y_OFFSET).toInt()

        shadowRadius = (density * SHADOW_RADIUS).toInt()

        val circle: ShapeDrawable
        if (elevationSupported()) {
            circle = ShapeDrawable(OvalShape())
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density)
        } else {
            val oval = OvalShadow(shadowRadius, diameter)
            circle = ShapeDrawable(oval)
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, circle.paint)
            circle.paint.setShadowLayer(shadowRadius.toFloat(), shadowXOffset.toFloat(), shadowYOffset.toFloat(),
                    KEY_SHADOW_COLOR)
            val padding = shadowRadius
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding)
        }
        circle.paint.color = color
        setBackgroundDrawable(circle)
    }

    private fun elevationSupported(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= 21
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!elevationSupported()) {
            setMeasuredDimension(measuredWidth + shadowRadius * 2, measuredHeight + shadowRadius * 2)
        }
    }

    override fun onAnimationStart() {
        super.onAnimationStart()
        animationListener?.onAnimationStart(animation)
    }

    override fun onAnimationEnd() {
        super.onAnimationEnd()
        animationListener?.onAnimationEnd(animation)
    }

    /**
     * Update the background color of the circle image view.
     *
     * @param colorRes Id of a color resource.
     */
    fun setBackgroundColorRes(colorRes: Int) {
        setBackgroundColor(ContextCompat.getColor(context, colorRes))
    }

    override fun setBackgroundColor(color: Int) {
        if (background is ShapeDrawable) {
            (background as ShapeDrawable).paint.color = color
        }
    }

    private inner class OvalShadow(radius: Int, private val mCircleDiameter: Int): OvalShape() {

        private var mRadialGradient: RadialGradient? = null
        private val mShadowPaint: Paint = Paint()

        init {
            shadowRadius = radius
            mRadialGradient = RadialGradient((mCircleDiameter / 2).toFloat(), (mCircleDiameter / 2).toFloat(),
                    radius.toFloat(), intArrayOf(FILL_SHADOW_COLOR, Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP)
            mShadowPaint.shader = mRadialGradient
        }

        override fun draw(canvas: Canvas, paint: Paint?) {
            val viewWidth = this@CircleImageView.width
            val viewHeight = this@CircleImageView.height

            canvas.drawCircle((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), (mCircleDiameter / 2 + shadowRadius).toFloat(),
                    mShadowPaint)
            canvas.drawCircle((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), (mCircleDiameter / 2).toFloat(), paint)
        }
    }

    companion object {
        private val KEY_SHADOW_COLOR: Int = ColorUtil.parseColor("0x1E000000")
        private val FILL_SHADOW_COLOR: Int = ColorUtil.parseColor("0x3D000000")

        private val X_OFFSET: Float = 0f
        private val Y_OFFSET: Float = 1.75f
        private val SHADOW_RADIUS: Float = 3.5f
        private val SHADOW_ELEVATION: Int = 4
    }
}