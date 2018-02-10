package com.qcloud.qclib.widget.customview.verticalviewpager

import android.view.View

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/1/20 14:37.
 */
class ViewPositionComparator: Comparator<View> {
    override fun compare(lhs: View, rhs: View): Int {
        val llp = lhs.layoutParams as VerticalLayoutParams
        val rlp = rhs.layoutParams as VerticalLayoutParams
        if (llp.isDecor != rlp.isDecor) {
            return if (llp.isDecor) 1 else -1
        }
        return llp.position - rlp.position
    }
}