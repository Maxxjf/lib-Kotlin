package com.qcloud.qclib.beans

import java.io.Serializable

/**
 * 类说明：权限
 * Author: Kuzan
 * Date: 2018/1/12 9:39.
 */
class PermissionBean constructor (
        /**权限名称*/
        val name: String,
        /**是否已获得权限*/
        val granted: Boolean,
        /**是否显示请求许可*/
        private val shouldShowRequestPermissionRationale: Boolean): Serializable {

    @SuppressWarnings("SimplifiableIfStatement")
    override fun equals(other: Any?): Boolean {
        if (this == other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as PermissionBean
        if (granted != that.granted) {
            return false
        }
        if (shouldShowRequestPermissionRationale != that.shouldShowRequestPermissionRationale) {
            return false
        }
        return name == that.name
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + if (granted) 1 else 0
        result = 31 * result + if (shouldShowRequestPermissionRationale) 1 else 0
        return result
    }

    override fun toString(): String {
        return "PermissionBean(name='$name', granted=$granted, shouldShowRequestPermissionRationale=$shouldShowRequestPermissionRationale)"
    }
}