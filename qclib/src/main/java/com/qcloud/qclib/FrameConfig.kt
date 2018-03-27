package com.qcloud.qclib

import android.content.Context
import android.os.Environment
import com.qcloud.qclib.utils.ConvertUtil
import com.qcloud.qclib.utils.StringUtil
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.w3c.dom.NodeList
import java.io.IOException
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * 类说明：旗云框架常量
 * Author: Kuzan
 * Date: 2017/12/8 14:25.
 */
class FrameConfig {

    init {
        throw UnsupportedOperationException("cannot be instantiated")
    }

    /**
     * 全局静态数据，以下属性可在项目的raw/config.xml下配置
     * */
    companion object {
        // sd卡路径
        val sdPath = Environment.getExternalStorageDirectory().path
        // 网络编码
        var netUnicode = "UTF-8"
        // 是否开启全局异常处理
        var openCrashHandler = false
        // 服务器地址
        var server: String? = null
        // 文件服务器地址
        var fileServer: String? = null
        // 是否允许开启服务器后门
        var isPastern = false
        // 日志输出目录，默认sdcard根目录
        var logPath = Companion.sdPath
        // 缓存路径
        var cachePath: String = "responses"
        // http缓存大小，默认10M
        var cacheSize = 10 * 1024 * 1024
        // 图片缓存路径
        var imageCachePath: String? = null
        // 图片缓存大小，默认50M
        var imageCacheSize = 50
        // app与服务器约定的签名
        var appSign: String? = null
        // 全局上下文
        var appContext: Context? = null
        // 是否缓存
        var isCache: Boolean = false

        /**
         * 用户自定义的一些属性 .
         */
        private val dyamicValue = HashMap<String, String>()

        /**
         * 获取自定义值的属性 . <br>
         *
         * @param key KEY
         * @return 值
         */
        fun getDynamicValue(key: String): String? = dyamicValue[key]

        /**
         * 传入系统assets目录下的一个文件全路径.. <br>
         *
         * @param context   上下文
         * @param filePath  不包含assets的全路径
         * @return 是否解析成功
         */
        fun initSystemConfig(context: Context, filePath: String): Boolean {
            try {
                appContext = context
                return initSystemConfig(context.resources.assets.open(filePath))
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        /**
         * 传入系统raw目录下的一个XML的资源ID,并且对其进行解析. <br>
         *
         * @param context   上下文
         * @param rawId     XML的资源ID
         * @return 是否解析成功
         */
        fun initSystemConfig(context: Context, rawId: Int): Boolean {
            try {
                appContext = context
                return initSystemConfig(context.resources.openRawResource(rawId))
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        /**
         * 初始化系统配置
         *
         * @param inputStream
         * */
        private fun initSystemConfig(inputStream: InputStream): Boolean {
            Observable.create<NodeList> { emitter ->
                val factory = DocumentBuilderFactory.newInstance()
                val builder = factory.newDocumentBuilder()
                val document = builder.parse(inputStream)
                val root = document.documentElement
                val nodes = root.getElementsByTagName("system")
                val sysNodes = nodes.item(0).childNodes
                emitter.onNext(sysNodes)
                emitter.onComplete()
            }.map { nodeList ->
                val list = (0 until nodeList.length).map { nodeList.item(it) }
                list
            }.flatMap { nodes ->
                Observable.fromIterable(nodes)
            }.subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe({ node ->
                        when {
                            // 编码格式
                            "netUnicode".endsWith(node.nodeName) -> Companion.netUnicode = node.firstChild.nodeValue
                            // 服务器域名
                            "server".endsWith(node.nodeName) -> Companion.server = node.firstChild.nodeValue
                            // 是否允许开启服务器后门
                            "is_pastern".endsWith(node.nodeName) -> {
                                if (StringUtil.isNotBlank(node.firstChild.nodeValue)) {
                                    Companion.isPastern = "1" == node.firstChild.nodeValue
                                }
                            }
                            // 是否缓存
                            "is_cache".endsWith(node.nodeName) -> Companion.isCache = "1" == node.firstChild.nodeValue
                            // 缓存路径
                            "cache_path".endsWith(node.nodeName) -> Companion.cachePath = node.firstChild.nodeValue
                            // 图片缓存路径
                            "image_cache_path".endsWith(node.nodeName) -> Companion.imageCachePath = node.firstChild.nodeValue
                            // 图片缓存大小，默认50M
                            "image_cache_size".endsWith(node.nodeName) -> {
                                imageCacheSize = ConvertUtil.toInt(node.firstChild.nodeValue, 50)
                                // 配置的图片缓存文件不能大于250M.
                                if (imageCacheSize > 250) {
                                    imageCacheSize = 250
                                }
                            }
                            // 文件服务器地址
                            "file_server".endsWith(node.nodeName) -> Companion.fileServer = node.firstChild.nodeValue
                            // app与服务器约定的签名
                            "app_sign".endsWith(node.nodeName) -> Companion.appSign = node.firstChild.nodeValue
                            // 日志输出目录
                            "log_path".endsWith(node.nodeName) -> Companion.logPath = node.firstChild.nodeValue
                            // 是否开启全局异常处理
                            "debug".endsWith(node.nodeName) -> {
                                if (StringUtil.isNotBlank(node.firstChild.nodeValue)) {
                                    Companion.openCrashHandler = "1" == node.firstChild.nodeValue
                                }
                            }
                            // 自定义一些属性
                            StringUtil.isNotBlank(node.nodeName) && node.firstChild != null && node.firstChild.nodeValue != null -> {
                                Companion.dyamicValue.put(node.nodeName, node.firstChild.nodeValue.trim())
                            }
                        }
                    }, { throwable ->
                        throw RuntimeException(throwable)
                    }, {
                        inputStream.close()
                    })

            return true
        }
    }
}