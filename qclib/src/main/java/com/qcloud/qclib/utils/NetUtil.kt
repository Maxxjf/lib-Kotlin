package com.qcloud.qclib.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * 类说明：网络相关的工具类
 * Author: Kuzan
 * Date: 2017/12/6 19:04.
 */
@SuppressLint("MissingPermission")
object NetUtil {

    /**
     * 判断网络是否连接
     *
     * @param context 上下文
     */
    fun isConnected(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.activeNetworkInfo
        if (info != null && info.isConnected) {
            if (info.state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }

    /**
     * 判断是否是wifi连接
     *
     * @param context
     */
    fun isWifi(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.activeNetworkInfo
        if (info != null && info.isConnected) {
            if (info.type == ConnectivityManager.TYPE_WIFI) {
                return true
            }
        }
        return false
    }

    /**
     * 打开网络设置界面
     */
    fun openSetting(activity: Activity) {
        val intent = Intent("/")
        val cn = ComponentName("com.android.settings", "com.android.settings.WirelessSettings")
        intent.component = cn
        intent.action = "android.intent.action.VIEW"
        activity.startActivityForResult(intent, 0)
    }
}