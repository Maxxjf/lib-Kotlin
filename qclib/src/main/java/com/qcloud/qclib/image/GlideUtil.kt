package com.qcloud.qclib.image

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import com.qcloud.qclib.rxtask.RxScheduler
import com.qcloud.qclib.rxtask.task.IOTask
import com.qcloud.qclib.utils.StringUtil
import jp.wasabeef.glide.transformations.CropCircleTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.File

/**
 * 类说明：Glide图片加载
 * Author: Kuzan
 * Date: 2018/1/10 9:01.
 */
object GlideUtil {
    private val TAG = GlideUtil::class.java.simpleName

    /**
     * 加载图片
     *
     * @param activity
     * @param imageView     图片控件
     * @param url           图片地址
     */
    fun loadImage(@NonNull activity: Activity, @NonNull imageView: ImageView, url: String?) {
        loadImage(Glide.with(activity), imageView, url, 0)
    }

    /**
     * 加载图片
     *
     * @param activity
     * @param imageView     图片控件
     * @param filePath      图片路径
     */
    fun loadImageForFile(@NonNull activity: Activity, @NonNull imageView: ImageView, filePath: String?) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(activity), imageView, File(filePath), 0)
        } else {
            Log.e(TAG, "file path is error！filePath = " + filePath)
        }
    }

    /**
     * 加载图片
     *
     * @param activity
     * @param imageView     图片控件
     * @param bytes         图片数组
     */
    fun loadImage(@NonNull activity: Activity, @NonNull imageView: ImageView, bytes: ByteArray?) {
        loadImage(Glide.with(activity), imageView, bytes, 0)
    }

    /**
     * 加载图片
     *
     * @param activity
     * @param imageView     图片控件
     * @param resId         图片资源
     */
    fun loadImage(@NonNull activity: Activity, @NonNull imageView: ImageView, @DrawableRes resId: Int) {
        loadImage(Glide.with(activity), imageView, resId, 0)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param url           图片地址
     */
    fun loadImage(@NonNull fragment: FragmentActivity, @NonNull imageView: ImageView, url: String?) {
        loadImage(Glide.with(fragment), imageView, url, 0)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param filePath      图片路径
     */
    fun loadImageForFile(@NonNull fragment: FragmentActivity, @NonNull imageView: ImageView, filePath: String?) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(fragment), imageView, File(filePath), 0)
        } else {
            Log.e(TAG, "file path is error！filePath = " + filePath)
        }
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param bytes         图片数组
     */
    fun loadImage(@NonNull fragment: FragmentActivity, @NonNull imageView: ImageView, bytes: ByteArray?) {
        loadImage(Glide.with(fragment), imageView, bytes, 0)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param resId         图片资源
     */
    fun loadImage(@NonNull fragment: FragmentActivity, @NonNull imageView: ImageView, @DrawableRes resId: Int) {
        loadImage(Glide.with(fragment), imageView, resId, 0)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param url           图片地址
     */
    fun loadImage(@NonNull fragment: Fragment, @NonNull imageView: ImageView, url: String?) {
        loadImage(Glide.with(fragment), imageView, url, 0)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param filePath      图片路径
     */
    fun loadImageForFile(@NonNull fragment: Fragment, @NonNull imageView: ImageView, filePath: String?) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(fragment), imageView, File(filePath), 0)
        } else {
            Log.e(TAG, "file path is error！filePath = " + filePath)
        }
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param bytes         图片数组
     */
    fun loadImage(@NonNull fragment: Fragment, @NonNull imageView: ImageView, bytes: ByteArray?) {
        loadImage(Glide.with(fragment), imageView, bytes, 0)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param resId         图片资源
     */
    fun loadImage(@NonNull fragment: Fragment, @NonNull imageView: ImageView, @DrawableRes resId: Int) {
        loadImage(Glide.with(fragment), imageView, resId, 0)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param url           图片地址
     */
    fun loadImage(@NonNull fragment: android.app.Fragment, @NonNull imageView: ImageView, url: String?) {
        loadImage(Glide.with(fragment), imageView, url, 0)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param filePath      图片路径
     */
    fun loadImageForFile(@NonNull fragment: android.app.Fragment, @NonNull imageView: ImageView, filePath: String?) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(fragment), imageView, File(filePath), 0)
        } else {
            Log.e(TAG, "file path is error！filePath = " + filePath)
        }
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param bytes         图片数组
     */
    fun loadImage(@NonNull fragment: android.app.Fragment, @NonNull imageView: ImageView, bytes: ByteArray?) {
        loadImage(Glide.with(fragment), imageView, bytes, 0)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param resId         图片资源
     */
    fun loadImage(@NonNull fragment: android.app.Fragment, @NonNull imageView: ImageView, @DrawableRes resId: Int) {
        loadImage(Glide.with(fragment), imageView, resId, 0)
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageView     图片控件
     * @param url           图片地址
     */
    fun loadImage(@NonNull context: Context, @NonNull imageView: ImageView, url: String?) {
        loadImage(Glide.with(context), imageView, url, 0)
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageView     图片控件
     * @param filePath      图片路径
     */
    fun loadImageForFile(@NonNull context: Context, @NonNull imageView: ImageView, filePath: String?) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(context), imageView, File(filePath), 0)
        } else {
            Log.e(TAG, "file path is error！filePath = " + filePath)
        }
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageView     图片控件
     * @param bytes         图片数组
     */
    fun loadImage(@NonNull context: Context, @NonNull imageView: ImageView, bytes: ByteArray?) {
        loadImage(Glide.with(context), imageView, bytes, 0)
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageView     图片控件
     * @param resId         图片资源
     */
    fun loadImage(@NonNull context: Context, @NonNull imageView: ImageView, @DrawableRes resId: Int) {
        loadImage(Glide.with(context), imageView, resId, 0)
    }

    /**
     * 加载图片
     *
     * @param activity
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull activity: Activity, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(activity), imageView, url, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param activity
     * @param imageView     图片控件
     * @param filePath      图片路径
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImageForFile(@NonNull activity: Activity, @NonNull imageView: ImageView, filePath: String?, @DrawableRes bitmapRes: Int,
                         isPlace: Boolean = true, isSkipCache: Boolean = false) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(activity), imageView, File(filePath), bitmapRes, 0, 0, isPlace, isSkipCache)
        } else {
            loadImage(Glide.with(activity), imageView, bitmapRes, bitmapRes, 0, 0, isPlace, isSkipCache)
        }
    }

    /**
     * 加载图片
     *
     * @param activity
     * @param imageView     图片控件
     * @param bytes         图片数组
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull activity: Activity, @NonNull imageView: ImageView, bytes: ByteArray?, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(activity), imageView, bytes, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param activity
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull activity: Activity, @NonNull imageView: ImageView, @DrawableRes resId: Int, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(activity), imageView, resId, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: FragmentActivity, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, url, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param filePath      图片路径
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImageForFile(@NonNull fragment: FragmentActivity, @NonNull imageView: ImageView, filePath: String?, @DrawableRes bitmapRes: Int,
                         isPlace: Boolean = true, isSkipCache: Boolean = false) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(fragment), imageView, File(filePath), bitmapRes, 0, 0, isPlace, isSkipCache)
        } else {
            loadImage(Glide.with(fragment), imageView, bitmapRes, bitmapRes, 0, 0, isPlace, isSkipCache)
        }
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param bytes         图片数组
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: FragmentActivity, @NonNull imageView: ImageView, bytes: ByteArray?, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, bytes, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: FragmentActivity, @NonNull imageView: ImageView, @DrawableRes resId: Int, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, resId, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: Fragment, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, url, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param filePath      图片路径
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImageForFile(@NonNull fragment: Fragment, @NonNull imageView: ImageView, filePath: String?, @DrawableRes bitmapRes: Int,
                         isPlace: Boolean = true, isSkipCache: Boolean = false) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(fragment), imageView, File(filePath), bitmapRes, 0, 0, isPlace, isSkipCache)
        } else {
            loadImage(Glide.with(fragment), imageView, bitmapRes, bitmapRes, 0, 0, isPlace, isSkipCache)
        }
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param bytes         图片数组
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: Fragment, @NonNull imageView: ImageView, bytes: ByteArray?, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, bytes, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: Fragment, @NonNull imageView: ImageView, @DrawableRes resId: Int, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, resId, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: android.app.Fragment, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, url, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param filePath      图片路径
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImageForFile(@NonNull fragment: android.app.Fragment, @NonNull imageView: ImageView, filePath: String?, @DrawableRes bitmapRes: Int,
                         isPlace: Boolean = true, isSkipCache: Boolean = false) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(fragment), imageView, File(filePath), bitmapRes, 0, 0, isPlace, isSkipCache)
        } else {
            loadImage(Glide.with(fragment), imageView, bitmapRes, bitmapRes, 0, 0, isPlace, isSkipCache)
        }
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param bytes         图片数组
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: android.app.Fragment, @NonNull imageView: ImageView, bytes: ByteArray?, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, bytes, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: android.app.Fragment, @NonNull imageView: ImageView, @DrawableRes resId: Int, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, resId, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull context: Context, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(context), imageView, url, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageView     图片控件
     * @param filePath      图片路径
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImageForFile(@NonNull context: Context, @NonNull imageView: ImageView, filePath: String?, @DrawableRes bitmapRes: Int,
                         isPlace: Boolean = true, isSkipCache: Boolean = false) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(context), imageView, File(filePath), bitmapRes, 0, 0, isPlace, isSkipCache)
        } else {
            loadImage(Glide.with(context), imageView, bitmapRes, bitmapRes, 0, 0, isPlace, isSkipCache)
        }
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageView     图片控件
     * @param bytes         图片数组
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull context: Context, @NonNull imageView: ImageView, bytes: ByteArray?, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(context), imageView, bytes, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull context: Context, @NonNull imageView: ImageView, @DrawableRes resId: Int, @DrawableRes bitmapRes: Int,
                  isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(context), imageView, resId, bitmapRes, 0, 0, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param activity
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull activity: Activity, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(activity), imageView, url, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param activity
     * @param imageView     图片控件
     * @param filePath      图片路径
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImageForFile(@NonNull activity: Activity, @NonNull imageView: ImageView, filePath: String?, @DrawableRes bitmapRes: Int,
                         width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(activity), imageView, File(filePath), bitmapRes, width, height, isPlace, isSkipCache)
        } else {
            loadImage(Glide.with(activity), imageView, bitmapRes, bitmapRes, width, height, isPlace, isSkipCache)
        }
    }

    /**
     * 加载图片
     *
     * @param activity
     * @param imageView     图片控件
     * @param bytes         图片数组
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull activity: Activity, @NonNull imageView: ImageView, bytes: ByteArray?, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(activity), imageView, bytes, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param activity
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull activity: Activity, @NonNull imageView: ImageView, @DrawableRes resId: Int, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(activity), imageView, resId, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: FragmentActivity, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, url, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param filePath      图片路径
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImageForFile(@NonNull fragment: FragmentActivity, @NonNull imageView: ImageView, filePath: String?, @DrawableRes bitmapRes: Int,
                         width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(fragment), imageView, File(filePath), bitmapRes, width, height, isPlace, isSkipCache)
        } else {
            loadImage(Glide.with(fragment), imageView, bitmapRes, bitmapRes, width, height, isPlace, isSkipCache)
        }
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param bytes         图片数组
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: FragmentActivity, @NonNull imageView: ImageView, bytes: ByteArray?, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, bytes, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: FragmentActivity, @NonNull imageView: ImageView, @DrawableRes resId: Int, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, resId, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: Fragment, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, url, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param filePath      图片路径
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImageForFile(@NonNull fragment: Fragment, @NonNull imageView: ImageView, filePath: String?, @DrawableRes bitmapRes: Int,
                         width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(fragment), imageView, File(filePath), bitmapRes, width, height, isPlace, isSkipCache)
        } else {
            loadImage(Glide.with(fragment), imageView, bitmapRes, bitmapRes, width, height, isPlace, isSkipCache)
        }
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param bytes         图片数组
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: Fragment, @NonNull imageView: ImageView, bytes: ByteArray?, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, bytes, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: Fragment, @NonNull imageView: ImageView, @DrawableRes resId: Int, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, resId, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: android.app.Fragment, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, url, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param filePath      图片路径
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImageForFile(@NonNull fragment: android.app.Fragment, @NonNull imageView: ImageView, filePath: String?, @DrawableRes bitmapRes: Int,
                         width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(fragment), imageView, File(filePath), bitmapRes, width, height, isPlace, isSkipCache)
        } else {
            loadImage(Glide.with(fragment), imageView, bitmapRes, bitmapRes, width, height, isPlace, isSkipCache)
        }
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param bytes         图片数组
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: android.app.Fragment, @NonNull imageView: ImageView, bytes: ByteArray?, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, bytes, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param fragment
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull fragment: android.app.Fragment, @NonNull imageView: ImageView, @DrawableRes resId: Int, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(fragment), imageView, resId, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull context: Context, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(context), imageView, url, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageView     图片控件
     * @param filePath      图片路径
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImageForFile(@NonNull context: Context, @NonNull imageView: ImageView, filePath: String?, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(context), imageView, File(filePath), bitmapRes, width, height, isPlace, isSkipCache)
        } else {
            loadImage(Glide.with(context), imageView, bitmapRes, bitmapRes, width, height, isPlace, isSkipCache)
        }
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageView     图片控件
     * @param bytes         图片数组
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull context: Context, @NonNull imageView: ImageView, bytes: ByteArray?, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(context), imageView, bytes, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadImage(@NonNull context: Context, @NonNull imageView: ImageView, @DrawableRes resId: Int, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(context), imageView, resId, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载圆形图片
     *
     * @param context
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadCircleImage(@NonNull context: Context, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                         width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(context), imageView, url, bitmapRes, width, height, isPlace, isSkipCache, CropCircleTransformation(context))
    }

    /**
     * @param context
     * @param imageView     图片控件
     * @param filePath      图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadCircleImageForFile(@NonNull context: Context, @NonNull imageView: ImageView, filePath: String?, @DrawableRes bitmapRes: Int,
                                width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(context), imageView, File(filePath), bitmapRes, width, height, isPlace, isSkipCache, CropCircleTransformation(context))
        } else {
            loadImage(Glide.with(context), imageView, bitmapRes, bitmapRes, width, height, isPlace, isSkipCache, CropCircleTransformation(context))
        }
    }

    /**
     * 加载圆形图片
     *
     * @param context
     * @param imageView     图片控件
     * @param bytes         图片byte数组
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadCircleImage(@NonNull context: Context, @NonNull imageView: ImageView, bytes: ByteArray?, @DrawableRes bitmapRes: Int,
                         width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(context), imageView, bytes, bitmapRes, width, height, isPlace, isSkipCache, CropCircleTransformation(context))
    }

    /**
     * 加载圆形图片
     *
     * @param context
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadCircleImage(@NonNull context: Context, @NonNull imageView: ImageView, @DrawableRes resId: Int, @DrawableRes bitmapRes: Int,
                         width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(context), imageView, resId, bitmapRes, width, height, isPlace, isSkipCache, CropCircleTransformation(context))
    }

    /**
     * 加载圆角图片
     *
     * @param context
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param radius        圆角角度
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadRoundedImage(@NonNull context: Context, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                         radius: Int, width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(context), imageView, url, bitmapRes, width, height, isPlace, isSkipCache, RoundedCornersTransformation(context, radius, 0))
    }

    /**
     * 加载圆角图片
     *
     * @param context
     * @param imageView     图片控件
     * @param filePath      图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param radius        圆角角度
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadRoundedImageForFile(@NonNull context: Context, @NonNull imageView: ImageView, filePath: String?, @DrawableRes bitmapRes: Int,
                                radius: Int, width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        if (StringUtil.isNotBlank(filePath)) {
            loadImage(Glide.with(context), imageView, File(filePath), bitmapRes, width, height, isPlace, isSkipCache, RoundedCornersTransformation(context, radius, 0))
        } else {
            loadImage(Glide.with(context), imageView, bitmapRes, bitmapRes, width, height, isPlace, isSkipCache, RoundedCornersTransformation(context, radius, 0))
        }
    }

    /**
     * 加载圆角图片
     *
     * @param context
     * @param imageView     图片控件
     * @param bytes         图片byte数组
     * @param bitmapRes     加载失败显示的图片
     * @param radius        圆角角度
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadRoundedImage(@NonNull context: Context, @NonNull imageView: ImageView, bytes: ByteArray?, @DrawableRes bitmapRes: Int,
                         radius: Int, width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(context), imageView, bytes, bitmapRes, width, height, isPlace, isSkipCache, RoundedCornersTransformation(context, radius, 0))
    }

    /**
     * 加载圆角图片
     *
     * @param context
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param radius        圆角角度
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     *
     */
    fun loadRoundedImage(@NonNull context: Context, @NonNull imageView: ImageView, @DrawableRes resId: Int, @DrawableRes bitmapRes: Int,
                         radius: Int, width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(Glide.with(context), imageView, resId, bitmapRes, width, height, isPlace, isSkipCache, RoundedCornersTransformation(context, radius, 0))
    }

    /**
     * 加载图片
     *
     * @param manager
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为false
     * @param isSkipCache   是否跳过内存缓存，默认为true
     */
    fun loadImage(@NonNull manager: RequestManager, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(manager.load(url), imageView, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param manager
     * @param imageView     图片控件
     * @param file          图片文件
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为false
     * @param isSkipCache   是否跳过内存缓存，默认为true
     */
    fun loadImage(@NonNull manager: RequestManager, @NonNull imageView: ImageView, file: File?, @DrawableRes bitmapRes: Int,
                  width: Int = 0, height: Int = 0, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(manager.load(file), imageView, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param manager
     * @param imageView     图片控件
     * @param bytes         图片byte数组
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为false
     * @param isSkipCache   是否跳过内存缓存，默认为true
     */
    private fun loadImage(@NonNull manager: RequestManager, @NonNull imageView: ImageView, bytes: ByteArray?,
                          @DrawableRes bitmapRes: Int, width: Int = 0, height: Int = 0,
                          isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(manager.load(bytes), imageView, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param manager
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度，默认为0
     * @param height        图片高度，默认为0
     * @param isPlace       是否显示加载前的图片，默认为false
     * @param isSkipCache   是否跳过内存缓存，默认为true
     */
    private fun loadImage(@NonNull manager: RequestManager, @NonNull imageView: ImageView, @DrawableRes resId: Int?,
                          @DrawableRes bitmapRes: Int, width: Int = 0, height: Int = 0,
                          isPlace: Boolean = true, isSkipCache: Boolean = false) {
        loadImage(manager.load(resId), imageView, bitmapRes, width, height, isPlace, isSkipCache)
    }

    /**
     * 加载图片
     *
     * @param manager
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度
     * @param height        图片高度
     * @param isPlace       是否显示加载前的图片
     * @param isSkipCache   是否跳过内存缓存
     * @param bitmapTransformations 图片转换效果
     */
    fun loadImage(@NonNull manager: RequestManager, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                  width: Int, height: Int, isPlace: Boolean, isSkipCache: Boolean, vararg bitmapTransformations: Transformation<Bitmap>) {
        loadImage(manager.load(url), imageView, bitmapRes, width, height, isPlace, isSkipCache, *bitmapTransformations)
    }

    /**
     * 加载图片
     *
     * @param manager
     * @param imageView     图片控件
     * @param file          图片文件
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度
     * @param height        图片高度
     * @param isPlace       是否显示加载前的图片
     * @param isSkipCache   是否跳过内存缓存
     * @param bitmapTransformations 图片转换效果
     */
    fun loadImage(@NonNull manager: RequestManager, @NonNull imageView: ImageView, file: File?, @DrawableRes bitmapRes: Int,
                  width: Int, height: Int, isPlace: Boolean, isSkipCache: Boolean, vararg bitmapTransformations: Transformation<Bitmap>) {
        loadImage(manager.load(file), imageView, bitmapRes, width, height, isPlace, isSkipCache, *bitmapTransformations)
    }

    /**
     * 加载图片
     *
     * @param manager
     * @param imageView     图片控件
     * @param bytes         图片byte数组
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度
     * @param height        图片高度
     * @param isPlace       是否显示加载前的图片
     * @param isSkipCache   是否跳过内存缓存
     * @param bitmapTransformations 图片转换效果
     */
    private fun loadImage(@NonNull manager: RequestManager, @NonNull imageView: ImageView, bytes: ByteArray?,
                          @DrawableRes bitmapRes: Int, width: Int, height: Int, isPlace: Boolean, isSkipCache: Boolean,
                          vararg bitmapTransformations: Transformation<Bitmap>) {
        loadImage(manager.load(bytes), imageView, bitmapRes, width, height, isPlace, isSkipCache, *bitmapTransformations)
    }

    /**
     * 加载图片
     *
     * @param manager
     * @param imageView     图片控件
     * @param resId         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param width         图片宽度
     * @param height        图片高度
     * @param isPlace       是否显示加载前的图片
     * @param isSkipCache   是否跳过内存缓存
     * @param bitmapTransformations 图片转换效果
     */
    private fun loadImage(@NonNull manager: RequestManager, @NonNull imageView: ImageView, @DrawableRes resId: Int?,
                          @DrawableRes bitmapRes: Int, width: Int, height: Int, isPlace: Boolean, isSkipCache: Boolean,
                          vararg bitmapTransformations: Transformation<Bitmap>) {
        loadImage(manager.load(resId), imageView, bitmapRes, width, height, isPlace, isSkipCache, *bitmapTransformations)
    }

    /**
     * 加载图片
     *
     * @param builder
     * @param imageView     图片控件
     * @param bitmapRes     加载失败显示的图片
     * @param width         宽度
     * @param height        高度
     * @param isPlace       是否显示加载前的图片
     * @param isSkipCache   是否跳过内存缓存
     * @param bitmapTransformations 图片转换效果，可变参数
     * */
    private fun loadImage(@NonNull builder: RequestBuilder<Drawable>, @NonNull imageView: ImageView,
                          @DrawableRes bitmapRes: Int, width: Int, height: Int, isPlace: Boolean, isSkipCache: Boolean,
                          vararg bitmapTransformations: Transformation<Bitmap>) {
        val options = initOptions(bitmapRes, width, height, isPlace, isSkipCache, *bitmapTransformations)
        builder.apply(options)
                .into(imageView)
    }

    /**
     * 加载自定义转换图片
     *
     * @param context
     * @param imageView     图片控件
     * @param filePath      图片路径
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片
     * @param isSkipCache   是否跳过内存缓存
     */
    fun loadTransformImageForFile(@NonNull context: Context, @NonNull imageView: ImageView, filePath: String?, @DrawableRes bitmapRes: Int,
                           isPlace: Boolean = true, isSkipCache: Boolean, transformation: BitmapTransformation?) {
        if (StringUtil.isNotBlank(filePath)) {
            loadTransformImage(Glide.with(context).load(File(filePath)), imageView, bitmapRes, isPlace, isSkipCache, transformation)
        } else {
            loadTransformImage(Glide.with(context).load(bitmapRes), imageView, bitmapRes, isPlace, isSkipCache, transformation)
        }

    }

    /**
     * 加载自定义转换图片
     *
     * @param context
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片
     * @param isSkipCache   是否跳过内存缓存
     */
    fun loadTransformImage(@NonNull context: Context, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int,
                           isPlace: Boolean = true, isSkipCache: Boolean, transformation: BitmapTransformation?) {
        loadTransformImage(Glide.with(context).load(url), imageView, bitmapRes, isPlace, isSkipCache, transformation)
    }

    /**
     * 加载自定义转换图片
     *
     * @param context
     * @param imageView     图片控件
     * @param bytes         图片资源
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片
     * @param isSkipCache   是否跳过内存缓存
     */
    fun loadTransformImage(@NonNull context: Context, @NonNull imageView: ImageView, bytes: ByteArray?, @DrawableRes bitmapRes: Int,
                           isPlace: Boolean = true, isSkipCache: Boolean, transformation: BitmapTransformation?) {
        loadTransformImage(Glide.with(context).load(bytes), imageView, bitmapRes, isPlace, isSkipCache, transformation)
    }

    /**
     * 加载自定义转换图片
     *
     * @param context
     * @param imageView     图片控件
     * @param resId         图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片
     * @param isSkipCache   是否跳过内存缓存
     */
    fun loadTransformImage(@NonNull context: Context, @NonNull imageView: ImageView, @DrawableRes resId: Int, @DrawableRes bitmapRes: Int,
                           isPlace: Boolean = true, isSkipCache: Boolean, transformation: BitmapTransformation?) {
        loadTransformImage(Glide.with(context).load(resId), imageView, bitmapRes, isPlace, isSkipCache, transformation)
    }

    /**
     * 加载自定义转换图片
     *
     * @param builder
     * @param imageView     图片控件
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片
     * @param isSkipCache   是否跳过内存缓存
     * @param transformation 自定义图片转换效果
     */
    @SuppressLint("CheckResult")
    private fun loadTransformImage(@NonNull builder: RequestBuilder<Drawable>, @NonNull imageView: ImageView,
                                   @DrawableRes bitmapRes: Int, isPlace: Boolean, isSkipCache: Boolean,
                                   transformation: BitmapTransformation?) {
        val options = initOptions(bitmapRes, isPlace, isSkipCache)
        if (transformation != null) {
            options.transforms(transformation)
        }
        builder.apply(options)
                .into(imageView)
    }

    /**
     * 加载gif图片
     *
     * @param context
     * @param imageView     图片控件
     * @param url           图片地址
     * @param bitmapRes     加载失败显示的图片
     * @param isPlace       是否显示加载前的图片
     * @param isSkipCache   是否跳过内存缓存
     */
    fun loadGif(@NonNull context: Context, @NonNull imageView: ImageView, url: String?, @DrawableRes bitmapRes: Int, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        val options = initOptions(bitmapRes, isPlace, isSkipCache)
        if (StringUtil.isNotBlank(url)) {
            loadGif(context, imageView, url, options)
        } else {
            loadGifForResource(context, imageView, bitmapRes, options)
        }
    }

    /**
     * 加载gif图片
     *
     * @param context
     * @param imageView     图片控件
     * @param url           图片地址
     * @param options       图片处理选项
     */
    fun loadGif(@NonNull context: Context, @NonNull imageView: ImageView, url: String?, @NonNull options: RequestOptions) {
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(imageView)
    }

    /**
     * 从资源里加载gif图片
     *
     * @param context
     * @param imageView     图片控件
     * @param resId         图片ID
     * @param bitmapRes     占位图
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     */
    fun loadGifForResource(@NonNull context: Context, @NonNull imageView: ImageView, @DrawableRes resId: Int,
                           @DrawableRes bitmapRes: Int, isPlace: Boolean = true, isSkipCache: Boolean = false) {
        val options = initOptions(bitmapRes, isPlace, isSkipCache)
        loadGifForResource(context, imageView, resId, options)
    }

    /**
     * 从资源里加载gif图片
     *
     * @param context
     * @param imageView     图片控件
     * @param resId         图片ID
     * @param options       图片处理选项
     */
    fun loadGifForResource(@NonNull context: Context, @NonNull imageView: ImageView, @DrawableRes resId: Int, @NonNull options: RequestOptions) {
        Glide.with(context)
                .asGif()
                .load(resId)
                .apply(options)
                .into(imageView)
    }

    /**
     * 初始化图片基本处理选项
     *
     * @param bitmapRes     占位图
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     * */
    @SuppressLint("CheckResult")
    fun initOptions(@DrawableRes bitmapRes: Int, isPlace: Boolean = true, isSkipCache: Boolean = false): RequestOptions {
        // DiskCacheStrategy.NONE：      表示不缓存任何内容。
        // DiskCacheStrategy.DATA：      表示只缓存原始图片。
        // DiskCacheStrategy.RESOURCE：  表示只缓存转换过后的图片。
        // DiskCacheStrategy.ALL ：      表示既缓存原始图片，也缓存转换过后的图片。
        // DiskCacheStrategy.AUTOMATIC： 表示让Glide根据图片资源智能地选择使用哪一种缓存策略（默认选项）。

        val options = RequestOptions()
                .skipMemoryCache(isSkipCache)   // 是否跳过内存缓存
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)  // 磁盘缓存方式
        if (bitmapRes != 0) {
            options.error(bitmapRes)
            if (isPlace) {
                options.placeholder(bitmapRes)
            }
        }
        return options
    }

    /**
     * 初始化图片基本处理选项
     *
     * @param bitmapRes     占位图
     * @param width         长度
     * @param height        高度
     * @param isPlace       是否显示加载前的图片，默认为true
     * @param isSkipCache   是否跳过内存缓存，默认为false
     * @param bitmapTransformations 图片转换效果
     *
     * */
    @SuppressLint("CheckResult")
    fun initOptions(@DrawableRes bitmapRes: Int, width: Int, height: Int, isPlace: Boolean = true, isSkipCache: Boolean = false,
                    vararg bitmapTransformations: Transformation<Bitmap>): RequestOptions {
        // DiskCacheStrategy.NONE：      表示不缓存任何内容。
        // DiskCacheStrategy.DATA：      表示只缓存原始图片。
        // DiskCacheStrategy.RESOURCE：  表示只缓存转换过后的图片。
        // DiskCacheStrategy.ALL ：      表示既缓存原始图片，也缓存转换过后的图片。
        // DiskCacheStrategy.AUTOMATIC： 表示让Glide根据图片资源智能地选择使用哪一种缓存策略（默认选项）。

        val options = RequestOptions()
                .skipMemoryCache(isSkipCache)   // 是否跳过内存缓存
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)  // 磁盘缓存方式
        if (bitmapRes != 0) {
            options.error(bitmapRes)
            if (isPlace) {
                options.placeholder(bitmapRes)
            }
        }
        if (width > 0 && height > 0) {
            options.override(width, height)
        }
        if (bitmapTransformations.isNotEmpty()) {
            options.transforms(*bitmapTransformations)
        }
        return options
    }

    /**
     * 清空Glide磁盘缓存
     * 注意！！！该方法必须在子线程中执行
     *
     * @param context
     */
    fun clearDiskCache(@NonNull context: Context) {
        RxScheduler.doOnIOThread(object: IOTask<Void> {
            override fun doOnIOThread() {
                Glide.get(context.applicationContext).clearDiskCache()
            }
        })
    }

    /**
     * 清空Glide内存缓存
     *
     * @param context
     */
    fun clearMemory(@NonNull context: Context) = Glide.get(context).clearMemory()
}