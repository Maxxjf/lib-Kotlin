package com.qcloud.qclib.widget.customview.wheelview.entity

/**
 * 类说明：日期时间选择器枚举
 * Author: Kuzan
 * Date: 2017/9/5 13:54.
 */
enum class DateTimeEnum constructor(val key: Int, val value: String) {
    /**不显示 */
    NONE(-1, "不显示"),
    /**年月日 */
    YEAR_MONTH_DAY(0, "年月日"),
    /**年月 */
    YEAR_MONTH(1, "年月"),
    /**月日 */
    MONTH_DAY(2, "月日"),
    /**24小时 */
    HOUR_24(3, "24小时"),
    /**12小时 */
    HOUR_12(4, "12小时");

    companion object {
        fun valueOf(key: Int): DateTimeEnum {
            return when (key) {
                -1 -> NONE
                0 -> YEAR_MONTH_DAY
                1 -> YEAR_MONTH
                2 -> MONTH_DAY
                3 -> HOUR_24
                4 -> HOUR_12
                else -> NONE
            }
        }
    }
}
