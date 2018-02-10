package com.qcloud.qclib.widget.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import com.qcloud.qclib.R
import com.qcloud.qclib.utils.ColorUtil
import com.qcloud.qclib.utils.DensityUtil

/**
 * 类说明：自定义进度条
 * Author: Kuzan
 * Date: 2018/1/20 11:47.
 */
open class CustomProgressBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.progressBarStyle): ProgressBar(context, attrs, defStyleAttr) {

    private val mPaint = Paint()
    private var mMode: Mode = Mode.System
    private var mTextColor: Int = ColorUtil.parseColor("#70A800")
    private var mTextSize: Int = DensityUtil.sp2px(context, 10f)
    private var mTextMargin: Int = DensityUtil.dp2px(context, 4f)
    private var mReachedColor: Int = ColorUtil.parseColor("#70A800")
    private var mReachedHeight: Int = DensityUtil.dp2px(context, 2f)
    private var mUnReachedColor: Int = ColorUtil.parseColor("#CCCCCC")
    private var mUnReachedHeight: Int = DensityUtil.dp2px(context, 1f)

    private var isCapRounded: Boolean = false
    private var isHiddenText: Boolean = false

    private var mRadius: Int = DensityUtil.dp2px(context, 16f)
    private var mMaxUnReachedEndX: Int = 0
    private var mMaxStrokeWidth: Int = 0
    private var mTextHeight: Int = 0
    private var mTextWidth: Int = 0

    private var mArcRectF: RectF? = null
    private val mTextRect: Rect = Rect()

    private var mText: String = ""

    init {
        mPaint.isAntiAlias = true
        initCustomAttrs(context, attrs)
        mMaxStrokeWidth = Math.max(mReachedHeight, mUnReachedHeight)
    }

    private fun initCustomAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar)
        val count = typedArray.indexCount
        for (i in 0 until count) {
            initAttr(typedArray.getIndex(i), typedArray)
        }
        typedArray.recycle()
    }

    open fun initAttr(attr: Int, typedArray: TypedArray) {
        when (attr) {
            R.styleable.CustomProgressBar_pb_mode -> {
                val ordinal = typedArray.getInt(attr, Mode.System.ordinal)
                mMode = Mode.values()[ordinal]
            }
            R.styleable.CustomProgressBar_pb_textColor -> {
                mTextColor = typedArray.getColor(attr, mTextColor)
            }
            R.styleable.CustomProgressBar_pb_textSize -> {
                mTextSize = typedArray.getDimensionPixelOffset(attr, mTextSize)
            }
            R.styleable.CustomProgressBar_pb_textMargin -> {
                mTextMargin = typedArray.getDimensionPixelOffset(attr, mTextMargin)
            }
            R.styleable.CustomProgressBar_pb_reachedColor -> {
                mReachedColor = typedArray.getColor(attr, mReachedColor)
            }
            R.styleable.CustomProgressBar_pb_reachedHeight -> {
                mReachedHeight = typedArray.getDimensionPixelOffset(attr, mReachedHeight)
            }
            R.styleable.CustomProgressBar_pb_unReachedColor -> {
                mUnReachedColor = typedArray.getColor(attr, mUnReachedColor)
            }
            R.styleable.CustomProgressBar_pb_unReachedHeight -> {
                mUnReachedHeight = typedArray.getDimensionPixelOffset(attr, mUnReachedHeight)
            }
            R.styleable.CustomProgressBar_pb_isCapRounded -> {
                isCapRounded = typedArray.getBoolean(attr, isCapRounded)
                if (isCapRounded) {
                    mPaint.strokeCap = Paint.Cap.ROUND
                }
            }
            R.styleable.CustomProgressBar_pb_isHiddenText -> {
                isHiddenText = typedArray.getBoolean(attr, isHiddenText)
            }
            R.styleable.CustomProgressBar_pb_radius -> {
                mRadius = typedArray.getDimensionPixelOffset(attr, mRadius)
            }
        }
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        when (mMode) {
            Mode.System -> {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
            Mode.Horizontal -> {
                calculateTextWidthAndHeight()

                val width = View.MeasureSpec.getSize(widthMeasureSpec)

                var expectHeight = paddingTop + paddingBottom
                expectHeight += if (isHiddenText) {
                    Math.max(mReachedHeight, mUnReachedHeight)
                } else {
                    Math.max(mTextHeight, Math.max(mReachedHeight, mUnReachedHeight))
                }
                val height = View.resolveSize(expectHeight, heightMeasureSpec)
                setMeasuredDimension(width, height)

                mMaxUnReachedEndX = measuredWidth - paddingLeft - paddingRight
            }
            Mode.Circle -> {
                var expectSize = mRadius * 2 + mMaxStrokeWidth + paddingLeft + paddingRight
                val width = View.resolveSize(expectSize, widthMeasureSpec)
                val height = View.resolveSize(expectSize, heightMeasureSpec)
                expectSize = Math.min(width, height)

                mRadius = (expectSize - paddingLeft - paddingRight - mMaxStrokeWidth) / 2
                if (mArcRectF == null) {
                    mArcRectF = RectF()
                }
                mArcRectF!!.set(0f, 0f, (mRadius * 2).toFloat(), (mRadius * 2).toFloat())

                setMeasuredDimension(expectSize, expectSize)
            }
            Mode.Comet -> {
                // TODO
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }

            Mode.Wave -> {
                // TODO
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
        }
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        when (mMode) {
            Mode.System -> super.onDraw(canvas)
            Mode.Horizontal -> onDrawHorizontal(canvas)
            Mode.Circle -> onDrawCircle(canvas)
            Mode.Comet -> super.onDraw(canvas)
            Mode.Wave -> super.onDraw(canvas)

        }
    }

    /**
     * 画垂直方向
     * */
    private fun onDrawHorizontal(canvas: Canvas) {
        canvas.save()
        canvas.translate(paddingLeft.toFloat(), (measuredHeight / 2).toFloat())

        val reachedRatio = progress * 1.0f / max
        var reachedEndX = reachedRatio * mMaxUnReachedEndX

        if (isHiddenText) {
            if (reachedEndX > mMaxUnReachedEndX) {
                reachedEndX = mMaxUnReachedEndX.toFloat()
            }
            if (reachedEndX > 0) {
                mPaint.color = mReachedColor
                mPaint.strokeWidth = mReachedHeight.toFloat()
                mPaint.style = Paint.Style.STROKE
                canvas.drawLine(0f, 0f, reachedEndX, 0f, mPaint)
            }

            var unReachedStartX = reachedEndX
            if (isCapRounded) {
                unReachedStartX += (mReachedHeight + mUnReachedHeight) * 1.0f / 2
            }
            if (unReachedStartX < mMaxUnReachedEndX) {
                mPaint.color = mUnReachedColor
                mPaint.strokeWidth = mUnReachedHeight.toFloat()
                mPaint.style = Paint.Style.STROKE
                canvas.drawLine(unReachedStartX, 0f, mMaxUnReachedEndX.toFloat(), 0f, mPaint)
            }
        } else {
            calculateTextWidthAndHeight()
            val maxReachedEndX = mMaxUnReachedEndX - mTextWidth - mTextMargin
            if (reachedEndX > maxReachedEndX) {
                reachedEndX = maxReachedEndX.toFloat()
            }
            if (reachedEndX > 0) {
                mPaint.color = mReachedColor
                mPaint.strokeWidth = mReachedHeight.toFloat()
                mPaint.style = Paint.Style.STROKE

                canvas.drawLine(0f, 0f, reachedEndX, 0f, mPaint)
            }

            mPaint.textAlign = Paint.Align.LEFT
            mPaint.style = Paint.Style.FILL
            mPaint.color = mTextColor
            val textStartX = if (reachedEndX > 0) reachedEndX + mTextMargin else reachedEndX
            canvas.drawText(mText, textStartX, (mTextHeight / 2).toFloat(), mPaint)

            val unReachedStartX = textStartX + mTextWidth.toFloat() + mTextMargin.toFloat()
            if (unReachedStartX < mMaxUnReachedEndX) {
                mPaint.color = mUnReachedColor
                mPaint.strokeWidth = mUnReachedHeight.toFloat()
                mPaint.style = Paint.Style.STROKE
                canvas.drawLine(unReachedStartX, 0f, mMaxUnReachedEndX.toFloat(), 0f, mPaint)
            }
        }

        canvas.restore()
    }

    /**
     * 画圆
     * */
    private fun onDrawCircle(canvas: Canvas) {
        canvas.save()
        canvas.translate((paddingLeft + mMaxStrokeWidth / 2).toFloat(), (paddingTop + mMaxStrokeWidth / 2).toFloat())

        mPaint.style = Paint.Style.STROKE
        mPaint.color = mUnReachedColor
        mPaint.strokeWidth = mUnReachedHeight.toFloat()
        canvas.drawCircle(mRadius.toFloat(), mRadius.toFloat(), mRadius.toFloat(), mPaint)

        mPaint.style = Paint.Style.STROKE
        mPaint.color = mReachedColor
        mPaint.strokeWidth = mReachedHeight.toFloat()
        val sweepAngle = progress * 1.0f / max * 360
        canvas.drawArc(mArcRectF!!, 0f, sweepAngle, false, mPaint)

        if (!isHiddenText) {
            calculateTextWidthAndHeight()
            mPaint.style = Paint.Style.FILL
            mPaint.color = mTextColor
            mPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(mText, mRadius.toFloat(), (mRadius + mTextHeight / 2).toFloat(), mPaint)
        }

        canvas.restore()
    }

    private fun calculateTextWidthAndHeight() {
        mText = String.format("%d", progress * 100 / max) + "%"
        mPaint.textSize = mTextSize.toFloat()
        mPaint.style = Paint.Style.FILL

        mPaint.getTextBounds(mText, 0, mText.length, mTextRect)
        mTextWidth = mTextRect.width()
        mTextHeight = mTextRect.height()
    }

    private enum class Mode {
        System,
        Horizontal,
        Circle,
        Comet,
        Wave
    }

}