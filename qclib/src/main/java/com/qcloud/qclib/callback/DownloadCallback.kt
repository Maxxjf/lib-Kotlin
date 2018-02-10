package com.qcloud.qclib.callback

import android.support.annotation.NonNull
import com.lzy.okgo.model.Progress
import java.io.File

/**
 * 类说明：下载文件回调
 * Author: Kuzan
 * Date: 2018/1/16 11:27.
 */
interface DownloadCallback {
    /** 正在下载中...*/
    fun onAccept(acceptStr: String)

    /** 下载进度 */
    fun onProgress(@NonNull progress: Progress)

    /** 下载出错 */
    fun onError(errMsg: String)

    /**下载完成 */
    fun onComplete(completeMsg: String)

    /** 下载成功 */
    fun onSuccess(file: File?)
}