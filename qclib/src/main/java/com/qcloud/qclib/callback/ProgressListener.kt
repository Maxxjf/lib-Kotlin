package com.qcloud.qclib.callback

/**
 * 类说明：文件下载监听
 * Author: Kuzan
 * Date: 2018/3/27 20:08.
 */
interface ProgressListener {
    /**
     * @param progress     已经下载或上传字节数
     * @param total        总字节数
     * @param done         是否完成
     */
    fun onProgress(progress: Long, total: Long, done: Boolean)
}