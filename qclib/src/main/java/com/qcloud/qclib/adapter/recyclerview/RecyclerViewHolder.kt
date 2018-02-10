package com.qcloud.qclib.adapter.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/1/15 18:12.
 */
class RecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val mBaseViewHolder = BaseViewHolder.Companion.getViewHolder(itemView)
}