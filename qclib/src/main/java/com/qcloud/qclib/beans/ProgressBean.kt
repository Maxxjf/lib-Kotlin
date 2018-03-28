package com.qcloud.qclib.beans

/**
 * 类说明：下载进度条
 * Author: Kuzan
 * Date: 2018/3/28 10:14.
 */
class ProgressBean constructor(
        var progress: Long,         // 已经下载或上传字节数
        var total: Long,            // 总字节数
        var isDone: Boolean) {      // 是否完成
}