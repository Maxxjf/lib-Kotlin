package com.qcloud.qclib.beans

import com.qcloud.qclib.enums.RequestStatusEnum
import java.io.Serializable

/**
 * 类说明：请求响应的基本数据
 * Author: Kuzan
 * Date: 2018/1/16 9:35.
 */
class BaseResponse<T>: Serializable {
    var status: Int = RequestStatusEnum.SUCCESS.status   // 200成功
    var message: String? = "成功"
    var url: String? = null
    var data: T? = null

    override fun toString(): String {
        return "BaseResponse(status=$status, message=$message, url=$url, data=$data)"
    }
}