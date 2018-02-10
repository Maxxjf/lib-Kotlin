package com.qcloud.qclib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * 类说明：用SharedPreferences 存储一些简单属性 .
 * Author: Kuzan
 * Date: 2017/12/2 15:01.
 */
@SuppressLint("CommitPrefEdits")
object SharedUtil {
    @Volatile
    private var mSharedPreferences: SharedPreferences? = null

    @Synchronized
    fun initSharedPreferences(context: Context): SharedPreferences? {
        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        }
        return mSharedPreferences
    }

    /**
     * 写入一个String值
     *
     * @param key           key
     * @param value         值
     * */
    fun writeString(key: String, value: String?) {
        val editor = mSharedPreferences?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    /**
     * 获取String值
     *
     * @param key           key
     * @param defaultValue  默认值，可不传
     * */
    fun getString(key: String, defaultValue: String? = null): String? = mSharedPreferences?.getString(key, defaultValue)

    /**
     * 写入一个Int值
     *
     * @param key           key
     * @param value         值
     * */
    fun writeInt(key: String, value: Int) {
        val editor = mSharedPreferences?.edit()
        editor?.putInt(key, value)
        editor?.apply()
    }

    /**
     * 获取Int值
     *
     * @param key           key
     * @param defaultValue  默认值，可不传，默认返回0
     * */
    fun getInt(key: String, defaultValue: Int = 0): Int =
            if (mSharedPreferences != null) {
                mSharedPreferences!!.getInt(key, defaultValue)
            } else {
                defaultValue
            }

    /**
     * 写入一个Boolean值
     *
     * @param key           key
     * @param value         值
     */
    fun writeBoolean(key: String, value: Boolean) {
        val editor = mSharedPreferences?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    /**
     * 获取Boolean值
     *
     * @param key           key
     * @param defaultValue  默认值，可不传，默认返回false
     * */
    fun getBoolean(key: String, defaultValue: Boolean = false) =
            if (mSharedPreferences != null) {
                mSharedPreferences!!.getBoolean(key, defaultValue)
            } else {
                defaultValue
            }

    /**
     * 写入一个Long值
     *
     * @param key           key
     * @param value         值
     */
    fun writeLong(key: String, value: Long) {
        val editor = mSharedPreferences?.edit()
        editor?.putLong(key, value)
        editor?.apply()
    }

    /**
     * 获取Long值
     *
     * @param key           key
     * @param defaultValue  默认值，可不传，默认返回-1L
     * */
    fun getLong(key: String, defaultValue: Long = -1L): Long =
            if (mSharedPreferences != null) {
                mSharedPreferences!!.getLong(key, defaultValue)
            } else {
                defaultValue
            }

    /**
     * 写入一个Float值
     *
     * @param key           key
     * @param value         值
     */
    fun writeFloat(key: String, value: Float) {
        val editor = mSharedPreferences?.edit()
        editor?.putFloat(key, value)
        editor?.apply()
    }

    /**
     * 获取Float值
     *
     * @param key           key
     * @param defaultValue  默认值，可不传，默认返回0f
     * */
    fun getLong(key: String, defaultValue: Float = 0F): Float =
            if (mSharedPreferences != null) {
                mSharedPreferences!!.getFloat(key, defaultValue)
            } else {
                defaultValue
            }
}