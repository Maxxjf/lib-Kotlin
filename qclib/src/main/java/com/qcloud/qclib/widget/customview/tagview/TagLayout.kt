package com.qcloud.qclib.widget.customview.tagview

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.qcloud.qclib.R
import com.qcloud.qclib.utils.DensityUtil
import java.util.HashSet

/**
 * Description: 标签布局
 * Author: gaobaiqiang
 * 2018/4/17 下午9:02.
 */
class TagLayout @JvmOverloads constructor(
        private val mContext: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : FlowLayout(mContext, attrs, defStyle), TagAdapter.OnDataChangedListener {

    var adapter: TagAdapter<*>? = null
        set(adapter) {
            field = adapter
            field?.onDataChangedListener = this
            mSelectedView.clear()
            changeAdapter()
        }
    private var mAutoSelectEffect = true
    private var mSelectedMax = -1  //-1为不限制数量
    private var mMotionEvent: MotionEvent? = null

    private val mSelectedView = HashSet<Int>()

    private var mOnSelectListener: OnSelectListener? = null     // 标签选择事件
    private var mOnTagClickListener: OnTagClickListener? = null // 标签点击事件

    val selectedList: Set<Int>
        get() = HashSet(mSelectedView)

    init {
        parseAttrs(attrs)

        if (mAutoSelectEffect) {
            isClickable = true
        }
    }

    private fun parseAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = mContext.obtainStyledAttributes(attrs, R.styleable.TagLayout)
            try {
                mAutoSelectEffect = a.getBoolean(R.styleable.TagLayout_auto_select_effect, true)
                mSelectedMax = a.getInt(R.styleable.TagLayout_max_select, -1)
            } finally {
                a.recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val cCount = childCount

        for (i in 0 until cCount) {
            val tagView = getChildAt(i) as TagView
            if (tagView.visibility == View.GONE) {
                continue
            }
            if (tagView.tagView.visibility == View.GONE) {
                tagView.visibility = View.GONE
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun changeAdapter() {
        removeAllViews()
        val adapter = this.adapter
        var tagViewContainer: TagView
        val preCheckedList = this.adapter!!.preCheckedList
        for (i in 0 until adapter!!.count) {
            val tagView = adapter.getView(this, i, adapter.getItem(i))

            tagViewContainer = TagView(context)
            tagView.isDuplicateParentStateEnabled = true
            if (tagView.layoutParams != null) {
                tagViewContainer.layoutParams = tagView.layoutParams
            } else {
                val lp = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.setMargins(DensityUtil.dp2px(context, 5f),
                        DensityUtil.dp2px(context, 5f),
                        DensityUtil.dp2px(context, 5f),
                        DensityUtil.dp2px(context, 5f))
                tagViewContainer.layoutParams = lp
            }
            tagViewContainer.addView(tagView)
            addView(tagViewContainer)


            if (preCheckedList.contains(i)) {
                tagViewContainer.isChecked = true
            }

            if (adapter.setSelected(i, adapter.getItem(i))) {
                mSelectedView.add(i)
                tagViewContainer.isChecked = true
            }
        }
        mSelectedView.addAll(preCheckedList)

    }

    fun refreshCheck(position: Int) {
        val adapter = this.adapter
        val preCheckedList = this.adapter!!.preCheckedList
        val tagViewContainer = TagView(context)
        if (preCheckedList.contains(position)) {
            tagViewContainer.isChecked = true
        }

        if (adapter!!.setSelected(position, adapter.getItem(position))) {
            mSelectedView.add(position)
            tagViewContainer.isChecked = true
        }

        mSelectedView.addAll(preCheckedList)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            mMotionEvent = MotionEvent.obtain(event)
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        if (mMotionEvent == null) {
            return super.performClick()
        }

        val x = mMotionEvent!!.x.toInt()
        val y = mMotionEvent!!.y.toInt()
        mMotionEvent = null

        val child = findChild(x, y)
        val pos = findPosByView(child)
        if (child != null) {
            doSelect(child, pos)
            if (mOnTagClickListener != null) {
                return mOnTagClickListener!!.onTagClick(child.tagView, pos, this)
            }
        }
        return true
    }

    fun setMaxSelectCount(count: Int) {
        if (mSelectedView.size > count) {
            mSelectedView.clear()
        }
        mSelectedMax = count
    }

    private fun doSelect(child: TagView, position: Int) {
        if (mAutoSelectEffect) {
            val isCheck: Boolean
            if (!child.isChecked) {
                //处理max_select=1的情况
                if (mSelectedMax == 1 && mSelectedView.size == 1) {
                    val iterator = mSelectedView.iterator()
                    val preIndex = iterator.next()
                    val pre = getChildAt(preIndex) as TagView
                    pre.isChecked = false
                    child.isChecked = true
                    mSelectedView.remove(preIndex)
                    mSelectedView.add(position)
                } else {
                    if (mSelectedMax > 0 && mSelectedView.size >= mSelectedMax) {
                        return
                    }
                    child.isChecked = true
                    mSelectedView.add(position)
                }
                isCheck = true
            } else {
                child.isChecked = false
                mSelectedView.remove(position)
                isCheck = false
            }
            if (mOnSelectListener != null) {
                mOnSelectListener!!.onSelected(HashSet(mSelectedView), position, isCheck)
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState())

        var selectPos = ""
        if (mSelectedView.size > 0) {
            for (key in mSelectedView) {
                selectPos += key.toString() + "|"
            }
            selectPos = selectPos.substring(0, selectPos.length - 1)
        }
        bundle.putString(KEY_CHOOSE_POS, selectPos)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val mSelectPos = state.getString(KEY_CHOOSE_POS)
            if (!TextUtils.isEmpty(mSelectPos)) {
                val split = mSelectPos!!.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (pos in split) {
                    val index = Integer.parseInt(pos)
                    mSelectedView.add(index)

                    val tagView = getChildAt(index) as TagView
                    tagView.isChecked = true
                }

            }
            super.onRestoreInstanceState(state.getParcelable(KEY_DEFAULT))
            return
        }
        super.onRestoreInstanceState(state)
    }

    private fun findPosByView(child: View?): Int {
        val cCount = childCount
        for (i in 0 until cCount) {
            val v = getChildAt(i)
            if (v === child) return i
        }
        return -1
    }

    private fun findChild(x: Int, y: Int): TagView? {
        val cCount = childCount
        for (i in 0 until cCount) {
            val v = getChildAt(i) as TagView
            if (v.visibility == View.GONE) {
                continue
            }
            val outRect = Rect()
            v.getHitRect(outRect)
            if (outRect.contains(x, y)) {
                return v
            }
        }
        return null
    }

    override fun onChanged() {
        mSelectedView.clear()
        changeAdapter()
    }

    fun setOnSelectListener(onSelectListener: OnSelectListener) {
        mOnSelectListener = onSelectListener
        if (mOnSelectListener != null) {
            isClickable = true
        }
    }

    fun setOnTagClickListener(onTagClickListener: OnTagClickListener?) {
        mOnTagClickListener = onTagClickListener
        if (onTagClickListener != null) {
            isClickable = true
        }
    }

    /**
     * 标签选择事件
     */
    interface OnSelectListener {
        fun onSelected(selectPosSet: Set<Int>, position: Int, isCheck: Boolean)
    }

    /**
     * 标签点击事件
     */
    interface OnTagClickListener {
        fun onTagClick(view: View, position: Int, parent: FlowLayout): Boolean
    }

    companion object {
        private val KEY_CHOOSE_POS = "key_choose_pos"
        private val KEY_DEFAULT = "key_default"
    }
}