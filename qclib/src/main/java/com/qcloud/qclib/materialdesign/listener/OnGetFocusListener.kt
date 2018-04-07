package com.qcloud.qclib.materialdesign.listener

import android.text.Editable



/**
 * 类说明：获取焦点
 * Author: Kuzan
 * Date: 2018/4/7 10:47.
 */
interface OnGetFocusListener {
    fun onGetFocus()

    fun afterTextChanged(s: Editable)

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)

    fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
}