package com.qcloud.qclib.network

/**
 * 类型：ReturnBean
 * Author: iceberg
 * Date: 2018/3/16.
 * 接口返回类型
 */
class ReturnBean<T>(t:T) {
    var status:Int = 0
    var message:String = ""
    var data: T? = null
    var url:String=""
}