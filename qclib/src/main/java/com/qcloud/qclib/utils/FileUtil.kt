package com.qcloud.qclib.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.util.Log
import java.io.*
import java.text.DecimalFormat

/**
 * 类说明：文件处理工具
 * Author: Kuzan
 * Date: 2017/11/25 10:57.
 */
object FileUtil {
    const val GB = 1073741824L
    const val MB = 1048576L
    const val KB = 1024L

    /**
     * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return true 存在
     */
    fun isSdCardExist(): Boolean = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /**
     * 获取SD卡根目录路径 "/mnt/sdcard"
     *
     * @return
     */
    fun getSdCardPath(): String {
        if (isSdCardExist()) {
            return Environment.getExternalStorageDirectory().path
        } else {
            throw IOException("no sdcard path found")
        }
    }

    /**
     * 删除文件
     *
     * @param fileName
     */
    fun deleteFile(fileName: String) {
        if (StringUtil.isNotBlank(fileName)) {
            val file = File(fileName)
            if (isFileExists(file)) {
                file.delete()
            }
        }
    }

    /**
     * 创建文件
     *
     * @param path
     * @return
     */
    fun createFile(path: String): File {
        val file = File(path)
        val dirs = File(file.parent) // 目录
        if (!isFileExists(dirs)) {
            // 创建目录
            dirs.mkdirs()
        }
        if (!isFileExists(file)) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    /**
     * 从sdcard读取一个文件
     *
     * @param filePath 如/Camera/P5035-205230.jpg，sdcard下的完整路径
     * @return
     */
    fun readFile(filePath: String?): String? {
        if (StringUtil.isBlank(filePath)) {
            return null
        }
        val file = File(filePath)
        if (isFileExists(file)) {
            try {
                val fis = FileInputStream(file)
                val b = ByteArray(fis.available())
                fis.read(b)
                fis.close()
                return String(b)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return null
    }

    /**
     * 向sdcard写入一个文件
     *
     * @param dirPath  文件路径
     * @param fileName 如"debug"
     * @param content  文字内容
     */
    fun writeFile(dirPath: String, fileName: String, content: String) {
        if (!isDirectoryExists(dirPath)) {
            File(dirPath).mkdirs()
        }
        val file = File(dirPath, fileName)
        file.createNewFile()

        var osw: OutputStreamWriter? = null
        try {
            osw = OutputStreamWriter(FileOutputStream(file), "utf-8")
            osw.write(content)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            osw?.close()
        }
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    fun getFileByPath(filePath: String?): File? {
        return if (StringUtil.isBlank(filePath)) {
            null
        } else {
            File(filePath)
        }
    }

    /**
     * 文件是否已存在
     * @param file
     *
     * @return true存在
     * */
    fun isFileExists(file: File?): Boolean = file != null && file.exists()

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return true 存在
     */
    fun isFileExists(filePath: String): Boolean = isFileExists(getFileByPath(filePath))

    /**
     * 判断指定目录是否存在 .
     *
     * @param dirPath
     * @return true 存在
     */
    fun isDirectoryExists(dirPath: String?): Boolean {
        val file = File(dirPath)
        return isFileExists(file) && file.isDirectory
    }

    /**
     * 判断文件是否已创建
     * */
    fun isFileMkdirs(file: File?) = file != null && file.exists() && file.mkdirs()

    /**
     * 获取全路径中的文件拓展名
     *
     * @param file 文件
     * @return 文件拓展名
     */
    fun getFileExtension(file: File?): String? = if (file != null) getFileExtension(file.path) else null

    /**
     * 获取全路径中的文件拓展名
     *
     * @param filePath 文件路径
     * @return 文件拓展名
     */
    fun getFileExtension(filePath: String?): String? {
        if (StringUtil.isBlank(filePath)) {
            return filePath
        }
        val lastPoi = filePath!!.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)

        return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
    }

    /**
     * 获取应用专属缓存目录
     *      android 4.4及以上系统不需要申请SD卡读写权限
     *      因此也不用考虑6.0系统动态申请SD卡读写权限问题，切随应用被卸载后自动清空 不会污染用户存储空间
     *
     * @param context 上下文
     * @param type 文件夹类型 可以为空，为空则返回API得到的一级目录
     *
     * @return 缓存文件夹 如果没有SD卡或SD卡有问题则返回内存缓存目录，否则优先返回SD卡缓存目录
     */
    fun getCacheDirectory(context: Context, type: String?): File? {
        var appCacheDir = getExternalCacheDirectory(context, type)
        if (appCacheDir == null) {
            appCacheDir = getInternalCacheDirectory(context, type)
        }

        if (appCacheDir == null) {
            Log.e("getCacheDirectory","getCacheDirectory fail ,the reason is mobile phone unknown exception !")
        } else {
            if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
                Log.e("getCacheDirectory","getCacheDirectory fail ,the reason is make directory fail !")
            }
        }
        return appCacheDir
    }

    /**
     * 获取SD卡缓存目录
     * @param context 上下文
     * @param type 文件夹类型 如果为空则返回 /storage/emulated/0/Android/data/app_package_name/cache
     *             否则返回对应类型的文件夹如Environment.DIRECTORY_PICTURES 对应的文件夹为 .../data/app_package_name/files/Pictures
     * {@link android.os.Environment#DIRECTORY_MUSIC},
     * {@link android.os.Environment#DIRECTORY_PODCASTS},
     * {@link android.os.Environment#DIRECTORY_RINGTONES},
     * {@link android.os.Environment#DIRECTORY_ALARMS},
     * {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
     * {@link android.os.Environment#DIRECTORY_PICTURES}, or
     * {@link android.os.Environment#DIRECTORY_MOVIES}.or 自定义文件夹名称
     * @return 缓存目录文件夹 或 null（无SD卡或SD卡挂载失败）
     */
    fun getExternalCacheDirectory(context: Context, type: String?): File? {
        var appCacheDir: File? = null

        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            appCacheDir = if (StringUtil.isBlank(type)) {
                context.externalCacheDir
            } else {
                context.getExternalFilesDir(type)
            }
            if (appCacheDir == null) {
                // 有些手机需要通过自定义目录
                appCacheDir = File(Environment.getExternalStorageDirectory(), "Android/data/" + context.packageName + "/cache/" + type)
            }

            if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
                Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is make directory fail !")
            }
        } else {
            Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is sdCard nonexistence or sdCard mount fail !")
        }

        return appCacheDir
    }

    /**
     * 获取内存缓存目录
     *
     * @param type 子目录，可以为空，为空直接返回一级目录
     * @return 缓存目录文件夹 或 null（创建目录文件失败）
     * 注：该方法获取的目录是能供当前应用自己使用，外部应用没有读写权限，如 系统相机应用
     */
    fun getInternalCacheDirectory(context: Context, type: String?): File? {
        var appCacheDir  = if (StringUtil.isBlank(type)) {
            // /data/data/app_package_name/cache
            context.cacheDir
        } else {
            // /data/data/app_package_name/files/type
            File(context.filesDir, type)
        }

        if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
            Log.e("getInternalDirectory","getInternalDirectory fail ,the reason is make directory fail !")
        }

        return appCacheDir
    }

    /**
     * 从第三方文件选择器获取路径。
     * 参见：http://blog.csdn.net/zbjdsbj/article/details/42387551
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun uriToPath(context: Context, uri: Uri): String? {
        val externalStorageProvider = "com.android.externalstorage.documents"
        val downloadsProvider = "com.android.providers.downloads.documents"
        val mediaProvider = "com.android.providers.media.documents"

        val path = uri.path
        val scheme = uri.scheme
        val authority = uri.authority

        // 是否是4.4及以上版本
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            when (authority) {
                externalStorageProvider -> if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
                downloadsProvider -> {
                    contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId)!!)
                    return queryPathFromMediaStore(context, contentUri, null, null)
                }
                mediaProvider -> {
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    return queryPathFromMediaStore(context, contentUri, selection, selectionArgs)
                }
            }
        } else {
            if ("content".equals(scheme, ignoreCase = true)) {
                // Return the remote address
                return if (authority == "com.google.android.apps.photos.content") {
                    uri.lastPathSegment
                } else {
                    queryPathFromMediaStore(context, uri, null, null)
                }
            } else if ("file".equals(scheme, ignoreCase = true)) {
                return uri.path
            }// File
        }// MediaStore (and general)
        Log.v("TAG", "uri to path: " + path)
        return path
    }

    private fun queryPathFromMediaStore(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var filePath: String? = null
        try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null) {
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                filePath = cursor.getString(column_index)
                cursor.close()
            }
        } catch (e: IllegalArgumentException) {
            Log.e("TAG", e.toString() + "")
        }

        return filePath
    }

    /**
     * 获取文件大小为标准单位
     *
     * @param fileSize 文件大小
     * */
    fun toFileSizeString(fileSize: Long): String {
        val df = DecimalFormat("0.00")
        return when {
            fileSize < KB -> fileSize.toString() + "B"
            fileSize < MB -> df.format((fileSize / KB).toDouble()).toString() + "K"
            fileSize < GB -> df.format((fileSize / MB).toDouble()).toString() + "M"
            else -> df.format((fileSize / GB).toDouble()).toString() + "G"
        }
    }
}