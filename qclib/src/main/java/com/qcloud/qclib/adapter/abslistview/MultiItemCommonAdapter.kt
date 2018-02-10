package com.qcloud.qclib.adapter.abslistview

import android.content.Context
import android.view.View
import android.view.ViewGroup

/**
 * 类说明：支持多种Item类型适配器
 * Author: Kuzan
 * Date: 2018/1/17 13:44.
 */
abstract class MultiItemCommonAdapter<T>(context: Context,
                                         datas: List<T>,
                                         protected val mMultiItemTypeSupport: MultiItemTypeSupport<T>?): CommonAdapter<T>(context, datas, -1) {

    override fun getViewTypeCount(): Int {
        return mMultiItemTypeSupport?.getViewTypeCount() ?: super.getViewTypeCount()
    }

    override fun getItemViewType(position: Int): Int {
        return mMultiItemTypeSupport?.getItemViewType(position, mDatas[position]) ?: super.getItemViewType(position)
    }

    override fun getView(p0: Int, p1: View, p2: ViewGroup): View {
        return if (mMultiItemTypeSupport != null) {
            val layoutId = mMultiItemTypeSupport.getLayoutId(p0, getItem(p0))
            val viewHolder = ViewHolder.get(mContext, p1, p2, layoutId, p0)
            convert(viewHolder, getItem(p0))
            viewHolder.mConvertView
        } else {
            super.getView(p0, p1, p2)
        }
    }
}