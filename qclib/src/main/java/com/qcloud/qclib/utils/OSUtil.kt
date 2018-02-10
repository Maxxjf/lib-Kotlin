package com.qcloud.qclib.utils

import android.annotation.SuppressLint
import android.os.Build
import java.io.IOException

/**
 * 类说明：手机系统判断
 * Author: Kuzan
 * Date: 2017/12/7 10:34.
 */
object OSUtil {
    // 小米
    private const val KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code"
    private const val KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name"
    private const val KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage"
    // 华为
    private const val KEY_EMUI_VERSION_NAME = "ro.build.version.emui"
    // 魅族
    private const val KEY_DISPLAY = "ro.build.display.id"

    /**
     * 判断是否为miui
     * Is miui boolean.
     *
     * @return the boolean
     */
    fun isMiui(): Boolean {
        val property = getSystemProperty(KEY_MIUI_VERSION_NAME)
        return StringUtil.isNotBlank(property)
    }

    /**
     * 判断手机是否是小米
     *
     * @return
     */
    fun isMIUI(): Boolean {
        return try {
            val prop = MiuiProperties.newInstance()
            val code = prop.getProperty(KEY_MIUI_VERSION_CODE)
            val name = prop.getProperty(KEY_MIUI_VERSION_NAME)
            val storage = prop.getProperty(KEY_MIUI_INTERNAL_STORAGE)

            return StringUtil.isNotBlank(code) || StringUtil.isNotBlank(name) || StringUtil.isNotBlank(storage)
        } catch (e: IOException) {
            false
        }
    }

    /**
     * 获得miui的版本
     * Gets miui version.
     *
     * @return the miui version
     */
    fun getMiuiVersion(): String = if (isMiui()) getSystemProperty(KEY_MIUI_VERSION_NAME) else ""

    /**
     * 判断miui版本是否大于等于6
     * Is miui 6 later boolean.
     *
     * @return the boolean
     */
    fun isMiui6Later(): Boolean {
        val version = getMiuiVersion()
        return StringUtil.isNotBlank(version) && Integer.valueOf(version.substring(1)) >= 6
    }

    /**
     * 判断是否为emui
     * Is emui boolean.
     *
     * @return the boolean
     */
    fun isEmui(): Boolean {
        val property = getSystemProperty(KEY_EMUI_VERSION_NAME)
        return StringUtil.isNotBlank(property)
    }

    /**
     * 得到emui的版本
     * Gets emui version.
     *
     * @return the emui version
     */
    fun getEmuiVersion(): String = if (isEmui()) getSystemProperty(KEY_EMUI_VERSION_NAME) else ""

    /**
     * 判断是否为emui3.1版本
     * Is emui 3 1 boolean.
     *
     * @return the boolean
     */
    fun isEmui3_1(): Boolean {
        val version = getEmuiVersion()
        return version == "EmotionUI 3" || version.contains("EmotionUI_3.1")
    }

    /**
     * 判断手机是否是魅族
     *
     * @return
     */
    fun isFlyme(): Boolean {
        return try {
            val method = Build::class.java.getMethod("hasSmartBar")

            return method != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 判断是否为flymeOS
     * Is flyme os boolean.
     *
     * @return the boolean
     */
    fun isFlymeOS(): Boolean = getFlymeOSFlag().toLowerCase().contains("flyme")

    /**
     * 判断flymeOS的版本是否大于等于4
     * Is flyme os 4 later boolean.
     *
     * @return the boolean
     */
    fun isFlymeOS4Later(): Boolean {
        val version = getFlymeOSVersion()
        if (StringUtil.isNotBlank(version)) {
            val num = if (version.toLowerCase().contains("os")) {
                Integer.valueOf(version.substring(9, 10))
            } else {
                Integer.valueOf(version.substring(6, 7))
            }
            if (num >= 4) {
                return true
            }
        }
        return false
    }

    /**
     * 判断flymeOS的版本是否等于5
     * Is flyme os 5 boolean.
     *
     * @return the boolean
     */
    fun isFlymeOS5(): Boolean {
        val version = getFlymeOSVersion()
        if (StringUtil.isNotBlank(version)) {
            val num = if (version.toLowerCase().contains("os")) {
                Integer.valueOf(version.substring(9, 10))
            } else {
                Integer.valueOf(version.substring(6, 7))
            }
            if (num == 4) {
                return true
            }
        }
        return false
    }

    /**
     * 得到flymeOS的版本
     * Gets flyme os version.
     *
     * @return the flyme os version
     */
    fun getFlymeOSVersion(): String = if (isFlymeOS()) getSystemProperty(KEY_DISPLAY) else ""

    /**
     * 获取flymeOS属性
     * */
    fun getFlymeOSFlag(): String = getSystemProperty(KEY_DISPLAY)

    /**
     * 获取系统属性
     *
     * @param key 系统名
     * @param defaultValue 默认值
     * */
    @SuppressLint("PrivateApi")
    fun getSystemProperty(key: String, defaultValue: String = ""): String {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val method = clazz.getMethod("get", String::class.java, String::class.java)
            return method.invoke(clazz, key, defaultValue) as String
        } catch (e: Exception) {
            defaultValue
        }
    }
}