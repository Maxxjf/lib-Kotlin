package com.qcloud.qclib.imageselect.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.qcloud.qclib.R
import com.qcloud.qclib.image.GlideUtil
import java.util.*

/**
 * 类说明：图片选择器
 * Author: Kuzan
 * Date: 2017/5/25 14:58.
 */
class ImageSelectAdapter(
        private val mContext: Context,
        private val mImageSize: Int,
        private val mMaxCount: Int) : RecyclerView.Adapter<ImageSelectAdapter.ViewHolder>() {
    private var mData: MutableList<String>? = null
    private val inflater: LayoutInflater = LayoutInflater.from(mContext)

    private var listener: OnImageSelectListener? = null

    val selectImages = ArrayList<String>()

    val data: List<String>?
        get() = mData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_of_select_images, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lp = holder.ivImage.layoutParams as FrameLayout.LayoutParams
        lp.width = mImageSize
        lp.height = mImageSize
        holder.ivImage.layoutParams = lp

        val image = mData!![position]
        GlideUtil.loadImageForFile(mContext, holder.ivImage, image,
                R.drawable.img_default_portrait, mImageSize, mImageSize, true, true)

        if (mMaxCount != 0) {
            holder.ivSelectIcon.visibility = View.VISIBLE
        } else {
            holder.ivSelectIcon.visibility = View.GONE
        }

        if (selectImages.contains(image)) {
            holder.ivSelectIcon.setImageResource(R.drawable.icon_select)
        } else {
            holder.ivSelectIcon.setImageResource(R.drawable.icon_un_select)
        }

        holder.itemView.setOnClickListener {
            if (selectImages.contains(image)) {
                selectImages.remove(image)
                holder.ivSelectIcon.setImageResource(R.drawable.icon_un_select)
                if (listener != null) {
                    listener!!.OnImageSelect(image, false, selectImages.size)
                }
            } else if (mMaxCount <= 0 || selectImages.size < mMaxCount) {
                selectImages.add(image)
                holder.ivSelectIcon.setImageResource(R.drawable.icon_select)
                if (listener != null) {
                    listener!!.OnImageSelect(image, true, selectImages.size)
                }
            }
        }
    }

    fun refresh(data: MutableList<String>) {
        mData = data
        notifyDataSetChanged()
    }

    fun add(data: String) {
        if (mData == null) {
            mData = ArrayList()
        }

        mData!!.add(data)
        notifyDataSetChanged()
    }

    fun addAll(data: ArrayList<String>?) {

        if (data == null || data.isEmpty()) {
            return
        }

        if (mData == null) {
            mData = ArrayList()
        }

        mData!!.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivImage: ImageView = itemView.findViewById<View>(R.id.iv_image) as ImageView
        var ivSelectIcon: ImageView = itemView.findViewById<View>(R.id.iv_select_icon) as ImageView
    }

    interface OnImageSelectListener {
        fun OnImageSelect(image: String, isSelect: Boolean, selectCount: Int)
    }

    fun setOnImageSelectListener(listener: OnImageSelectListener) {
        this.listener = listener
    }
}
