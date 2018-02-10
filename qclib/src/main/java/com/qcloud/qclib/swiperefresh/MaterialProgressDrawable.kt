package com.qcloud.qclib.swiperefresh

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.support.annotation.IntDef
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 类说明：加载动画实现
 * Author: Kuzan
 * Date: 2018/1/22 10:23.
 */
open class MaterialProgressDrawable(context: Context, private val mParent: View): Drawable(), Animatable {

    @Suppress("DEPRECATED_JAVA_ANNOTATION")
    @Retention(RetentionPolicy.CLASS)
    @IntDef(value = [LARGE.toLong(), DEFAULT.toLong()])
    annotation class ProgressDrawableSize


    private val mAnimators: MutableList<Animation> = ArrayList()

    private val mCallback = object : Drawable.Callback {
        override fun unscheduleDrawable(p0: Drawable?, p1: Runnable?) {
            unscheduleSelf(p1)
        }

        override fun invalidateDrawable(p0: Drawable?) {
            invalidateSelf()
        }

        override fun scheduleDrawable(p0: Drawable?, p1: Runnable?, p2: Long) {
            scheduleSelf(p1, p2)
        }
    }

    private val mRing: Ring
        get() = Ring(mCallback)

    private var rotation: Float = 0.toFloat()
        set(rotation) {
            field = rotation
            invalidateSelf()
        }

    private val mResources: Resources = context.resources
    private var mAnimation: Animation? = null
    private var mRotationCount: Float = 0.0f
    private var mWidth: Double = 0.0
    private var mHeight: Double = 0.0
    private var mFinishing: Boolean = false

    init {
        updateSizes(DEFAULT)
        setupAnimators()
    }

    /**
     * Set the overall size for the progress spinner. This updates the radius
     * and stroke width of the ring.
     *
     * @param size One of {@link MaterialProgressDrawable.LARGE} or
     *            {@link MaterialProgressDrawable.DEFAULT}
     */
    fun updateSizes(@ProgressDrawableSize size: Int) {
        if (size == LARGE) {
            setSizeParameters(CIRCLE_DIAMETER_LARGE.toDouble(), CIRCLE_DIAMETER_LARGE.toDouble(), CENTER_RADIUS_LARGE.toDouble(),
                    STROKE_WIDTH_LARGE.toDouble(), ARROW_WIDTH_LARGE.toFloat(), ARROW_HEIGHT_LARGE.toFloat())
        } else {
            setSizeParameters(CIRCLE_DIAMETER.toDouble(), CIRCLE_DIAMETER.toDouble(), CENTER_RADIUS.toDouble(), STROKE_WIDTH.toDouble(),
                    ARROW_WIDTH.toFloat(), ARROW_HEIGHT.toFloat())
        }
    }

    private fun setSizeParameters(progressCircleWidth: Double, progressCircleHeight: Double,
                                  centerRadius: Double, strokeWidth: Double, arrowWidth: Float, arrowHeight: Float) {
        val ring = mRing
        val metrics = mResources.displayMetrics
        val screenDensity = metrics.density

        mWidth = progressCircleWidth * screenDensity
        mHeight = progressCircleHeight * screenDensity
        ring.strokeWidth = strokeWidth.toFloat() * screenDensity
        ring.centerRadius = centerRadius * screenDensity
        ring.setColorIndex(0)
        ring.setArrowDimensions(arrowWidth * screenDensity, arrowHeight * screenDensity)
        ring.setInsets(mWidth.toInt(), mHeight.toInt())
    }

    /**
     * @param show Set to true to display the arrowhead on the progress spinner.
     */
    fun showArrow(show: Boolean) {
        mRing.setShowArrow(show)
    }

    /**
     * @param scale Set the scale of the arrowhead for the spinner.
     */
    fun setArrowScale(scale: Float) {
        mRing.setArrowScale(scale)
    }

    /**
     * Set the start and end trim for the progress spinner arc.
     *
     * @param startAngle start angle
     * @param endAngle end angle
     */
    fun setStartEndTrim(startAngle: Float, endAngle: Float) {
        mRing.startTrim = startAngle
        mRing.endTrim = endAngle
    }

    /**
     * Set the amount of rotation to apply to the progress spinner.
     *
     * @param rotation Rotation is from [0..1]
     */
    open fun setProgressRotation(rotation: Float) {
        mRing.rotation = rotation
    }

    /**
     * Update the background color of the circle image view.
     */
    fun setBackgroundColor(color: Int) {
        mRing.setBackgroundColor(color)
    }

    /**
     * Set the colors used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colors
     */
    fun setColorSchemeColors(vararg colors: Int) {
        mRing.setColors(colors)
        mRing.setColorIndex(0)
    }

    override fun getIntrinsicHeight(): Int {
        return mHeight.toInt()
    }

    override fun getIntrinsicWidth(): Int {
        return mWidth.toInt()
    }

    override fun draw(c: Canvas) {
        val bounds = bounds
        val saveCount = c.save()
        c.rotate(rotation, bounds.exactCenterX(), bounds.exactCenterY())

        mRing.draw(c, bounds)
        c.restoreToCount(saveCount)
    }

    override fun setAlpha(alpha: Int) {
        mRing.alpha = alpha
    }

    override fun getAlpha(): Int {
        return mRing.alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mRing.setColorFilter(colorFilter)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
    override fun isRunning(): Boolean {
        val animators = mAnimators
        val N = animators.size
        for (i in 0 until N) {
            val animator = animators[i]
            if (animator.hasStarted() && !animator.hasEnded()) {
                return true
            }
        }
        return false
    }

    override fun start() {
        mAnimation!!.reset()
        mRing.storeOriginals()
        // Already showing some part of the ring
        if (mRing.endTrim != mRing.startTrim) {
            mFinishing = true
            mAnimation!!.duration = (ANIMATION_DURATION / 2).toLong()
            mParent.startAnimation(mAnimation)
        } else {
            mRing.setColorIndex(0)
            mRing.resetOriginals()
            mAnimation!!.duration = ANIMATION_DURATION.toLong()
            mParent.startAnimation(mAnimation)
        }
    }

    override fun stop() {
        mParent.clearAnimation()
        rotation = 0f
        mRing.setShowArrow(false)
        mRing.setColorIndex(0)
        mRing.resetOriginals()
    }

    private fun getMinProgressArc(ring: Ring): Float {
        return Math.toRadians(
                ring.strokeWidth / (2.0 * Math.PI * ring.centerRadius)).toFloat()
    }

    private fun evaluateColorChange(fraction: Float, startValue: Int, endValue: Int): Int {
        val startInt = startValue
        val startA = startInt shr 24 and 0xff
        val startR = startInt shr 16 and 0xff
        val startG = startInt shr 8 and 0xff
        val startB = startInt and 0xff

        val endInt = endValue
        val endA = endInt shr 24 and 0xff
        val endR = endInt shr 16 and 0xff
        val endG = endInt shr 8 and 0xff
        val endB = endInt and 0xff

        return (startA + (fraction * (endA - startA)).toInt() shl 24) or
                (startR + (fraction * (endR - startR)).toInt() shl 16) or
                (startG + (fraction * (endG - startG)).toInt() shl 8) or
                (startB + (fraction * (endB - startB)).toInt())
    }

    /**
     * Update the ring color if this is within the last 25% of the animation.
     * The new ring color will be a translation from the starting ring color to
     * the next color.
     */
    private fun updateRingColor(interpolatedTime: Float, ring: Ring) {
        if (interpolatedTime > COLOR_START_DELAY_OFFSET) {
            // scale the interpolatedTime so that the full
            // transformation from 0 - 1 takes place in the
            // remaining time
            ring.setColor(evaluateColorChange((interpolatedTime - COLOR_START_DELAY_OFFSET) / (1.0f - COLOR_START_DELAY_OFFSET), ring.startingColor,
                    ring.nextColor))
        }
    }

    private fun setupAnimators() {
        val ring = mRing
        val animation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (mFinishing) {
                    applyFinishTranslation(interpolatedTime, ring)
                } else {
                    // The minProgressArc is calculated from 0 to create an
                    // angle that matches the stroke width.
                    val minProgressArc = getMinProgressArc(ring)
                    val startingEndTrim = ring.startingEndTrim
                    val startingTrim = ring.startingStartTrim
                    val startingRotation = ring.startingRotation

                    updateRingColor(interpolatedTime, ring)

                    // Moving the start trim only occurs in the first 50% of a
                    // single ring animation
                    if (interpolatedTime <= START_TRIM_DURATION_OFFSET) {
                        // scale the interpolatedTime so that the full
                        // transformation from 0 - 1 takes place in the
                        // remaining time
                        val scaledTime = interpolatedTime / (1.0f - START_TRIM_DURATION_OFFSET)
                        val startTrim = startingTrim + (MAX_PROGRESS_ARC - minProgressArc) * MATERIAL_INTERPOLATOR
                                .getInterpolation(scaledTime)
                        ring.startTrim = startTrim
                    }

                    // Moving the end trim starts after 50% of a single ring
                    // animation completes
                    if (interpolatedTime > END_TRIM_START_DELAY_OFFSET) {
                        // scale the interpolatedTime so that the full
                        // transformation from 0 - 1 takes place in the
                        // remaining time
                        val minArc = MAX_PROGRESS_ARC - minProgressArc
                        val scaledTime = (interpolatedTime - START_TRIM_DURATION_OFFSET) / (1.0f - START_TRIM_DURATION_OFFSET)
                        val endTrim = startingEndTrim + minArc * MATERIAL_INTERPOLATOR.getInterpolation(scaledTime)
                        ring.endTrim = endTrim
                    }

                    var rotation = startingRotation + 0.25f * interpolatedTime
                    ring.rotation = rotation

                    val groupRotation = FULL_ROTATION / NUM_POINTS * interpolatedTime + FULL_ROTATION * (mRotationCount / NUM_POINTS)
                    rotation = groupRotation
                }
            }
        }
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.RESTART
        animation.interpolator = LINEAR_INTERPOLATOR
        animation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {
                mRotationCount = 0f
            }

            override fun onAnimationEnd(animation: Animation) {
                // do nothing
            }

            override fun onAnimationRepeat(animation: Animation) {
                ring.storeOriginals()
                ring.goToNextColor()
                ring.startTrim = ring.endTrim
                if (mFinishing) {
                    // finished closing the last ring from the swipe gesture; go
                    // into progress mode
                    mFinishing = false
                    animation.duration = ANIMATION_DURATION.toLong()
                    ring.setShowArrow(false)
                } else {
                    mRotationCount = (mRotationCount + 1) % NUM_POINTS
                }
            }
        })
        mAnimation = animation
    }

    private fun applyFinishTranslation(interpolatedTime: Float, ring: Ring) {
        // shrink back down and complete a full rotation before
        // starting other circles
        // Rotation goes between [0..1].
        updateRingColor(interpolatedTime, ring)
        val targetRotation = (Math.floor((ring.startingRotation / MAX_PROGRESS_ARC).toDouble()) + 1f).toFloat()
        val minProgressArc = getMinProgressArc(ring)
        val startTrim = ring.startingStartTrim + (ring.startingEndTrim - minProgressArc - ring.startingStartTrim) * interpolatedTime
        ring.startTrim = startTrim
        ring.endTrim = ring.startingEndTrim
        val rotation = ring.startingRotation + (targetRotation - ring.startingRotation) * interpolatedTime
        ring.rotation = rotation
    }

    companion object {
        private val LINEAR_INTERPOLATOR = LinearInterpolator()
        private val MATERIAL_INTERPOLATOR = FastOutSlowInInterpolator()

        private val FULL_ROTATION: Float = 1080.0f
        /**ProgressBar 大图*/
        val LARGE = 0
        /**ProgressBar 标准*/
        val DEFAULT = 1

        /**标准图*/
        val CIRCLE_DIAMETER = 40
        private val CENTER_RADIUS: Float = 8.75f
        private val STROKE_WIDTH: Float = 2.5f

        /**大图*/
        val CIRCLE_DIAMETER_LARGE = 56
        private val CENTER_RADIUS_LARGE: Float = 12.5f
        private val STROKE_WIDTH_LARGE: Float = 3f

        /** 动画的色彩过渡 */
        private val COLOR_START_DELAY_OFFSET: Float = 0.75f
        private val END_TRIM_START_DELAY_OFFSET: Float = 0.5f
        private val START_TRIM_DURATION_OFFSET: Float = 0.5f

        /**单个进程的持续时间以毫秒为单位旋转*/
        private val ANIMATION_DURATION = 1332
        private val NUM_POINTS: Float = 5f

        private val ARROW_WIDTH = 10
        private val ARROW_HEIGHT = 5
        val ARROW_OFFSET_ANGLE: Float = 5f

        private val ARROW_WIDTH_LARGE = 12
        private val ARROW_HEIGHT_LARGE = 6
        private val MAX_PROGRESS_ARC: Float = 0.8f

    }
}