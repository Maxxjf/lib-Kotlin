package com.qcloud.qclib.imageselect.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * 类说明：图片文件工具类
 * Author: Kuzan
 * Date: 2017/5/25 14:58.
 */
object ImageUtil {

    fun saveImage(bitmap: Bitmap, path: String): String {

        val name: String = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)).toString() + ".png"
        var b: FileOutputStream? = null
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()// 创建文件夹
        }

        val fileName = path + File.separator + name

        try {
            b = FileOutputStream(fileName)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, b)// 把数据写入文件
            return fileName
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            try {
                if (b != null) {
                    b.flush()
                    b.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return ""
    }

    /**
     * 根据计算的inSampleSize，得到压缩后图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    fun decodeSampledBitmapFromResource(pathName: String, reqWidth: Int, reqHeight: Int): Bitmap? {

        var degree = 0

        try {
            val exifInterface = ExifInterface(pathName)
            val result = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            when (result) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            return null
        }

        try {
            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(pathName, options)
            // 调用上面定义的方法计算inSampleSize值
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight)

            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false
            //            options.inPreferredConfig = Bitmap.Config.RGB_565;
            var bitmap: Bitmap? = BitmapFactory.decodeFile(pathName, options)

            if (degree != 0) {
                val newBitmap = rotaingImageView(bitmap, degree)
                bitmap?.recycle()
                return newBitmap
            }

            return bitmap
        } catch (error: OutOfMemoryError) {
            Log.e("tag", "内存泄露！")
            return null
        }
    }

    /**
     * 旋转图片
     *
     * @param bitmap
     * @param angle
     * @return Bitmap
     */
    fun rotaingImageView(bitmap: Bitmap?, angle: Int): Bitmap {
        //旋转图片 动作
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        // 创建新的图片
        return Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * 计算inSampleSize，用于压缩图片
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options,
                                      reqWidth: Int, reqHeight: Int): Int {
        // 源图片的宽度
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1

        if (width > reqWidth && height > reqHeight) {
            //         计算出实际宽度和目标宽度的比率
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
    fun toRoundBitmap(bitmap: Bitmap?): Bitmap? {
        var newBitmap: Bitmap? = bitmap ?: return null
        var width = newBitmap!!.width
        var height = newBitmap.height
        val roundPx: Float
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        val dst_left: Float
        val dst_top: Float
        val dst_right: Float
        val dst_bottom: Float
        if (width <= height) {
            roundPx = (width / 2).toFloat()

            left = 0f
            top = 0f
            right = width.toFloat()
            bottom = width.toFloat()

            height = width

            dst_left = 0f
            dst_top = 0f
            dst_right = width.toFloat()
            dst_bottom = width.toFloat()
        } else {
            roundPx = (height / 2).toFloat()

            val clip = ((width - height) / 2).toFloat()

            left = clip
            right = width - clip
            top = 0f
            bottom = height.toFloat()
            width = height

            dst_left = 0f
            dst_top = 0f
            dst_right = height.toFloat()
            dst_bottom = height.toFloat()
        }

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        val src = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        val dst = Rect(dst_left.toInt(), dst_top.toInt(), dst_right.toInt(), dst_bottom.toInt())
        val rectF = RectF(dst)

        paint.isAntiAlias = true// 设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0) // 填充整个Canvas

        // 以下有两种方法画圆,drawRoundRect和drawCircle
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        // canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(newBitmap, src, dst, paint) // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

        newBitmap.recycle()

        return output
    }

    /**
     * 从URI 获取 url；
     * @param context
     * @param uri
     * @return
     */
    fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null)
            data = uri.path
        else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }
}