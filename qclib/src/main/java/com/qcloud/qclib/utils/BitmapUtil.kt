package com.qcloud.qclib.utils

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.util.Base64
import android.view.View
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


/**
 * 类说明：图片处理工具
 * Author: Kuzan
 * Date: 2017/12/2 16:07.
 */
object BitmapUtil {
    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     * */
    fun toRoundBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val r = if (width > height) {
            height
        } else {
            width
        }

        var backgroundBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(backgroundBmp)
        val paint = Paint()
        paint.isAntiAlias = true

        val rect = RectF(0F, 0F, r.toFloat(), r.toFloat())
        canvas.drawRoundRect(rect, (r/2).toFloat(), (r/2).toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, null, rect, paint)
        bitmap.recycle()
        return backgroundBmp
    }

    /**
     * 圆角位图的方法
     *
     * @param bitmap 需要转化成圆角的位图
     * @param pixels 圆角的度数，数值越大，圆角越大
     * @param frameColor 边框颜色，默认绿色
     * @return 处理后的圆角位图
     */
    fun toRoundCorner(bitmap: Bitmap, pixels: Int, frameColor: Int = Color.GREEN): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, width, height)
        val rectF = RectF(rect)

        paint.isAntiAlias = true
        paint.color = frameColor
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawRoundRect(rectF, pixels.toFloat(), pixels.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        bitmap.recycle()
        return output
    }

    /**
     * 从图片链接中获取图片数据
     *
     * @param imgUrl
     * @return
     * */
    fun getBitmapFromUrl(imgUrl: String?): Bitmap? {
        if (StringUtil.isBlank(imgUrl)) {
            return null
        }

        var tmpUrl = imgUrl
        if (StringUtil.subStr(imgUrl, 1) == "/") {
            tmpUrl = StringUtil.subStr(imgUrl, 1, imgUrl!!.length)
        }

        return try {
            val url = URL(tmpUrl)
            val conn = url.openConnection() as HttpURLConnection
            // 设置请求方法为GET
            conn.requestMethod = "GET"
            // 设置请求过时时间为20秒
            conn.readTimeout = 20 * 1000
            // 通过输入流获得图片数据
            val inputStream = conn.inputStream

            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 从View中获取Bitmap
     * */
    fun getBitmapForView(view: View): Bitmap {
        view.isDrawingCacheEnabled = true
        return view.drawingCache
    }

    /**
     * 根据宽高缩放图片
     *
     * @param bitmap
     * @param newWidth 要放大后的宽
     * @param newHeight 要放大后的高
     * @return
     * */
    fun zoomBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // 计算缩放比例
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height

        // 取得想要缩放的matrix参数
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        // 得到新的图片
        val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)

        bitmap.recycle()
        return newBitmap
    }

    /**
     * 使用compress的方法质量压缩图片
     *
     * @param bitmap
     * @param maxSize 压缩最大的图片大小，如果bitmap本身的大小小于maxSize，则不作处理
     * */
    fun compressBitmap(bitmap: Bitmap, maxSize: Float): Bitmap {
        // 将bitmap放至数组中，意在获得bitmap的大小（与实际读取的原文件要大）
        val baos = ByteArrayOutputStream()
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var quality = 100
        // 格式、质量、输出流
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos)
        while (baos.size() / 1024 > maxSize) {  // 循环判断如果压缩后图片是否大于maxSize,大于继续压缩
            // 重置baos
            baos.reset()
            // 每次减少2
            quality -= 2
            // 这里压缩options%，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos)
        }

        // 把压缩后的数据baos存放到ByteArrayInputStream中
        val bais = ByteArrayInputStream(baos.toByteArray())
        // 把ByteArrayInputStream数据生成图片
        val outBitmap = BitmapFactory.decodeStream(bais, null, null)

        bitmap.recycle()
        try {
            bais.close()
            baos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 这里可直接返回bitmap
        return outBitmap
    }

    /**
     * 使用compress的方法质量压缩图片，压缩成一定的宽高
     *
     * @param bitmap
     * @param maxSize 压缩最大的图片大小，如果bitmap本身的大小小于maxSize，则不作处理
     * @param newWidth  要压缩后的宽
     * @param newHeight 要压缩后的高
     * */
    fun compressBitmap(bitmap: Bitmap, maxSize: Float, newWidth: Int, newHeight: Int): Bitmap {
        // 先按宽高压缩图片
        val newBitmap = zoomBitmap(bitmap, newWidth, newHeight)
        // 然后压缩图片
        return compressBitmap(newBitmap, maxSize)
    }

    /**
     * 保存bitmap
     *
     * @param bitmap
     * @param imgName 图片名称
     * @param pathName 文件夹名称
     * */
    fun saveBitmap(bitmap: Bitmap, imgName: String, pathName: String): String {
        val name = imgName + ".png"
        val file = File(pathName)
        if (!FileUtil.isFileExists(file)) {
            file.mkdirs()
        }
        val fileName = pathName + File.separator + name

        var fos: FileOutputStream? = null

        try {
            fos = FileOutputStream(fileName)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
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

        return fileName
    }

    /**
     * bitmap to byte[];
     *
     * @param bitmap
     * @return
     */
    fun bitmap2Bytes(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    /**
     * byte[] to bitmap
     *
     * @param bytes
     * @param outWidth 输出的宽
     * @param outHeight 输出的高
     * */
    fun bytes2Bitmap(bytes: ByteArray, outWidth: Int, outHeight: Int): Bitmap? {
        return if (bytes.isNotEmpty()) {
            try {
                val options = BitmapFactory.Options()
                // 不进行图片抖动处理
                options.inDither = false
                // 设置让解码器以最佳方式解码
                options.inPreferredConfig = null
                if (outWidth > 0 && outHeight > 0) {
                    options.outWidth = outWidth
                    options.outHeight = outHeight
                }
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                // 96dpi
                bitmap.density = 96
                return bitmap
            } catch (e: Exception) {
                return null
            }
        } else {
            null
        }
    }

    /**
     * byte[] to bitmap
     *
     * @param bytes
     * */
    fun bytes2Bitmap(bytes: ByteArray): Bitmap? = bytes2Bitmap(bytes, -1, -1)

    /**
     * 将Drawable转换为Bitmap
     * 参考：http://kylines.iteye.com/blog/1660184
     *
     * @param drawable
     */
    fun drawable2Bitmap(drawable: Drawable): Bitmap? {
        return when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            is ColorDrawable -> {
                val bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                canvas.drawColor(drawable.color)
                return bitmap
            }
            is NinePatchDrawable -> {
                val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                drawable.draw(canvas)
                return bitmap
            }
            else -> null
        }
    }

    /**
     * bitmap 转 drawable
     *
     * @param bitmap
     * */
    fun bitmap2Drawable(bitmap: Bitmap?): Drawable? = BitmapDrawable(Resources.getSystem(), bitmap)

    /**
     * bytes 转 drawable
     *
     * @param bytes
     * */
    fun bytes2Drawable(bytes: ByteArray): Drawable? = bitmap2Drawable(bytes2Bitmap(bytes))

    /**
     * bitmapToBase64
     *
     * @param @param bitmap
     * @param @return    设定文件
     * @return String    返回类型
     * @throws
     */
    @SuppressLint("NewApi")
    fun bitmapToBase64(bitmap: Bitmap?): String? {

        // 要返回的字符串
        var reslut: String? = null

        var baos: ByteArrayOutputStream? = null

        try {
            if (bitmap != null) {

                baos = ByteArrayOutputStream()
                /**
                 * 压缩只对保存有效果bitmap还是原来的大小
                 */
                bitmap.compress(CompressFormat.JPEG, 30, baos)

                baos.flush()
                baos.close()
                // 转换为字节数组
                val byteArray = baos.toByteArray()

                // 转换为字符串
                reslut = Base64.encodeToString(byteArray, Base64.DEFAULT)
            } else {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (baos != null) {
                    baos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return reslut

    }

    /**
     * base64ToBitmap
     *
     * @param @param base64String
     * @param @return    设定文件
     * @return Bitmap    返回类型
     * @throws
     */
    fun base64ToBitmap(base64String: String): Bitmap {
        val decode = Base64.decode(base64String, Base64.DEFAULT)

        val bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)

        return bitmap
    }
}