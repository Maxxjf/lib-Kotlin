package com.qcloud.qclib.utils

/**
 * 类说明：Byte工具类
 * Author: Kuzan
 * Date: 2017/11/23 16:54.
 */
object ByteUtil {

    /**
     * byte数组转成字符串
     *
     * @param bytes 数组
     * @param splitStr 分隔符
     * @return
     * */
    fun bytes2Str(bytes: ByteArray, splitStr: String): String {
        val sb = StringBuffer()
        for (i in bytes.indices) {
            sb.append(Integer.toHexString(i and 0xff))
            sb.append(splitStr)
        }
        if (sb.length >= splitStr.length) {
            sb.delete(sb.length - splitStr.length, sb.length)
        }
        return sb.toString()
    }

    /**
     * 将字节数组转换成16进制字符
     *
     * @param bytes 需要转换的字节数组
     * @return 转换后的16进制字符
     * */
    fun byte2hex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (n in bytes.indices) {
            val stmp = Integer.toHexString(n and 0xff)
            if (stmp.length == 1) {
                sb.append("0")
            }
            sb.append(stmp)
        }
        return sb.toString().toUpperCase()
    }
}