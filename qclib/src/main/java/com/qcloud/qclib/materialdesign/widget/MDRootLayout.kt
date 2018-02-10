package com.qcloud.qclib.materialdesign.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.ScrollView
import com.qcloud.qclib.R
import com.qcloud.qclib.materialdesign.enums.GravityEnum
import com.qcloud.qclib.materialdesign.enums.StackingBehavior
import com.qcloud.qclib.utils.DialogUtil

/**
 * 类说明：基于Material Design 弹窗的布局容器，所有弹窗列表的布局要以此为根布局
 * Author: Kuzan
 * Date: 2018/2/8 17:41.
 */
class MDRootLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {

    private val buttons = arrayOfNulls<MDButton>(3)

    /**控件*/
    private var titleBar: View? = null
    private var content: View? = null
    private var checkPrompt: CheckBox? = null

    private var drawTopDivider: Boolean = false
    private var drawBottomDivider: Boolean = false
    private var isStacked: Boolean = false
    private var useFullPadding: Boolean = true
    private var reducePaddingNoTitleNoButtons: Boolean = false

    private var stackBehavior = StackingBehavior.ADAPTIVE

    private var noTitlePaddingFull: Int = 0
    private var buttonPaddingFull: Int = 0
    private var buttonBarHeight: Int = 0

    private var buttonGravity = GravityEnum.START
    private var buttonHorizontalEdgeMargin: Int = 0
    private var dividerPaint: Paint = Paint()
    private var dividerWidth: Int = 0

    var maxHeight: Int = 0
    var noTitleNoPadding: Boolean = false

    var topOnScrollChangedListener: ViewTreeObserver.OnScrollChangedListener? = null
    var bottomOnScrollChangedListener: ViewTreeObserver.OnScrollChangedListener? = null

    init {
        initLayout(context, attrs, defStyleAttr)
    }

    private fun initLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val r = context.resources

        val a = context.obtainStyledAttributes(attrs, R.styleable.MDRootLayout, defStyleAttr, 0)
        reducePaddingNoTitleNoButtons = a.getBoolean(R.styleable.MDRootLayout_md_reduce_padding_no_title_no_buttons, true)
        a.recycle()

        noTitlePaddingFull = r.getDimensionPixelSize(R.dimen.padding_8)
        buttonPaddingFull = r.getDimensionPixelSize(R.dimen.padding_4)

        buttonHorizontalEdgeMargin = r.getDimensionPixelSize(R.dimen.padding_6)
        buttonBarHeight = r.getDimensionPixelSize(R.dimen.tab_height)

        dividerWidth = r.getDimensionPixelSize(R.dimen.store_width)
        dividerPaint.color = DialogUtil.resolveColor(context, R.attr.md_divider_color)
        setWillNotDraw(false)
    }

    private fun isVisible(v: View?): Boolean {
        var visible = v != null && v.visibility != View.GONE
        if (visible && v is MDButton) {
            visible = v.text.toString().trim().isNotEmpty()
        }
        return visible
    }

    private fun canScrollViewScroll(sv: ScrollView?): Boolean {
        if (sv == null) {
            return false
        }
        if (sv.childCount == 0) {
            return false
        }
        val childHeight = sv.getChildAt(0).measuredHeight
        return sv.measuredHeight - sv.paddingTop - sv.paddingBottom < childHeight
    }

    private fun canWebViewScroll(view: WebView): Boolean {
        return view.measuredHeight < view.contentHeight * view.scale
    }

    private fun canAdapterViewScroll(lv: AdapterView<*>): Boolean {
        /* Force it to layout it's children */
        if (lv.lastVisiblePosition == -1) {
            return false
        }

        /* We can scroll if the first or last item is not visible */
        val firstItemVisible = lv.firstVisiblePosition == 0
        val lastItemVisible = lv.lastVisiblePosition == lv.count - 1

        return if (firstItemVisible && lastItemVisible && lv.childCount > 0) {
            /* Or the first item's top is above or own top */
            if (lv.getChildAt(0).top < lv.paddingTop) {
                true
            } else {
                lv.getChildAt(lv.childCount - 1).bottom > lv.height - lv.paddingBottom
            }
            /* or the last item's bottom is beyond our own bottom */
        } else {
            true
        }
    }

    private fun getBottomView(viewGroup: ViewGroup?): View? {
        if (viewGroup == null || viewGroup.childCount == 0) {
            return null
        }
        var bottomView: View? = null
        for (i in viewGroup.childCount - 1 downTo 0) {
            val child = viewGroup.getChildAt(i)
            if (child.visibility == View.VISIBLE && child.bottom == viewGroup.measuredHeight) {
                bottomView = child
                break
            }
        }
        return bottomView
    }

    private fun getTopView(viewGroup: ViewGroup?): View? {
        if (viewGroup == null || viewGroup.childCount == 0) {
            return null
        }
        var topView: View? = null
        for (i in viewGroup.childCount - 1 downTo 0) {
            val child = viewGroup.getChildAt(i)
            if (child.visibility == View.VISIBLE && child.top == 0) {
                topView = child
                break
            }
        }
        return topView
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            when (v.id) {
                R.id.md_titleFrame -> titleBar = v
                R.id.md_buttonDefaultNeutral -> buttons[INDEX_NEUTRAL] = v as MDButton
                R.id.md_buttonDefaultNegative -> buttons[INDEX_NEGATIVE] = v as MDButton
                R.id.md_buttonDefaultPositive -> buttons[INDEX_POSITIVE] = v as MDButton
                R.id.md_promptCheckbox -> checkPrompt = v as CheckBox
                else -> content = v
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        var height = View.MeasureSpec.getSize(heightMeasureSpec)

        if (height > maxHeight) {
            height = maxHeight
        }

        useFullPadding = true
        var hasButtons = false

        val stacked: Boolean
        if (stackBehavior === StackingBehavior.ALWAYS) {
            stacked = true
        } else if (stackBehavior === StackingBehavior.NEVER) {
            stacked = false
        } else {
            var buttonsWidth = 0
            for (button in buttons) {
                if (button != null && isVisible(button)) {
                    button.setStacked(false, false)
                    measureChild(button, widthMeasureSpec, heightMeasureSpec)
                    buttonsWidth += button.measuredWidth
                    hasButtons = true
                }
            }

            val buttonBarPadding = context.resources.getDimensionPixelSize(R.dimen.padding_6)
            val buttonFrameWidth = width - 2 * buttonBarPadding
            stacked = buttonsWidth > buttonFrameWidth
        }

        var stackedHeight = 0
        isStacked = stacked
        if (stacked) {
            for (button in buttons) {
                if (button != null && isVisible(button)) {
                    button.setStacked(true, false)
                    measureChild(button, widthMeasureSpec, heightMeasureSpec)
                    stackedHeight += button.measuredHeight
                    hasButtons = true
                }
            }
        }

        var availableHeight = height
        var fullPadding = 0
        var minPadding = 0
        if (hasButtons) {
            if (isStacked) {
                availableHeight -= stackedHeight
                fullPadding += 2 * buttonPaddingFull
                minPadding += 2 * buttonPaddingFull
            } else {
                availableHeight -= buttonBarHeight
                fullPadding += 2 * buttonPaddingFull
                /* No minPadding */
            }
        } else {
            /* Content has 8dp, we add 16dp and get 24dp, the frame margin */
            fullPadding += 2 * buttonPaddingFull
        }

        if (isVisible(titleBar)) {
            titleBar!!.measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED)
            availableHeight -= titleBar!!.measuredHeight
        } else if (!noTitleNoPadding) {
            fullPadding += noTitlePaddingFull
        }

        if (isVisible(content)) {
            content!!.measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(availableHeight - minPadding, View.MeasureSpec.AT_MOST))

            if (content!!.measuredHeight <= availableHeight - fullPadding) {
                if (!reducePaddingNoTitleNoButtons || isVisible(titleBar) || hasButtons) {
                    useFullPadding = true
                    availableHeight -= content!!.measuredHeight + fullPadding
                } else {
                    useFullPadding = false
                    availableHeight -= content!!.measuredHeight + minPadding
                }
            } else {
                useFullPadding = false
                availableHeight = 0
            }
        }

        setMeasuredDimension(width, height - availableHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (content != null) {
            if (drawTopDivider) {
                val y = content!!.top
                canvas.drawRect(0f, (y - dividerWidth).toFloat(), measuredWidth.toFloat(), y.toFloat(), dividerPaint)
            }

            if (drawBottomDivider) {
                var y = content!!.bottom
                if (checkPrompt != null && checkPrompt!!.visibility == View.GONE) {
                    y = checkPrompt!!.top
                }
                canvas.drawRect(0f, y.toFloat(), measuredWidth.toFloat(), (y + dividerWidth).toFloat(), dividerPaint)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var t = top
        var b = bottom
        if (isVisible(titleBar)) {
            val height = titleBar!!.measuredHeight
            titleBar!!.layout(left, t, right, t + height)
            t += height
        } else if (!noTitleNoPadding && useFullPadding) {
            t += noTitlePaddingFull
        }

        if (isVisible(content)) {
            content!!.layout(left, t, right, t + content!!.measuredHeight)
        }

        if (isStacked) {
            b -= buttonPaddingFull
            for (mButton in buttons) {
                if (isVisible(mButton)) {
                    mButton!!.layout(left, b - mButton.measuredHeight, right, b)
                    b -= mButton.measuredHeight
                }
            }
        } else {
            val barTop: Int
            var barBottom = b
            if (useFullPadding) {
                barBottom -= buttonPaddingFull
            }
            barTop = barBottom - buttonBarHeight

            var offset = buttonHorizontalEdgeMargin

            /* Used with CENTER gravity */
            var neutralLeft = -1
            var neutralRight = -1

            if (isVisible(buttons[INDEX_POSITIVE])) {
                val bl: Int
                val br: Int
                if (buttonGravity === GravityEnum.END) {
                    bl = left + offset
                    br = bl + buttons[INDEX_POSITIVE]!!.measuredWidth
                } else {
                    /* START || CENTER */
                    br = right - offset
                    bl = br - buttons[INDEX_POSITIVE]!!.measuredWidth
                    neutralRight = bl
                }
                buttons[INDEX_POSITIVE]!!.layout(bl, barTop, br, barBottom)
                offset += buttons[INDEX_POSITIVE]!!.measuredWidth
            }

            if (isVisible(buttons[INDEX_NEGATIVE])) {
                val bl: Int
                val br: Int
                if (buttonGravity === GravityEnum.END) {
                    bl = left + offset
                    br = bl + buttons[INDEX_NEGATIVE]!!.measuredWidth
                } else if (buttonGravity === GravityEnum.START) {
                    br = right - offset
                    bl = br - buttons[INDEX_NEGATIVE]!!.measuredWidth
                } else {
                    /* CENTER */
                    bl = left + buttonHorizontalEdgeMargin
                    br = bl + buttons[INDEX_NEGATIVE]!!.measuredWidth
                    neutralLeft = br
                }
                buttons[INDEX_NEGATIVE]!!.layout(bl, barTop, br, barBottom)
            }

            if (isVisible(buttons[INDEX_NEUTRAL])) {
                val bl: Int
                val br: Int
                when {
                    buttonGravity === GravityEnum.END -> {
                        br = right - buttonHorizontalEdgeMargin
                        bl = br - buttons[INDEX_NEUTRAL]!!.measuredWidth
                    }
                    buttonGravity === GravityEnum.START -> {
                        bl = left + buttonHorizontalEdgeMargin
                        br = bl + buttons[INDEX_NEUTRAL]!!.measuredWidth
                    }
                    else -> {
                        /* CENTER */
                        if (neutralLeft == -1 && neutralRight != -1) {
                            neutralLeft = neutralRight - buttons[INDEX_NEUTRAL]!!.measuredWidth
                        } else if (neutralRight == -1 && neutralLeft != -1) {
                            neutralRight = neutralLeft + buttons[INDEX_NEUTRAL]!!.measuredWidth
                        } else if (neutralRight == -1) {
                            neutralLeft = (right - left) / 2 - buttons[INDEX_NEUTRAL]!!.measuredWidth / 2
                            neutralRight = neutralLeft + buttons[INDEX_NEUTRAL]!!.measuredWidth
                        }
                        bl = neutralLeft
                        br = neutralRight
                    }
                }

                buttons[INDEX_NEUTRAL]!!.layout(bl, barTop, br, barBottom)
            }
        }

        setUpDividersVisibility(content, true, true)
    }

    fun setStackingBehavior(behavior: StackingBehavior) {
        stackBehavior = behavior
        invalidate()
    }

    fun setDividerColor(color: Int) {
        dividerPaint.color = color
        invalidate()
    }

    fun setButtonGravity(gravity: GravityEnum) {
        buttonGravity = gravity
        invertGravityIfNecessary()
    }

    private fun invertGravityIfNecessary() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return
        }
        val config = resources.configuration
        if (config.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            when (buttonGravity) {
                GravityEnum.START -> buttonGravity = GravityEnum.END
                GravityEnum.END -> buttonGravity = GravityEnum.START
            }
        }
    }

    fun setButtonStackedGravity(gravity: GravityEnum) {
        for (mButton in buttons) if (mButton != null) {
            mButton.stackedGravity = gravity
        }
    }

    private fun setUpDividersVisibility(view: View?, setForTop: Boolean, setForBottom: Boolean) {
        if (view == null) {
            return
        }
        if (view is ScrollView) {
            if (canScrollViewScroll(view)) {
                addScrollListener(view, setForTop, setForBottom)
            } else {
                if (setForTop) {
                    drawTopDivider = false
                }
                if (setForBottom) {
                    drawBottomDivider = false
                }
            }
        } else if (view is AdapterView<*>) {
            val sv = view as AdapterView<*>?
            if (canAdapterViewScroll(sv!!)) {
                addScrollListener(sv, setForTop, setForBottom)
            } else {
                if (setForTop) {
                    drawTopDivider = false
                }
                if (setForBottom) {
                    drawBottomDivider = false
                }
            }
        } else if (view is WebView) {
            view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (view.measuredHeight != 0) {
                        if (!canWebViewScroll(view)) {
                            if (setForTop) {
                                drawTopDivider = false
                            }
                            if (setForBottom) {
                                drawBottomDivider = false
                            }
                        } else {
                            addScrollListener(view, setForTop, setForBottom)
                        }
                        view.viewTreeObserver.removeOnPreDrawListener(this)
                    }
                    return true
                }
            })
        } else if (view is RecyclerView) {
            val canScroll = canRecyclerViewScroll(view as RecyclerView?)
            if (setForTop) {
                drawTopDivider = canScroll
            }
            if (setForBottom) {
                drawBottomDivider = canScroll
            }
            if (canScroll) {
                addScrollListener(view, setForTop, setForBottom)
            }
        } else if (view is ViewGroup) {
            val topView = getTopView(view as ViewGroup?)
            setUpDividersVisibility(topView, setForTop, setForBottom)
            val bottomView = getBottomView(view as ViewGroup?)
            if (bottomView !== topView) {
                setUpDividersVisibility(bottomView, false, true)
            }
        }
    }

    private fun addScrollListener(vg: ViewGroup, setForTop: Boolean, setForBottom: Boolean) {
        if (!setForBottom && topOnScrollChangedListener == null || setForBottom && bottomOnScrollChangedListener == null) {
            if (vg is RecyclerView) {
                val scrollListener = object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        var hasButtons = false
                        for (button in buttons) {
                            if (button != null && button.visibility != View.GONE) {
                                hasButtons = true
                                break
                            }
                        }
                        invalidateDividersForScrollingView(vg, setForTop, setForBottom, hasButtons)
                        invalidate()
                    }
                }
                vg.addOnScrollListener(scrollListener)
                scrollListener.onScrolled(vg, 0, 0)
            } else {
                val onScrollChangedListener = ViewTreeObserver.OnScrollChangedListener {
                    var hasButtons = false
                    for (button in buttons) {
                        if (button != null && button.visibility != View.GONE) {
                            hasButtons = true
                            break
                        }
                    }
                    if (vg is WebView) {
                        invalidateDividersForWebView(vg, setForTop, setForBottom, hasButtons)
                    } else {
                        invalidateDividersForScrollingView(vg, setForTop, setForBottom, hasButtons)
                    }
                    invalidate()
                }
                if (!setForBottom) {
                    topOnScrollChangedListener = onScrollChangedListener
                    vg.viewTreeObserver.addOnScrollChangedListener(topOnScrollChangedListener)
                } else {
                    bottomOnScrollChangedListener = onScrollChangedListener
                    vg.viewTreeObserver.addOnScrollChangedListener(bottomOnScrollChangedListener)
                }
                onScrollChangedListener.onScrollChanged()
            }
        }
    }

    private fun invalidateDividersForScrollingView(view: ViewGroup, setForTop: Boolean, setForBottom: Boolean, hasButtons: Boolean) {
        if (setForTop && view.childCount > 0) {
            drawTopDivider = (titleBar != null
                    && titleBar!!.visibility != View.GONE
                    && view.scrollY + view.paddingTop > view.getChildAt(0).top)
        }
        if (setForBottom && view.childCount > 0) {
            drawBottomDivider = hasButtons && view.scrollY + view.height - view.paddingBottom < view.getChildAt(view.childCount - 1).bottom
        }
    }

    private fun invalidateDividersForWebView(view: WebView, setForTop: Boolean, setForBottom: Boolean, hasButtons: Boolean) {
        if (setForTop) {
            drawTopDivider = (titleBar != null
                    && titleBar!!.visibility != View.GONE
                    && view.scrollY + view.paddingTop > 0)
        }
        if (setForBottom) {
            drawBottomDivider = hasButtons && view.scrollY + view.measuredHeight - view.paddingBottom < view.contentHeight * view.scale
        }
    }

    companion object {
        private const val INDEX_NEUTRAL = 0
        private const val INDEX_NEGATIVE = 1
        private const val INDEX_POSITIVE = 2

        fun canRecyclerViewScroll(view: RecyclerView?): Boolean {
            return (view != null
                    && view.layoutManager != null
                    && view.layoutManager.canScrollVertically())
        }
    }
}