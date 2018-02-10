package com.qcloud.qclib.imageselect.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import com.qcloud.qclib.imageselect.utils.ImageUtil
import com.qcloud.qclib.utils.ScreenUtil

/**
 * 类说明：裁剪图片
 * Author: Kuzan
 * Date: 2017/5/25 14:58.
 */
class CutImageView : AppCompatImageView {

    private var mDownPoint: PointF? = null
    private var mMiddlePoint: PointF? = null
    private var mMatrix: Matrix? = null
    private var mTempMatrix: Matrix? = null
    //    private Bitmap mBitmap;

    private var mBitmapWidth: Int = 0
    private var mBitmapHeight: Int = 0

    private val MODE_NONE = 0
    private val MODE_DRAG = 1
    private val MODE_ZOOM = 2
    private val MODE_POINTER_UP = 3
    private var CURR_MODE = MODE_NONE

    private var mLastDistance: Float = 0.toFloat()

    private val mFrontGroundPaint = Paint()
    //    private int mRadius;
    private var mTargetWidth: Int = 0
    private var mTargetHeight: Int = 0
    private var mXfermode: Xfermode? = null
    private var r: Rect? = null
    private var rf: RectF? = null

    private var mCircleCenterX: Float = 0.toFloat()
    private var mCircleCenterY: Float = 0.toFloat()
    private var mCircleX: Float = 0.toFloat()
    private var mCircleY: Float = 0.toFloat()
    private var isCutImage: Boolean = false

    private var mType = RECT_TYPE

    constructor(context: Context) : super(context) {
        initDefaultSize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initDefaultSize()
    }

    private fun initDefaultSize() {
        val width = ScreenUtil.getScreenWidth(context)
        val height = ScreenUtil.getScreenHeight(context)

        if (width < height) {
            mTargetWidth = width / 2
            mTargetHeight = width / 2
        } else {
            mTargetWidth = height / 2
            mTargetHeight = height / 2
        }
    }

    /**
     * 设置要剪裁的图片资源
     *
     * @param resId
     */
    override fun setImageResource(resId: Int) {

        val bitmap = BitmapFactory.decodeResource(resources, resId)
        setBitmapData(bitmap, mType, 200, 200)
    }

    fun setBitmapData(bitmap: Bitmap?, type: Int, width: Int, height: Int) {

        if (bitmap == null) {
            return
        }

        if (width > 0 && height > 0) {
            setRadius(width, height)
        }

        if (type == CIRCLE_TYPE) {
            mType = type

            if (mTargetWidth < mTargetHeight) {
                mTargetHeight = mTargetWidth
            } else {
                mTargetWidth = mTargetHeight
            }

        } else {
            mType = RECT_TYPE
        }

        mBitmapHeight = bitmap.height
        mBitmapWidth = bitmap.width
        setImageBitmap(bitmap)
        init()
    }

    private fun init() {
        mDownPoint = PointF()
        mMiddlePoint = PointF()
        mMatrix = Matrix()
        mTempMatrix = Matrix()
        mFrontGroundPaint.color = Color.parseColor("#ac000000")
        mFrontGroundPaint.isAntiAlias = true
        mXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

        scaleType = ImageView.ScaleType.MATRIX

        post { center() }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mCircleCenterX = (width / 2).toFloat()
        mCircleCenterY = (height / 2).toFloat()
        mCircleX = mCircleCenterX - mTargetWidth / 2
        mCircleY = mCircleCenterY - mTargetHeight / 2
    }

    private fun setRadius(width: Int, height: Int) {
        mTargetWidth = if (width > ScreenUtil.getScreenWidth(context)) {
            ScreenUtil.getScreenWidth(context)
        } else {
            width
        }

        mTargetHeight = if (height > ScreenUtil.getScreenHeight(context)) {
            ScreenUtil.getScreenHeight(context)
        } else {
            height
        }
    }

    @SuppressLint("WrongConstant")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isCutImage) {
            return
        }
        if (rf == null || rf!!.isEmpty) {
            r = Rect(0, 0, width, height)
            rf = RectF(r)
        }
        // 画入前景圆形蒙板层
        val sc = canvas.saveLayer(rf, null, Canvas.MATRIX_SAVE_FLAG
                or Canvas.CLIP_SAVE_FLAG or Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                or Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                or Canvas.CLIP_TO_LAYER_SAVE_FLAG or Canvas.ALL_SAVE_FLAG)
        //画入矩形黑色半透明蒙板层
        canvas.drawRect(r!!, mFrontGroundPaint)
        //设置Xfermode，目的是为了去除矩形黑色半透明蒙板层和圆形的相交部分
        mFrontGroundPaint.xfermode = mXfermode
        if (mType == CIRCLE_TYPE) {
            //画入圆形
            canvas.drawCircle(mCircleCenterX, mCircleCenterY, (mTargetHeight / 2).toFloat(),
                    mFrontGroundPaint)
        } else {
            //画入正方形
            canvas.drawRect(mCircleCenterX - mTargetWidth / 2, mCircleCenterY - mTargetHeight / 2,
                    mCircleCenterX + mTargetWidth / 2, mCircleCenterY + mTargetHeight / 2, mFrontGroundPaint)
        }

        canvas.restoreToCount(sc)
        //清除Xfermode，防止影响下次画图
        mFrontGroundPaint.xfermode = null
    }

    /**
     * 截取Bitmap
     *
     * @return
     */
    fun clipImage(): Bitmap? {
        isCutImage = true
        val paint = Paint()
        //        invalidate();
        isDrawingCacheEnabled = true
        //        Bitmap bitmap = getDrawingCache().copy(getDrawingCache().getConfig(),
        //                false);
        var bitmap: Bitmap? = drawingCache
        //        setDrawingCacheEnabled(false);
        val targetBitmap = Bitmap.createBitmap(mTargetWidth, mTargetHeight,
                Bitmap.Config.ARGB_8888)
        val canvas = Canvas(targetBitmap)
        val dst = RectF((-bitmap!!.width / 2 + mTargetWidth / 2).toFloat(), (-height / 2 + mTargetHeight / 2).toFloat(), (bitmap.width / 2 + mTargetWidth / 2).toFloat(), (height / 2 + mTargetHeight / 2).toFloat())

        canvas.drawBitmap(bitmap, null, dst, paint)
        isDrawingCacheEnabled = false
        bitmap.recycle()
        isCutImage = false
        return if (mType == CIRCLE_TYPE) {
            //返回圆形图片
            ImageUtil.toRoundBitmap(targetBitmap)
        } else {
            //返回正方形图片
            targetBitmap
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (mMatrix == null) {
            return super.onTouchEvent(event)
        }

        val values = FloatArray(9)
        mMatrix!!.getValues(values)
        val left = values[Matrix.MTRANS_X]
        val top = values[Matrix.MTRANS_Y]
        val right = left + mBitmapWidth * values[Matrix.MSCALE_X]
        val bottom = top + mBitmapHeight * values[Matrix.MSCALE_Y]
        var x: Float
        var y: Float

        when (event.action and MotionEvent.ACTION_MASK) {

            MotionEvent.ACTION_DOWN -> {
                CURR_MODE = MODE_DRAG
                mDownPoint!!.set(event.x, event.y)
            }
            MotionEvent.ACTION_POINTER_DOWN -> if (getDistance(event) > 10f) {
                CURR_MODE = MODE_ZOOM
                midPoint(mMiddlePoint, event)
                mLastDistance = getDistance(event)
            }
            MotionEvent.ACTION_MOVE ->
                //如果当前模式为拖曳（单指触屏）
                if (CURR_MODE == MODE_DRAG || CURR_MODE == MODE_POINTER_UP) {
                    if (CURR_MODE == MODE_DRAG) {

                        x = event.x - mDownPoint!!.x
                        y = event.y - mDownPoint!!.y
                        //left靠边
                        if (x + left > mCircleX) {
                            x = 0f
                        }
                        //right靠边
                        if (x + right < mCircleX + mTargetWidth) {
                            x = 0f
                        }
                        //top靠边
                        if (y + top > mCircleY) {
                            y = 0f
                        }
                        //bottom靠边
                        if (y + bottom < mCircleY + mTargetHeight) {
                            y = 0f
                        }
                        mMatrix!!.postTranslate(x, y)
                        mDownPoint!!.set(event.x, event.y)

                    } else {
                        CURR_MODE = MODE_DRAG
                        mDownPoint!!.set(event.x, event.y)
                    }
                } else {
                    //否则当前模式为缩放（双指触屏）
                    val distance = getDistance(event)
                    if (distance > 10f) {
                        val scale = distance / mLastDistance

                        //left靠边
                        if (left >= mCircleX) {
                            mMiddlePoint!!.x = 0f
                        }
                        //right靠边
                        if (right <= mCircleX + mTargetWidth) {
                            mMiddlePoint!!.x = right
                        }
                        //top靠边
                        if (top >= mCircleY) {
                            mMiddlePoint!!.y = 0f
                        }
                        //bottom靠边
                        if (bottom <= mCircleY + mTargetHeight) {
                            mMiddlePoint!!.y = bottom
                        }
                        mTempMatrix!!.set(mMatrix)
                        mTempMatrix!!.postScale(scale, scale, mMiddlePoint!!.x, mMiddlePoint!!.y)

                        val temp_values = FloatArray(9)
                        mTempMatrix!!.getValues(temp_values)
                        val temp_left = temp_values[Matrix.MTRANS_X]
                        val temp_top = temp_values[Matrix.MTRANS_Y]
                        val temp_right = temp_left + mBitmapWidth * temp_values[Matrix.MSCALE_X]
                        val temp_bottom = temp_top + mBitmapHeight * temp_values[Matrix.MSCALE_Y]
                        //靠边预判断
                        if (temp_left > mCircleX || temp_right < mCircleX + mTargetWidth ||
                                temp_top > mCircleY || temp_bottom < mCircleY + mTargetHeight) {
                            return true
                        }
                        mMatrix!!.postScale(scale, scale, mMiddlePoint!!.x, mMiddlePoint!!.y)
                        mLastDistance = getDistance(event)
                    }
                }
            MotionEvent.ACTION_UP -> CURR_MODE = MODE_NONE
            MotionEvent.ACTION_POINTER_UP -> CURR_MODE = MODE_POINTER_UP
        }
        imageMatrix = mMatrix
        return true
    }

    /**
     * 两点的距离
     */
    private fun getDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    /**
     * 两点的中点
     */
    private fun midPoint(point: PointF?, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point!!.set(x / 2, y / 2)
    }

    /**
     * 横向、纵向居中
     */
    protected fun center() {

        val height = mBitmapHeight.toFloat()
        val width = mBitmapWidth.toFloat()
        val screenWidth = getWidth().toFloat()
        val screenHeight = getHeight().toFloat()
        var scale = 1f
        if (width >= height) {
            scale = screenWidth / width

            if (scale * height < mTargetHeight) {
                scale = mTargetHeight / height
            }

        } else {
            if (height <= screenHeight) {
                scale = screenWidth / width
            } else {
                scale = screenHeight / height
            }

            if (scale * width < mTargetWidth) {
                scale = mTargetWidth / width
            }
        }

        val deltaX = (screenWidth - width * scale) / 2f
        val deltaY = (screenHeight - height * scale) / 2f
        mMatrix!!.postScale(scale, scale)
        mMatrix!!.postTranslate(deltaX, deltaY)
        imageMatrix = mMatrix
    }

    companion object {
        val RECT_TYPE = 1
        val CIRCLE_TYPE = 2
    }
}
