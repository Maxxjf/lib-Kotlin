package com.qcloud.qclib.callback

import android.support.annotation.NonNull
import com.lzy.okgo.model.Progress
import com.qcloud.qclib.beans.UploadFileBean

/**
 * 类说明：上传文件回调
 * Author: Kuzan
 * Date: 2018/1/16 11:32.
 */
interface UploadCallback {
    /** 正在上传中...*/
    fun onAccept(acceptStr: String)

    /** 上传进度 */
    fun onProgress(@NonNull progress: Progress)

    /** 上传出错 */
    fun onError(errMsg: String)

    /**上传完成 */
    fun onComplete(completeMsg: String)

    /** 上传成功 */
    fun onSuccess(bean: UploadFileBean?)
}