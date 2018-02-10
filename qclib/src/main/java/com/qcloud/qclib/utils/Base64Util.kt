package com.qcloud.qclib.utils

import android.util.Base64
import java.io.*

/**
 * 类说明：Base64工具类
 * Author: Kuzan
 * Date: 2017/11/23 17:54.
 */
object Base64Util {
    /**
     * String 转Base64
     * */
    fun stringToBase64(str: String?): String {

        if (StringUtil.isBlank(str)) {
            return ""
        }

        var base = ""
        val baos = ByteArrayOutputStream()
        try {
            // 创建对象输出流，并封装字节流
            val oos = ObjectOutputStream(baos)
            // 将对象写入字节流
            oos.writeObject(str)
            // 将字节流编码成base64的字符窜
            base = String(Base64.encode(baos.toByteArray(), 0))
            oos.close()
            baos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return base
    }

    /**
     * Base64转String
     *
     * @param base
     * */
    fun base64ToString(base: String?): String {
        if (StringUtil.isBlank(base)) {
            return ""
        }

        var str = ""
        val base64: ByteArray = Base64.decode(base, 0) ?: return ""

        //封装到字节流
        val bais = ByteArrayInputStream(base64)
        try {
            //再次封装
            val bis = ObjectInputStream(bais)
            try {
                //读取对象
                str = bis.readObject() as String
                bis.close()
                bais.close()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        } catch (e: StreamCorruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return str
    }
}