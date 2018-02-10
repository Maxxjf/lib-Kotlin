package com.qcloud.qclib.adapter.abslistview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

/**
 * 类说明：通用的适配器 ListView / GridView
 * Author: Kuzan
 * Date: 2018/1/17 12:03.
 */
abstract class CommonAdapter<T>(
        protected var mContext: Context,
        protected var mDatas: List<T>,
        private val layoutId: Int): BaseAdapter() {

    protected val mInflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun getCount(): Int {
        return mDatas.size
    }

    override fun getItem(p0: Int): T {
        return mDatas[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View, p2: ViewGroup): View {
        val holder = ViewHolder.get(mContext, p1, p2, layoutId, p0)
        convert(holder, getItem(p0))
        return holder.mConvertView
    }

    abstract fun convert(holder: ViewHolder, t: T)
}