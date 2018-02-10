package com.qcloud.qclib.enums

/**
 * 类说明：日期时间格式枚举
 * Author: Kuzan
 * Date: 2017/11/27 11:57.
 */
enum class DateStyleEnum
constructor(var value: String) {
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
    MM_DD_HH_MM_SS("MM-dd HH:mm:ss"),
    MM_DD_HH_MM("MM-dd HH:mm"),
    YYYY_MM_DD("yyyy-MM-dd"),
    YYYY_MM("yyyy-MM"),
    MM_DD("MM-dd"),
    HH_MM_SS("HH:mm:ss"),
    HH_MM("HH:mm"),

    YYYY_MM_DD_HH_MM_SS_EN("yyyy/MM/dd HH:mm:ss"),
    YYYY_MM_DD_HH_MM_EN("yyyy/MM/dd HH:mm"),
    MM_DD_HH_MM_SS_EN("MM/dd HH:mm:ss"),
    MM_DD_HH_MM_EN("MM/dd HH:mm"),
    YYYY_MM_DD_EN("yyyy/MM/dd"),
    YYYY_MM_EN("yyyy/MM"),
    MM_DD_EN("MM/dd"),

    YYYY_MM_DD_HH_MM_SS_CN("yyyy年MM月dd日 HH:mm:ss"),
    YYYY_MM_DD_HH_MM_CN("yyyy年MM月dd日 HH:mm"),
    MM_DD_HH_MM_SS_CN("MM月dd日 HH:mm:ss"),
    MM_DD_HH_MM_CN("MM月dd日 HH:mm"),
    YYYY_MM_DD_CN("yyyy年MM月dd日"),
    YYYY_MM_CN("yyyy年MM月"),
    MM_DD_CN("MM月dd日"),

    MM_DD_HK("MM.dd"),
    YYYY_MM_HK("yyyy.MM"),
    YYYY_MM_DD_HK("yyyy.MM.dd"),
    MM_DD_HH_MM_HK("MM.dd HH:mm"),
    MM_DD_HH_MM_SS_HK("MM.dd HH:mm:ss"),
    YYYY_MM_DD_HH_MM_HK("yyyy.MM.dd HH:mm"),
    YYYY_MM_DD_HH_MM_SS_HK("yyyy.MM.dd HH:mm:ss"),

    DB_DATA_FORMAT("yyyy-MM-DD HH:mm:ss"),
    LOCALE_DATE_FORMAT("yyyy年M月d日 HH:mm:ss"),
    NEWS_ITEM_DATE_FORMAT("hh:mm M月d日 yyyy"),

    FILE_NAME_FORMAT("yyyyMMdd_HHmmss");
}