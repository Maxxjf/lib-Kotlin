package com.qcloud.qclib.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.qcloud.qclib.beans.AppInfoBean
import java.io.File

/**
 * 类说明：App有关工具类
 * Author: Kuzan
 * Date: 2017/11/24 19:07.
 */
object AppUtil {

    /**
     * 打开App
     *
     * @param context     上下文
     * @param packageName 包名
     */
    fun launchApp(context: Context, packageName: String?) {
        if (StringUtil.isBlank(packageName)) {
            return
        }
        val intent = IntentUtil.getLaunchAppIntent(context, packageName!!)
        if (intent != null) {
            context.startActivity(intent)
        }
    }

    /**
     * 打开App
     *
     * @param activity    activity
     * @param packageName 包名
     * @param requestCode 请求值
     */
    fun launchApp(activity: Activity, packageName: String?, requestCode: Int) {
        if (StringUtil.isBlank(packageName)) return
        val intent = IntentUtil.getLaunchAppIntent(activity, packageName!!)
        if (intent != null) {
            activity.startActivityForResult(intent, requestCode)
        }
    }

    /**
     * 判断App是否安装
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 已安装<br>{@code false}: 未安装
     */
    fun isInstallApp(context: Context, packageName: String?) = StringUtil.isNotBlank(packageName) && IntentUtil.getLaunchAppIntent(context, packageName!!) != null

    /**
     * 安装App(支持6.0 7.0)
     *
     * @param context  上下文
     * @param filePath 文件路径
     * @param packageName 项目路径名
     */
    fun installApp(context: Context, filePath: String, packageName: String) = installApp(context, FileUtil.getFileByPath(filePath), packageName)


    /**
     * 安装App（支持6.0 7.0）
     *
     * @param context 上下文
     * @param file    文件
     * @param packageName 项目路径名
     */
    fun installApp(context: Context, file: File?, packageName: String) {
        if (!FileUtil.isFileExists(file)) return
        val intent = IntentUtil.getInstallAppIntent(context, file, packageName)
        if (intent != null) {
            context.startActivity(intent)
        }
    }

    /**
     * 安装App（支持6.0 7.0）
     *
     * @param activity    activity
     * @param filePath    文件路径
     * @param packageName 项目路径名
     * @param requestCode 请求值
     */
    fun installApp(activity: Activity, filePath: String, packageName: String, requestCode: Int) =
        installApp(activity, FileUtil.getFileByPath(filePath), packageName, requestCode)


    /**
     * 安装App(支持6.0 7.0)
     *
     * @param activity    activity
     * @param file        文件
     * @param packageName 项目路径名
     * @param requestCode 请求值
     */
    fun installApp(activity: Activity, file: File?, packageName: String, requestCode: Int) {
        if (!FileUtil.isFileExists(file)) return
        val intent = IntentUtil.getInstallAppIntent(activity, file, packageName)
        if (intent != null) {
            activity.startActivityForResult(intent, requestCode)
        }
    }

    /**
     * 获取App信息
     * <p>AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）</p>
     *
     * @param context 上下文
     * @return 当前应用的AppInfo
     */
    fun getAppInfo(context: Context): AppInfoBean? = getAppInfo(context, context.packageName)

    /**
     * 获取App信息
     * <p>AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）</p>
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 当前应用的AppInfo
     */
    fun getAppInfo(context: Context, packageName: String): AppInfoBean? =
        try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            getAppInfo(pm, pi)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }

    /**
     * 得到AppInfo的Bean
     *
     * @param pm 包的管理
     * @param pi 包的信息
     * @return AppInfo类
     */
    fun getAppInfo(pm: PackageManager?, pi: PackageInfo?): AppInfoBean? {
        if (pm == null || pi == null) return null

        val bean = AppInfoBean()

        val ai = pi.applicationInfo
        bean.name = ai.loadLabel(pm).toString()
        bean.icon = ai.loadIcon(pm)
        bean.packageName = pi.packageName
        bean.packagePath = ai.sourceDir
        bean.versionName = pi.versionName
        bean.versionCode = pi.versionCode
        bean.isSystem = (ApplicationInfo.FLAG_SYSTEM and ai.flags) != 0

        return bean
    }

    /**
     * 获取所有已安装App信息
     * <p>{@link #getAppInfo(PackageManager, PackageInfo)}（名称，图标，包名，包路径，版本号，版本Code，是否系统应用）</p>
     * <p>依赖上面的getAppInfo方法</p>
     *
     * @param context 上下文
     * @return 所有已安装的AppInfo列表
     */
    fun getAppsInfo(context: Context): List<AppInfoBean> {
        val list = ArrayList<AppInfoBean>()
        val pm = context.packageManager
        // 获取系统中安装的所有软件信息
        val installedPackages = pm.getInstalledPackages(0)
        installedPackages.mapNotNullTo(list) { getAppInfo(pm, it) }
        return list
    }

    /**
     * 获取已经安装的app信息
     *
     * @param context
     * */
    fun getInstallApps(context: Context): List<AppInfoBean> {
        val list = ArrayList<AppInfoBean>()
        val apps = getAppsInfo(context)
        apps.filterTo(list) { isInstallApp(context, it.packageName) }

        return list
    }
}