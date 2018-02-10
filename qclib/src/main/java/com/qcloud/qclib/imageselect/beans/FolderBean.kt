package com.qcloud.qclib.imageselect.beans

import com.qcloud.qclib.utils.StringUtil
import java.util.*

/**
 * 类说明：图片文件夹
 * Author: Kuzan
 * Date: 2017/5/25 14:58.
 */
class FolderBean {

    var name: String? = null    // 名称
    var images: ArrayList<String>? = null   // 图片地址

    constructor(name: String) {
        this.name = name
    }

    constructor(name: String, images: ArrayList<String>) {
        this.name = name
        this.images = images
    }

    fun addImage(image: String) {
        if (StringUtil.isNotBlank(image)) {
            if (images == null) {
                images = ArrayList()
            }
            images!!.add(image)
        }
    }

    override fun toString(): String {
        return "Folder{" +
                "name='" + name + '\'' +
                ", images=" + images +
                '}'
    }
}
