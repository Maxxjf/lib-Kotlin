package com.qcloud.qclib.image

import android.content.Context
import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.DiskCache
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.qcloud.qclib.FrameConfig
import com.qcloud.qclib.utils.FileUtil
import java.io.InputStream


/**
 * 类说明：注册Glide
 * Author: Kuzan
 * Date: 2018/2/2 17:37.
 */
@GlideModule
class QcloudGlideModule: AppGlideModule() {

    /**
     * 禁用清单解析
     * 这样可以改善 Glide 的初始启动时间，并避免尝试解析元数据时的一些潜在问题。
     *
     * @return
     */
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory())
    }

    /**
     * 缓存机制
     * */
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        if (FileUtil.isSdCardExist() && context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) != null) {
            builder.setDiskCache(DiskLruCacheFactory(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).absolutePath, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR, (FrameConfig.imageCacheSize * 1024 * 1024).toLong()))
        } else {
            builder.setDiskCache(InternalCacheDiskCacheFactory(context, (FrameConfig.imageCacheSize * 1024 * 1024).toLong()))
        }
        // 把图片编码转为ARGB_8888（默认是RGB_565）
        if (isARGB_8888) {
            builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888)
        }
    }

    companion object {
        var isARGB_8888 = true
    }
}