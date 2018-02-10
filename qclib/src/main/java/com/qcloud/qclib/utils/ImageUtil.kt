package com.qcloud.qclib.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.qcloud.qclib.enums.DateStyleEnum
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * 类说明：图片处理工具
 * Author: Kuzan
 * Date: 2017/11/27 11:29.
 */
object ImageUtil {
    /**
     * 保存图片
     *
     * @param bitmap 图片bitmap
     * @param path 保存路径
     *
     * @return 文件名称
     * */
    fun saveImage(bitmap: Bitmap, path: String): String? {
        val name = DateUtil.getCurrTime(DateStyleEnum.FILE_NAME_FORMAT.value) + ".png"
        var fos: FileOutputStream? = null
        val file = File(path)
        if (!FileUtil.isFileExists(file)) {
            file.mkdirs()
        }

        val fileName = path + File.separator + name

        try {
            fos = FileOutputStream(fileName)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            return fileName
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            try {
                if (fos != null) {
                    fos.flush()
                    fos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return null
    }

    /**
     * 压缩图片，得到压缩后图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    fun decodeSampledBitmapFromResource(pathName: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        var degree: Int

        try {
            val exifInterface = ExifInterface(pathName)
            val result = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            degree = when (result) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 90
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        try {
            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(pathName, options)
            // 调用下面定义的方法计算inSampleSize值
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeFile(pathName, options)

            if (degree != 0) {
                val newBitmap = rotatingImageView(bitmap, degree)
                bitmap.recycle()
                return newBitmap
            }
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return null
        }

        return null
    }

    /**
     * 旋转图片
     *
     * @param bitmap
     * @param angle
     * @return Bitmap
     */
    fun rotatingImageView(bitmap: Bitmap, angle: Int): Bitmap {
        //旋转图片 动作
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())

        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * 按照样本大小计算图片大小，用于压缩图片
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // 源图片的宽度
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1

        if (width > reqWidth && height > reqHeight) {
            // 计算出实际宽度和目标宽度的比率
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            inSampleSize = Math.max(widthRatio, heightRatio)
        }

        return inSampleSize
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    fun toRoundBitmap(bitmap: Bitmap): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        val roundPx: Float
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        val dstLeft: Float
        val dstTop: Float
        val dstRight: Float
        val dstBottom: Float

        if (width <= height) {
            roundPx = (width / 2).toFloat()

            height = width

            left = 0f
            top = 0f
            right = width.toFloat()
            bottom = width.toFloat()

            dstLeft = 0f
            dstTop = 0f
            dstRight = width.toFloat()
            dstBottom = width.toFloat()
        } else {
            roundPx = (height / 2).toFloat()
            val clip = (width - height) / 2
            width = height

            left = clip.toFloat()
            right = (width - clip).toFloat()
            top = 0f
            bottom = height.toFloat()

            dstLeft = 0f
            dstTop = 0f
            dstRight = height.toFloat()
            dstBottom = height.toFloat()
        }

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        val src = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        val dst = Rect(dstLeft.toInt(), dstTop.toInt(), dstRight.toInt(), dstBottom.toInt())
        val rectF = RectF(dst)

        paint.isAntiAlias = true // 设置画笔无锯齿
        canvas.drawARGB(0, 0, 0, 0) // 填充整个Canvas

        // 以下有两种方法画圆,drawRoundRect和drawCircle
        // 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        // canvas.drawCircle(roundPx, roundPx, roundPx, paint)

        // 设置两张图片相交时的模式
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        canvas.drawBitmap(bitmap, src, dst, paint)

        bitmap.recycle()

        return output
    }

    /**
     * 从URI 获取 url；
     * @param context
     * @param uri
     * @return
     */
    fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        var path: String? = null
        val scheme = uri.scheme
        if (scheme == null) {
            path = uri.path
        } else if (scheme == ContentResolver.SCHEME_FILE) {
            path = uri.path
        } else if (scheme == ContentResolver.SCHEME_CONTENT) {
            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index >= 0) {
                        path = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return path
    }

    /**
     * 创建图片文件
     *
     * @return 图片路径
     * */
    fun createImageFile(): Uri {
        val timeStamp = DateUtil.getCurrTime(DateStyleEnum.FILE_NAME_FORMAT.value)
        val imageFileName = "jpeg_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        var image: File? = null

        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.fromFile(image)
    }
}