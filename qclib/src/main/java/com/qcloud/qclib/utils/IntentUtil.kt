package com.qcloud.qclib.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.content.FileProvider
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.File

/**
 * 类说明：意图相关工具类
 * Author: Kuzan
 * Date: 2017/11/24 19:09.
 */
object IntentUtil {
    const val OPEN_SYSTEM_ALERT_TOAST_CODE = 121

    /**
     * 获取安装App（支持6.0）的意图
     *
     * @param filePath 文件路径
     * @return intent
     */
    fun getInstallAppIntent(context: Context, filePath: String?, packName: String): Intent?
            = getInstallAppIntent(context, FileUtil.getFileByPath(filePath), packName)

    /**
     * 获取安装App(支持6.0)的意图
     *
     * @param file 文件
     * @param packName 包名，用来处理android7.0，在项目res下创建xml文件夹,参见库里的res/xml
     *                  在AndroidManifest下添加
     * <provider
     *     android:authorities="com.xxx.xxx.fileprovider"
     *     android:name="android.support.v4.content.FileProvider"
     *     android:grantUriPermissions="true"
     *     android:exported="false">
     *     <meta-data
     *          android:name="android.support.FILE_PROVIDER_PATHS"
     *          android:resource="@xml/filepaths"/>
     * </provider>
     *
     * @return intent
     */
    fun getInstallAppIntent(context: Context, file: File?, packName: String): Intent? {
        if (StringUtil.isBlankObject(file)) return null

        val intent = Intent(Intent.ACTION_VIEW)
        val type = if (Build.VERSION.SDK_INT < 23) {
            "application/vnd.android.package-archive"
        } else {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getFileExtension(file))
        }

        val uri = if (Build.VERSION.SDK_INT >= 24) {
            // 处理android7.0问题
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            FileProvider.getUriForFile(context.applicationContext, packName + ".fileprovider", file!!)
        } else {
            Uri.fromFile(file)
        }
        Log.e("getInstallAppIntent", uri.toString())
        intent.setDataAndType(uri, type)
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 获取卸载App的意图
     *
     * @param packName 包名
     * @return intent
     */
    fun getUninstallAppIntent(packName: String): Intent? {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:" + packName)
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 获取打开App的意图
     *
     * @param context     上下文
     * @param packName    包名
     * @return intent
     */
    fun getLaunchAppIntent(context: Context, packName: String): Intent? = context.packageManager.getLaunchIntentForPackage(packName)

    /**
     * 获取App具体设置的意图
     *
     * @param packName 包名
     * @return intent
     */
    fun getAppDetailsSettingsIntent(packName: String): Intent? {
        val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
        intent.data = Uri.parse("package:" + packName)
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 获取分享文本的意图
     *
     * @param content 分享文本
     * @return intent
     */
    fun getShareTextIntent(content: String): Intent? {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, content)
        return intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 获取分享图片的意图
     *
     * @param content   文本
     * @param imagePath 图片文件路径
     * @return intent
     */
    fun getShareImageIntent(content: String, imagePath: String): Intent? = getShareImageIntent(content, FileUtil.getFileByPath(imagePath))

    /**
     * 获取分享图片的意图
     *
     * @param content 文本
     * @param image   图片文件
     * @return intent
     */
    fun getShareImageIntent(content: String, image: File?): Intent? = if (FileUtil.isFileExists(image)) getShareImageIntent(content, Uri.fromFile(image)) else null

    /**
     * 获取分享图片的意图
     *
     * @param content 分享文本
     * @param uri     图片uri
     * @return intent
     */
    fun getShareImageIntent(content: String, uri: Uri): Intent? {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, content)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/*"
        return intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 获取其他应用组件的意图
     *
     * @param packName    包名
     * @param className   全类名
     * @param bundle      bundle
     * @return intent
     */
    fun getComponentIntent(packName: String, className: String, bundle: Bundle? = null): Intent? {
        val intent = Intent(Intent.ACTION_VIEW)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        intent.component = ComponentName(packName, className)
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 获取关机的意图
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.SHUTDOWN"/>}</p>
     *
     * @return intent
     */
    fun getShutdownIntent(): Intent? {
        val intent = Intent(Intent.ACTION_SHUTDOWN)
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 获取拍照的意图
     *
     * @param outUri 输出的uri
     * @return 拍照的意图
     */
    fun getCaptureIntent(outUri: Uri): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
        return intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
    }


    /**
     * 判断是否开启悬浮窗
     *
     * @param context
     * @return boolean true代表已开启
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    fun isOpenAlert(context: Context): Boolean = Settings.canDrawOverlays(context)

    /**
     * 开启悬浮窗权限意图
     *
     * @param context
     * @param packName / context.getPackageName()
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    fun openToastAlert(context: Context, packName: String) {
        val uri = Uri.parse("package:" + packName)
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
        context.startActivity(intent)
    }

    /**
     * 开启悬浮窗权限意图
     *
     * @param activity
     * @param packName / context.getPackageName()
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    fun openToastAlertForResult(activity: Activity, packName: String) {
        val uri = Uri.parse("package:" + packName)
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
        activity.startActivityForResult(intent, OPEN_SYSTEM_ALERT_TOAST_CODE)
    }
}