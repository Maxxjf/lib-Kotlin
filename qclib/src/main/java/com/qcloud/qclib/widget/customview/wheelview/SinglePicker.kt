package com.qcloud.qclib.widget.customview.wheelview

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import android.support.annotation.StringRes
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.qcloud.qclib.R
import com.qcloud.qclib.base.BasePopupWindow
import com.qcloud.qclib.utils.DensityUtil
import com.qcloud.qclib.utils.ScreenUtil
import com.qcloud.qclib.widget.customview.wheelview.entity.DividerConfig
import java.text.DecimalFormat
import java.util.*

/**
 * 类说明：滑轮单项选择器
 * Author: Kuzan
 * Date: 2018/1/19 16:47.
 */
class SinglePicker<T>(context: Context): BasePopupWindow(context), View.OnClickListener {
    private var mBtnCancel: TextView? = null
    private var mBtnFinish: TextView? = null
    private var mTvTitle: TextView? = null
    private var mLayoutTitle: RelativeLayout? = null
    private var mLine: View? = null
    private var mWheelView: WheelView? = null

    /**不能识别对象，只能是int,float,double,long类型*/
    private val items: MutableList<T> = ArrayList()
    private val itemStrings: MutableList<String> = ArrayList()

    private var onWheelListener: OnWheelListener<T>? = null
    private var onItemPickListener: OnItemPickListener<T>? = null

    private var selectedItemIndex: Int = 0
    private var itemWidth: Int = 0

    override val viewId: Int
        get() = R.layout.pop_wheel_picker
    override val animId: Int
        get() = R.style.AnimationPopupWindow_bottom_to_up

    override fun initPop() {
        super.initPop()
        itemWidth = ScreenUtil.getScreenWidth(mContext)

        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun initAfterViews() {
        mBtnCancel = mView.findViewById(R.id.btn_cancel)
        mBtnFinish = mView.findViewById(R.id.btn_finish)
        mTvTitle = mView.findViewById(R.id.tv_title)
        mLayoutTitle = mView.findViewById(R.id.layout_title)
        mLine = mView.findViewById(R.id.line)
        mWheelView = mView.findViewById(R.id.wheel_view)

        val params = mWheelView!!.layoutParams
        params.width = itemWidth
        mWheelView?.layoutParams = params
        mWheelView?.setOnItemSelectListener(object : WheelView.OnItemSelectListener {
            override fun onSelected(index: Int) {
                selectedItemIndex = index
                if (onWheelListener != null) {
                    onWheelListener!!.onWheeled(selectedItemIndex, items[index])
                }
            }
        })

        mBtnCancel?.setOnClickListener(this)
        mBtnFinish?.setOnClickListener(this)
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        setPopWindowBgAlpha(1.0f)
    }


    override fun onClick(p0: View) {
        if (p0.id == R.id.btn_finish) {
            onItemPickListener?.onItemPicked(selectedItemIndex, getSelectedItem())
            dismiss()
        } else {
            dismiss()
        }
    }

    /** 设置数据项  */
    fun replaceItems(items: Array<T>) {
        setItems(Arrays.asList(*items))
    }

    /** 设置数据项  */
    fun replaceItems(items: MutableList<T>) {
        setItems(items)
    }

    /** 添加数据项  */
    fun addItem(item: T) {
        items.add(item)
        itemStrings.add(formatToString(item))
    }

    /** 移除数据项  */
    fun removeItem(item: T) {
        items.remove(item)
        itemStrings.remove(formatToString(item))
    }

    /** 设置数据项  */
    fun setItems(items: MutableList<T>?) {
        if (items == null || items.isEmpty()) {
            return
        }
        this.items.clear()
        this.items.addAll(items)

        items.mapTo(itemStrings) { formatToString(it) }
        if (mWheelView != null) {
            if (selectedItemIndex < 0 || selectedItemIndex >= items.size) {
                selectedItemIndex = 0
            }
            mWheelView?.setItems(itemStrings, selectedItemIndex)
        }
    }

    /** 设置选项的宽(dp)  */
    fun setItemWidth(itemWidth: Int) {
        if (mWheelView != null) {
            val params = mWheelView!!.layoutParams
            params.width = DensityUtil.dp2px(mContext, itemWidth.toFloat())
            mWheelView!!.layoutParams = params
        } else {
            this.itemWidth = itemWidth
        }
    }

    /** 设置默认选中的项的索引  */
    fun setSelectedIndex(index: Int) {
        if (index >= 0) {
            selectedItemIndex = index
        }
    }

    /** 将数据转为String类型  */
    private fun formatToString(item: T): String {
        return if (item is Float || item is Double) {
            DecimalFormat("0.00").format(item)
        } else {
            item.toString()
        }
    }

    /** 设置顶部标题栏背景颜色  */
    fun setTopBackgroundColor(@ColorInt topBackgroundColor: Int) {
        if (mLayoutTitle != null) {
            mLayoutTitle?.setBackgroundColor(topBackgroundColor)
        }
    }

    /** 设置顶部标题栏背景颜色  */
    fun setTopBackgroundResource(topBackgroundRes: Int) {
        mLayoutTitle?.setBackgroundResource(topBackgroundRes)
    }

    /** 设置顶部标题栏下划线是否显示  */
    fun setTopLineVisible(topLineVisible: Boolean) {
        if (mLine != null) {
            mLine?.visibility = if (topLineVisible) View.VISIBLE else View.GONE
        }
    }

    /** 设置顶部标题栏下划线颜色  */
    fun setTopLineColor(@ColorInt topLineColor: Int) {
        if (mLine != null) {
            mLine?.setBackgroundColor(topLineColor)
        }
    }

    /** 设置顶部标题栏取消按钮是否显示  */
    fun setCancelVisible(cancelVisible: Boolean) {
        if (mBtnCancel != null) {
            mBtnCancel?.visibility = if (cancelVisible) View.VISIBLE else View.GONE
        }
    }

    /**
     * 设置顶部标题栏取消按钮文字
     */
    fun setCancelText(cancelText: CharSequence) {
        if (mBtnCancel != null) {
            mBtnCancel?.text = cancelText
        }
    }

    /** 设置顶部标题栏取消按钮文字  */
    fun setCancelText(@StringRes textRes: Int) {
        setCancelText(mContext.resources.getString(textRes))
    }

    /** 设置顶部标题栏取消按钮文字颜色  */
    fun setCancelTextColor(@ColorInt cancelTextColor: Int) {
        if (mBtnCancel != null) {
            mBtnCancel?.setTextColor(cancelTextColor)
        }
    }

    /** 设置顶部标题栏取消按钮文字大小（单位为sp）  */
    fun setCancelTextSize(@IntRange(from = 10, to = 40) cancelTextSize: Int) {
        if (mBtnCancel != null) {
            mBtnCancel?.textSize = cancelTextSize.toFloat()
        }
    }

    /** 设置顶部标题栏确定按钮文字  */
    fun setFinishText(submitText: CharSequence) {
        if (mBtnFinish != null) {
            mBtnFinish?.text = submitText
        }
    }

    /** 设置顶部标题栏确定按钮文字  */
    fun setFinishText(@StringRes textRes: Int) {
        setFinishText(mContext.resources.getString(textRes))
    }

    /** 设置顶部标题栏确定按钮文字颜色  */
    fun setFinishTextColor(@ColorInt submitTextColor: Int) {
        mBtnFinish?.setTextColor(submitTextColor)
    }

    /** 设置顶部标题栏确定按钮文字大小（单位为sp）  */
    fun setFinishTextSize(@IntRange(from = 10, to = 40) submitTextSize: Int) {
        if (mBtnFinish != null) {
            mBtnFinish?.textSize = submitTextSize.toFloat()
        }
    }

    /** 设置顶部标题栏标题文字  */
    fun setTitleText(titleText: CharSequence) {
        if (mTvTitle != null) {
            mTvTitle?.text = titleText
        }
    }

    /** 设置顶部标题栏标题文字  */
    fun setTitleText(@StringRes textRes: Int) {
        setTitleText(mContext.resources.getString(textRes))
    }

    /** 设置顶部标题栏标题文字颜色  */
    fun setTitleTextColor(@ColorInt titleTextColor: Int) {
        mTvTitle?.setTextColor(titleTextColor)
    }

    /** 设置顶部标题栏标题文字大小（单位为sp）  */
    fun setTitleTextSize(@IntRange(from = 10, to = 40) titleTextSize: Int) {
        if (mTvTitle != null) {
            mTvTitle?.textSize = titleTextSize.toFloat()
        }
    }

    /** 可用于设置每项的高度，范围为2-4  */
    fun setLineSpaceMultiplier(@FloatRange(from = 2.0, to = 4.0) multiplier: Float) {
        mWheelView?.setLineSpaceMultiplier(multiplier)
    }

    /** 可用于设置每项的宽度，单位为dp  */
    fun setPadding(padding: Int) {
        mWheelView?.setPadding(padding)
    }

    /** 设置文字大小  */
    fun setTextSize(textSize: Int) {
        mWheelView?.setTextSize(textSize.toFloat())
    }

    /** 设置文字颜色  */
    fun setTextColor(@ColorInt textColorFocus: Int, @ColorInt textColorNormal: Int) {
        mWheelView?.setTextColor(textColorNormal, textColorFocus)
    }

    /** 设置文字颜色  */
    fun setTextColor(@ColorInt textColor: Int) {
        mWheelView?.setTextColor(textColor)
    }

    /** 设置分隔线配置项，设置null将隐藏分割线及阴影  */
    fun setDividerConfig(config: DividerConfig?) {
        var newConfig = config
        if (newConfig == null) {
            newConfig = DividerConfig()
            newConfig.setVisible(false)
            newConfig.setShadowVisible(false)
        }
        mWheelView?.setDividerConfig(newConfig)
    }

    /**
     * 设置选项偏移量，可用来要设置显示的条目数，范围为1-5。
     * 1显示3条、2显示5条、3显示7条……
     */
    fun setOffset(@IntRange(from = 1, to = 5) offset: Int) {
        mWheelView?.setOffset(offset)
    }

    /**
     * 设置是否禁用循环
     */
    fun setCycleDisable(cycleDisable: Boolean) {
        mWheelView?.setCycleDisable(cycleDisable)
    }

    /**
     * 得到选择器视图，可内嵌到其他视图容器
     */
    override fun getContentView(): View {
        return mView
    }

    /**
     * 获取选中的item
     * */
    fun getSelectedItem(): T {
        return items[selectedItemIndex]
    }

    /**
     * 设置滑动过程监听器
     */
    fun setOnWheelListener(onWheelListener: OnWheelListener<T>) {
        this.onWheelListener = onWheelListener
    }

    /**
     * 设置确认选择监听器
     */
    fun setOnItemPickListener(listener: OnItemPickListener<T>) {
        this.onItemPickListener = listener
    }

    interface OnItemPickListener<in T> {
        fun onItemPicked(index: Int, item: T)
    }

    interface OnWheelListener<in T> {
        fun onWheeled(index: Int, item: T)
    }
}