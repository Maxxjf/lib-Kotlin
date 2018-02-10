package com.qcloud.qclib.widget.customview.banner

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup

/**
 * 类说明：自定义轮播图
 * Author: Kuzan
 * Date: 2018/1/19 11:23.
 */
class BannerPagerAdapter<T>(val mContext: Context, val mCreator: ViewCreator<T>): PagerAdapter() {

    val mList: MutableList<T> = ArrayList()
    var mOnPageClickListener: OnPageClickListener<T>? = null
    private val views = SparseArray<View>()

    override fun getCount(): Int {
        return if (mList.isEmpty()) 0 else mList.size + 2
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 == arg1
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // Warning：不要在这里调用removeView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view = views[position]
        if (view == null) {
            view = mCreator.createView(mContext, position)
            views.put(position, view)
        }
        // 如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        val vp = view.parent
        if (vp != null) {
            val parent = vp as ViewGroup
            parent.removeView(view)
        }

        val item = getActualPosition(position)
        val t = mList[item]
        mCreator.updateUi(mContext, view, item, t)

        view.setOnClickListener {
            mOnPageClickListener?.onPageClick(item, t)
        }

        container.addView(view)
        return view
    }

    /**
     * 获取当前position
     * */
    private fun getActualPosition(position: Int): Int {
        return when (position) {
            0 -> mList.size - 1
            count - 1 -> 0
            else -> position - 1
        }
    }

    fun replaceList(list: List<T>?) {
        if (list != null) {
            mList.clear()
            mList.addAll(list)
        } else {
            mList.clear()
        }
        notifyDataSetChanged()
    }
}