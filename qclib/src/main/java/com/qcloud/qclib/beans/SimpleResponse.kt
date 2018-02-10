package com.qcloud.qclib.beans

import com.qcloud.qclib.enums.RequestStatusEnum
import java.io.Serializable

/**
 * 类说明：简单响应数据类型
 * Author: Kuzan
 * Date: 2018/1/16 10:08.
 */
class SimpleResponse: Serializable {
    var status: Int = RequestStatusEnum.SUCCESS.status   // 200成功
    var message: String? = "成功"

    fun <T> toBaseResponse(): BaseResponse<T> {
        val response = BaseResponse<T>()
        response.status = status
        response.message = message
        return response
    }

    override fun toString(): String {
        return "SimpleResponse(status=$status, message=$message)"
    }
}