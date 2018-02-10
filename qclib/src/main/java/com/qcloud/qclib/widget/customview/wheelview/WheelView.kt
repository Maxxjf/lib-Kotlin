package com.qcloud.qclib.widget.customview.wheelview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import com.qcloud.qclib.utils.ColorUtil
import com.qcloud.qclib.utils.DensityUtil
import com.qcloud.qclib.widget.customview.wheelview.entity.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/1/19 14:48.
 */
class WheelView @JvmOverloads constructor(
        val mContext: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : View(mContext, attrs, defStyleAttr) {

    private val handler: MessageHandler = MessageHandler(this)

    private var gestureDetector: GestureDetector? = null
    private var onItemSelectListener: OnItemSelectListener? = null
    private var onWheelListener: OnWheelListener? = null
    /**附加单位是否仅仅只显示在选中项后面*/
    private var onlyShowCenterLabel = true
    private var mFuture: ScheduledFuture<*>? = null
    /**未选项画笔*/
    private var paintOuterText: Paint = Paint()
    /**选中项画笔*/
    private var paintCenterText: Paint = Paint()
    /**分割线画笔*/
    private var paintIndicator: Paint = Paint()
    /**阴影画笔*/
    private var paintShadow: Paint = Paint()
    /**所有选项*/
    private val items: MutableList<WheelItem> = ArrayList()
    /**附加单位*/
    private var label: String? = null
    /**最大的文字宽*/
    private var maxTextWidth: Int = 0
    /**最大的文字高*/
    private var maxTextHeight: Int = 0
    /**文字大小，单位为sp*/
    private var textSize = TEXT_SIZE
    /**每行高度*/
    var itemHeight: Float = 0.toFloat()
    /**字体样式*/
    private var typeface = Typeface.DEFAULT
    /**未选项文字颜色*/
    private var textColorOuter = TEXT_COLOR_NORMAL
    /**选中项文字颜色*/
    private var textColorCenter = TEXT_COLOR_FOCUS
    private var dividerConfig = DividerConfig()
    /**条目间距倍数，可用来设置上下间距*/
    private var lineSpaceMultiplier = LINE_SPACE_MULTIPLIER
    /**文字的左右边距,单位为px*/
    private var padding = TEXT_PADDING
    /**循环滚动*/
    var isLoop = true
    /**第一条线Y坐标值*/
    private var firstLineY: Float = 0.toFloat()
    /**第二条线Y坐标*/
    private var secondLineY: Float = 0.toFloat()
    /**滚动总高度y值*/
    var totalScrollY = 0f
    /**初始化默认选中项*/
    var initPosition = -1
    /**选中项的索引*/
    private var selectedIndex: Int = 0
    private var preCurrentIndex: Int = 0
    /**绘制几个条目*/
    private var visibleItemCount = ITEM_OFF_SET * 2 + 1
    /**控件高度*/
    var wMeasuredHeight: Int = 0
    /**控件宽度*/
    var wMeasuredWidth: Int = 0
    /**半径*/
    private var radius: Int = 0
    private var offset = 0
    private var previousY = 0f
    private var startTime: Long = 0
    private var widthMeasureSpec: Int = 0
    private var gravity = Gravity.CENTER
    /**中间选中文字开始绘制位置*/
    private var drawCenterContentStart = 0
    /**非中间文字开始绘制位置*/
    private var drawOutContentStart = 0
    /**偏移量*/
    private var centerContentOffset: Float = 0.toFloat()
    /**使用比重还是包裹内容？*/
    private var useWeight = false

    init {
        // 屏幕密度：0.75、1.0、1.5、2.0、3.0，根据密度不同进行适配
        val density = resources.displayMetrics.density
        when {
            density < 1 -> centerContentOffset = 2.4F
            density >= 1 && density < 2 -> centerContentOffset = 4.5F
            density >= 2 && density < 3 -> centerContentOffset = 6.0F
            density >= 3 -> centerContentOffset = density * 2.5F
        }
        judgeLineSpace()
        initView(context)
    }

    /**
     * 判断间距是否在有效范围内
     */
    private fun judgeLineSpace() {
        if (lineSpaceMultiplier < 1.5f) {
            lineSpaceMultiplier = 1.5f
        } else if (lineSpaceMultiplier > 4.0f) {
            lineSpaceMultiplier = 4.0f
        }
    }

    private fun initView(context: Context) {
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                scrollBy(velocityY)
                return true
            }
        })
        gestureDetector?.setIsLongpressEnabled(false)
        initPaints()
        initDataForIDE()
    }

    private fun initPaints() {
        // 未选项画笔
        paintOuterText.isAntiAlias = true
        paintOuterText.color = textColorOuter
        paintOuterText.typeface = typeface
        paintOuterText.textSize = textSize.toFloat()

        // 选中项画笔
        paintCenterText.isAntiAlias = true
        paintCenterText.color = textColorCenter
        paintCenterText.textScaleX = 1.1f
        paintCenterText.typeface = typeface
        paintCenterText.textSize = textSize.toFloat()

        // 分割线画笔
        paintIndicator = Paint()
        paintIndicator.isAntiAlias = true
        paintIndicator.color = dividerConfig.color
        paintIndicator.strokeWidth = dividerConfig.thick
        paintIndicator.alpha = dividerConfig.alpha

        // 阴影画笔
        paintShadow.isAntiAlias = true
        paintShadow.color = dividerConfig.shadowColor
        paintShadow.alpha = dividerConfig.shadowAlpha
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    private fun initDataForIDE() {
        if (isInEditMode) {
            setItems(arrayOf("库赞", "男", "海军", "大将"))
        }
    }

    /**
     * 重新测量
     */
    private fun remeasure() {
        measureTextWidthHeight()
        //半圆的周长
        val halfCircumference = (itemHeight * (visibleItemCount - 1)).toInt()
        //整个圆的周长除以PI得到直径，这个直径用作控件的总高度
        wMeasuredHeight = (halfCircumference * 2 / Math.PI).toInt()
        //求出半径
        radius = (halfCircumference / Math.PI).toInt()
        val params = layoutParams
        //控件宽度
        if (useWeight) {
            wMeasuredWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        } else if (params != null && params.width > 0) {
            wMeasuredWidth = params.width
        } else {
            wMeasuredWidth = maxTextWidth
            if (padding < 0) {
                padding = DensityUtil.dp2px(mContext, ITEM_PADDING)
            }
            wMeasuredWidth += padding * 2
            if (!TextUtils.isEmpty(label)) {
                wMeasuredWidth += obtainTextWidth(paintCenterText, label)
            }
        }
        Log.d("WheelView", "wMeasuredWidth=$wMeasuredWidth,wMeasuredHeight=$wMeasuredHeight")
        //计算两条横线 和 选中项画笔的基线Y位置
        firstLineY = (wMeasuredHeight - itemHeight) / 2.0f
        secondLineY = (wMeasuredHeight + itemHeight) / 2.0f
        //初始化显示的item的position
        if (initPosition == -1) {
            initPosition = if (isLoop) {
                (items.size + 1) / 2
            } else {
                0
            }
        }
        preCurrentIndex = initPosition
    }

    /**
     * 计算最大length的Text的宽高度
     */
    private fun measureTextWidthHeight() {
        val rect = Rect()
        for (i in items.indices) {
            val s1 = obtainContentText(items[i])
            paintCenterText.getTextBounds(s1, 0, s1.length, rect)
            val textWidth = rect.width()
            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth
            }
            paintCenterText.getTextBounds("测试", 0, 2, rect)
            maxTextHeight = rect.height() + 2
        }
        itemHeight = lineSpaceMultiplier * maxTextHeight
    }

    /**
     * 滚动惯性的实现
     */
    private fun scrollBy(velocityY: Float) {
        cancelFuture()
        val command = InertiaTimerTask(this, velocityY)
        mFuture = Executors.newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(command, 0, VELOCITY_FLING.toLong(), TimeUnit.MILLISECONDS)
    }

    override fun onDraw(canvas: Canvas) {
        if (items.isEmpty()) {
            return
        }
        //可见项的数组
        val visibleItemStrings = arrayOfNulls<String>(visibleItemCount)
        //滚动的Y值高度除去每行的高度，得到滚动了多少个项，即change数
        val change = (totalScrollY / itemHeight).toInt()
        //滚动中实际的预选中的item(即经过了中间位置的item) ＝ 滑动前的位置 ＋ 滑动相对位置
        preCurrentIndex = initPosition + change % items.size
        if (!isLoop) {//不循环的情况
            if (preCurrentIndex < 0) {
                preCurrentIndex = 0
            }
            if (preCurrentIndex > items.size - 1) {
                preCurrentIndex = items.size - 1
            }
        } else {//循环
            if (preCurrentIndex < 0) {//举个例子：如果总数是5，preCurrentIndex ＝ －1，那么preCurrentIndex按循环来说，其实是0的上面，也就是4的位置
                preCurrentIndex += items.size
            }
            if (preCurrentIndex > items.size - 1) {//同理上面,自己脑补一下
                preCurrentIndex -= items.size
            }
        }
        //跟滚动流畅度有关，总滑动距离与每个item高度取余，即并不是一格格的滚动，每个item不一定滚到对应Rect里的，这个item对应格子的偏移值
        val itemHeightOffset = totalScrollY % itemHeight
        // 设置数组中每个元素的值
        var counter = 0
        while (counter < visibleItemCount) {
            var index = preCurrentIndex - (visibleItemCount / 2 - counter)//索引值，即当前在控件中间的item看作数据源的中间，计算出相对源数据源的index值
            //判断是否循环，如果是循环数据源也使用相对循环的position获取对应的item值，如果不是循环则超出数据源范围使用""空白字符串填充，在界面上形成空白无数据的item项
            when {
                isLoop -> {
                    index = getLoopMappingIndex(index)
                    visibleItemStrings[counter] = items[index].name
                }
                index < 0 -> visibleItemStrings[counter] = ""
                index > items.size - 1 -> visibleItemStrings[counter] = ""
                else -> visibleItemStrings[counter] = items[index].name
            }
            counter++
        }
        //绘制中间两条横线
        if (dividerConfig.visible) {
            val ratio = dividerConfig.ratio
            canvas.drawLine(wMeasuredWidth * ratio, firstLineY, wMeasuredWidth * (1 - ratio), firstLineY, paintIndicator)
            canvas.drawLine(wMeasuredWidth * ratio, secondLineY, wMeasuredWidth * (1 - ratio), secondLineY, paintIndicator)
        }
        if (dividerConfig.shadowVisible) {
            paintShadow.color = dividerConfig.shadowColor
            paintShadow.alpha = dividerConfig.shadowAlpha
            canvas.drawRect(0.0f, firstLineY, wMeasuredWidth.toFloat(), secondLineY, paintShadow)
        }
        counter = 0
        while (counter < visibleItemCount) {
            canvas.save()
            // 弧长 L = itemHeight * counter - itemHeightOffset
            // 求弧度 α = L / r  (弧长/半径) [0,π]
            val radian = ((itemHeight * counter - itemHeightOffset) / radius).toDouble()
            // 弧度转换成角度(把半圆以Y轴为轴心向右转90度，使其处于第一象限及第四象限
            // angle [-90°,90°]
            val angle = (90.0 - radian / Math.PI * 180.0).toFloat()//item第一项,从90度开始，逐渐递减到 -90度
            // 计算取值可能有细微偏差，保证负90°到90°以外的不绘制
            if (angle >= 90f || angle <= -90f) {
                canvas.restore()
            } else {
                //获取内容文字
                var contentText: String
                //如果是label每项都显示的模式，并且item内容不为空、label也不为空
                val tempStr = obtainContentText(visibleItemStrings[counter])
                contentText = if (!onlyShowCenterLabel && !TextUtils.isEmpty(label) && !TextUtils.isEmpty(tempStr)) {
                    tempStr + label!!
                } else {
                    tempStr
                }
                remeasureTextSize(contentText)
                //计算开始绘制的位置
                measuredCenterContentStart(contentText)
                measuredOutContentStart(contentText)
                val translateY = (radius.toDouble() - Math.cos(radian) * radius - Math.sin(radian) * maxTextHeight / 2.0).toFloat()
                //根据Math.sin(radian)来更改canvas坐标系原点，然后缩放画布，使得文字高度进行缩放，形成弧形3d视觉差
                canvas.translate(0.0f, translateY)
                canvas.scale(1.0f, Math.sin(radian).toFloat())
                if (translateY <= firstLineY && maxTextHeight + translateY >= firstLineY) {
                    // 条目经过第一条线
                    canvas.save()
                    canvas.clipRect(0f, 0f, wMeasuredWidth.toFloat(), firstLineY - translateY)
                    canvas.scale(1.0f, Math.sin(radian).toFloat() * SCALE_CONTENT)
                    canvas.drawText(contentText, drawOutContentStart.toFloat(), maxTextHeight.toFloat(), paintOuterText)
                    canvas.restore()
                    canvas.save()
                    canvas.clipRect(0f, firstLineY - translateY, wMeasuredWidth.toFloat(), itemHeight.toInt().toFloat())
                    canvas.scale(1.0f, Math.sin(radian).toFloat() * 1.0f)
                    canvas.drawText(contentText, drawCenterContentStart.toFloat(), maxTextHeight - centerContentOffset, paintCenterText)
                    canvas.restore()
                } else if (translateY <= secondLineY && maxTextHeight + translateY >= secondLineY) {
                    // 条目经过第二条线
                    canvas.save()
                    canvas.clipRect(0f, 0f, wMeasuredWidth.toFloat(), secondLineY - translateY)
                    canvas.scale(1.0f, Math.sin(radian).toFloat() * 1.0f)
                    canvas.drawText(contentText, drawCenterContentStart.toFloat(), maxTextHeight - centerContentOffset, paintCenterText)
                    canvas.restore()
                    canvas.save()
                    canvas.clipRect(0f, secondLineY - translateY, wMeasuredWidth.toFloat(), itemHeight.toInt().toFloat())
                    canvas.scale(1.0f, Math.sin(radian).toFloat() * SCALE_CONTENT)
                    canvas.drawText(contentText, drawOutContentStart.toFloat(), maxTextHeight.toFloat(), paintOuterText)
                    canvas.restore()
                } else if (translateY >= firstLineY && maxTextHeight + translateY <= secondLineY) {
                    // 中间条目
                    canvas.clipRect(0, 0, wMeasuredWidth, maxTextHeight)
                    //让文字居中
                    val Y = maxTextHeight - centerContentOffset//因为圆弧角换算的向下取值，导致角度稍微有点偏差，加上画笔的基线会偏上，因此需要偏移量修正一下
                    var i = 0
                    for (item in items) {
                        if (item.name == tempStr) {
                            selectedIndex = i
                            break
                        }
                        i++
                    }
                    if (onlyShowCenterLabel && !TextUtils.isEmpty(label)) {
                        contentText += label
                    }
                    canvas.drawText(contentText, drawCenterContentStart.toFloat(), Y, paintCenterText)
                } else {
                    // 其他条目
                    canvas.save()
                    canvas.clipRect(0, 0, wMeasuredWidth, itemHeight.toInt())
                    canvas.scale(1.0f, Math.sin(radian).toFloat() * SCALE_CONTENT)
                    canvas.drawText(contentText, drawOutContentStart.toFloat(), maxTextHeight.toFloat(), paintOuterText)
                    canvas.restore()
                }
                canvas.restore()
                paintCenterText.textSize = textSize.toFloat()
            }
            counter++
        }
    }

    /**
     * 根据文字的长度 重新设置文字的大小 让其能完全显示
     */
    private fun remeasureTextSize(contentText: String) {
        val rect = Rect()
        paintCenterText.getTextBounds(contentText, 0, contentText.length, rect)
        var width = rect.width()
        var size = textSize
        while (width > wMeasuredWidth) {
            size--
            //设置2条横线中间的文字大小
            paintCenterText.textSize = size.toFloat()
            paintCenterText.getTextBounds(contentText, 0, contentText.length, rect)
            width = rect.width()
        }
        //设置2条横线外面的文字大小
        paintOuterText.textSize = size.toFloat()
    }

    /**
     * 递归计算出对应的索引
     */
    private fun getLoopMappingIndex(index: Int): Int {
        var newIndex = index
        if (newIndex < 0) {
            newIndex += items.size
            newIndex = getLoopMappingIndex(newIndex)
        } else if (index > items.size - 1) {
            newIndex -= items.size
            newIndex = getLoopMappingIndex(newIndex)
        }
        return newIndex
    }

    /**
     * 根据传进来的对象来获取需要显示的值
     *
     * @param item 数据源的项
     * @return 对应显示的字符串
     */
    private fun obtainContentText(item: Any?): String {
        return when (item) {
            null -> ""
            is WheelItem -> item.name
            is Int -> {
                // 如果为整形则最少保留两位数.
                String.format(Locale.getDefault(), "%02d", item)
            }
            else -> item.toString()
        }
    }

    private fun obtainTextWidth(paint: Paint?, str: String?): Int {
        var iRet = 0
        if (str != null && str.isNotEmpty()) {
            val len = str.length
            val widths = FloatArray(len)
            paint!!.getTextWidths(str, widths)
            for (j in 0 until len) {
                iRet += Math.ceil(widths[j].toDouble()).toInt()
            }
        }
        return iRet
    }

    private fun measuredCenterContentStart(content: String) {
        val rect = Rect()
        paintCenterText.getTextBounds(content, 0, content.length, rect)
        when (gravity) {
            Gravity.CENTER -> {
                //显示内容居中
                drawCenterContentStart = ((wMeasuredWidth - rect.width()) * 0.5).toInt()
            }
            Gravity.LEFT -> drawCenterContentStart = 0
            Gravity.RIGHT -> {
                //添加偏移量
                drawCenterContentStart = wMeasuredWidth - rect.width() - centerContentOffset.toInt()
            }
        }
    }

    private fun measuredOutContentStart(content: String) {
        val rect = Rect()
        paintOuterText.getTextBounds(content, 0, content.length, rect)
        when (gravity) {
            Gravity.CENTER -> drawOutContentStart = ((wMeasuredWidth - rect.width()) * 0.5).toInt()
            Gravity.LEFT -> drawOutContentStart = 0
            Gravity.RIGHT -> drawOutContentStart = wMeasuredWidth - rect.width() - centerContentOffset.toInt()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        this.widthMeasureSpec = widthMeasureSpec
        remeasure()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventConsumed = gestureDetector!!.onTouchEvent(event)
        val parent = parent
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //按下
                startTime = System.currentTimeMillis()
                cancelFuture()
                previousY = event.rawY
                parent?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                //滑动中
                val dy = previousY - event.rawY
                previousY = event.rawY
                totalScrollY += dy
                // 边界处理。
                if (!isLoop) {
                    var top = -initPosition * itemHeight
                    var bottom = (items.size - 1 - initPosition) * itemHeight
                    if (totalScrollY - itemHeight * 0.25 < top) {
                        top = totalScrollY - dy
                    } else if (totalScrollY + itemHeight * 0.25 > bottom) {
                        bottom = totalScrollY - dy
                    }
                    if (totalScrollY < top) {
                        totalScrollY = top.toInt().toFloat()
                    } else if (totalScrollY > bottom) {
                        totalScrollY = bottom.toInt().toFloat()
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                //完成滑动，手指离开屏幕
                if (!eventConsumed) {//未消费掉事件
                    /*
                     * 关于弧长的计算
                     *
                     * 弧长公式： L = α*R
                     * 反余弦公式：arccos(cosα) = α
                     * 由于之前是有顺时针偏移90度，
                     * 所以实际弧度范围α2的值 ：α2 = π/2-α    （α=[0,π] α2 = [-π/2,π/2]）
                     * 根据正弦余弦转换公式 cosα = sin(π/2-α)
                     * 代入，得： cosα = sin(π/2-α) = sinα2 = (R - y) / R
                     * 所以弧长 L = arccos(cosα)*R = arccos((R - y) / R)*R
                     */
                    val y = event.y
                    val L = Math.acos(((radius - y) / radius).toDouble()) * radius
                    //item0 有一半是在不可见区域，所以需要加上 itemHeight / 2
                    val circlePosition = ((L + itemHeight / 2) / itemHeight).toInt()
                    val extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight
                    //已滑动的弧长值
                    offset = ((circlePosition - visibleItemCount / 2) * itemHeight - extraOffset).toInt()
                    if (System.currentTimeMillis() - startTime > 120) {
                        // 处理拖拽事件
                        smoothScroll(ACTION_DRAG)
                    } else {
                        // 处理条目点击事件
                        smoothScroll(ACTION_CLICK)
                    }
                }
                parent?.requestDisallowInterceptTouchEvent(false)
            }
            else -> {
                if (!eventConsumed) {
                    val y = event.y
                    val L = Math.acos(((radius - y) / radius).toDouble()) * radius
                    val circlePosition = ((L + itemHeight / 2) / itemHeight).toInt()
                    val extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight
                    offset = ((circlePosition - visibleItemCount / 2) * itemHeight - extraOffset).toInt()
                    if (System.currentTimeMillis() - startTime > 120) {
                        smoothScroll(ACTION_DRAG)
                    } else {
                        smoothScroll(ACTION_CLICK)
                    }
                }
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
        invalidate()
        return true
    }

    fun cancelFuture() {
        if (mFuture != null && !mFuture!!.isCancelled) {
            mFuture!!.cancel(true)
            mFuture = null
        }
    }

    fun itemSelectedCallback() {
        if (onItemSelectListener == null && onWheelListener == null) {
            return
        }
        postDelayed({
            if (onItemSelectListener != null) {
                onItemSelectListener!!.onSelected(selectedIndex)
            }
            if (onWheelListener != null) {
                onWheelListener!!.onSelected(true, selectedIndex, items[selectedIndex].name)
            }
        }, 200L)
    }

    /**
     * 平滑滚动的实现
     */
    fun smoothScroll(actionType: Int) {
        cancelFuture()
        if (actionType == ACTION_FLING || actionType == ACTION_DRAG) {
            offset = ((totalScrollY % itemHeight + itemHeight) % itemHeight).toInt()
            offset = if (offset.toFloat() > itemHeight / 2.0f) {
                //如果超过Item高度的一半，滚动到下一个Item去
                (itemHeight - offset.toFloat()).toInt()
            } else {
                -offset
            }
        }
        //停止的时候，位置有偏移，不是全部都能正确停止到中间位置的，这里把文字位置挪回中间去
        val command = SmoothScrollTimerTask(this, offset)
        mFuture = Executors.newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(command, 0, 10, TimeUnit.MILLISECONDS)
    }

    /**
     * 设置显示的选项个数，必须是奇数
     */
    fun setVisibleItemCount(count: Int) {
        if (count % 2 == 0) {
            throw IllegalArgumentException("must be odd")
        }
        if (count != visibleItemCount) {
            visibleItemCount = count
        }
    }

    /**
     * 设置是否禁用循环滚动
     */
    fun setCycleDisable(cycleDisable: Boolean) {
        isLoop = !cycleDisable
    }

    /**
     * 设置滚轮个数偏移量
     */
    fun setOffset(@IntRange(from = 1, to = 5) offset: Int) {
        if (offset < 1 || offset > 5) {
            throw IllegalArgumentException("must between 1 and 5")
        }
        var count = offset * 2 + 1
        count += if (offset % 2 == 0) {
            offset
        } else {
            offset - 1
        }
        setVisibleItemCount(count)
    }

    fun getSelectedIndex(): Int {
        return selectedIndex
    }

    fun setSelectedIndex(index: Int) {
        if (items.isEmpty()) {
            return
        }
        val size = items.size
        if (index == 0 || index in 1..(size - 1) && index != selectedIndex) {
            initPosition = index
            totalScrollY = 0f   // 回归顶部，不然重设索引的话位置会偏移，就会显示出不对位置的数据
            offset = 0
            invalidate()
        }
    }

    fun setItems(items: List<*>) {
        this.items.clear()
        for (item in items) {
            if (item is WheelItem) {
                this.items.add(item)
            } else if (item is CharSequence || item is Number) {
                this.items.add(StringItem(item.toString()))
            } else {
                throw IllegalArgumentException("please implements " + WheelItem::class.java.name)
            }
        }
        remeasure()
        invalidate()
    }

    fun setItems(items: List<*>, index: Int) {
        setItems(items)
        setSelectedIndex(index)
    }

    fun setItems(list: Array<String>) {
        setItems(Arrays.asList(*list))
    }

    fun setItems(list: List<String>, item: String) {
        var index = list.indexOf(item)
        if (index == -1) {
            index = 0
        }
        setItems(list, index)
    }

    fun setItems(list: Array<String>, index: Int) {
        setItems(Arrays.asList(*list), index)
    }

    fun setItems(items: Array<String>, item: String) {
        setItems(Arrays.asList(*items), item)
    }

    /**
     * 附加在右边的单位字符串
     */
    @JvmOverloads
    fun setLabel(label: String, onlyShowCenterLabel: Boolean = true) {
        this.label = label
        this.onlyShowCenterLabel = onlyShowCenterLabel
    }

    fun setGravity(gravity: Int) {
        this.gravity = gravity
    }

    fun setTextColor(@ColorInt colorNormal: Int, @ColorInt colorFocus: Int) {
        this.textColorOuter = colorNormal
        this.textColorCenter = colorFocus
        paintOuterText.color = colorNormal
        paintCenterText.color = colorFocus
    }

    fun setTextColor(@ColorInt color: Int) {
        this.textColorOuter = color
        this.textColorCenter = color
        paintOuterText.color = color
        paintCenterText.color = color
    }

    fun setTypeface(font: Typeface) {
        typeface = font
        paintOuterText.typeface = typeface
        paintCenterText.typeface = typeface
    }

    fun setTextSize(size: Float) {
        if (size > 0.0f) {
            textSize = (context.resources.displayMetrics.density * size).toInt()
            paintOuterText.textSize = textSize.toFloat()
            paintCenterText.textSize = textSize.toFloat()
        }
    }

    fun setDividerColor(@ColorInt color: Int) {
        dividerConfig.setColor(color)
        paintIndicator.color = color
    }

    fun setLineConfig(config: DividerConfig) {
        setDividerConfig(config)
    }

    fun setDividerConfig(config: DividerConfig?) {
        if (null == config) {
            dividerConfig.setVisible(false)
            dividerConfig.setShadowVisible(false)
            return
        }
        this.dividerConfig = config
        paintIndicator.color = config.color
        paintIndicator.strokeWidth = config.thick
        paintIndicator.alpha = config.alpha
        paintShadow.color = config.shadowColor
        paintShadow.alpha = config.shadowAlpha
    }

    fun setLineSpaceMultiplier(@FloatRange(from = 2.0, to = 4.0) multiplier: Float) {
        lineSpaceMultiplier = multiplier
        judgeLineSpace()
    }

    fun setPadding(padding: Int) {
        this.padding = DensityUtil.dp2px(mContext, padding.toFloat())
    }

    fun setUseWeight(useWeight: Boolean) {
        this.useWeight = useWeight
    }

    /**
     * 获取选项个数
     */
    open fun getItemCount(): Int {
        return items.size
    }

    fun setOnItemSelectListener(onItemSelectListener: WheelView.OnItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener
    }

    @Deprecated("use {@link #setOnItemSelectListener(OnItemSelectListener)} instead")
    fun setOnWheelListener(listener: WheelView.OnWheelListener) {
        onWheelListener = listener
    }

    interface OnItemSelectListener {
        /**
         * 滑动选择回调
         *
         * @param index 当前选择项的索引
         */
        fun onSelected(index: Int)
    }

    /**
     * 兼容旧版本API
     *
     * @deprecated use {@link OnItemSelectListener} instead
     */
    interface OnWheelListener {
        fun onSelected(isUserScroll: Boolean, index: Int, item: String)
    }

    /**
     * @deprecated use {@link OnItemSelectListener} instead
     */
    interface OnWheelViewListener: OnWheelListener {}

    companion object {
        val LINE_SPACE_MULTIPLIER = 2.5f
        val TEXT_PADDING = -1
        val TEXT_SIZE = 16//sp
        val TEXT_COLOR_FOCUS: Int = ColorUtil.parseColor("0XFF0288CE")
        val TEXT_COLOR_NORMAL: Int = ColorUtil.parseColor("0XFFBBBBBB")
        val DIVIDER_COLOR: Int = ColorUtil.parseColor("0XFF83CDE6")
        val DIVIDER_ALPHA = 220
        val DIVIDER_THICK = 2f//px
        val ITEM_OFF_SET = 3
        val ITEM_PADDING = 13f      // px,480X800的手机边距不能太大
        val ACTION_CLICK = 1        // 点击
        val ACTION_FLING = 2        // 滑翔
        val ACTION_DRAG = 3         // 拖拽
        val VELOCITY_FLING = 5      // 修改这个值可以改变滑行速度
        val SCALE_CONTENT = 0.8f    // 非中间文字用此控制高度，压扁形成3D错觉
    }
}