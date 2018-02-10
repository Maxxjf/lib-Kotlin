package com.qcloud.qclib.utils

import android.app.AppOpsManager
import android.content.Context
import android.os.Build

/**
 * 类说明：权限工具类
 * Author: Kuzan
 * Date: 2017/12/9 16:35.
 */
object PermissionUtil {
    private const val CHECK_OP_NO_THROW = "checkOpNoThrow"
    private const val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"

    /**
     * 用来判断是否开启通知权限
     *
     * @param context
     * @return
     * */
    fun isNotificationEnabled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val appInfo = context.applicationInfo
            val pkg = context.applicationContext.packageName
            val uid = appInfo.uid

            return try {
                val appOpsClass = Class.forName(AppOpsManager::class.java.name)
                val method = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String::class.java)
                val field = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)
                val value = field.get(Int::class.java) as Int
                return method.invoke(appOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED
            } catch (e: Exception) {
                false
            }
        } else {
            return false
        }
    }
}