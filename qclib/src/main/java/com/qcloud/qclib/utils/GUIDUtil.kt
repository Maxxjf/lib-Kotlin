package com.qcloud.qclib.utils

import java.util.*

/**
 * 类说明：UUID生成工具类
 * Author: Kuzan
 * Date: 2018/4/10 11:29.
 */
object GUIDUtil {
    /**
     * 生成UUID
     * */
    fun createUUID(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }

    /**
     * 获取UUID，不带-
     * */
    fun getUUIDStr(): String {
        val uuid = createUUID()
        return StringUtil.replace(uuid, "-", "") ?: ""
    }

    /**
     * 将UUID字母转数字
     * */
    fun UUID2Number(uuid: String): String {
        var sb = StringBuffer()
        for (i in 0 until uuid.length) {
            val value = uuid.substring(i, i + 1)
            if (StringUtil.isNumberStr(value)) {
                sb.append(value)
            } else {
                sb.append(createRandom())
            }
        }
        return sb.toString()
    }

    /**
     * 生成0~9的随机数
     * */
    private fun createRandom(): Int {
        val random = Random()
        return random.nextInt(10)
    }
}