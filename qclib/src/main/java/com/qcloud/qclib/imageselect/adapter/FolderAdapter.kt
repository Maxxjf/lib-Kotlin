package com.qcloud.qclib.imageselect.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.qcloud.qclib.R
import com.qcloud.qclib.adapter.recyclerview.BaseViewHolder
import com.qcloud.qclib.adapter.recyclerview.CommonRecyclerAdapter
import com.qcloud.qclib.image.GlideUtil
import com.qcloud.qclib.imageselect.beans.FolderBean

/**
 * 类说明：文件选择器
 * Author: Kuzan
 * Date: 2017/5/25 14:58.
 */
class FolderAdapter(context: Context, private val mImageSize: Int) : CommonRecyclerAdapter<FolderBean>(context) {

    private var selectItem: Int = 0

    override val viewId: Int
        get() = R.layout.item_of_folder

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val folder = mList[position]

        holder.setText(R.id.tv_folder_name, folder.name!!)
                .setVisible(R.id.iv_select_icon, selectItem == position)

        val images = folder.images

        if (images != null && !images.isEmpty()) {
            holder.setText(R.id.tv_image_size, images.size.toString() + "张")
            GlideUtil.loadImageForFile(mContext, holder.get<View>(R.id.iv_image) as ImageView, images[0],
                    R.drawable.img_default_portrait, mImageSize, mImageSize, true, true)
        } else {
            holder.setText(R.id.tv_image_size, "0张")
            holder.setImageResource(R.id.iv_image, R.drawable.img_default_portrait)
        }
    }

    fun setSelectItem(position: Int) {
        selectItem = position
        notifyDataSetChanged()
    }

}
