package com.qcloud.qclib.utils

import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

/**
 * 类说明：小米系统性质
 * Author: Kuzan
 * Date: 2017/12/8 11:39.
 */
class MiuiProperties @Throws(IOException::class)
constructor() {
    private val properties: Properties = Properties()

    init {
        properties.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
    }

    val isEmpty: Boolean
        get() = properties.isEmpty

    fun containsKey(key: Any): Boolean {
        return properties.containsKey(key)
    }

    fun containsValue(value: Any): Boolean {
        return properties.containsValue(value)
    }

    fun getProperty(name: String, defaultValue: String = ""): String {
        return properties.getProperty(name, defaultValue)
    }

    fun keys(): Enumeration<Any> {
        return properties.keys()
    }

    fun keySet(): Set<Any> {
        return properties.keys
    }

    fun size(): Int {
        return properties.size
    }

    fun values(): Collection<Any> {
        return properties.values
    }

    companion object {
        @Throws(IOException::class)
        fun newInstance(): MiuiProperties {
            return MiuiProperties()
        }
    }
}