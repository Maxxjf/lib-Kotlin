package com.qcloud.qclib.enums

/**
 * 类说明：网络请求返回码
 * Author: Kuzan
 * Date: 2018/1/16 9:48.
 */
enum class RequestStatusEnum constructor(var status: Int, var message: String) {
    SUCCESS(200, "成功"),
    ERROR(500, "系统异常"),
    NO_ROOT(403, "权限不足/没有登陆"),
    INVALID(404, "API接口无效");
}