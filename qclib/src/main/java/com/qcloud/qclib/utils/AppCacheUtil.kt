package com.qcloud.qclib.utils

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * 类说明：应用缓存工具类
 * Author: Kuzan
 * Date: 2017/12/7 11:51.
 */
object AppCacheUtil {
    /**
     * 获取当前缓存
     *
     * @param context
     * @return
     */
    fun getTotalCacheSize(context: Context): String? {
        var cacheSize = getFolderSize(context.cacheDir)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            cacheSize += getFolderSize(context.externalCacheDir)
        }
        return FileUtil.toFileSizeString(cacheSize.toLong())
    }

    /**
     * 删除缓存
     * @param context
     */
    fun clearAllCache(context: Context) {
        deleteFile(context.cacheDir)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            deleteFile(context.externalCacheDir)

            context.deleteDatabase("webview.db")
            context.deleteDatabase("webviewCache.db")

            // WebView 缓存文件
            val appCacheDir = File(context.filesDir.absolutePath + "/webcache")
            deleteFile(appCacheDir)

            val webViewCacheDir = File(context.cacheDir.absolutePath + "webviewCache")
            deleteFile(webViewCacheDir)
        }
    }

    /**
     * 删除缓存目录
     *
     * @param file
     * */
    fun deleteFile(file: File?): Boolean {
        return if (FileUtil.isFileExists(file)) {
            val childFile = file!!.list()
            if (childFile
                    .map { deleteFile(File(file, it)) }
                    .none { it }) false else file.delete()
        } else {
            true
        }
    }

    /**
     * 获取文件大小
     *  context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/目录，一般放一些长时间保存的数据
     *  context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
     *
     * @param file
     * */
    fun getFolderSize(file: File?): Int {
        return try {
            if (FileUtil.isFileExists(file)) {
                val fileList = file!!.listFiles()
                if (fileList != null) {
                    var size = fileList.sumBy {
                        // 如果下面还有文件
                        if (it.isDirectory) {
                            getFolderSize(it)
                        } else {
                            it.length().toInt()
                        }
                    }
                    return size
                }
            }
            0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}