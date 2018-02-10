package com.qcloud.qclib.widget.customview.wheelview

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.*
import android.support.annotation.IntRange
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.qcloud.qclib.R
import com.qcloud.qclib.base.BasePopupWindow
import com.qcloud.qclib.utils.DateUtil
import com.qcloud.qclib.widget.customview.wheelview.entity.DividerConfig
import java.io.Serializable
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

/**
 * 类说明：日期时间选择器，可同时选中日期及时间
 * Author: Kuzan
 * Date: 2018/1/19 17:18.
 */
open class DateTimePicker @JvmOverloads constructor(
        context: Context,
        @DateMode val dateMode: Int = YEAR_MONTH_DAY,
        @TimeMode val timeMode: Int = HOUR_24): BasePopupWindow(context), View.OnClickListener {

    private val TAG = "DateTimePicker"

    private val mBtnCancel = mView.findViewById<TextView>(R.id.btn_cancel)
    private val mBtnFinish = mView.findViewById<TextView>(R.id.btn_finish)
    private val mTvTitle = mView.findViewById<TextView>(R.id.tv_title)
    private val mLayoutTitle = mView.findViewById<RelativeLayout>(R.id.layout_title)
    private val mLine = mView.findViewById<View>(R.id.line)
    private val mWheelYear = mView.findViewById<WheelView>(R.id.wheel_year)
    private val mTvYear = mView.findViewById<TextView>(R.id.tv_year)
    private val mLayoutYear = mView.findViewById<LinearLayout>(R.id.layout_year)
    private val mWheelMonth = mView.findViewById<WheelView>(R.id.wheel_month)
    private val mTvMonth = mView.findViewById<TextView>(R.id.tv_month)
    private val mLayoutMonth = mView.findViewById<LinearLayout>(R.id.layout_month)
    private val mWheelDay = mView.findViewById<WheelView>(R.id.wheel_day)
    private val mTvDay = mView.findViewById<TextView>(R.id.tv_day)
    private val mLayoutDay = mView.findViewById<LinearLayout>(R.id.layout_day)
    private val mWheelHour = mView.findViewById<WheelView>(R.id.wheel_hour)
    private val mTvHour = mView.findViewById<TextView>(R.id.tv_hour)
    private val mLayoutHour = mView.findViewById<LinearLayout>(R.id.layout_hour)
    private val mWheelMinute = mView.findViewById<WheelView>(R.id.wheel_minute)
    private val mTvMinute = mView.findViewById<TextView>(R.id.tv_minute)
    private val mLayoutMinute = mView.findViewById<LinearLayout>(R.id.layout_minute)

    /**年*/
    private val years: MutableList<String> = ArrayList()
    /**月*/
    private val months: MutableList<String> = ArrayList()
    /**日*/
    private val days: MutableList<String> = ArrayList()
    /**时*/
    private val hours: MutableList<String> = ArrayList()
    /**分*/
    private val minutes: MutableList<String> = ArrayList()

    var selectedYearIndex = 0
    var selectedMonthIndex = 0
    var selectedDayIndex = 0

    val selectedYear: String
        get() {
            if (dateMode == YEAR_MONTH_DAY || dateMode == YEAR_MONTH) {
                if (years.size <= selectedYearIndex) {
                    selectedYearIndex = years.size - 1
                }
                return years[selectedYearIndex]
            }
            return ""
        }

    val selectedMonth: String
        get() {
            if (dateMode != NONE) {
                if (months.size <= selectedMonthIndex) {
                    selectedMonthIndex = months.size - 1
                }
                return months[selectedMonthIndex]
            }
            return ""
        }

    val selectedDay: String
        get() {
            if (dateMode == YEAR_MONTH_DAY || dateMode == MONTH_DAY) {
                if (days.size <= selectedDayIndex) {
                    selectedDayIndex = days.size - 1
                }
                return days[selectedDayIndex]
            }
            return ""
        }

    private var selectedHour = ""
    private var selectedMinute = ""

    private var onWheelListener: OnWheelListener? = null
    private var onDateTimePickListener: OnDateTimePickListener? = null

    private var startYear = 2010
    private var startMonth = 1
    private var startDay = 1
    private var endYear = 2050
    private var endMonth = 12
    private var endDay = 31
    private var startHour: Int = 0
    private var startMinute = 0
    private var endHour: Int = 0
    private var endMinute = 59
    private var textSize = WheelView.TEXT_SIZE
    private var useWeight = false
    private var resetWhileWheel = true

    @Suppress("DEPRECATED_JAVA_ANNOTATION")
    @IntDef(value = [(NONE.toLong()), (YEAR_MONTH_DAY.toLong()), (YEAR_MONTH.toLong()), (MONTH_DAY.toLong())])
    @Retention(RetentionPolicy.SOURCE)
    annotation class DateMode

    @Suppress("DEPRECATED_JAVA_ANNOTATION")
    @IntDef(value = [(NONE.toLong()), (HOUR_24.toLong()), (HOUR_12.toLong())])
    @Retention(RetentionPolicy.SOURCE)
    annotation class TimeMode

    init {
        if (dateMode == NONE || timeMode == NONE) {
            throw IllegalArgumentException("The modes are NONE at the same time")
        }
        if (dateMode == YEAR_MONTH_DAY && timeMode != NONE) {
            textSize = 12
        }
        // 根据时间模式初始化小时范围
        if (timeMode == HOUR_12) {
            startHour = 1
            endHour = 12
        } else {
            startHour = 0
            endHour = 23
        }
        initWheelView()
    }

    override val viewId: Int
        get() = R.layout.pop_date_picker
    override val animId: Int
        get() = R.style.AnimationPopupWindow_bottom_to_up

    override fun initPop() {
        super.initPop()
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun initAfterViews() {
        initView()
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        setPopWindowBgAlpha(1.0f)
    }

    /**
     * 初始化控件
     * */
    private fun initView() {
        mLayoutYear.visibility = View.GONE
        mLayoutMonth.visibility = View.GONE
        mLayoutDay.visibility = View.GONE
        mLayoutHour.visibility = View.GONE
        mLayoutMinute.visibility = View.GONE

        mBtnCancel.setOnClickListener(this)
        mBtnFinish.setOnClickListener(this)
    }

    /**
     * 初始化滚动图
     */
    private fun initWheelView() {
        // 如果未设置默认项，则需要在此初始化数据
        if ((dateMode == YEAR_MONTH_DAY || dateMode == YEAR_MONTH) && years.size == 0) {
            Log.v(TAG, "init years before make view")
            initYearData()
        }
        if (dateMode != NONE && months.size == 0) {
            Log.v(TAG, "init months before make view")
            val yearIndex = DateUtil.trimZero(selectedYear)
            changeMonthData(yearIndex)
        }
        if ((dateMode == YEAR_MONTH_DAY || dateMode == MONTH_DAY) && days.size == 0) {
            Log.v(TAG, "init days before make view")
            val yearIndex: Int = if (dateMode == YEAR_MONTH_DAY) {
                DateUtil.trimZero(selectedYear)
            } else {
                Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR)
            }
            val monthIndex = DateUtil.trimZero(selectedMonth)
            changeDayData(yearIndex, monthIndex)
        }
        if (timeMode != NONE && hours.size == 0) {
            Log.v(TAG, "init hours before make view")
            initHourData()
        }
        if (timeMode != NONE && minutes.size == 0) {
            Log.v(TAG, "init minutes before make view")
            changeMinuteData(DateUtil.trimZero(selectedHour))
        }

        mWheelYear.setTextSize(textSize.toFloat())
        mWheelMonth.setTextSize(textSize.toFloat())
        mWheelDay.setTextSize(textSize.toFloat())
        mWheelHour.setTextSize(textSize.toFloat())
        mWheelMinute.setTextSize(textSize.toFloat())

        mWheelYear.setUseWeight(useWeight)
        mWheelMonth.setUseWeight(useWeight)
        mWheelDay.setUseWeight(useWeight)
        mWheelHour.setUseWeight(useWeight)
        mWheelMinute.setUseWeight(useWeight)

        // 初始化年
        if (dateMode == YEAR_MONTH_DAY || dateMode == YEAR_MONTH) {
            mLayoutYear.visibility = View.VISIBLE
            mWheelYear.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
            mWheelYear.setItems(years, selectedYearIndex)
            mWheelYear.setOnItemSelectListener(object : WheelView.OnItemSelectListener {
                override fun onSelected(index: Int) {
                    selectedYearIndex = index
                    val selectedYearStr = years[selectedYearIndex]
                    onWheelListener?.onYearWheeled(selectedYearIndex, selectedYearStr)
                    Log.v(TAG, "change months after year wheeled")
                    if (resetWhileWheel) {
                        selectedMonthIndex = 0//重置月份索引
                        selectedDayIndex = 0//重置日子索引
                    }
                    //需要根据年份及月份动态计算天数
                    val selectedYear = DateUtil.trimZero(selectedYearStr)
                    changeMonthData(selectedYear)
                    mWheelMonth.setItems(months, selectedMonthIndex)
                    onWheelListener?.onMonthWheeled(selectedMonthIndex, months[selectedMonthIndex])
                    changeDayData(selectedYear, DateUtil.trimZero(months[selectedMonthIndex]))
                    mWheelDay.setItems(days, selectedDayIndex)
                    onWheelListener?.onDayWheeled(selectedDayIndex, days[selectedDayIndex])
                }
            })
        }

        // 初始化月
        if (dateMode != NONE) {
            mLayoutMonth.visibility = View.VISIBLE
            mWheelMonth.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
            mWheelMonth.setItems(months, selectedMonthIndex)
            mWheelMonth.setOnItemSelectListener(object : WheelView.OnItemSelectListener {
                override fun onSelected(index: Int) {
                    selectedMonthIndex = index
                    val selectedMonthStr = months[selectedMonthIndex]
                    onWheelListener?.onMonthWheeled(selectedMonthIndex, selectedMonthStr)
                    if (dateMode == YEAR_MONTH_DAY || dateMode == MONTH_DAY) {
                        Log.v(TAG, "change days after month wheeled")
                        if (resetWhileWheel) {
                            selectedDayIndex = 0//重置日子索引
                        }
                        val yearIndex: Int = if (dateMode == YEAR_MONTH_DAY) {
                            DateUtil.trimZero(selectedYear)
                        } else {
                            Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR)
                        }
                        changeDayData(yearIndex, DateUtil.trimZero(selectedMonthStr))
                        mWheelDay.setItems(days, selectedDayIndex)
                        onWheelListener?.onDayWheeled(selectedDayIndex, days[selectedDayIndex])
                    }
                }
            })
        }

        // 初始化日
        if (dateMode == YEAR_MONTH_DAY || dateMode == MONTH_DAY) {
            mLayoutDay.visibility = View.VISIBLE
            mWheelDay.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
            mWheelDay.setItems(days, selectedDayIndex)
            mWheelDay.setOnItemSelectListener(object : WheelView.OnItemSelectListener {
                override fun onSelected(index: Int) {
                    selectedDayIndex = index
                    onWheelListener?.onDayWheeled(selectedDayIndex, days[selectedDayIndex])
                }
            })
        }

        // 初始化小时
        if (timeMode != NONE) {
            mLayoutHour.visibility = View.VISIBLE
            mWheelHour.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
            mWheelHour.setItems(hours, selectedHour)
            mWheelHour.setOnItemSelectListener(object : WheelView.OnItemSelectListener {
                override fun onSelected(index: Int) {
                    selectedHour = hours[index]
                    if (onWheelListener != null) {
                        onWheelListener!!.onHourWheeled(index, selectedHour)
                    }
                    Log.v(TAG, "change minutes after hour wheeled")
                    changeMinuteData(DateUtil.trimZero(selectedHour))
                    mWheelMinute.setItems(minutes, selectedMinute)
                }
            })

            // 初始化分钟
            mLayoutMinute.visibility = View.VISIBLE
            mWheelMinute.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
            mWheelMinute.setItems(minutes, selectedMinute)
            mWheelMinute.setOnItemSelectListener(object : WheelView.OnItemSelectListener {
                override fun onSelected(index: Int) {
                    selectedMinute = minutes[index]
                    onWheelListener?.onMinuteWheeled(index, selectedMinute)
                }
            })
        }
    }

    override fun onClick(p0: View) {
        if (p0.id == R.id.btn_finish) {
            onFinishClick()
            dismiss()
        } else {
            dismiss()
        }
    }

    @SuppressLint("SwitchIntDef")
    private fun onFinishClick() {
        if (onDateTimePickListener == null) {
            return
        }
        val year = selectedYear
        val month = selectedMonth
        val day = selectedDay
        val hour = getSelectedHour()
        val minute = getSelectedMinute()

        when (dateMode) {
            YEAR_MONTH_DAY -> {
                (onDateTimePickListener as OnYearMonthDayTimePickListener).onDateTimePicked(year, month, day, hour, minute)
            }
            YEAR_MONTH -> {
                (onDateTimePickListener as OnYearMonthTimePickListener).onDateTimePicked(year, month, hour, minute)
            }
            MONTH_DAY -> {
                (onDateTimePickListener as OnMonthDayTimePickListener).onDateTimePicked(month, day, hour, minute)
            }
            NONE -> {
                (onDateTimePickListener as OnTimePickListener).onDateTimePicked(hour, minute)
            }
        }
    }

    /** 可用于设置每项的高度，范围为2-4 */
    fun setLineSpaceMultiplier(@FloatRange(from = 2.0, to = 4.0) multiplier: Float) {
        mWheelYear?.setLineSpaceMultiplier(multiplier)
        mWheelMonth?.setLineSpaceMultiplier(multiplier)
        mWheelDay?.setLineSpaceMultiplier(multiplier)
        mWheelHour?.setLineSpaceMultiplier(multiplier)
        mWheelMinute?.setLineSpaceMultiplier(multiplier)
    }

    /** 可用于设置每项的宽度，单位为dp */
    fun setPadding(padding: Int) {
        mWheelYear?.setPadding(padding)
        mWheelMonth?.setPadding(padding)
        mWheelDay?.setPadding(padding)
        mWheelHour?.setPadding(padding)
        mWheelMinute?.setPadding(padding)
    }

    /** 设置文字大小 */
    fun setTextSize(textSize: Float) {
        mWheelYear?.setTextSize(textSize)
        mWheelMonth?.setTextSize(textSize)
        mWheelDay?.setTextSize(textSize)
        mWheelHour?.setTextSize(textSize)
        mWheelMinute?.setTextSize(textSize)
    }

    /** 设置文字颜色  */
    fun setTextColor(@ColorInt textColorFocus: Int, @ColorInt textColorNormal: Int) {
        mWheelYear?.setTextColor(textColorNormal, textColorFocus)
        mWheelMonth?.setTextColor(textColorNormal, textColorFocus)
        mWheelDay?.setTextColor(textColorNormal, textColorFocus)
        mWheelHour?.setTextColor(textColorNormal, textColorFocus)
        mWheelMinute?.setTextColor(textColorNormal, textColorFocus)
    }

    /** 设置文字颜色  */
    fun setTextColor(@ColorInt textColor: Int) {
        mWheelYear?.setTextColor(textColor)
        mWheelMonth?.setTextColor(textColor)
        mWheelDay?.setTextColor(textColor)
        mWheelHour?.setTextColor(textColor)
        mWheelMinute?.setTextColor(textColor)
    }

    /** 设置分隔线配置项，设置null将隐藏分割线及阴影  */
    fun setDividerConfig(config: DividerConfig? = null) {
        var newConfig = config
        if (config == null) {
            newConfig = DividerConfig()
            newConfig.setVisible(false)
            newConfig.setShadowVisible(false)
        }
        mWheelYear?.setDividerConfig(newConfig)
        mWheelMonth?.setDividerConfig(newConfig)
        mWheelDay?.setDividerConfig(newConfig)
        mWheelHour?.setDividerConfig(newConfig)
        mWheelMinute?.setDividerConfig(newConfig)
    }

    /**
     * 设置选项偏移量，可用来要设置显示的条目数，范围为1-5。
     * 1显示3条、2显示5条、3显示7条……
     */
    fun setOffset(@IntRange(from = 1, to = 5) offset: Int) {
        mWheelYear?.setOffset(offset)
        mWheelMonth?.setOffset(offset)
        mWheelDay?.setOffset(offset)
        mWheelHour?.setOffset(offset)
        mWheelMinute?.setOffset(offset)
    }

    /** 设置是否禁用循环  */
    fun setCycleDisable(cycleDisable: Boolean) {
        mWheelYear?.setCycleDisable(cycleDisable)
        mWheelMonth?.setCycleDisable(cycleDisable)
        mWheelDay?.setCycleDisable(cycleDisable)
        mWheelHour?.setCycleDisable(cycleDisable)
        mWheelMinute?.setCycleDisable(cycleDisable)
    }

    /** 设置顶部标题栏背景颜色  */
    fun setTopBackgroundColor(@ColorInt topBackgroundColor: Int) {
        mLayoutTitle?.setBackgroundColor(topBackgroundColor)
    }

    /** 设置顶部标题栏背景颜色  */
    fun setTopBackgroundResource(topBackgroundRes: Int) {
        mLayoutTitle?.setBackgroundResource(topBackgroundRes)
    }

    /** 设置顶部标题栏下划线是否显示  */
    fun setTopLineVisible(topLineVisible: Boolean) {
        if (mLine != null) {
            mLine.visibility = if (topLineVisible) View.VISIBLE else View.GONE
        }
    }

    /** 设置顶部标题栏下划线颜色  */
    fun setTopLineColor(@ColorInt topLineColor: Int) {
        mLine?.setBackgroundColor(topLineColor)
    }

    /** 设置顶部标题栏取消按钮是否显示  */
    fun setCancelVisible(cancelVisible: Boolean) {
        if (mBtnCancel != null) {
            mBtnCancel.visibility = if (cancelVisible) View.VISIBLE else View.GONE
        }
    }

    /**
     * 设置顶部标题栏取消按钮文字
     */
    fun setCancelText(cancelText: CharSequence) {
        if (mBtnCancel != null) {
            mBtnCancel.text = cancelText
        }
    }

    /** 设置顶部标题栏取消按钮文字  */
    fun setCancelText(@StringRes textRes: Int) {
        setCancelText(mContext.resources.getString(textRes))
    }

    /** 设置顶部标题栏取消按钮文字颜色  */
    fun setCancelTextColor(@ColorInt cancelTextColor: Int) {
        mBtnCancel?.setTextColor(cancelTextColor)
    }

    /** 设置顶部标题栏取消按钮文字大小（单位为sp）  */
    fun setCancelTextSize(@IntRange(from = 10, to = 40) cancelTextSize: Int) {
        if (mBtnCancel != null) {
            mBtnCancel.textSize = cancelTextSize.toFloat()
        }
    }

    /** 设置顶部标题栏确定按钮文字  */
    fun setFinishText(submitText: CharSequence) {
        if (mBtnFinish != null) {
            mBtnFinish.text = submitText
        }
    }

    /** 设置顶部标题栏确定按钮文字  */
    fun setFinishText(@StringRes textRes: Int) {
        setFinishText(mContext.resources.getString(textRes))
    }

    /** 设置顶部标题栏确定按钮文字颜色  */
    fun setFinishTextColor(@ColorInt submitTextColor: Int) {
        mBtnFinish?.setTextColor(submitTextColor)
    }

    /** 设置顶部标题栏确定按钮文字大小（单位为sp）  */
    fun setFinishTextSize(@IntRange(from = 10, to = 40) submitTextSize: Int) {
        if (mBtnFinish != null) {
            mBtnFinish.textSize = submitTextSize.toFloat()
        }
    }

    /** 设置顶部标题栏标题文字  */
    fun setTitleText(titleText: CharSequence) {
        if (mTvTitle != null) {
            mTvTitle.text = titleText
        }
    }

    /** 设置顶部标题栏标题文字  */
    fun setTitleText(@StringRes textRes: Int) {
        setTitleText(mContext.resources.getString(textRes))
    }

    /** 设置顶部标题栏标题文字颜色  */
    fun setTitleTextColor(@ColorInt titleTextColor: Int) {
        mTvTitle?.setTextColor(titleTextColor)
    }

    /** 设置顶部标题栏标题文字大小（单位为sp）  */
    fun setTitleTextSize(@IntRange(from = 10, to = 40) titleTextSize: Int) {
        if (mTvTitle != null) {
            mTvTitle.textSize = titleTextSize.toFloat()
        }
    }

    /** 是否使用比重来平分布局 */
    fun setUseWeight(useWeight: Boolean) {
        this.useWeight = useWeight
    }

    /** 滚动时是否重置下一级的索引 */
    fun setResetWhileWheel(resetWhileWheel: Boolean) {
        this.resetWhileWheel = resetWhileWheel
    }

    /** 设置年份范围 */
    @Deprecated("use {@link #setDateRangeStart(int, int, int)}\n" +
            "      and {@link #setDateRangeEnd(int, int, int)} or  instead")
    open fun setRange(startYear: Int, endYear: Int) {
        if (dateMode == NONE) {
            throw IllegalArgumentException("Date mode invalid")
        }
        this.startYear = startYear
        this.endYear = endYear
        initYearData()
    }

    /**
     * 设置范围：开始的年月日
     */
    open fun setDateRangeStart(startYear: Int, startMonth: Int, startDay: Int) {
        if (dateMode == NONE) {
            throw IllegalArgumentException("Date mode invalid")
        }
        this.startYear = startYear
        this.startMonth = startMonth
        this.startDay = startDay
        initYearData()
    }

    /**
     * 设置范围：结束的年月日
     */
    open fun setDateRangeEnd(endYear: Int, endMonth: Int, endDay: Int) {
        if (dateMode == NONE) {
            throw IllegalArgumentException("Date mode invalid")
        }
        this.endYear = endYear
        this.endMonth = endMonth
        this.endDay = endDay
        initYearData()
    }

    /**
     * 设置范围：开始的年月日
     */
    open fun setDateRangeStart(startYearOrMonth: Int, startMonthOrDay: Int) {
        if (dateMode == NONE) {
            throw IllegalArgumentException("Date mode invalid")
        }
        if (dateMode == YEAR_MONTH_DAY) {
            throw IllegalArgumentException("Not support year/month/day mode")
        }
        if (dateMode == YEAR_MONTH) {
            this.startYear = startYearOrMonth
            this.startMonth = startMonthOrDay
        } else if (dateMode == MONTH_DAY) {
            val year = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR)
            endYear = year
            startYear = endYear
            this.startMonth = startYearOrMonth
            this.startDay = startMonthOrDay
        }
        initYearData()
    }

    /**
     * 设置范围：结束的年月日
     */
    open fun setDateRangeEnd(endYearOrMonth: Int, endMonthOrDay: Int) {
        if (dateMode == NONE) {
            throw IllegalArgumentException("Date mode invalid")
        }
        if (dateMode == YEAR_MONTH_DAY) {
            throw IllegalArgumentException("Not support year/month/day mode")
        }
        if (dateMode == YEAR_MONTH) {
            this.endYear = endYearOrMonth
            this.endMonth = endMonthOrDay
        } else if (dateMode == MONTH_DAY) {
            this.endMonth = endYearOrMonth
            this.endDay = endMonthOrDay
        }
        initYearData()
    }

    /**
     * 设置范围：开始的时分
     */
    open fun setTimeRangeStart(startHour: Int, startMinute: Int) {
        if (timeMode == NONE) {
            throw IllegalArgumentException("Time mode invalid")
        }
        var illegal = false
        if (startHour < 0 || startMinute < 0 || startMinute > 59) {
            illegal = true
        }
        if (timeMode == HOUR_12 && (startHour == 0 || startHour > 12)) {
            illegal = true
        }
        if (timeMode == HOUR_24 && startHour >= 24) {
            illegal = true
        }
        if (illegal) {
            throw IllegalArgumentException("Time out of range")
        }
        this.startHour = startHour
        this.startMinute = startMinute
        initHourData()
    }

    /**
     * 设置范围：结束的时分
     */
    open fun setTimeRangeEnd(endHour: Int, endMinute: Int) {
        if (timeMode == NONE) {
            throw IllegalArgumentException("Time mode invalid")
        }
        var illegal = false
        if (endHour < 0 || endMinute < 0 || endMinute > 59) {
            illegal = true
        }
        if (timeMode == HOUR_12 && (endHour == 0 || endHour > 12)) {
            illegal = true
        }
        if (timeMode == HOUR_24 && endHour >= 24) {
            illegal = true
        }
        if (illegal) {
            throw IllegalArgumentException("Time out of range")
        }
        this.endHour = endHour
        this.endMinute = endMinute
        initHourData()
    }

    /**
     * 设置年月日时分的显示单位
     */
    open fun setLabel(yearLabel: String, monthLabel: String, dayLabel: String, hourLabel: String, minuteLabel: String) {
        if (mTvYear != null) {
            mTvYear.text = yearLabel
        }
        if (mTvMonth != null) {
            mTvMonth.text = monthLabel
        }
        if (mTvDay != null) {
            mTvDay.text = dayLabel
        }
        if (mTvHour != null) {
            mTvHour.text = hourLabel
        }
        if (mTvMinute != null) {
            mTvMinute.text = minuteLabel
        }
    }

    /**
     * 设置默认选中的年月日时分
     */
    open fun setSelectedItem(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        if (dateMode != YEAR_MONTH_DAY) {
            throw IllegalArgumentException("Date mode invalid")
        }
        Log.v(TAG, "change months and days while set selected")
        changeMonthData(year)
        changeDayData(year, month)
        selectedYearIndex = findItemIndex(years, year)
        selectedMonthIndex = findItemIndex(months, month)
        selectedDayIndex = findItemIndex(days, day)
        if (timeMode != NONE) {
            selectedHour = DateUtil.fillZero(hour)
            selectedMinute = DateUtil.fillZero(minute)
        }
    }

    /**
     * 设置默认选中的年月时分或者月日时分
     */
    open fun setSelectedItem(yearOrMonth: Int, monthOrDay: Int, hour: Int, minute: Int) {
        if (dateMode == YEAR_MONTH_DAY) {
            throw IllegalArgumentException("Date mode invalid")
        }
        if (dateMode == MONTH_DAY) {
            Log.v(TAG, "change months and days while set selected")
            val year = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR)
            endYear = year
            startYear = endYear
            changeMonthData(year)
            changeDayData(year, yearOrMonth)
            selectedMonthIndex = findItemIndex(months, yearOrMonth)
            selectedDayIndex = findItemIndex(days, monthOrDay)
        } else if (dateMode == YEAR_MONTH) {
            Log.v(TAG, "change months while set selected")
            changeMonthData(yearOrMonth)
            selectedYearIndex = findItemIndex(years, yearOrMonth)
            selectedMonthIndex = findItemIndex(months, monthOrDay)
        }
        if (timeMode != NONE) {
            selectedHour = DateUtil.fillZero(hour)
            selectedMinute = DateUtil.fillZero(minute)
        }
    }

    private fun findItemIndex(items: MutableList<String>, item: Int): Int {
        //折半查找有序元素的索引
        val index = Collections.binarySearch<Serializable>(items, item, Comparator<Any> { lhs, rhs ->
            var lhsStr = lhs.toString()
            var rhsStr = rhs.toString()
            lhsStr = if (lhsStr.startsWith("0")) lhsStr.substring(1) else lhsStr
            rhsStr = if (rhsStr.startsWith("0")) rhsStr.substring(1) else rhsStr
            try {
                Integer.parseInt(lhsStr) - Integer.parseInt(rhsStr)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                0
            }
        })
        if (index < 0) {
            throw IllegalArgumentException("Item[$item] out of range")
        }
        return index
    }

    fun getSelectedHour(): String {
        return if (timeMode != NONE) {
            selectedHour
        } else ""
    }

    fun getSelectedMinute(): String {
        return if (timeMode != NONE) {
            selectedMinute
        } else ""
    }

    /**
     * 初始化年数据
     */
    private fun initYearData() {
        years.clear()
        when {
            startYear == endYear -> years.add(startYear.toString())
            startYear < endYear -> {
                // 年份正序
                (startYear..endYear).mapTo(years) { it.toString() }
            }
            else -> {
                // 年份逆序
                (startYear downTo endYear).mapTo(years) { it.toString() }
            }
        }
        if (!resetWhileWheel) {
            if (dateMode == YEAR_MONTH_DAY || dateMode == YEAR_MONTH) {
                val index = years.indexOf(DateUtil.fillZero(Calendar.getInstance().get(Calendar.YEAR)))
                selectedYearIndex = if (index == -1) {
                    //当前设置的年份不在指定范围，则默认选中范围开始的年
                    0
                } else {
                    index
                }
            }
        }
    }

    /**
     * 改变月份数据
     */
    private fun changeMonthData(selectedYear: Int) {
        var preSelectMonth = ""
        if (!resetWhileWheel) {
            preSelectMonth = if (months.size > selectedMonthIndex) {
                months[selectedMonthIndex]
            } else {
                DateUtil.fillZero(Calendar.getInstance().get(Calendar.MONTH) + 1)
            }
            Log.v(TAG, "preSelectMonth=" + preSelectMonth)
        }
        months.clear()
        if (startMonth < 1 || endMonth < 1 || startMonth > 12 || endMonth > 12) {
            throw IllegalArgumentException("Month out of range [1-12]")
        }
        if (startYear == endYear) {
            if (startMonth > endMonth) {
                (endMonth downTo startMonth).mapTo(months) { DateUtil.fillZero(it) }
            } else {
                (startMonth..endMonth).mapTo(months) { DateUtil.fillZero(it) }
            }
        } else if (selectedYear == startYear) {
            (startMonth..12).mapTo(months) { DateUtil.fillZero(it) }
        } else if (selectedYear == endYear) {
            (1..endMonth).mapTo(months) { DateUtil.fillZero(it) }
        } else {
            (1..12).mapTo(months) { DateUtil.fillZero(it) }
        }
        if (!resetWhileWheel) {
            //当前设置的月份不在指定范围，则默认选中范围开始的月份
            val preSelectMonthIndex = months.indexOf(preSelectMonth)
            selectedMonthIndex = if (preSelectMonthIndex == -1) 0 else preSelectMonthIndex
        }
    }

    /**
     * 改变天数据
     */
    private fun changeDayData(selectedYear: Int, selectedMonth: Int) {
        val maxDays = DateUtil.dayOfMonth(selectedYear, selectedMonth)
        var preSelectDay = ""
        if (!resetWhileWheel) {
            if (selectedDayIndex >= maxDays) {
                //如果之前选择的日是之前年月的最大日，则日自动为该年月的最大日
                selectedDayIndex = maxDays - 1
            }
            preSelectDay = if (days.size > selectedDayIndex) {
                //年或月变动时，保持之前选择的日不动
                days[selectedDayIndex]
            } else {
                DateUtil.fillZero(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            }
            Log.v(TAG, "maxDays=$maxDays, preSelectDay=$preSelectDay")
        }
        days.clear()
        if (selectedYear == startYear && selectedMonth == startMonth
                && selectedYear == endYear && selectedMonth == endMonth) {
            //开始年月及结束年月相同情况
            (startDay..endDay).mapTo(days) { DateUtil.fillZero(it) }
        } else if (selectedYear == startYear && selectedMonth == startMonth) {
            //开始年月相同情况
            (startDay..maxDays).mapTo(days) { DateUtil.fillZero(it) }
        } else if (selectedYear == endYear && selectedMonth == endMonth) {
            //结束年月相同情况
            (1..endDay).mapTo(days) { DateUtil.fillZero(it) }
        } else {
            (1..maxDays).mapTo(days) { DateUtil.fillZero(it) }
        }
        if (!resetWhileWheel) {
            //当前设置的日子不在指定范围，则默认选中范围开始的日子
            val preSelectDayIndex = days.indexOf(preSelectDay)
            selectedDayIndex = if (preSelectDayIndex == -1) 0 else preSelectDayIndex
        }
    }

    /**
     * 初始化小时数据
     */
    private fun initHourData() {
        hours.clear()
        var currentHour = 0
        if (!resetWhileWheel) {
            currentHour = if (timeMode == HOUR_24) {
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            } else {
                Calendar.getInstance().get(Calendar.HOUR)
            }
        }
        for (i in startHour..endHour) {
            val hour = DateUtil.fillZero(i)
            if (!resetWhileWheel) {
                if (i == currentHour) {
                    selectedHour = hour
                }
            }
            hours.add(hour)
        }
        if (hours.indexOf(selectedHour) == -1) {
            //当前设置的小时不在指定范围，则默认选中范围开始的小时
            selectedHour = hours[0]
        }
        if (!resetWhileWheel) {
            selectedMinute = DateUtil.fillZero(Calendar.getInstance().get(Calendar.MINUTE))
        }
    }

    /**
     * 改变分钟数据
     */
    private fun changeMinuteData(selectedHour: Int) {
        minutes.clear()
        when {
            startHour == endHour -> {
                if (startMinute > endMinute) {
                    val temp = startMinute
                    startMinute = endMinute
                    endMinute = temp
                }
                (startMinute..endMinute).mapTo(minutes) { DateUtil.fillZero(it) }
            }
            selectedHour == startHour -> {
                (startMinute..59).mapTo(minutes) { DateUtil.fillZero(it) }
            }
            selectedHour == endHour -> {
                (0..endMinute).mapTo(minutes) { DateUtil.fillZero(it) }
            }
            else -> {
                (0..59).mapTo(minutes) { DateUtil.fillZero(it) }
            }
        }
        if (minutes.indexOf(selectedMinute) == -1) {
            //当前设置的分钟不在指定范围，则默认选中范围开始的分钟
            selectedMinute = minutes[0]
        }
    }

    /**
     * 滑动监听
     * */
    interface OnWheelListener {

        fun onYearWheeled(index: Int, year: String)

        fun onMonthWheeled(index: Int, month: String)

        fun onDayWheeled(index: Int, day: String)

        fun onHourWheeled(index: Int, hour: String)

        fun onMinuteWheeled(index: Int, minute: String)
    }

    open fun setOnWheelListener(listener: OnWheelListener) {
        this.onWheelListener = listener
    }

    /**
     * 日期时间监听
     * */
    open fun setOnDateTimePickListener(listener: OnDateTimePickListener) {
        this.onDateTimePickListener = listener
    }

    interface OnDateTimePickListener

    /**
     * 年月日时间监听
     */
    interface OnYearMonthDayTimePickListener : OnDateTimePickListener {
        fun onDateTimePicked(year: String, month: String, day: String, hour: String, minute: String)
    }

    /**
     * 年月监听
     */
    interface OnYearMonthTimePickListener : OnDateTimePickListener {
        fun onDateTimePicked(year: String, month: String, hour: String, minute: String)
    }


    @Deprecated("use {@link OnYearMonthTimePickListener} instead")
    interface OnYearMonthPickListener : OnYearMonthTimePickListener

    /**
     * 月日监听
     */
    interface OnMonthDayTimePickListener : OnDateTimePickListener {

        fun onDateTimePicked(month: String, day: String, hour: String, minute: String)
    }


    @Deprecated("use {@link OnMonthDayTimePickListener} instead")
    interface OnMonthDayPickListener : OnMonthDayTimePickListener

    /**
     * 时间监听
     */
    interface OnTimePickListener : OnDateTimePickListener {
        fun onDateTimePicked(hour: String, minute: String)
    }

    companion object {
        /** 不显示  */
        val NONE = -1
        /** 年月日  */
        val YEAR_MONTH_DAY = 0
        /** 年月  */
        val YEAR_MONTH = 1
        /** 月日  */
        val MONTH_DAY = 2
        /** 24小时  */
        val HOUR_24 = 3

        @Deprecated("use {@link #HOUR_24} instead")
        val HOUR_OF_DAY = 3
        /** 12小时  */
        val HOUR_12 = 4

        @Deprecated("use {@link #HOUR_12} instead")
        val HOUR = 4
    }
}