package com.qcloud.qclib.widget.customview.wheelview

import android.content.Context

/**
 * 类说明：日期选择器
 * Author: Kuzan
 * Date: 2018/1/20 9:52.
 */
class DatePicker @JvmOverloads constructor (
        context: Context,
        @DateMode dateMode: Int = Companion.YEAR_MONTH_DAY,
        @TimeMode timeMode: Int = Companion.NONE): DateTimePicker(context, dateMode, timeMode) {

    @Deprecated("use {@link #setLabel(String, String, String)} instead",
            ReplaceWith("super.setLabel(yearLabel, monthLabel, dayLabel, hourLabel, minuteLabel)"))
    override fun setLabel(yearLabel: String, monthLabel: String, dayLabel: String, hourLabel: String, minuteLabel: String) {
        super.setLabel(yearLabel, monthLabel, dayLabel, hourLabel, minuteLabel)
    }

    /**
     * 设置年月日的单位
     */
    fun setLabel(yearLabel: String, monthLabel: String, dayLabel: String) {
        super.setLabel(yearLabel, monthLabel, dayLabel, "", "")
    }

    @Deprecated("use {@link #setRangeStart(int, int, int)} instead",
            ReplaceWith("super.setDateRangeStart(startYear, startMonth, startDay)"))
    override fun setDateRangeStart(startYear: Int, startMonth: Int, startDay: Int) {
        super.setDateRangeStart(startYear, startMonth, startDay)
    }

    @Deprecated("use {@link #setRangeEnd(int, int, int)} instead",
            ReplaceWith("super.setDateRangeEnd(endYear, endMonth, endDay)"))
    override fun setDateRangeEnd(endYear: Int, endMonth: Int, endDay: Int) {
        super.setDateRangeEnd(endYear, endMonth, endDay)
    }

    @Deprecated("use {@link #setRangeStart(int, int)} instead",
            ReplaceWith("super.setDateRangeStart(startYearOrMonth, startMonthOrDay)"))
    override fun setDateRangeStart(startYearOrMonth: Int, startMonthOrDay: Int) {
        super.setDateRangeStart(startYearOrMonth, startMonthOrDay)
    }

    @Deprecated("use {@link #setRangeEnd(int, int)} instead",
            ReplaceWith("super.setDateRangeEnd(endYearOrMonth, endMonthOrDay)"))
    override fun setDateRangeEnd(endYearOrMonth: Int, endMonthOrDay: Int) {
        super.setDateRangeEnd(endYearOrMonth, endMonthOrDay)
    }

    @Deprecated("nonsupport",
            ReplaceWith("throw UnsupportedOperationException(Time range nonsupport)"))
    override fun setTimeRangeStart(startHour: Int, startMinute: Int) {
        throw UnsupportedOperationException("Time range nonsupport")
    }

    @Deprecated("nonsupport",
            ReplaceWith("throw UnsupportedOperationException(Time range nonsupport)"))
    override fun setTimeRangeEnd(endHour: Int, endMinute: Int) {
        throw UnsupportedOperationException("Time range nonsupport")
    }

    /**
     * 设置年份范围
     *
     */
    @Deprecated("use setRangeStart and setRangeEnd instead",
            ReplaceWith("super.setRange(startYear, endYear)"))
    override fun setRange(startYear: Int, endYear: Int) {
        super.setRange(startYear, endYear)
    }

    /**
     * 设置范围：开始的年月日
     */
    fun setRangeStart(startYear: Int, startMonth: Int, startDay: Int) {
        super.setDateRangeStart(startYear, startMonth, startDay)
    }

    /**
     * 设置范围：结束的年月日
     */
    fun setRangeEnd(endYear: Int, endMonth: Int, endDay: Int) {
        super.setDateRangeEnd(endYear, endMonth, endDay)
    }

    /**
     * 设置范围：开始的年月日
     */
    fun setRangeStart(startYearOrMonth: Int, startMonthOrDay: Int) {
        super.setDateRangeStart(startYearOrMonth, startMonthOrDay)
    }

    /**
     * 设置范围：结束的年月日
     */
    fun setRangeEnd(endYearOrMonth: Int, endMonthOrDay: Int) {
        super.setDateRangeEnd(endYearOrMonth, endMonthOrDay)
    }

    @Deprecated("use {@link #setSelectedItem(int, int, int)} instead",
            ReplaceWith("super.setSelectedItem(year, month, day, hour, minute)"))
    override fun setSelectedItem(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        super.setSelectedItem(year, month, day, hour, minute)
    }

    @Deprecated("use {@link #setSelectedItem(int, int)} instead",
            ReplaceWith("super.setSelectedItem(yearOrMonth, monthOrDay, hour, minute)"))
    override fun setSelectedItem(yearOrMonth: Int, monthOrDay: Int, hour: Int, minute: Int) {
        super.setSelectedItem(yearOrMonth, monthOrDay, hour, minute)
    }

    /**
     * 设置默认选中的年月日
     */
    fun setSelectedItem(year: Int, month: Int, day: Int) {
        super.setSelectedItem(year, month, day, 0, 0)
    }

    /**
     * 设置默认选中的年月或者月日
     */
    fun setSelectedItem(yearOrMonth: Int, monthOrDay: Int) {
        super.setSelectedItem(yearOrMonth, monthOrDay, 0, 0)
    }

    @Deprecated("use {@link #setOnWheelListener(OnWheelListener)} instead",
            ReplaceWith("super.setOnWheelListener(onWheelListener)"))
    override fun setOnWheelListener(onWheelListener: DateTimePicker.OnWheelListener) {
        super.setOnWheelListener(onWheelListener)
    }

    fun setOnWheelListener(listener: OnWheelListener?) {
        if (null == listener) {
            return
        }
        super.setOnWheelListener(object : DateTimePicker.OnWheelListener {
            override fun onYearWheeled(index: Int, year: String) {
                listener.onYearWheeled(index, year)
            }

            override fun onMonthWheeled(index: Int, month: String) {
                listener.onMonthWheeled(index, month)
            }

            override fun onDayWheeled(index: Int, day: String) {
                listener.onDayWheeled(index, day)
            }

            override fun onHourWheeled(index: Int, hour: String) {

            }

            override fun onMinuteWheeled(index: Int, minute: String) {

            }
        })
    }

    @Deprecated("use {@link #setOnDatePickListener(OnDatePickListener)} instead",
            ReplaceWith("super.setOnDateTimePickListener(listener)"))
    override fun setOnDateTimePickListener(listener: DateTimePicker.OnDateTimePickListener) {
        super.setOnDateTimePickListener(listener)
    }

    fun setOnDatePickListener(listener: OnDatePickListener?) {
        if (null == listener) {
            return
        }
        when (listener) {
            is OnYearMonthDayPickListener -> {
                super.setOnDateTimePickListener(object : DateTimePicker.OnYearMonthDayTimePickListener {
                    override fun onDateTimePicked(year: String, month: String, day: String, hour: String, minute: String) {
                        listener.onDatePicked(year, month, day)
                    }
                })
            }
            is OnYearMonthPickListener -> {
                super.setOnDateTimePickListener(object : DateTimePicker.OnYearMonthTimePickListener {
                    override fun onDateTimePicked(year: String, month: String, hour: String, minute: String) {
                        listener.onDatePicked(year, month)
                    }
                })
            }
            is OnMonthDayPickListener -> {
                super.setOnDateTimePickListener(object : DateTimePicker.OnMonthDayTimePickListener {
                    override fun onDateTimePicked(month: String, day: String, hour: String, minute: String) {
                        listener.onDatePicked(month, day)
                    }
                })
            }
        }
    }

    interface OnDatePickListener

    interface OnYearMonthDayPickListener : OnDatePickListener {
        fun onDatePicked(year: String, month: String, day: String)
    }

    interface OnYearMonthPickListener : OnDatePickListener {
        fun onDatePicked(year: String, month: String)
    }

    interface OnMonthDayPickListener : OnDatePickListener {
        fun onDatePicked(month: String, day: String)
    }

    interface OnWheelListener {
        fun onYearWheeled(index: Int, year: String)

        fun onMonthWheeled(index: Int, month: String)

        fun onDayWheeled(index: Int, day: String)
    }
}