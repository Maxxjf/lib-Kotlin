package com.qcloud.qclib.callback

import java.io.File

/**
 * 类说明：下载文件回调
 * Author: Kuzan
 * Date: 2017/7/31 17:26.
 */
interface DownloadCallback {
    /** 正在下载中... */
    fun onAccept(acceptStr: String)

    /** 下载进度  */
    fun onProgress(progress: Long, total: Long)

    /** 下载出错  */
    fun onError(errMsg: String)

    /**下载完成  */
    fun onComplete(completeMsg: String)

    /** 下载成功 */
    fun onSuccess(file: File)
}
