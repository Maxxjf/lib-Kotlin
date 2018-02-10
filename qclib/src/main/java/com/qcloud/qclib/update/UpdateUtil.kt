package com.qcloud.qclib.update

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Environment
import com.qcloud.qclib.toast.QToast
import com.qcloud.qclib.utils.FileUtil
import com.qcloud.qclib.utils.StringUtil
import java.io.File

/**
 * 类说明：版本更新工具类
 * Author: Kuzan
 * Date: 2017/6/17 10:32.
 */
object UpdateUtil {
    /**
     * 启动下载
     *
     * @param context
     * @param downLoadUrl apk下载链接
     */
    fun startUpgrade(context: Context, downLoadUrl: String) {
        if (StringUtil.isNotBlank(downLoadUrl)) {
            val intent = Intent(context, UpdateService::class.java)
            intent.putExtra("downLoadUrl", downLoadUrl)
            intent.action = UpdateService.DOWN_ACTION_START
            context.startService(intent)
        } else {
            QToast.show(context, "下载链接为空")
        }
    }

    /**
     * 停止下载
     *
     * @param context
     */
    fun stopUpgrade(context: Context) {
        val intent = Intent(context, UpdateService::class.java)
        intent.action = UpdateService.DOWN_ACTION_STOP
        context.startService(intent)
    }

    /**
     * 获取应用版本名
     *
     * @param context
     * @return
     */
    fun getVersionName(context: Context): String? {
        val packageManager = context.packageManager
        val packInfo: PackageInfo
        try {
            packInfo = packageManager.getPackageInfo(context.packageName, 0)
            return packInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    fun getVersionCode(context: Context): Int {
        val packageManager = context.packageManager
        val packInfo: PackageInfo
        try {
            packInfo = packageManager.getPackageInfo(context.packageName, 0)
            return packInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return -1
    }

    /**
     * 获取app名称
     *
     * @param context
     * @return
     */
    fun getAppName(context: Context): String {
        val packageManager = context.packageManager
        val packInfo: PackageInfo
        try {
            packInfo = packageManager.getPackageInfo(context.packageName, 0)
            val res = packInfo.applicationInfo.labelRes
            return context.resources.getString(res)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * 获取应用启动activity名字
     */
    fun getLauncherActivityName(context: Context): String {
        val resolveIntent = Intent(Intent.ACTION_MAIN, null)
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        resolveIntent.`package` = context.packageName
        val pManager = context.packageManager
        val apps = pManager.queryIntentActivities(resolveIntent, 0)
        val ri = apps.iterator().next()
        return if (ri != null) {
            ri.activityInfo.name
        } else ""
    }

    /**
     * 获取保存更新apk的文件路径
     *
     * @param context
     * @return
     */
    fun getLocalFilePath(context: Context): String {
        return if (FileUtil.isSdCardExist()) {
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath + File.separator + context.packageName + ".apk"
        } else ""
    }
}
