package com.qcloud.qclib.utils

import java.io.*
import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.and

/**
 * 类说明：数据类型转换、单位转换
 * Author: Kuzan
 * Date: 2017/12/4 11:50.
 */
object ConvertUtil {
    /**
     * 转成int类型
     *
     * @param obj 要转换的数据
     * @param defaultValue 默认值，可不传，则默认为0
     * */
    fun toInt(obj: Any, defaultValue: Int = 0): Int {
        return try {
            Integer.parseInt(obj.toString())
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }

    /**
     * byte数组转int
     *
     * @param bytes byte 数组
     * */
    fun toInt(bytes: ByteArray, defaultValue: Int): Int = bytes.sumBy { (it and 0xFF.toByte()).toInt() shl (8 * it) }

    /**
     * 转短整型
     *
     * @param first 字节1
     * @param second 字节2
     * */
    fun toShort(first: Byte, second: Byte): Int = (first.toInt() shl 8) + (second and  0xFF.toByte())

    /**
     * 转长整型
     *
     * @param obj 要转的对象
     * @param defaultValue 默认值
     * */
    fun toLong(obj: Any, defaultValue: Long = 0L): Long {
        return try {
            java.lang.Long.parseLong(obj.toString())
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }

    /**
     * 转float类型
     *
     * @param obj 要转的对象
     * @param defaultValue 默认值
     * */
    fun toFloat(obj: Any, defaultValue: Float = 0F): Float {
        return try {
            java.lang.Float.parseFloat(obj.toString())
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }

    /**
     * 转字节数组
     *
     * @param 数值
     * @return byte [ ]
     */
    fun toByteArray(number: Int): ByteArray = ByteBuffer.allocate(4).putInt(number).array()

    /**
     * 16进制转字节数组
     *
     * @param hexStr 16进制数
     * @param isHex 是否16进制数
     * */
    fun toByteArray(hexStr: String, isHex: Boolean = true): ByteArray {
        if (!isHex) {
            return hexStr.toByteArray()
        }
        val hexData = hexStr.replace("\\s+".toRegex(), "")
        val hexDigits = "0123456789ABCDEF"

        val baos = ByteArrayOutputStream(hexData.length / 2)
        // 将每2位16进制整数组装成一个字节
        var i = 0
        while (i < hexData.length) {
            baos.write(hexDigits.indexOf(hexData[i]) shl 4 or hexDigits
                    .indexOf(hexData[i + 1]))
            i += 2
        }
        val bytes = baos.toByteArray()
        try {
            baos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bytes
    }

    /**
     * 十六进制转十进制
     *
     * @param hexStr 16进制数
     * @param defaultValue 默认值
     * */
    fun hexToInt(hexStr: String, defaultValue: Int = 0): Int {
        return try {
            Integer.parseInt(hexStr, 16)
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }

    /**
     * 转16进制
     *
     * @param bytes byte数组
     * @return 16进制
     */
    fun toHexString(vararg bytes: Byte): String {
        val digits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
        val buffer = CharArray(bytes.size * 2)
        var i = 0
        var j = 0
        while (i < bytes.size) {
            // 转无符号整型
            val k = if (bytes[i] < 0) bytes[i] + 256 else bytes[i].toInt()
            buffer[j++] = digits[k.ushr(4)]
            buffer[j++] = digits[k and  0xf]
            ++i
        }
        return String(buffer)
    }

    /**
     * int 转16进制
     *
     * @param number 数值
     * @return 16进制数
     */
    fun toHexString(number: Int): String = Integer.toHexString(number)

    /**
     * 转二进制
     *
     * @param bytes byte数组
     * @return 16进制数
     */
    fun toBinaryString(vararg bytes: Byte): String {
        val digits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
        val buffer = CharArray(bytes.size * 8)
        var i = 0
        var j = 0
        while (i < bytes.size) {
            val k = if (bytes[i] < 0) bytes[i] + 256 else bytes[i].toInt()
            buffer[j++] = digits[k.ushr(7) and 0x1]
            buffer[j++] = digits[k.ushr(6) and 0x1]
            buffer[j++] = digits[k.ushr(5) and 0x1]
            buffer[j++] = digits[k.ushr(4) and 0x1]
            buffer[j++] = digits[k.ushr(3) and 0x1]
            buffer[j++] = digits[k.ushr(2) and 0x1]
            buffer[j++] = digits[k.ushr(1) and 0x1]
            buffer[j++] = digits[k and 0x1]
            ++i
        }
        return String(buffer)
    }

    /**
     * 转二进制
     *
     * @param number 数值
     * @return 二进制数
     */
    fun toBinaryString(number: Int): String = Integer.toBinaryString(number)

    /**
     * list 转 array
     *
     * @param list
     * */
    inline fun <reified T> listToArray(list: List<T>): Array<T> = list.toTypedArray()

    /**
     * array 转 list
     *
     * @param array
     * */
    fun <T> arrayToList(array: Array<T>): List<T> = Arrays.asList(*array)

    /**
     * array 转 String
     *
     * @param objs
     * */
    fun arrayToString(objs: Array<Any>): String = Arrays.deepToString(objs)

    /**
     * 转字节数组
     *
     * @param iStream
     * */
    fun toByteArray(iStream: InputStream): ByteArray? {
        return try {
            val baos = ByteArrayOutputStream()
            val buff = ByteArray(100)
            while (true) {
                val len = iStream.read(buff, 0, 100)
                if (len == -1) {
                    break
                } else {
                    baos.write(buff, 0, len)
                }
            }
            val bytes = baos.toByteArray()
            baos.close()
            iStream.close()
            return bytes
        } catch (e: IOException) {
            null
        }
    }

    /**
     * String的字符串转换成unicode的String
     * @param str 全角字符串
     * @return String 每个unicode之间无分隔符
     * @throws Exception
     */
    fun stringToUnicode(str: String): String? {
        val sb = StringBuilder()
        for (i in 0 until str.length) {
            val c = str[i]
            val intAsc = c.toInt()
            val strHex = Integer.toHexString(intAsc)
            if (intAsc > 128) {
                sb.append("\\u" + strHex)
            } else {
                // 低位在前面补00
                sb.append("\\u00" + strHex)
            }
        }
        return String(sb)
    }

    /**
     * unicode的String转换成String的字符串
     * @param hexStr 16进制值字符串 （一个unicode为2byte）
     * @return String 全角字符串
     */
    fun unicodeToString(hexStr: String): String {
        val len = hexStr.length / 6
        val sb = StringBuilder()

        for (i in 0 until len) {
            val s = hexStr.substring(i*6, (i+1) * 6)
            // 高位需要补上00再转
            val s1 = s.substring(2, 4) + "00"
            // 低位直接转
            val s2 = s.substring(4)
            // 将16进制的string转为int
            val n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16)
            // 将int转换为字符
            val chars = Character.toChars(n)
            sb.append(String(chars))
        }
        return String(sb)
    }

    /**
     * 字符转换
     *
     * utf 转 gbk
     *
     * @param str
     * @param nowCharset "utf-8"
     * @param toCharset "gbk"
     * */
    fun charsetCovert(str: String, nowCharset: String, toCharset: String): String {
        return try {
            String(str.toByteArray(charset(nowCharset)), charset(toCharset))
        } catch (e: UnsupportedEncodingException) {
            str
        }
    }

    /**
     * 流转String
     *
     * @param iStream
     * */
    fun inputStreamToString(iStream: InputStream, charset: String = "utf-8"): String? {
        return try {
            val sb = StringBuilder()
            val reader = BufferedReader(InputStreamReader(iStream, charset))
            while (true) {
                val line = reader.readLine()
                if (line == null) {
                    break
                } else {
                    sb.append(line).append("\n")
                }
            }
            reader.close()
            iStream.close()

            String(sb)
        } catch (e: IOException) {
            null
        }
    }
}