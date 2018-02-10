package com.qcloud.qclib.utils

import java.security.MessageDigest

/**
 * 类说明：md5加密解密工具
 * Author: Kuzan
 * Date: 2017/11/23 17:03.
 */
object EncryptUtil {
    /**
     * md5加密</br>
     *
     * @praram str 需要进行md5加密的字符
     * @return 已进行md5的加密的字符
     */
    fun md5(str: String): String = encode(str, "MD5")

    /**
     * md5加密 - 大写
     *
     * @param str str 需要进行md5加密的字符
     * @return 已进行md5的加密的字符
     * */
    fun md5ToUpperCase(str: String): String = encode(str, "MD5").toUpperCase()

    /**
     * sha1 加密
     *
     * @praram str 需要进行sha1加密的字符
     * @return 已进行sha1的加密的字符
     */
    fun sha1(str: String): String = encode(str, "SHA-1")

    /**
     * 按类型对字符串进行加密并转换成16进制输出
     *
     * @param str 字符串
     * @param type 可加密类型md5, des , sha1
     * @return 加密后的字符串
     * */
    private fun encode(str: String, type: String): String {
        try {
            val alga = MessageDigest.getInstance(type)
            alga.update(str.toByteArray())
            val digesta = alga.digest()

            return ByteUtil.byte2hex(digesta)
        } catch (e: Exception) {
        }

        return ""
    }
}