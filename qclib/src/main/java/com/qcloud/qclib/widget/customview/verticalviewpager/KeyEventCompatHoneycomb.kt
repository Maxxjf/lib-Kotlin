package com.qcloud.qclib.widget.customview.verticalviewpager

import android.annotation.TargetApi
import android.support.annotation.RequiresApi
import android.view.KeyEvent

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/1/20 17:53.
 */
@RequiresApi(11)
@TargetApi(11)
object KeyEventCompatHoneycomb {
    fun normalizeMetaState(metaState: Int): Int {
        return KeyEvent.normalizeMetaState(metaState)
    }

    fun metaStateHasModifiers(metaState: Int, modifiers: Int): Boolean {
        return KeyEvent.metaStateHasModifiers(metaState, modifiers)
    }

    fun metaStateHasNoModifiers(metaState: Int): Boolean {
        return KeyEvent.metaStateHasNoModifiers(metaState)
    }

    fun isCtrlPressed(event: KeyEvent): Boolean {
        return event.isCtrlPressed
    }
}
