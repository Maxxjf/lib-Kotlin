package com.qcloud.qclib.swiperefresh

import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/1/22 10:43.
 */
class Ring(private val mCallback: Drawable.Callback) {

    private val mTempBounds = RectF()
    private val mPaint = Paint()
    private val mArrowPaint = Paint()

    var startTrim = 0.0f
        set(startTrim) {
            field = startTrim
            invalidateSelf()
        }
    var endTrim = 0.0f
        set(endTrim) {
            field = endTrim
            invalidateSelf()
        }
    var rotation = 0.0f
        set(rotation) {
            field = rotation
            invalidateSelf()
        }
    /**
     * @param strokeWidth Set the stroke width of the progress spinner in pixels.
     */
    var strokeWidth = 5.0f
        set(strokeWidth) {
            field = strokeWidth
            mPaint.strokeWidth = strokeWidth
            invalidateSelf()
        }
    var insets = 2.5f
        private set

    private var mColors: IntArray = intArrayOf(Color.BLACK)
    private var mColorIndex: Int = 0
    var startingStartTrim: Float = 0.toFloat()
        private set
    var startingEndTrim: Float = 0.toFloat()
        private set
    var startingRotation: Float = 0.toFloat()
        private set

    private var mShowArrow: Boolean = false
    private var mArrow: Path? = null
    private var mArrowScale: Float = 0.toFloat()

    var centerRadius: Double = 0.toDouble()
    private var mArrowWidth: Int = 0
    private var mArrowHeight: Int = 0
    var alpha: Int = 0
    private val mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mBackgroundColor: Int = 0
    private var mCurrentColor: Int = 0

    val nextColor: Int
        get() = mColors[nextColorIndex]

    private val nextColorIndex: Int
        get() = (mColorIndex + 1) % mColors.size

    val startingColor: Int
        get() = mColors[mColorIndex]

    init {
        mPaint.strokeCap = Paint.Cap.SQUARE
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE

        mArrowPaint.style = Paint.Style.FILL
        mArrowPaint.isAntiAlias = true
    }

    fun setBackgroundColor(color: Int) {
        mBackgroundColor = color
    }

    /**
     * Set the dimensions of the arrowhead.
     *
     * @param width Width of the hypotenuse of the arrow head
     * @param height Height of the arrow point
     */
    fun setArrowDimensions(width: Float, height: Float) {
        mArrowWidth = width.toInt()
        mArrowHeight = height.toInt()
    }

    /**
     * Draw the progress spinner
     */
    fun draw(c: Canvas, bounds: Rect) {
        val arcBounds = mTempBounds
        arcBounds.set(bounds)
        arcBounds.inset(insets, insets)

        val startAngle = (startTrim + rotation) * 360
        val endAngle = (endTrim + rotation) * 360
        val sweepAngle = endAngle - startAngle

        mPaint.color = mCurrentColor
        c.drawArc(arcBounds, startAngle, sweepAngle, false, mPaint)

        drawTriangle(c, startAngle, sweepAngle, bounds)

        if (alpha < 255) {
            mCirclePaint.color = mBackgroundColor
            mCirclePaint.alpha = 255 - alpha
            c.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), (bounds.width() / 2).toFloat(),
                    mCirclePaint)
        }
    }

    private fun drawTriangle(c: Canvas, startAngle: Float, sweepAngle: Float, bounds: Rect) {
        if (mShowArrow) {
            if (mArrow == null) {
                mArrow = Path()
                mArrow!!.fillType = Path.FillType.EVEN_ODD
            } else {
                mArrow!!.reset()
            }

            // Adjust the position of the triangle so that it is inset as
            // much as the arc, but also centered on the arc.
            val inset = insets.toInt() / 2 * mArrowScale
            val x = (centerRadius * Math.cos(0.0) + bounds.exactCenterX()).toFloat()
            val y = (centerRadius * Math.sin(0.0) + bounds.exactCenterY()).toFloat()

            // Update the path each time. This works around an issue in SKIA
            // where concatenating a rotation matrix to a scale matrix
            // ignored a starting negative rotation. This appears to have
            // been fixed as of API 21.
            mArrow!!.moveTo(0f, 0f)
            mArrow!!.lineTo(mArrowWidth * mArrowScale, 0f)
            mArrow!!.lineTo(mArrowWidth * mArrowScale / 2, mArrowHeight * mArrowScale)
            mArrow!!.offset(x - inset, y)
            mArrow!!.close()
            // draw a triangle
            mArrowPaint.color = mCurrentColor
            c.rotate(startAngle + sweepAngle - MaterialProgressDrawable.ARROW_OFFSET_ANGLE, bounds.exactCenterX(),
                    bounds.exactCenterY())
            c.drawPath(mArrow!!, mArrowPaint)
        }
    }

    /**
     * Set the colors the progress spinner alternates between.
     *
     * @param colors Array of integers describing the colors. Must be non-`null`.
     */
    fun setColors(colors: IntArray) {
        mColors = colors
        // if colors are reset, make sure to reset the color index as well
        setColorIndex(0)
    }

    /**
     * Set the absolute color of the progress spinner. This is should only
     * be used when animating between current and next color when the
     * spinner is rotating.
     *
     * @param color int describing the color.
     */
    fun setColor(color: Int) {
        mCurrentColor = color
    }

    /**
     * @param index Index into the color array of the color to display in
     * the progress spinner.
     */
    fun setColorIndex(index: Int) {
        mColorIndex = index
        mCurrentColor = mColors[mColorIndex]
    }

    /**
     * Proceed to the next available ring color. This will automatically
     * wrap back to the beginning of colors.
     */
    fun goToNextColor() {
        setColorIndex(nextColorIndex)
    }

    fun setColorFilter(filter: ColorFilter?) {
        mPaint.colorFilter = filter
        invalidateSelf()
    }

    fun setInsets(width: Int, height: Int) {
        val minEdge = Math.min(width, height).toFloat()
        val insets: Float
        insets = if (centerRadius <= 0 || minEdge < 0) {
            Math.ceil((strokeWidth / 2.0f).toDouble()).toFloat()
        } else {
            (minEdge / 2.0f - centerRadius).toFloat()
        }
        this.insets = insets
    }

    /**
     * @param show Set to true to show the arrow head on the progress spinner.
     */
    fun setShowArrow(show: Boolean) {
        if (mShowArrow != show) {
            mShowArrow = show
            invalidateSelf()
        }
    }

    /**
     * @param scale Set the scale of the arrowhead for the spinner.
     */
    fun setArrowScale(scale: Float) {
        if (scale != mArrowScale) {
            mArrowScale = scale
            invalidateSelf()
        }
    }

    /**
     * If the start / end trim are offset to begin with, store them so that
     * animation starts from that offset.
     */
    fun storeOriginals() {
        startingStartTrim = startTrim
        startingEndTrim = endTrim
        startingRotation = rotation
    }

    /**
     * Reset the progress spinner to default rotation, start and end angles.
     */
    fun resetOriginals() {
        startingStartTrim = 0f
        startingEndTrim = 0f
        startingRotation = 0f
        startTrim = 0f
        endTrim = 0f
        rotation = 0f
    }

    private fun invalidateSelf() {
        mCallback.invalidateDrawable(null)
    }
}