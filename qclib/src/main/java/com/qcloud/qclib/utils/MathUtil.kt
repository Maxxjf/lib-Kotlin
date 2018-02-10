package com.qcloud.qclib.utils

import java.util.*

/**
 * 类说明：数据工具类
 * Author: Kuzan
 * Date: 2017/12/18 11:30.
 */
object MathUtil {
    /**
     * 随机数
     *
     * @param 区间最大值
     * */
    fun randomNum(range : Int): Double = Math.random() * range

    /**
     * 随机数
     *
     * @param maxValue 最大值
     * @param minValue 最小值
     * */
    fun randomNum(maxValue: Int, minValue: Int = 0): Int {
        val max = maxValue + 1
        val min = minValue + 1
        val random = Random()
        val result = random.nextInt(max)%(max-min+1) + min
        return result - 1
    }
}