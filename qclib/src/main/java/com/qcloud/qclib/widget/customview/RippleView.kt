package com.qcloud.qclib.widget.customview

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.AdapterView
import android.widget.RelativeLayout
import com.qcloud.qclib.R

/**
 * Description: 自定义基于Material design的波纹效果
 * Author: gaobaiqiang
 * 2018/3/3 下午10:01.
 */
class RippleView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var frameRate: Int = 10
    private var radiusMax: Int = 0
    private var animationRunning: Boolean = false
    private var timer: Int = 0
    private var timerEmpty: Int = 0
    private var durationEmpty: Int = -1
    private var mX: Float = -1f
    private var mY: Float = -1f
    private var originBitmap: Bitmap? = null

    var zoomDuration: Int = 0
    var zoomScale: Float = 0f
    var scaleAnimation: ScaleAnimation? = null
    var isZooming: Boolean = false
    var isCentered: Boolean = false

    private var rippleType: Int = RippleType.SIMPLE.type
    var rippleDuration: Int = 400
    var rippleAlpha = 90
    // 波纹颜色
    @ColorInt
    var rippleColor: Int = Color.GRAY
    var ripplePadding: Int = 0

    private var paint: Paint = Paint()
    private val canvasHandler: Handler = Handler()
    private val runnable = Runnable { invalidate() }

    private var onCompletionListener: OnRippleCompleteListener? = null

    private val gestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            animateRipple(e)
            sendClickEvent(true)
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            return true
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return true
        }
    })

    init {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        if (isInEditMode || attrs == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView)
        try {
            rippleColor = typedArray.getColor(R.styleable.RippleView_rv_color, ContextCompat.getColor(context, R.color.white))
            rippleType = typedArray.getInt(R.styleable.RippleView_rv_type, 0)
            isZooming = typedArray.getBoolean(R.styleable.RippleView_rv_zoom, false)
            isCentered = typedArray.getBoolean(R.styleable.RippleView_rv_centered, false)
            rippleDuration = typedArray.getInteger(R.styleable.RippleView_rv_ripple_duration, rippleDuration)
            frameRate = typedArray.getInteger(R.styleable.RippleView_rv_frame_rate, frameRate)
            rippleAlpha = typedArray.getInteger(R.styleable.RippleView_rv_alpha, rippleAlpha)
            ripplePadding = typedArray.getDimensionPixelSize(R.styleable.RippleView_rv_ripple_padding, 0)
            zoomScale = typedArray.getFloat(R.styleable.RippleView_rv_zoom_scale, 1.03f)
            zoomDuration = typedArray.getInt(R.styleable.RippleView_rv_zoom_duration, 200)
        } finally {
            typedArray.recycle()
        }

        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = rippleColor
        paint.alpha = rippleAlpha

        setWillNotDraw(false)
        isDrawingCacheEnabled = true
        isClickable = true
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (animationRunning) {
            canvas.save()
            if (rippleDuration <= timer * frameRate) {
                animationRunning = false
                timer = 0
                durationEmpty = -1
                timerEmpty = 0

                if (Build.VERSION.SDK_INT != 23) {
                    canvas.restore()
                }
                invalidate()
                if (onCompletionListener != null) {
                    onCompletionListener!!.onComplete(this)
                }
                return
            } else {
                canvasHandler.postDelayed(runnable, frameRate.toLong())
            }

            if (timer == 0) {
                canvas.save()
            }

            canvas.drawCircle(mX, mY, radiusMax * (timer.toFloat() * frameRate / rippleDuration), paint)

            paint.color = Color.parseColor("#ffff4444")

            if (rippleType == 1 && originBitmap != null && timer.toFloat() * frameRate / rippleDuration > 0.4f) {
                if (durationEmpty == -1) {
                    durationEmpty = rippleDuration - timer * frameRate
                }

                timerEmpty++
                val tmpBitmap = getCircleBitmap((radiusMax * (timerEmpty.toFloat() * frameRate / durationEmpty)).toInt())
                canvas.drawBitmap(tmpBitmap, 0f, 0f, paint)
                tmpBitmap.recycle()
            }

            paint.color = rippleColor

            if (rippleType == 1) {
                if (timer.toFloat() * frameRate / rippleDuration > 0.6f) {
                    paint.alpha = (rippleAlpha - rippleAlpha * (timerEmpty.toFloat() * frameRate / durationEmpty)).toInt()
                } else {
                    paint.alpha = rippleAlpha
                }
            } else {
                paint.alpha = (rippleAlpha - rippleAlpha * (timer.toFloat() * frameRate / rippleDuration)).toInt()
            }

            timer++
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h

        scaleAnimation = ScaleAnimation(1.0f, zoomScale, 1.0f, zoomScale, (w / 2).toFloat(), (h / 2).toFloat())
        scaleAnimation!!.duration = zoomDuration.toLong()
        scaleAnimation!!.repeatMode = Animation.REVERSE
        scaleAnimation!!.repeatCount = 1
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(event)) {
            animateRipple(event)
            sendClickEvent(false)
        }
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        this.onTouchEvent(ev)
        return super.onInterceptTouchEvent(ev)
    }

    /**
     * 推出波纹动画与位移事件的当前视图
     *
     * @param event
     */
    fun animateRipple(event: MotionEvent) {
        createAnimation(event.x, event.y)
    }

    /**
     * 以x和y位置为中心的当前视图启动纹波动画。
     *
     * @param x 波纹中心的水平位置
     * @param y 波纹中心的垂直位置
     */
    fun animateRipple(x: Float, y: Float) {
        createAnimation(x, y)
    }

    /**
     * 创建在x，y为中心的波纹动画
     *
     * @param x
     * @param y
     */
    private fun createAnimation(x: Float, y: Float) {
        if (isEnabled && !animationRunning) {
            if (isZooming)
                this.startAnimation(scaleAnimation)

            radiusMax = Math.max(mWidth, mHeight)

            if (rippleType != 2) {
                radiusMax /= 2
            }

            radiusMax -= ripplePadding

            if (isCentered || rippleType == 1) {
                this.mX = (measuredWidth / 2).toFloat()
                this.mY = (measuredHeight / 2).toFloat()
            } else {
                this.mX = x
                this.mY = y
            }

            animationRunning = true

            if (rippleType == 1 && originBitmap == null) {
                originBitmap = getDrawingCache(true)
            }

            invalidate()
        }
    }

    /**
     * 如果是一个列表则发送单击事件
     *
     * @param isLongClick 是否长按
     */
    private fun sendClickEvent(isLongClick: Boolean = false) {
        if (parent is AdapterView<*>) {
            val adapterView = parent as AdapterView<*>
            val position = adapterView.getPositionForView(this)
            val id = adapterView.getItemIdAtPosition(position)
            if (isLongClick) {
                if (adapterView.onItemLongClickListener != null) {
                    adapterView.onItemLongClickListener.onItemLongClick(adapterView, this, position, id)
                }
            } else {
                if (adapterView.onItemClickListener != null) {
                    adapterView.onItemClickListener!!.onItemClick(adapterView, this, position, id)
                }
            }
        }
    }

    /**
     * 生成圆角背景
     * */
    private fun getCircleBitmap(radius: Int): Bitmap {

        val output = Bitmap.createBitmap(originBitmap!!.width, originBitmap!!.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect((mX - radius).toInt(), (mY - radius).toInt(), (mX + radius).toInt(), (mY + radius).toInt())

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(mX, mY, radius.toFloat(), paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(originBitmap!!, rect, rect, paint)

        return output
    }

    fun getRippleType(): RippleType {
        return RippleType.values()[rippleType]
    }

    fun setRippleType(rippleType: RippleType) {
        this.rippleType = rippleType.ordinal
    }

    /**
     * 定义在波纹结束时调用的回调函数
     */
    interface OnRippleCompleteListener {
        fun onComplete(rippleView: RippleView)
    }

    enum class RippleType constructor(var type: Int) {
        SIMPLE(0),
        DOUBLE(1),
        RECTANGLE(2)
    }
}