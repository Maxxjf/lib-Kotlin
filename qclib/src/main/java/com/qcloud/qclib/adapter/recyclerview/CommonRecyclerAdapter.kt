package com.qcloud.qclib.adapter.recyclerview

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.qcloud.qclib.R

/**
 * 类说明：RecyclerView适配器
 * Author: Kuzan
 * Date: 2018/1/15 18:32.
 */
abstract class CommonRecyclerAdapter<T>(protected val mContext: Context): RecyclerView.Adapter<RecyclerViewHolder>() {
    open val mList: MutableList<T> = ArrayList()
    open var onHolderClick: OnHolderClickListener<T>? = null
    open var onItemClickListener: AdapterView.OnItemClickListener? = null
    open var onItemLongClickListener: AdapterView.OnItemLongClickListener? = null

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(mContext).inflate(viewId, parent, false)
        val typedValue = TypedValue()
        mContext.theme.resolveAttribute(R.attr.selectableItemBackground, typedValue, true)
        view.setBackgroundResource(typedValue.resourceId)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        onBindViewHolder(holder.mBaseViewHolder, position)

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener { view ->
                onItemClickListener?.onItemClick(null, view, holder.adapterPosition, holder.itemId)
            }
        }
        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener { view ->
                onItemLongClickListener?.onItemLongClick(null, view, holder.adapterPosition, holder.itemId)
                true
            }
        }
    }

    /**
     * 获取列表个数
     * */
    override fun getItemCount(): Int {
        return mList.size
    }

    /**
     * 替换某一个元素
     */
    fun replaceBean(position: Int, t: T?) {
        if (t != null) {
            mList.removeAt(position)
            mList.add(position, t)
            notifyItemChanged(position, t)
        }
    }

    /**
     * 添加单个元素到列表头
     */
    fun addBeanAtStart(t: T?) {
        if (t != null) {
            mList.add(0, t)
            notifyItemInserted(0)
        }
    }

    /**
     * 添加单个元素到列表尾
     */
    fun addBeanAtEnd(t: T?) {
        if (t != null) {
            mList.add(t)
            notifyItemInserted(itemCount - 1)
        }
    }

    /**
     * 添加单个元素到列表中某个位置
     */
    fun addBeanAtList(t: T?, position: Int) {
        if (t != null) {
            mList.add(position, t)
            notifyDataSetChanged()
        }
    }

    /**
     * 替换RecyclerView中的某一个数据
     */
    fun replaceItem(t: T?, position: Int) {
        if (position in 1..itemCount && t != null) {
            mList[position] = t
            notifyItemChanged(position)
        }
    }

    /**
     * 添加列表数据到列表头部
     */
    fun addListAtStart(list: List<T>?) {
        if (list != null && list.isNotEmpty()) {
            mList.addAll(0, list)
            notifyDataSetChanged()
        }
    }

    /**
     * 替换RecyclerView数据
     */
    fun replaceList(list: List<T>?) {
        if (list != null) {
            mList.clear()
            mList.addAll(list)
        } else {
            mList.clear()
        }
        notifyDataSetChanged()
    }

    /**
     * 添加列表数据到列表尾部
     */
    fun addListAtEnd(list: List<T>?) {
        if (list != null && list.isNotEmpty()) {
            mList.addAll(list)
            notifyItemRangeInserted(itemCount - 1, list.size)
        }
    }

    /**
     * 添加数列表据到列表尾部
     */
    fun addListAtEndAndNotify(list: List<T>?) {
        if (list != null && list.isNotEmpty()) {
            mList.addAll(list)
            notifyDataSetChanged()
        }
    }

    /**
     * 删除RecyclerView所有数据
     */
    fun removeAll() {
        notifyItemRangeRemoved(0, itemCount)
        mList.clear()
    }

    /**
     * 删除RecyclerView指定位置的数据
     */
    fun remove(t: T?) {
        if (t != null) {
            mList.remove(t)
            notifyDataSetChanged()
        }
    }

    /**
     * 删除RecyclerView指定位置的数据
     */
    fun remove(position: Int) {
        if (position in 0 until itemCount) {
            mList.removeAt(position)
            notifyItemRemoved(position)
            if (position != itemCount) {
                notifyItemRangeChanged(position, itemCount - position)
            }
        }
    }

    /**获取布局*/
    abstract val viewId: Int

    /**绑定Holder*/
    abstract fun onBindViewHolder(@NonNull holder: BaseViewHolder, position: Int)

    /**
     * 控件点击监听
     * */
    interface OnHolderClickListener<in T> {
        fun onHolderClick(@NonNull view: View, t: T, position: Int)
    }
}