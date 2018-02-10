package com.qcloud.qclib.beans

import android.graphics.drawable.Drawable

/**
 * 类说明：App信息
 * Author: Kuzan
 * Date: 2017/12/2 9:50.
 */
class AppInfoBean {
    var name: String? = null
    var icon: Drawable? = null
    var packageName: String? = null
    var packagePath: String? = null
    var versionName: String? = null
    var versionCode: Int = 1
    var isSystem: Boolean = false

    override fun toString(): String {
        return "App包名：" + packageName +
                "\nApp名称：" + name +
                "\nApp图标：" + icon +
                "\nApp路径：" + packagePath +
                "\nApp版本号：" + versionName +
                "\nApp版本码：" + versionCode +
                "\n是否系统App：" + isSystem
    }
}