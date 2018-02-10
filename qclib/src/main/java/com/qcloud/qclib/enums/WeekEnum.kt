package com.qcloud.qclib.enums

/**
 * 类说明：星期枚举
 * Author: Kuzan
 * Date: 2017/11/27 11:50.
 */
enum class WeekEnum
constructor(var key: Int,
            var nameZh: String,
            var nameEn: String,
            var nameEnShort: String,
            var number: Int) {
    MONDAY(1, "星期一", "Monday", "Mon.", 1),
    TUESDAY(2, "星期二", "Tuesday", "Tues.", 2),
    WEDNESDAY(3, "星期三", "Wednesday", "Wed.", 3),
    THURSDAY(4, "星期四", "Thursday", "Thur.", 4),
    FRIDAY(5, "星期五", "Friday", "Fri.", 5),
    SATURDAY(6, "星期六", "Saturday", "Sat.", 6),
    SUNDAY(0, "星期日", "Sunday", "Sun.", 7);

    companion object {
        fun keyOf(key: Int): WeekEnum = when(key) {
            1 -> MONDAY
            2 -> TUESDAY
            3 -> WEDNESDAY
            4 -> THURSDAY
            5 -> FRIDAY
            6 -> SATURDAY
            else -> SUNDAY
        }

        fun numberOf(number: Int): WeekEnum = when(number) {
            1 -> MONDAY
            2 -> TUESDAY
            3 -> WEDNESDAY
            4 -> THURSDAY
            5 -> FRIDAY
            6 -> SATURDAY
            7 -> SUNDAY
            else -> SUNDAY
        }
    }
}