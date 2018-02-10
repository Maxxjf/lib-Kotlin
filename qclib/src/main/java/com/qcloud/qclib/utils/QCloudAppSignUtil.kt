package com.qcloud.qclib.utils

import java.util.*

/**
 * 类说明：旗云app签名生成工具
 * Author: Kuzan
 * Date: 2017/11/23 16:59.
 */
object QCloudAppSignUtil {
    private val charArray = charArrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z')

    /**
     * 生成签名
     *
     * @param sign app签名
     * @param key  签名关键字
     * */
    fun signParamStr(sign: String?, key: String?): String {
        if (StringUtil.isBlank(sign)) {
            return ""
        }
        if (StringUtil.isBlank(key)) {
            return EncryptUtil.md5(sign!!)
        }
        return EncryptUtil.md5(sign + "@" + key)
    }

    /**
     * 解密
     *
     * @param str
     * */
    fun decryptParamStr(str: String?): Boolean {
        if (StringUtil.isBlank(str)) {
            return false
        }
        if (str!!.length != 24) {
            return false
        }

        val first = str[0]
        val firstIndex = getCharIndex(first)
        if (firstIndex == -1 || firstIndex > 23) {
            return false
        }

        val s = str[firstIndex]
        var sIndex = getCharIndex(s)
        if (sIndex == -1 || sIndex > 23) {
            return false
        }

        val sum = first.plus(s.toInt())
        var j = firstIndex + 1

        for (index in 0..10) {
            val nextIndex = if (j > 23) j%23 else j
            val nextJndex = if (nextIndex+11 > 23) (nextIndex+11)%23 else nextIndex+11
            val c1 = str[nextIndex]
            val c2 = str[nextJndex]
            val c3 = charArray[(c1+sum.toInt()+index).toInt()%25]
            if (c2 != c3) {
                return false
            }
            j++
        }

        return true
    }

    /**
     * 加密
     *
     * @return 加密后的字符串
     * */
    fun encryptCharStr(): String {
        val ca = CharArray(24)
        // index 从1开始
        val index = Random().nextInt(23) + 1
        val jndex = Random().nextInt(24)

        ca[0] = charArray[index]
        ca[index] = charArray[jndex]

        var j = index + 1
        val sum = ca[0] + ca[index].toInt()

        for (index in 0..10) {
            val next = Random().nextInt(26)
            val nextIndex = if (j > 23) j%23 else j
            val nextJndex = if ((nextIndex+11) > 23) (nextIndex+11)%23 else nextIndex+11
            ca[nextIndex] = charArray[next]
            ca[nextJndex] = charArray[(ca[nextIndex] + sum.toInt() + index).toInt()%25]
            j++
        }

        return String(ca)
    }

    private fun getCharIndex(c: Char): Int = charArray.indices.firstOrNull { charArray[it] == c } ?: -1
}