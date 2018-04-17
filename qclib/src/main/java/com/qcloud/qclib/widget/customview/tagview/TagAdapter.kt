package com.qcloud.qclib.widget.customview.tagview

import android.view.View
import java.util.*
import kotlin.collections.ArrayList

/**
 * Description: 标签适配器
 * Author: gaobaiqiang
 * 2018/4/17 下午8:57.
 */
abstract class TagAdapter<T> {

    private var mTagDatas: MutableList<T> = ArrayList()
    var onDataChangedListener: OnDataChangedListener? = null

    val preCheckedList = HashSet<Int>()

    val count: Int
        get() = mTagDatas.size

    constructor(datas: MutableList<T>) {
        mTagDatas = datas
    }

    constructor(datas: Array<T>) {
        mTagDatas = ArrayList(Arrays.asList(*datas))
    }

    fun setSelectedList(vararg poses: Int) {
        val set = HashSet<Int>()
        for (pos in poses) {
            set.add(pos)
        }
        setSelectedList(set)
    }

    fun setSelectedList(set: Set<Int>?) {
        preCheckedList.clear()
        if (set != null) {
            preCheckedList.addAll(set)
        }
        notifyDataChanged()
    }

    fun notifyDataChanged() {
        onDataChangedListener?.onChanged()
    }

    fun getItem(position: Int): T {
        return mTagDatas[position]
    }

    fun <T> setSelected(position: Int, t: T): Boolean {
        return false
    }

    abstract fun <T> getView(parent: FlowLayout, position: Int, t: T): View

    interface OnDataChangedListener {
        fun onChanged()
    }
}