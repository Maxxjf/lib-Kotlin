package com.qcloud.qclib.imageselect.window

import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.PopupWindow
import com.qcloud.qclib.R
import com.qcloud.qclib.imageselect.adapter.FolderAdapter
import com.qcloud.qclib.imageselect.beans.FolderBean
import com.qcloud.qclib.utils.ScreenUtil

/**
 * 类说明：图片文件夹弹窗
 * Author: Kuzan
 * Date: 2017/5/25 14:58.
 */
open class FolderPop(private val mContext: Context) : PopupWindow(mContext) {

    private var mView: View? = null
    private var mFolderList: RecyclerView? = null
    private var mAdapter: FolderAdapter? = null
    private var mListener: OnSelectListener? = null

    init {
        init()
    }

    protected fun init() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.pop_folder, null)
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ScreenUtil.getScreenHeight(mContext) * 2 / 3
        contentView = mView
        mFolderList = mView!!.findViewById<View>(R.id.list_folder) as RecyclerView
        mFolderList!!.layoutManager = LinearLayoutManager(mContext)
        mAdapter = FolderAdapter(mContext, 0)
        mAdapter!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if (position >= 0 && position < mAdapter!!.itemCount) {
                if (mListener != null) {
                    mListener!!.onSelect(mAdapter!!.mList[position])
                    mAdapter!!.setSelectItem(position)
                }
                dismiss()
            }
        }
        mFolderList!!.adapter = mAdapter
        setBackgroundDrawable(ContextCompat.getDrawable(mContext, android.R.color.transparent))
        isOutsideTouchable = true
        isTouchable = true
        isFocusable = true
        animationStyle = R.style.AnimationPopupWindow_folder
    }

    fun setFolders(folders: List<FolderBean>) {
        mAdapter!!.replaceList(folders)
    }

    override fun showAsDropDown(anchor: View) {
        super.showAsDropDown(anchor)
        setPopWindowBg(0.5f)
    }

    override fun dismiss() {
        super.dismiss()
        setPopWindowBg(1f)
    }

    open fun setPopWindowBg(alpha: Float) {
        // 设置背景颜色变暗
        val lp = (mContext as Activity).window.attributes
        lp.alpha = alpha
        mContext.window.attributes = lp
    }

    fun setOnSelectListener(l: OnSelectListener) {
        mListener = l
    }

    interface OnSelectListener {
        fun onSelect(folder: FolderBean)
    }

}
