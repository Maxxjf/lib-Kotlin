package com.qcloud.qclib.utils

import com.qcloud.qclib.enums.DateStyleEnum
import com.qcloud.qclib.enums.WeekEnum
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 类说明：日期时间工具类
 * Author: Kuzan
 * Date: 2017/11/27 11:47.
 */
object DateUtil {
    /**
     * 获取系统当前时间
     *
     * @param formatStr 时间格式 默认为yyyy-MM-dd HH:mm:ss
     * */
    fun getCurrTime(formatStr: String = DateStyleEnum.YYYY_MM_DD_HH_MM_SS.value): String {
        val formatter = getSimpleDateFormat(formatStr)
        val currDate = Date(System.currentTimeMillis())
        return formatter.format(currDate)
    }

    /**
     * 将Date转为String
     *
     * @param date 时间
     * @param formatStr 时间格式 默认为yyyy-MM-dd HH:mm:ss
     * */
    fun formatDate(date: Date, formatStr: String = DateStyleEnum.YYYY_MM_DD_HH_MM_SS.value): String {
        val formatter = getSimpleDateFormat(formatStr).format(date)
        return formatter.format(date)
    }

    /**
     * 将日期字符串转换为Date类型
     *
     * @param dateStr 日期字符串
     * @param formatStr 日期字符串格式 默认为yyyy-MM-dd HH:mm:ss
     * @return Date对象
     */
    fun parseDate(dateStr: String?, formatStr: String): Date? {
        if (StringUtil.isBlank(dateStr)) {
            return null
        }
        val formatter = getSimpleDateFormat(formatStr)
        try {
            return formatter.parse(dateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 时间戳转为date类型时间
     *
     * @param timeStamp
     * @param formatStr
     * */
    fun long2Date(timeStamp: Long, formatStr: String = DateStyleEnum.YYYY_MM_DD_HH_MM_SS.value): Date? {
        val dateStr = long2String(timeStamp, formatStr)
        return parseDate(dateStr, formatStr)
    }

    /**
     * 时间戳转为String类型时间
     * */
    fun long2String(timeStamp: Long, formatStr: String = DateStyleEnum.YYYY_MM_DD_HH_MM_SS.value): String {
        val date = Date(timeStamp)
        return formatDate(date, formatStr)
    }

    /**
     * string类型时间转为long
     *
     * @param dateStr
     * @param formatStr 时间格式要与dateStr里的格式一致
     * */
    fun string2Long(dateStr: String, formatStr: String): Long {
        val date = parseDate(dateStr, formatStr)
        return if (date != null) date2Long(date) else 0
    }

    /**
     * 将时间转成秒
     *
     * @param dateStr
     * */
    fun string2Second(dateStr: String, formatStr: String): Int {
        val date = parseDate(dateStr, formatStr)
        return if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)

            return hour * 60 * 60 + minute * 60 + second
        } else {
            0
        }
    }

    /**
     * date类型时间转为long
     *
     * @param date
     * */
    fun date2Long(date: Date): Long = date.time

    /**
     * 将日期字符串转化为另一日期字格式符串
     *
     * @param dateStr
     * @param formatStr
     * */
    fun transformDate(dateStr: String, formatStr: String = DateStyleEnum.YYYY_MM_DD_HH_MM_SS.value): String? {
        val dateFormat = getDateFormate(dateStr)
        return if (dateFormat != null) {
            val date = parseDate(dateStr, dateFormat)
            if (date != null) {
                formatDate(date, formatStr)
            } else {
                null
            }
        } else {
            null
        }
    }

    /**
     * 判断字符串是否为日期字符串
     *
     * @param dateStr
     * @return true 是日期字符串
     * */
    fun isDate(dateStr: String): Boolean {
        val formatStr = getDateFormate(dateStr)
        return if (StringUtil.isNotBlank(formatStr)) {
            parseDate(dateStr, formatStr!!) != null
        } else {
            false
        }
    }

    /**
     * 获取时间格式
     *
     * @param formatStr 时间格式 默认为yyyy-MM-dd HH:mm:ss
     * */
    fun getSimpleDateFormat(formatStr: String = DateStyleEnum.YYYY_MM_DD_HH_MM_SS.value) = SimpleDateFormat(formatStr, Locale.getDefault())

    /**
     * 获取时间格式字符串
     * */
    fun getDateFormate(dateStr: String?): String? {
        if (StringUtil.isBlank(dateStr)) {
            return null
        }
        var dateEnum: DateStyleEnum? = null

        for (dEnum in DateStyleEnum.values()) {
            val dateTmp = parseDate(dateStr, dEnum.value)
            if (dateTmp != null) {
                val dateTemStr = formatDate(dateTmp, dEnum.value)
                if (StringUtil.isEquals(dateStr, dateTemStr)) {
                    dateEnum = dEnum
                    break
                }
            }
        }

        return if (dateEnum != null) {
            dateEnum.value
        } else {
            null
        }
    }

    /**
     * 比较两个时间大小
     *
     * @param dateStr1
     * @param dateStr2
     *
     * @return 1 dateStr1在dateStr2前面; -1 dateStr1在dateStr2后面 0两个时间相等
     * */
    fun compareTime(dateStr1: String?, dateStr2: String?, formatStr: String = DateStyleEnum.YYYY_MM_DD_HH_MM_SS.value): Int {
        val date1 = parseDate(dateStr1, formatStr)
        val date2 = parseDate(dateStr2, formatStr)
        return compareTime(date1, date2)
    }

    /**
     * 比较两个时间大小
     *
     * @param date1
     * @param date2
     *
     * @return 1 date1在date2前面; -1 date1在date2后面 0两个时间相等
     * */
    fun compareTime(date1: Date?, date2: Date?): Int = when {
        date1 != null && date2 == null -> 1
        date1 == null && date2 != null -> -1
        date1 != null && date2 != null -> {
            when {
                date1.time > date2.time -> 1
                date1.time < date2.time -> -1
                else -> 0
            }
        }
        else -> 0
    }

    /**
     * 比较两个时间大小
     *
     * @param timeStamp1
     * @param timeStamp2
     *
     * @return 1 date1在date2前面; -1 date1在date2后面 0两个时间相等
     * */
    fun compareTime(timeStamp1: Long, timeStamp2: Long): Int = when {
        timeStamp1 > timeStamp2 -> 1
        timeStamp1 < timeStamp2 -> -1
        else -> 0
    }

    /**
     * 转换日期 将日期转为今天, 昨天, 前天, XXXX-XX-XX, ...
     *
     * @param timeStamp 时间 时间的格式要设为yyyy-MM-dd HH或以上
     * @return 当前日期转换为更容易理解的方式
     */
    fun translateDate2Day(timeStamp: Long): String {
        val oneDay = 24*60*60*1000
        val current = Calendar.getInstance()
        val today = Calendar.getInstance()  // 今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR))
        today.set(Calendar.MONTH, current.get(Calendar.MONTH))
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH))
        today.set(Calendar.HOUR_OF_DAY, 0)  // Calendar.HOUR:12小时制的小时数 Calendar.HOUR_OF_DAY:24小时制的小时数
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)

        val todayStartTime = today.timeInMillis

        return if (timeStamp >= todayStartTime - oneDay * 2 && timeStamp < todayStartTime - oneDay) {
            "前天"
        } else if (timeStamp >= todayStartTime - oneDay && timeStamp < todayStartTime) {
            "昨天"
        } else if (timeStamp >= todayStartTime && timeStamp < todayStartTime + oneDay) {
            "今天"
        } else if (timeStamp >= todayStartTime + oneDay && timeStamp < todayStartTime + oneDay * 2) {
            "明天"
        } else if (timeStamp >= todayStartTime + oneDay * 2 && timeStamp < todayStartTime + oneDay * 3) {
            "后天"
        } else {
            val formatter = getSimpleDateFormat(DateStyleEnum.YYYY_MM_DD.value)
            val date = Date(timeStamp)
            formatter.format(date)
        }
    }

    /**
     * 转换日期 转换为更为人性化的时间
     *
     * @param timeStamp 时间
     * @return
     */
    fun translateDate2Minute(timeStamp: Long): String {
        val curTime = getTimeStamp()
        val oneDay = (24 * 60 * 60 * 1000).toLong()

        val today = Calendar.getInstance()    //今天
        today.timeInMillis = curTime
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)

        val todayStartTime = today.timeInMillis

        if (timeStamp >= todayStartTime) {
            val d = (curTime - timeStamp)/1000
            System.out.print(todayStartTime.toString() + "," + curTime + "," + d.toString() + ",")
            when {
                d <= 60 -> return "1分钟前"
                d <= 60 * 60 -> {
                    var m = d / 60
                    if (m <= 0) {
                        m = 1
                    }
                    return m.toString() + "分钟前"
                }
                else -> {
                    val formatter = getSimpleDateFormat("今天 HH:mm")
                    val date = Date(timeStamp)
                    var dateStr = formatter.format(date)
                    if (StringUtil.isNotBlank(dateStr) && dateStr.contains(" 0")) {
                        dateStr = dateStr.replace(" 0", " ")
                    }
                    return dateStr
                }
            }
        } else {
            if (timeStamp < todayStartTime && timeStamp > todayStartTime - oneDay) {
                val formatter = getSimpleDateFormat("昨天 HH:mm")
                val date = Date(timeStamp)
                var dateStr = formatter.format(date)
                if (StringUtil.isNotBlank(dateStr) && dateStr.contains(" 0")) {
                    dateStr = dateStr.replace(" 0", " ")
                }
                return dateStr
            } else if (timeStamp < todayStartTime - oneDay && timeStamp > todayStartTime - 2 * oneDay) {
                val formatter = getSimpleDateFormat("前天 HH:mm")
                val date = Date(timeStamp)
                var dateStr = formatter.format(date)
                if (StringUtil.isNotBlank(dateStr) && dateStr.contains(" 0")) {
                    dateStr = dateStr.replace(" 0", " ")
                }
                return dateStr
            } else {
                val formatter = getSimpleDateFormat("yyyy-MM-dd HH:mm")
                val date = Date(timeStamp)
                var dateStr = formatter.format(date)
                if (StringUtil.isNotBlank(dateStr) && dateStr.contains(" 0")) {
                    dateStr = dateStr.replace(" 0", " ")
                }
                return dateStr
            }
        }
    }

    /**
     * 获取当前时间时间戳
     *
     * @return 时间戳
     * */
    fun getTimeStamp(): Long = System.currentTimeMillis()

    /**
     * 获取当前时间时间戳
     *
     * @return 时间戳
     * */
    fun getTimeStampStr(): String = System.currentTimeMillis().toString()

    /**
     * 判断是否是闰年。
     *
     * @param year
     * @return
     */
    fun isLeapYear(year: Int): Boolean = ((year % 100 == 0) && (year % 400 == 0)) || ((year % 100 != 0) && (year % 4 == 0))

    /**
     * 判断是否是小月（30天）
     *
     * @param month
     * @return
     */
    fun isMonthOf30(month: Int): Boolean = when (month) {
        4, 6, 9, 11 -> true
        else -> false
    }

    /**
     * 判断是否是大月（31天）
     *
     * @param month
     * @return
     */
    fun isMonthOf31(month: Int): Boolean = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> true
        else -> false
    }

    /**
     * 获取当月有多少天。
     *
     * @param year
     * @param month
     * @return
     */
    fun dayOfMonth(year: Int, month: Int): Int {
        return if (isMonthOf31(month)) {
            31
        } else if (isMonthOf30(month)) {
            30
        } else {
            if (isLeapYear(year)) {
                29
            } else {
                28
            }
        }
    }

    /**
     * 获取当月第一天是星期几（0为星期日）
     *
     * @param year
     * @param month
     * @return
     */
    fun getWeekOfMonthOfOne(year: Int, month: Int): Int {
        val dateStr = if (month < 10) {
            year.toString() + "-0" + month.toString() + "-01"
        } else {
            year.toString() + month.toString() + "-01"
        }

        val week = getWeek(dateStr)
        return week?.key ?: 0
    }

    /**
     * 获取日期的星期。失败返回星期天。
     *
     * @param dateStr 日期字符串
     * @return 星期
     */
    fun getWeek(dateStr: String): WeekEnum? {
        var week: WeekEnum? = null
        val formatStr = getDateFormate(dateStr)
        if (StringUtil.isNotBlank(formatStr)) {
            val date = parseDate(dateStr, formatStr!!)
            week = getWeek(date)
        }

        return week
    }

    /**
     * 获取日期的星期。失败返回null。
     *
     * @param date 日期
     * @return 星期
     */
    fun getWeek(date: Date?): WeekEnum? {
        var week: WeekEnum? = null
        if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            val weekNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1
            week = WeekEnum.keyOf(weekNumber)
        }

        return week
    }

    /**
     * 获取日期中的某数值。如获取月份
     *
     * @param date     日期
     * @param dateType 日期格式Calendar.MONTH
     * @return 数值
     */
    fun getInteger(date: Date?, dateType: Int): Int {
        return if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.get(dateType)
        } else {
            0
        }
    }

    /**
     * 增加日期中某类型的某数值。如增加日期
     *
     * @param dateStr  日期字符串
     * @param dateType 类型
     * @param amount   数值
     * @return 计算后日期字符串
     */
    fun addInteger(dateStr: String?, dateType: Int, amount: Int): String? {
        val formatStr = getDateFormate(dateStr)
        return if (StringUtil.isNotBlank(formatStr)) {
            var date = parseDate(dateStr, formatStr!!)
            date = addInteger(date, dateType, amount)
            if (date != null) {
                formatDate(date, formatStr)
            } else {
                null
            }
        } else {
            null
        }
    }

    /**
     * 增加日期中某类型的某数值。如增加日期
     *
     * @param date     日期
     * @param dateType 类型
     * @param amount   数值
     * @return 计算后日期
     */
    fun addInteger(date: Date?, dateType: Int, amount: Int): Date? {
        var myDate: Date? = null
        if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(dateType, amount)
            myDate = calendar.time
        }
        return myDate
    }

    /**
     * 增加日期的年份。失败返回null。
     *
     * @param dateStr 日期
     * @param amount  增加数量。可为负数
     * @return 增加年份后的日期字符串
     */
    fun addYear(dateStr: String?, amount: Int): String? = addInteger(dateStr, Calendar.YEAR, amount)

    /**
     * 增加日期的年份。失败返回null。
     *
     * @param date    日期
     * @param amount  增加数量。可为负数
     * @return 增加年份后的日期字符串
     */
    fun addYear(date: Date?, amount: Int): Date? = addInteger(date, Calendar.YEAR, amount)

    /**
     * 增加日期的月份。失败返回null。
     *
     * @param dateStr 日期
     * @param amount 增加数量。可为负数
     * @return 增加月份后的日期字符串
     */
    fun addMonth(dateStr: String?, amount: Int): String? = addInteger(dateStr, Calendar.MONTH, amount)

    /**
     * 增加日期的月份。失败返回null。
     *
     * @param date       日期
     * @param amount 增加数量。可为负数
     * @return 增加月份后的日期
     */
    fun addMonth(date: Date?, amount: Int): Date? = addInteger(date, Calendar.MONTH, amount)

    /**
     * 增加日期的天数。失败返回null。
     *
     * @param dateStr 日期字符串
     * @param amount 增加数量。可为负数
     * @return 增加天数后的日期字符串
     */
    fun addDay(dateStr: String?, amount: Int): String? = addInteger(dateStr, Calendar.DATE, amount)

    /**
     * 增加日期的天数。失败返回null。
     *
     * @param date      日期
     * @param amount 增加数量。可为负数
     * @return 增加天数后的日期
     */
    fun addDay(date: Date?, amount: Int): Date? = addInteger(date, Calendar.DATE, amount)

    /**
     * 增加日期的小时。失败返回null。
     *
     * @param dateStr       日期字符串
     * @param amount 增加数量。可为负数
     * @return 增加小时后的日期字符串
     */
    fun addHour(dateStr: String?, amount: Int): String? = addInteger(dateStr, Calendar.HOUR_OF_DAY, amount)

    /**
     * 增加日期的小时。失败返回null。
     *
     * @param date       日期
     * @param amount 增加数量。可为负数
     * @return 增加小时后的日期
     */
    fun addHour(date: Date?, amount: Int): Date? = addInteger(date, Calendar.HOUR_OF_DAY, amount)

    /**
     * 增加日期的分钟。失败返回null。
     *
     * @param dateStr       日期字符串
     * @param amount 增加数量。可为负数
     * @return 增加分钟后的日期字符串
     */
    fun addMinute(dateStr: String?, amount: Int): String? = addInteger(dateStr, Calendar.MINUTE, amount)

    /**
     * 增加日期的分钟。失败返回null。
     *
     * @param date       日期
     * @param amount 增加数量。可为负数
     * @return 增加分钟后的日期
     */
    fun addMinute(date: Date?, amount: Int): Date? = addInteger(date, Calendar.MINUTE, amount)

    /**
     * 增加日期的秒钟。失败返回null。
     *
     * @param dateStr       日期字符串
     * @param amount 增加数量。可为负数
     * @return 增加秒钟后的日期字符串
     */
    fun addSecond(dateStr: String?, amount: Int): String? = addInteger(dateStr, Calendar.SECOND, amount)

    /**
     * 增加日期的秒钟。失败返回null。
     *
     * @param date       日期
     * @param amount 增加数量。可为负数
     * @return 增加秒钟后的日期
     */
    fun addSecond(date: Date?, amount: Int): Date? = addInteger(date, Calendar.SECOND, amount)

    /**
     * 获取日期的年份。失败返回0。
     *
     * @param dateStr 日期字符串
     * @return 年份
     */
    fun getYear(dateStr: String?): Int {
        val formatStr = getDateFormate(dateStr)
        return if (StringUtil.isNotBlank(formatStr)) {
            getYear(parseDate(dateStr, formatStr!!))
        } else {
            0
        }
    }

    /**
     * 获取日期的年份。失败返回0。
     *
     * @param date 日期
     * @return 年份
     */
    fun getYear(date: Date?): Int = getInteger(date, Calendar.YEAR)

    /**
     * 获取日期的月份。失败返回0。
     *
     * @param dateStr 日期字符串
     * @return 月份
     */
    fun getMonth(dateStr: String?): Int {
        val formatStr = getDateFormate(dateStr)
        return if (StringUtil.isNotBlank(formatStr)) {
            getMonth(parseDate(dateStr, formatStr!!))
        } else {
            0
        }
    }

    /**
     * 获取日期的月份。失败返回0。
     *
     * @param date 日期
     * @return 月份
     */
    fun getMonth(date: Date?): Int = getInteger(date, Calendar.MONTH) + 1

    /**
     * 获取日期的哪一天。失败返回0。
     *
     * @param dateStr 日期
     * @return 月份
     */
    fun getDayOfMonth(dateStr: String?): Int {
        val formatStr = getDateFormate(dateStr)
        return if (StringUtil.isNotBlank(formatStr)) {
            getDayOfMonth(parseDate(dateStr, formatStr!!))
        } else {
            0
        }
    }

    /**
     * 获取日期的哪一天。失败返回0。
     *
     * @param date 日期
     * @return 月份
     */
    fun getDayOfMonth(date: Date?) = getInteger(date, Calendar.DAY_OF_MONTH)

    /**
     * 获取日期的天数。失败返回0。
     *
     * @param dateStr 日期字符串
     * @return 天
     */
    fun getDay(dateStr: String?): Int {
        val formatStr = getDateFormate(dateStr)
        return if (StringUtil.isNotBlank(formatStr)) {
            getDay(parseDate(dateStr, formatStr!!))
        } else {
            0
        }
    }

    /**
     * 获取日期的天数。失败返回0。
     *
     * @param date 日期
     * @return 天
     */
    fun getDay(date: Date?): Int = getInteger(date, Calendar.DATE)

    /**
     * 获取日期的小时。失败返回0。
     *
     * @param dateStr 日期字符串
     * @return 小时
     */
    fun getHour(dateStr: String?): Int {
        val formatStr = getDateFormate(dateStr)
        return if (StringUtil.isNotBlank(formatStr)) {
            getHour(parseDate(dateStr, formatStr!!))
        } else {
            0
        }
    }

    /**
     * 获取日期的小时。失败返回0。
     *
     * @param date 日期
     * @return 小时
     */
    fun getHour(date: Date?): Int = getInteger(date, Calendar.HOUR_OF_DAY)

    /**
     * 获取日期的分钟。失败返回0。
     *
     * @param dateStr 日期字符串
     * @return 分钟
     */
    fun getMinute(dateStr: String?): Int {
        val formatStr = getDateFormate(dateStr)
        return if (StringUtil.isNotBlank(formatStr)) {
            getMinute(parseDate(dateStr, formatStr!!))
        } else {
            0
        }
    }

    /**
     * 获取日期的分钟。失败返回0。
     *
     * @param date 日期
     * @return 分钟
     */
    fun getMinute(date: Date?): Int = getInteger(date, Calendar.MINUTE)

    /**
     * 获取日期的秒钟。失败返回0。
     *
     * @param dateStr 日期字符串
     * @return 秒钟
     */
    fun getSecond(dateStr: String?): Int {
        val formatStr = getDateFormate(dateStr)
        return if (StringUtil.isNotBlank(formatStr)) {
            getSecond(parseDate(dateStr, formatStr!!))
        } else {
            0
        }
    }

    /**
     * 获取日期的秒钟。失败返回0。
     *
     * @param date 日期
     * @return 秒钟
     */
    fun getSecond(date: Date?): Int = getInteger(date, Calendar.SECOND)

    /**
     * 月日时分秒，0-9前补0
     *
     * @param number the number
     * @return
     */
    fun fillZero(number: Int): String = if (number < 10) "0" + number.toString() else number.toString()

    /**
     * 截取掉前缀0以便转换为整数
     *
     * @param dateStr
     */
    fun trimZero(dateStr: String): Int {
        return try {
            var str = dateStr
            if (dateStr.startsWith("0")) {
                str = dateStr.substring(1)
            }
            str.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }
}