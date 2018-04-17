package com.qcloud.qclib.widget.customview.tagview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import android.widget.FrameLayout

/**
 * Description: 标签控件
 * Author: gaobaiqiang
 * 2018/4/17 下午8:38.
 */
class TagView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs), Checkable {

    private var isChecked: Boolean = false

    val tagView: View
        get() = getChildAt(0)

    public override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val states = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked()) {
            View.mergeDrawableStates(states, CHECK_STATE)
        }
        return states
    }

    override fun setChecked(checked: Boolean) {
        if (this.isChecked != checked) {
            this.isChecked = checked
            refreshDrawableState()
        }
    }

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun toggle() {
        setChecked(!isChecked)
    }

    companion object {
        private val CHECK_STATE = intArrayOf(android.R.attr.state_checked)
    }
}