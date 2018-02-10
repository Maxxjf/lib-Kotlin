package com.qcloud.qclib.materialdesign.enums

import com.qcloud.qclib.R

/**
 * 类说明：列表类型
 * Author: Kuzan
 * Date: 2018/2/9 17:13.
 */
enum class ListType {
    REGULAR,
    SINGLE,
    MULTI;

    companion object {
        fun getLayoutForType(type: ListType): Int {
            return when (type) {
                REGULAR -> R.layout.md_listitem
                SINGLE -> R.layout.md_listitem_singlechoice
                MULTI -> R.layout.md_listitem_multichoice
            }
        }
    }
}