package com.qcloud.qclib.materialdesign.listener

/**
 * 类说明：MaterialDesign输入错误回调
 * Author: Kuzan
 * Date: 2018/4/7 10:45.
 */
interface OnErrorListener {

    /**
     * @param input 输入的内容
     *
     * @return true 显示错误信息
     * */
    fun onError(input: CharSequence): Boolean
}