package com.qcloud.qclib.beans

import java.io.Serializable

/**
 * 类说明：上传文件返回
 * Author: Kuzan
 * Date: 2018/1/16 10:13.
 */
class UploadFileBean: Serializable {
    var fileUrl: String? = null
    var fileId: String? = null

    override fun toString(): String {
        return "UploadFileBean(fileUrl=$fileUrl, fileId=$fileId)"
    }
}