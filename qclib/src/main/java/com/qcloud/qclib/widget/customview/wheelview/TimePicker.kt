package com.qcloud.qclib.widget.customview.wheelview

import android.content.Context

/**
 * 类说明：时间选择器
 * Author: Kuzan
 * Date: 2018/1/20 10:11.
 */
class TimePicker @JvmOverloads constructor (
        context: Context,
        @TimeMode timeMode: Int = Companion.HOUR_24): DateTimePicker(context, Companion.NONE, timeMode) {

    /**
     * 设置时间显示的单位
     */
    fun setLabel(hourLabel: String, minuteLabel: String) {
        super.setLabel("", "", "", hourLabel, minuteLabel)
    }

    @Deprecated("nonsupport",
            ReplaceWith("throw UnsupportedOperationException(\"Date range nonsupport\")"))
    override fun setDateRangeStart(startYear: Int, startMonth: Int, startDay: Int) {
        throw UnsupportedOperationException("Date range nonsupport")
    }

    @Deprecated("nonsupport",
            ReplaceWith("throw UnsupportedOperationException(\"Date range nonsupport\")"))
    override fun setDateRangeEnd(endYear: Int, endMonth: Int, endDay: Int) {
        throw UnsupportedOperationException("Date range nonsupport")
    }

    @Deprecated("nonsupport",
            ReplaceWith("throw UnsupportedOperationException(\"Date range nonsupport\")"))
    override fun setDateRangeStart(startYearOrMonth: Int, startMonthOrDay: Int) {
        throw UnsupportedOperationException("Date range nonsupport")
    }

    @Deprecated("nonsupport",
            ReplaceWith("throw UnsupportedOperationException(\"Data range nonsupport\")"))
    override fun setDateRangeEnd(endYearOrMonth: Int, endMonthOrDay: Int) {
        throw UnsupportedOperationException("Data range nonsupport")
    }

    @Deprecated("use {@link #setRangeStart(int, int)} instead",
            ReplaceWith("super.setTimeRangeStart(startHour, startMinute)"))
    override fun setTimeRangeStart(startHour: Int, startMinute: Int) {
        super.setTimeRangeStart(startHour, startMinute)
    }


    @Deprecated("use {@link #setRangeEnd(int, int)} instead",
            ReplaceWith("super.setTimeRangeEnd(endHour, endMinute)"))
    override fun setTimeRangeEnd(endHour: Int, endMinute: Int) {
        super.setTimeRangeEnd(endHour, endMinute)
    }

    @Deprecated("use setRangeStart and setRangeEnd instead")
    override fun setRange(startHour: Int, endHour: Int) {
        super.setTimeRangeStart(startHour, 0)
        super.setTimeRangeEnd(endHour, 59)
    }

    /**
     * 设置范围：开始的时分
     */
    fun setRangeStart(startHour: Int, startMinute: Int) {
        super.setTimeRangeStart(startHour, startMinute)
    }

    /**
     * 设置范围：结束的时分
     */
    fun setRangeEnd(endHour: Int, endMinute: Int) {
        super.setTimeRangeEnd(endHour, endMinute)
    }

    @Deprecated("use {@link #setSelectedItem(int, int)} instead",
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
     * 设置默认选中的时间
     */
    fun setSelectedItem(hour: Int, minute: Int) {
        super.setSelectedItem(0, 0, hour, minute)
    }

    @Deprecated("use {@link #setOnWheelListener(OnWheelListener)} instead",
            ReplaceWith("super.setOnWheelListener(onWheelListener)"))
    override fun setOnWheelListener(onWheelListener: DateTimePicker.OnWheelListener) {
        super.setOnWheelListener(onWheelListener)
    }

    /**
     * 设置滑动监听器
     */
    fun setOnWheelListener(listener: OnWheelListener?) {
        if (null == listener) {
            return
        }
        super.setOnWheelListener(object : DateTimePicker.OnWheelListener {
            override fun onYearWheeled(index: Int, year: String) {}

            override fun onMonthWheeled(index: Int, month: String) {}

            override fun onDayWheeled(index: Int, day: String) {}

            override fun onHourWheeled(index: Int, hour: String) {
                listener.onHourWheeled(index, hour)
            }

            override fun onMinuteWheeled(index: Int, minute: String) {
                listener.onMinuteWheeled(index, minute)
            }
        })
    }

    @Deprecated("use {@link #setOnTimePickListener(OnTimePickListener)} instead",
            ReplaceWith("super.setOnDateTimePickListener(listener)"))
    override fun setOnDateTimePickListener(listener: DateTimePicker.OnDateTimePickListener) {
        super.setOnDateTimePickListener(listener)
    }

    fun setOnTimePickListener(listener: OnTimePickListener?) {
        if (null == listener) {
            return
        }
        super.setOnDateTimePickListener(object : DateTimePicker.OnTimePickListener {
            override fun onDateTimePicked(hour: String, minute: String) {
                listener.onTimePicked(hour, minute)
            }
        })
    }

    interface OnTimePickListener {
        fun onTimePicked(hour: String, minute: String)
    }

    interface OnWheelListener {
        fun onHourWheeled(index: Int, hour: String)

        fun onMinuteWheeled(index: Int, minute: String)
    }
//
//    /**
//     * 设置时间显示的单位
//     */
//    public void setLabel(String hourLabel, String minuteLabel) {
//        super.setLabel("", "", "", hourLabel, minuteLabel);
//    }
//
//    /**
//     * @deprecated nonsupport
//     */
//    @Deprecated
//    @Override
//    public final void setDateRangeStart(int startYear, int startMonth, int startDay) {
//        throw new UnsupportedOperationException("Date range nonsupport");
//    }
//
//    /**
//     * @deprecated nonsupport
//     */
//    @Deprecated
//    @Override
//    public final void setDateRangeEnd(int endYear, int endMonth, int endDay) {
//        throw new UnsupportedOperationException("Date range nonsupport");
//    }
//
//    /**
//     * @deprecated nonsupport
//     */
//    @Deprecated
//    @Override
//    public final void setDateRangeStart(int startYearOrMonth, int startMonthOrDay) {
//        throw new UnsupportedOperationException("Date range nonsupport");
//    }
//
//    /**
//     * @deprecated nonsupport
//     */
//    @Deprecated
//    @Override
//    public final void setDateRangeEnd(int endYearOrMonth, int endMonthOrDay) {
//        throw new UnsupportedOperationException("Data range nonsupport");
//    }
//
//    /**
//     * @deprecated use {@link #setRangeStart(int, int)} instead
//     */
//    @Deprecated
//    @Override
//    public void setTimeRangeStart(int startHour, int startMinute) {
//        super.setTimeRangeStart(startHour, startMinute);
//    }
//
//    /**
//     * @deprecated use {@link #setRangeEnd(int, int)} instead
//     */
//    @Deprecated
//    @Override
//    public void setTimeRangeEnd(int endHour, int endMinute) {
//        super.setTimeRangeEnd(endHour, endMinute);
//    }
//
//    /**
//     * @deprecated use setRangeStart and setRangeEnd instead
//     */
//    @Deprecated
//    public void setRange(int startHour, int endHour) {
//        super.setTimeRangeStart(startHour, 0);
//        super.setTimeRangeEnd(endHour, 59);
//    }
//
//
//    /**
//     * 设置范围：开始的时分
//     */
//    public void setRangeStart(int startHour, int startMinute) {
//        super.setTimeRangeStart(startHour, startMinute);
//    }
//
//    /**
//     * 设置范围：结束的时分
//     */
//    public void setRangeEnd(int endHour, int endMinute) {
//        super.setTimeRangeEnd(endHour, endMinute);
//    }
//
//    /**
//     * @deprecated use {@link #setSelectedItem(int, int)} instead
//     */
//    @Deprecated
//    @Override
//    public final void setSelectedItem(int year, int month, int day, int hour, int minute) {
//        super.setSelectedItem(year, month, day, hour, minute);
//    }
//
//    /**
//     * @deprecated use {@link #setSelectedItem(int, int)} instead
//     */
//    @Deprecated
//    @Override
//    public final void setSelectedItem(int yearOrMonth, int monthOrDay, int hour, int minute) {
//        super.setSelectedItem(yearOrMonth, monthOrDay, hour, minute);
//    }
//
//    /**
//     * 设置默认选中的时间
//     */
//    public void setSelectedItem(int hour, int minute) {
//        super.setSelectedItem(0, 0, hour, minute);
//    }
//
//    /**
//     * @deprecated use {@link #setOnWheelListener(OnWheelListener)} instead
//     */
//    @Deprecated
//    @Override
//    public final void setOnWheelListener(DateTimePicker.OnWheelListener onWheelListener) {
//        super.setOnWheelListener(onWheelListener);
//    }
//
//    /**
//     * 设置滑动监听器
//     */
//    public void setOnWheelListener(final OnWheelListener listener) {
//        if (null == listener) {
//            return;
//        }
//        super.setOnWheelListener(new DateTimePicker.OnWheelListener() {
//            @Override
//            public void onYearWheeled(int index, String year) {
//            }
//
//            @Override
//            public void onMonthWheeled(int index, String month) {
//            }
//
//            @Override
//            public void onDayWheeled(int index, String day) {
//            }
//
//            @Override
//            public void onHourWheeled(int index, String hour) {
//                listener.onHourWheeled(index, hour);
//            }
//
//            @Override
//            public void onMinuteWheeled(int index, String minute) {
//                listener.onMinuteWheeled(index, minute);
//            }
//        });
//    }
//
//    /**
//     * @deprecated use {@link #setOnTimePickListener(OnTimePickListener)} instead
//     */
//    @Deprecated
//    @Override
//    public final void setOnDateTimePickListener(OnDateTimePickListener listener) {
//        super.setOnDateTimePickListener(listener);
//    }
//
//    public void setOnTimePickListener(final OnTimePickListener listener) {
//        if (null == listener) {
//            return;
//        }
//        super.setOnDateTimePickListener(new DateTimePicker.OnTimePickListener() {
//            @Override
//            public void onDateTimePicked(String hour, String minute) {
//                listener.onTimePicked(hour, minute);
//            }
//        });
//    }
//
//    public interface OnTimePickListener {
//
//        void onTimePicked(String hour, String minute);
//
//    }
//
//    public interface OnWheelListener {
//
//        void onHourWheeled(int index, String hour);
//
//        void onMinuteWheeled(int index, String minute);
//    }
}