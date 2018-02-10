package com.qcloud.qclib.materialdesign.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.support.annotation.*
import android.support.annotation.IntRange
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.*
import android.util.Log
import android.view.*
import android.widget.*
import com.qcloud.qclib.R
import com.qcloud.qclib.materialdesign.enums.*
import com.qcloud.qclib.materialdesign.widget.*
import com.qcloud.qclib.utils.DialogUtil
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * 类说明：基于Material Design 自定义的Dialog
 * Author: Kuzan
 * Date: 2018/2/9 11:26.
 */
@SuppressLint("InflateParams")
class MaterialDialog constructor(val builder: Builder): BaseDialog(builder.mContext!!, DialogHelper.getTheme(builder)), View.OnClickListener, DefaultRvAdapter.InternalListCallback {

    private val handler: Handler = Handler()

    var iconView: ImageView? = null
    var titleView: TextView? = null
    var contentView: TextView? = null
    var inputEditText: EditText? = null
    var recyclerView: RecyclerView? = null
    var checkBoxPrompt: CheckBox? = null
    var titleFrame: View? = null

    var customViewFrame: FrameLayout? = null
    var progressBar: ProgressBar? = null
    var progressLabel: TextView? = null
    var progressMinMax: TextView? = null
    var inputMinMax: TextView? = null

    // 确认按钮
    var positiveButton: MDButton? = null
    // 中立按钮
    var neutralButton: MDButton? = null
    // 取消按钮
    var negativeButton: MDButton? = null

    // 列表类型
    var listType: ListType = ListType.SINGLE
    var selectedIndicesList: MutableList<Int>? = null

    val tag: Any?
        get() = builder.tag

    val listSelector: Drawable?
        get() {
            if (builder.listSelector != 0) {
                return ResourcesCompat.getDrawable(context.resources, builder.listSelector, null)
            }
            val d = DialogUtil.resolveDrawable(context, R.attr.md_list_selector)
            return d ?: DialogUtil.resolveDrawable(context, R.attr.md_list_selector)
        }

    /**是否选中*/
    var isPromptCheckBoxChecked: Boolean
        get() = checkBoxPrompt != null && checkBoxPrompt!!.isChecked
        set(checked) {
            if (checkBoxPrompt != null) {
                checkBoxPrompt!!.isChecked = checked
            }
        }

    /**自定义布局*/
    val customView: View?
        get() = builder.customView

    /**列表内容*/
    val items: List<CharSequence>?
        get() = builder.items
    /**进度条有关*/
    val currentProgress: Int
        get() = if (progressBar == null) {
            -1
        } else {
            progressBar!!.progress
        }

    val isIndeterminateProgress: Boolean
        get() = builder.indeterminateProgress

    var maxProgress: Int
        get() = if (progressBar == null) {
            -1
        } else progressBar!!.max
        set(max) {
            if (builder.progress <= -2) {
                throw IllegalStateException("Cannot use setMaxProgress() on this dialog.")
            }
            progressBar!!.max = max
        }

    val isCancelled: Boolean
        get() = !isShowing

    /**列表当前选中的索引*/
    var selectedIndex: Int
        get() = if (builder.listCallbackSingleChoice != null) {
            builder.selectedIndex
        } else {
            -1
        }
        @UiThread
        set(index) {
            builder.selectedIndex = index
            if (builder.adapter != null && builder.adapter is DefaultRvAdapter) {
                builder.adapter!!.notifyDataSetChanged()
            } else {
                throw IllegalStateException(
                        "You can only use setSelectedIndex() " + "with the default adapter implementation.")
            }
        }

    /**列表多选的选中索引 */
    var selectedIndices: Array<Int>?
        get() = if (builder.listCallbackMultiChoice != null) {
            selectedIndicesList!!.toTypedArray()
        } else {
            null
        }
        @UiThread
        set(indices) {
            if (indices != null) {
                selectedIndicesList = ArrayList()
                for (i in indices!!) {
                    selectedIndicesList!!.add(i)
                }
                if (builder.adapter != null && builder.adapter is DefaultRvAdapter) {
                    builder.adapter!!.notifyDataSetChanged()
                } else {
                    throw IllegalStateException("You can only use setSelectedIndices() with the default adapter implementation.")
                }
            }
        }

    init {
        val inflater = LayoutInflater.from(context)
        mView = inflater.inflate(DialogHelper.getInflateLayout(builder), null) as MDRootLayout
        DialogHelper.init(this)

        // 初始化之后，不要在生成器中保留上下文引用
        builder.mContext = null
    }

    fun setTypeface(target: TextView, t: Typeface?) {
        if (t == null) {
            return
        }
        val flags = target.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        target.paintFlags = flags
        target.typeface = t
    }

    /**
     * 检查是否有列表在ScrollView
     * */
    fun checkIfListInitScroll() {
        if (recyclerView == null) {
            return
        }
        recyclerView!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    recyclerView!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
                } else {
                    recyclerView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }

                if (listType == ListType.SINGLE || listType == ListType.MULTI) {
                    val selectedIndex = if (listType == ListType.SINGLE) {
                        if (builder.selectedIndex < 0) {
                            return
                        }
                        builder.selectedIndex
                    } else {
                        if (selectedIndicesList == null || selectedIndicesList!!.size == 0) {
                            return
                        }
                        selectedIndicesList!!.sort()
                        selectedIndicesList!![0]
                    }

                    val fSelectedIndex = selectedIndex
                    recyclerView!!.post {
                        recyclerView!!.requestFocus()
                        builder.layoutManager!!.scrollToPosition(fSelectedIndex)
                    }
                }
            }
        })
    }

    /**
     * 设置对话框recyclerview的适配器/布局管理器，它是项单击监听器
     * */
    fun invalidateList() {
        if (recyclerView == null) {
            return
        } else if ((builder.items == null || builder.items!!.size == 0) && builder.adapter == null) {
            return
        }
        if (builder.layoutManager == null) {
            builder.layoutManager = LinearLayoutManager(context)
        }
        if (recyclerView!!.layoutManager == null) {
            recyclerView!!.layoutManager = builder.layoutManager
        }
        recyclerView!!.adapter = builder.adapter
        (builder.adapter as DefaultRvAdapter).setCallback(this)
    }

    /**
     * 列表点击监听
     * */
    override fun onItemSelected(dialog: MaterialDialog, view: View, position: Int, text: CharSequence?, longPress: Boolean): Boolean {
        if (!view.isEnabled) {
            return false
        }
        if (listType == ListType.REGULAR) {
            // 默认适配器，非选择模式
            if (builder.autoDismiss) {
                dismiss()
            }
            if (!longPress && builder.listCallback != null) {
                builder.listCallback!!.onSelection(this, view, position, builder.items!![position])
            }
            if (longPress && builder.listLongCallback != null) {
                return builder.listLongCallback!!.onLongSelection(this, view, position, builder.items!![position])
            }
        } else {
            // 默认适配器，选择模式
            if (listType == ListType.MULTI) {
                val cb = view.findViewById<CheckBox>(R.id.md_control)
                if (!cb.isEnabled) {
                    return false
                }
                val shouldBeChecked = !selectedIndicesList!!.contains(position)
                if (shouldBeChecked) {
                    selectedIndicesList!!.add(position)
                    if (builder.alwaysCallMultiChoiceCallback) {
                        if (sendMultiChoiceCallback()) {
                            cb.isChecked = true
                        } else {
                            selectedIndicesList!!.remove(Integer.valueOf(position))
                        }
                    } else {
                        cb.isChecked = true
                    }
                } else {
                    selectedIndicesList!!.remove(Integer.valueOf(position))
                    if (builder.alwaysCallMultiChoiceCallback) {
                        if (sendMultiChoiceCallback()) {
                            cb.isChecked = false
                        } else {
                            selectedIndicesList!!.add(position)
                        }
                    } else {
                        cb.isChecked = false
                    }
                }
            } else if (listType == ListType.SINGLE) {
                val radio = view.findViewById<RadioButton>(R.id.md_control)
                if (!radio.isEnabled) {
                    return false
                }
                var allowSelection = true
                val oldSelected = builder.selectedIndex

                if (builder.autoDismiss && builder.positiveText == null) {
                    dismiss()
                    allowSelection = false
                    builder.selectedIndex = position
                    sendSingleChoiceCallback(view)
                } else if (builder.alwaysCallSingleChoiceCallback) {
                    builder.selectedIndex = position
                    allowSelection = sendSingleChoiceCallback(view)
                    builder.selectedIndex = oldSelected
                }
                if (allowSelection) {
                    builder.selectedIndex = position
                    radio.isChecked = true
                    builder.adapter!!.notifyItemChanged(oldSelected)
                    builder.adapter!!.notifyItemChanged(position)
                }
            }
        }
        return true
    }

    /**
     * 获取按钮背景
     * */
    fun getButtonSelector(which: DialogAction, isStacked: Boolean): Drawable? {
        if (isStacked) {
            if (builder.btnSelectorStacked != 0) {
                return ResourcesCompat.getDrawable(
                        context.resources, builder.btnSelectorStacked, null)
            }
            val d = DialogUtil.resolveDrawable(context, R.attr.md_btn_stacked_selector)
            return d ?: DialogUtil.resolveDrawable(context, R.attr.md_btn_stacked_selector)
        } else {
            when (which) {
                DialogAction.NEUTRAL -> {
                    if (builder.btnSelectorNeutral != 0) {
                        return ResourcesCompat.getDrawable(context.resources, builder.btnSelectorNeutral, null)
                    }
                    var d = DialogUtil.resolveDrawable(context, R.attr.md_btn_neutral_selector)
                    if (d != null) {
                        return d
                    }
                    d = DialogUtil.resolveDrawable(context, R.attr.md_btn_neutral_selector)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        RippleHelper.applyColor(d, builder.buttonRippleColor)
                    }
                    return d
                }
                DialogAction.NEGATIVE -> {
                    if (builder.btnSelectorNegative != 0) {
                        return ResourcesCompat.getDrawable(context.resources, builder.btnSelectorNegative, null)
                    }
                    var d = DialogUtil.resolveDrawable(context, R.attr.md_btn_negative_selector)
                    if (d != null) {
                        return d
                    }
                    d = DialogUtil.resolveDrawable(context, R.attr.md_btn_negative_selector)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        RippleHelper.applyColor(d, builder.buttonRippleColor)
                    }
                    return d
                }
                else -> {
                    if (builder.btnSelectorPositive != 0) {
                        return ResourcesCompat.getDrawable(context.resources, builder.btnSelectorPositive, null)
                    }
                    var d = DialogUtil.resolveDrawable(context, R.attr.md_btn_positive_selector)
                    if (d != null) {
                        return d
                    }
                    d = DialogUtil.resolveDrawable(context, R.attr.md_btn_positive_selector)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        RippleHelper.applyColor(d, builder.buttonRippleColor)
                    }
                    return d
                }
            }
        }
    }

    /**
     * 列表单选监听
     * */
    private fun sendSingleChoiceCallback(v: View): Boolean {
        if (builder.listCallbackSingleChoice == null) {
            return false
        }
        var text: CharSequence? = null
        if (builder.selectedIndex >= 0 && builder.selectedIndex < builder.items!!.size) {
            text = builder.items!![builder.selectedIndex]
        }
        return builder.listCallbackSingleChoice!!.onSelection(this, v, builder.selectedIndex, text?:"")
    }

    /**
     * 列表多选监听
     * */
    private fun sendMultiChoiceCallback(): Boolean {
        if (builder.listCallbackMultiChoice == null) {
            return false
        }
        selectedIndicesList!!.sort() // 确保索引处于有序状态。
        val selectedTitles = ArrayList<CharSequence>()
        for (i in selectedIndicesList!!) {
            if (i < 0 || i > builder.items!!.size - 1) {
                continue
            }
            selectedTitles.add(builder.items!![i])
        }
        return builder.listCallbackMultiChoice!!.onSelection(
                this,
                selectedIndicesList!!.toTypedArray(),
                selectedTitles.toTypedArray())
    }

    /**
     * 按钮点击
     * */
    override fun onClick(v: View) {
        val tag = v.tag as DialogAction
        when (tag) {
            // 确认按钮
            DialogAction.POSITIVE -> {
                if (builder.onPositiveCallback != null) {
                    builder.onPositiveCallback!!.onClick(this, tag)
                }
                if (!builder.alwaysCallSingleChoiceCallback) {
                    sendSingleChoiceCallback(v)
                }
                if (!builder.alwaysCallMultiChoiceCallback) {
                    sendMultiChoiceCallback()
                }
                if (builder.inputCallback != null && inputEditText != null && !builder.alwaysCallInputCallback) {
                    builder.inputCallback!!.onInput(this, inputEditText!!.text)
                }
                if (builder.autoDismiss) {
                    dismiss()
                }
            }
            // 取消按钮
            DialogAction.NEGATIVE -> {
                if (builder.onNegativeCallback != null) {
                    builder.onNegativeCallback!!.onClick(this, tag)
                }
                if (builder.autoDismiss) {
                    cancel()
                }
            }
            // 中立按钮
            DialogAction.NEUTRAL -> {
                if (builder.onNeutralCallback != null) {
                    builder.onNeutralCallback!!.onClick(this, tag)
                }
                if (builder.autoDismiss) {
                    dismiss()
                }
            }
        }
        if (builder.onAnyCallback != null) {
            builder.onAnyCallback!!.onClick(this, tag)
        }
    }

    @UiThread
    override fun show() {
        try {
            super.show()
        } catch (e: WindowManager.BadTokenException) {
            throw DialogException("Bad window token, you cannot show a dialog " + "before an Activity is created or after it's hidden.")
        }
    }

    /**
     * 根据类型获取按钮控件
     */
    fun getActionButton(which: DialogAction): MDButton? {
        return when (which) {
            DialogAction.NEUTRAL -> neutralButton
            DialogAction.NEGATIVE -> negativeButton
            else -> positiveButton
        }
    }

    /**
     * 根据内容获取按钮控件
     */
    @UiThread
    fun setActionButton(which: DialogAction, title: CharSequence?) {
        when (which) {
            DialogAction.NEUTRAL -> {
                builder.neutralText = title
                neutralButton!!.text = title
                neutralButton!!.visibility = if (title == null) View.GONE else View.VISIBLE
            }
            DialogAction.NEGATIVE -> {
                builder.negativeText = title
                negativeButton!!.text = title
                negativeButton!!.visibility = if (title == null) View.GONE else View.VISIBLE
            }
            else -> {
                builder.positiveText = title
                positiveButton!!.text = title
                positiveButton!!.visibility = if (title == null) View.GONE else View.VISIBLE
            }
        }
    }

    /**
     * 根据内容获取按钮控件
     */
    fun setActionButton(which: DialogAction, @StringRes titleRes: Int) {
        setActionButton(which, context.getText(titleRes))
    }

    /**
     * 判断是否有按钮
     */
    fun hasActionButtons(): Boolean {
        return numberOfActionButtons() > 0
    }

    /**
     * 获取按钮的数量
     */
    fun numberOfActionButtons(): Int {
        var number = 0
        if (positiveButton!!.visibility == View.VISIBLE) {
            number++
        }
        if (neutralButton!!.visibility == View.VISIBLE) {
            number++
        }
        if (negativeButton!!.visibility == View.VISIBLE) {
            number++
        }
        return number
    }

    /**
     * 设置标题
     * */
    @UiThread
    override fun setTitle(newTitle: CharSequence) {
        titleView!!.text = newTitle
    }

    /**
     * 设置标题
     * */
    @UiThread
    override fun setTitle(@StringRes newTitleRes: Int) {
        setTitle(context.getString(newTitleRes))
    }

    /**
     * 设置标题
     * */
    @UiThread
    fun setTitle(@StringRes newTitleRes: Int, vararg formatArgs: Any) {
        setTitle(context.getString(newTitleRes, formatArgs))
    }

    /**
     * 设置图标
     * */
    @UiThread
    fun setIcon(@DrawableRes resId: Int) {
        iconView!!.setImageResource(resId)
        iconView!!.visibility = if (resId != 0) View.VISIBLE else View.GONE
    }

    /**
     * 设置图标
     * */
    @UiThread
    fun setIcon(d: Drawable?) {
        iconView!!.setImageDrawable(d)
        iconView!!.visibility = if (d != null) View.VISIBLE else View.GONE
    }

    /**
     * 设置图标
     * */
    @UiThread
    fun setIconAttribute(@AttrRes attrId: Int) {
        val d = DialogUtil.resolveDrawable(context, attrId)
        setIcon(d)
    }

    /**
     * 设置内容
     * */
    @UiThread
    fun setContent(newContent: CharSequence) {
        contentView!!.text = newContent
        contentView!!.visibility = if (TextUtils.isEmpty(newContent)) View.GONE else View.VISIBLE
    }

    /**
     * 设置内容
     * */
    @UiThread
    fun setContent(@StringRes newContentRes: Int) {
        setContent(context.getString(newContentRes))
    }

    /**
     * 设置内容
     * */
    @UiThread
    fun setContent(@StringRes newContentRes: Int, vararg formatArgs: Any) {
        setContent(context.getString(newContentRes, formatArgs))
    }

    /**
     * 设置列表
     * */
    @UiThread
    fun setItems(vararg items: CharSequence) {
        if (builder.adapter == null) {
            throw IllegalStateException("This MaterialDialog instance does not " + "yet have an adapter set to it. You cannot use setItems().")
        }
        if (items.isNotEmpty()) {
            builder.items = ArrayList(items.size)
            Collections.addAll(builder.items!!, *items)
        } else {
            builder.items = null
        }
        if (builder.adapter !is DefaultRvAdapter) {
            throw IllegalStateException("When using a custom adapter, setItems() " + "cannot be used. Set items through the adapter instead.")
        }
        notifyItemsChanged()
    }

    /**
     * 刷新列表
     * */
    @UiThread
    fun notifyItemInserted(@IntRange(from = 0, to = Integer.MAX_VALUE.toLong()) index: Int) {
        builder.adapter!!.notifyItemInserted(index)
    }

    /**
     * 刷新列表
     * */
    @UiThread
    fun notifyItemChanged(@IntRange(from = 0, to = Integer.MAX_VALUE.toLong()) index: Int) {
        builder.adapter!!.notifyItemChanged(index)
    }

    /**
     * 刷新列表
     * */
    @UiThread
    fun notifyItemsChanged() {
        builder.adapter!!.notifyDataSetChanged()
    }

    /**
     * 设置进度条
     * */
    fun incrementProgress(by: Int) {
        setProgress(currentProgress + by)
    }

    /**
     * 设置进度条
     * */
    fun setProgress(progress: Int) {
        if (builder.progress <= -2) {
            Log.w("MaterialDialog",
                    "Calling setProgress(int) on an indeterminate progress dialog has no effect!")
            return
        }
        progressBar!!.progress = progress
        val progressNumberFormat = builder.progressNumberFormat
        val progressPercentFormat = builder.progressPercentFormat
        handler.post {
            if (progressLabel != null) {
                progressLabel!!.text = progressPercentFormat.format(
                        (currentProgress.toFloat() / maxProgress.toFloat()).toDouble())
            }
            if (progressMinMax != null) {
                progressMinMax!!.text = String.format(progressNumberFormat, currentProgress, maxProgress)
            }
        }
    }

    /**
     * 更改显示进度百分比的小文本的格式
     */
    fun setProgressPercentFormat(format: NumberFormat) {
        builder.progressPercentFormat = format
        setProgress(currentProgress) // invalidates display
    }

    /**
     * 更改显示当前和最大进度单元的小文本格式。
     */
    fun setProgressNumberFormat(format: String) {
        builder.progressNumberFormat = format
        setProgress(currentProgress) // invalidates display
    }

    /**
     * 清除所有选中的复选框多选择列表对话框。
     */
    @JvmOverloads
    fun clearSelectedIndices(sendCallback: Boolean = true) {
        if (listType != ListType.MULTI) {
            throw IllegalStateException("You can only use clearSelectedIndices() with multi choice list dialogs.")
        }
        if (builder.adapter != null && builder.adapter is DefaultRvAdapter) {
            if (selectedIndicesList != null) {
                selectedIndicesList!!.clear()
            }
            builder.adapter!!.notifyDataSetChanged()
            if (sendCallback && builder.listCallbackMultiChoice != null) {
                sendMultiChoiceCallback()
            }
        } else {
            throw IllegalStateException("You can only use clearSelectedIndices() with the default adapter implementation.")
        }
    }

    /**
     * 选择多选择列表对话框中的所有复选框。
     */
    @JvmOverloads
    fun selectAllIndices(sendCallback: Boolean = true) {
        if (listType != ListType.MULTI) {
            throw IllegalStateException("You can only use selectAllIndices() with multi choice list dialogs.")
        }
        if (builder.adapter != null && builder.adapter is DefaultRvAdapter) {
            if (selectedIndicesList == null) {
                selectedIndicesList = ArrayList()
            }
            for (i in 0 until builder.adapter!!.itemCount) {
                if (!selectedIndicesList!!.contains(i)) {
                    selectedIndicesList!!.add(i)
                }
            }
            builder.adapter!!.notifyDataSetChanged()
            if (sendCallback && builder.listCallbackMultiChoice != null) {
                sendMultiChoiceCallback()
            }
        } else {
            throw IllegalStateException("You can only use selectAllIndices() with the default adapter implementation.")
        }
    }

    /**
     * 显示弹窗
     * */
    override fun onShow(dialog: DialogInterface?) {
        if (inputEditText != null) {
            DialogUtil.showKeyboard(this)
            if (inputEditText!!.text.isNotEmpty()) {
                inputEditText!!.setSelection(inputEditText!!.text.length)
            }
        }
        super.onShow(dialog)
    }

    /**
     * 设置输入框回调
     * */
    fun setInternalInputCallback() {
        if (inputEditText == null) {
            return
        }
        inputEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val length = s.toString().length
                var emptyDisabled = false
                if (!builder.inputAllowEmpty) {
                    emptyDisabled = length == 0
                    val positiveAb = getActionButton(DialogAction.POSITIVE)
                    positiveAb!!.isEnabled = !emptyDisabled
                }
                invalidateInputMinMaxIndicator(length, emptyDisabled)
                if (builder.alwaysCallInputCallback) {
                    builder.inputCallback!!.onInput(this@MaterialDialog, s)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    /**
     * 设置无效输入Min Max指标
     * */
    fun invalidateInputMinMaxIndicator(currentLength: Int, emptyDisabled: Boolean) {
        if (inputMinMax != null) {
            if (builder.inputMaxLength > 0) {
                inputMinMax!!.text = String.format(Locale.getDefault(), "%d/%d", currentLength, builder.inputMaxLength)
                inputMinMax!!.visibility = View.VISIBLE
            } else {
                inputMinMax!!.visibility = View.GONE
            }
            val isDisabled = (emptyDisabled && currentLength == 0
                    || builder.inputMaxLength in 1..(currentLength - 1)
                    || currentLength < builder.inputMinLength)
            val colorText = if (isDisabled) builder.inputRangeErrorColor else builder.contentColor
            val colorWidget = if (isDisabled) builder.inputRangeErrorColor else builder.widgetColor
            if (builder.inputMaxLength > 0) {
                inputMinMax!!.setTextColor(colorText)
            }
            MDTintHelper.setTint(inputEditText!!, colorWidget)
            val positiveAb = getActionButton(DialogAction.POSITIVE)
            positiveAb!!.isEnabled = !isDisabled
        }
    }

    /**
     * 关闭弹窗
     * */
    override fun dismiss() {
        if (inputEditText != null) {
            DialogUtil.hideKeyboard(this)
        }
        super.dismiss()
    }

    /**
     * 用于常规列表对话框的回调。
     * */
    interface ListCallback {
        fun onSelection(dialog: MaterialDialog, itemView: View, position: Int, text: CharSequence)
    }

    /**
     * 用于常规列表对话框的长按回调。
     * */
    interface ListLongCallback {
        fun onLongSelection(dialog: MaterialDialog, itemView: View, position: Int, text: CharSequence): Boolean
    }

    /**
     * 用于单选列表对话框的回调。
     * */
    interface ListCallbackSingleChoice {
        /**
         * 返回item单选是否被选中
         * */
        fun onSelection(dialog: MaterialDialog, itemView: View, which: Int, text: CharSequence): Boolean
    }

    /**
     * 用于多选（复选框）列表对话框的回调。
     * */
    interface ListCallbackMultiChoice {
        /**
         * 返回多选item是否被选中
         * */
        fun onSelection(dialog: MaterialDialog, which: Array<Int>, text: Array<CharSequence>): Boolean
    }

    /**
     * 定义单个回调。
     * */
    interface SingleButtonCallback {
        fun onClick(dialog: MaterialDialog, which: DialogAction)
    }

    /**
     * 定义输入回调
     * */
    interface InputCallback {
        fun onInput(dialog: MaterialDialog, input: CharSequence)
    }

    /**
     * 用来构建一个material dialog
     * */
    class Builder(var mContext: Context?) {
        // 显示位置
        var titleGravity = GravityEnum.START
        var contentGravity = GravityEnum.START
        var btnStackedGravity = GravityEnum.END
        var itemsGravity = GravityEnum.START
        var buttonsGravity = GravityEnum.START

        // 字体颜色
        var buttonRippleColor: Int = 0
        var titleColor: Int = -1
        var contentColor: Int = -1

        // 显示内容
        var title: CharSequence? = null
        var content: CharSequence? = null
        var items: MutableList<CharSequence>? = null
        var positiveText: CharSequence? = null
        var neutralText: CharSequence? = null
        var negativeText: CharSequence? = null

        // 按钮是否可聚焦
        var positiveFocus: Boolean = false
        var neutralFocus: Boolean = false
        var negativeFocus: Boolean = false

        // 自定义布局
        var customView: View? = null
        // 按钮字体颜色有关
        var widgetColor: Int = -1
        var choiceWidgetColor: ColorStateList? = null
        var positiveColor: ColorStateList? = null
        var negativeColor: ColorStateList? = null
        var neutralColor: ColorStateList? = null
        var linkColor: ColorStateList? = null

        // 回调有关
        var onPositiveCallback: SingleButtonCallback? = null
        var onNegativeCallback: SingleButtonCallback? = null
        var onNeutralCallback: SingleButtonCallback? = null
        var onAnyCallback: SingleButtonCallback? = null
        var listCallback: ListCallback? = null
        var listLongCallback: ListLongCallback? = null
        var listCallbackSingleChoice: ListCallbackSingleChoice? = null
        var listCallbackMultiChoice: ListCallbackMultiChoice? = null

        // 主题
        var theme: Theme = Theme.LIGHT

        var alwaysCallMultiChoiceCallback: Boolean = false
        var alwaysCallSingleChoiceCallback: Boolean = false
        var cancelable: Boolean = true
        var canceledOnTouchOutside: Boolean = true

        var contentLineSpacingMultiplier: Float = 1.2f
        var selectedIndex: Int = -1

        // 列表选中索引
        var selectedIndices: Array<Int>? = null
        // 列表没有选中索引
        var disabledIndices: Array<Int>? = null

        var autoDismiss: Boolean = true
        var mediumFont: Typeface? = null

        var icon: Drawable? = null
        var limitIconToDefaultSize: Boolean = false
        var maxIconSize: Int = -1

        // 列表适配器
        var adapter: RecyclerView.Adapter<*>? = null
        var layoutManager: RecyclerView.LayoutManager? = null

        // 弹窗监听
        var dismissListener: DialogInterface.OnDismissListener? = null
        var cancelListener: DialogInterface.OnCancelListener? = null
        var keyListener: DialogInterface.OnKeyListener? = null
        var showListener: DialogInterface.OnShowListener? = null

        var stackingBehavior: StackingBehavior = StackingBehavior.ADAPTIVE
        var wrapCustomViewInScroll: Boolean = false

        var dividerColor: Int = -1
        var backgroundColor: Int = -1

        var indeterminateProgress: Boolean = false
        var showMinMax: Boolean = false

        // 进度条有关
        var progress: Int = -2
        var progressMax: Int = 0
        // 输入框有关
        var inputPrefill: CharSequence? = null
        var inputHint: CharSequence? = null
        var inputCallback: InputCallback? = null
        var inputAllowEmpty: Boolean = false
        var inputType: Int = -1
        var alwaysCallInputCallback: Boolean = false
        var inputMinLength: Int = -1
        var inputMaxLength: Int = -1
        var inputRangeErrorColor: Int = 0

        var itemIds: IntArray? = null

        // CheckBox有关
        var checkBoxPrompt: CharSequence? = null
        var checkBoxPromptInitiallyChecked: Boolean = false
        var checkBoxPromptListener: CompoundButton.OnCheckedChangeListener? = null
        var inputFilters: Array<InputFilter>? = null

        var progressNumberFormat: String = "%1d/%2d"
        var progressPercentFormat: NumberFormat = NumberFormat.getPercentInstance()
        var indeterminateIsHorizontalProgress: Boolean = false

        // 是否设置颜色有关
        var titleColorSet: Boolean = false
        var contentColorSet: Boolean = false
        var itemColorSet: Boolean = false
        var positiveColorSet: Boolean = false
        var neutralColorSet: Boolean = false
        var negativeColorSet: Boolean = false
        var widgetColorSet: Boolean = false
        var dividerColorSet: Boolean = false

        // 背景资源
        @DrawableRes
        var listSelector: Int = 0
        @DrawableRes
        var btnSelectorStacked: Int = 0
        @DrawableRes
        var btnSelectorPositive: Int = 0
        @DrawableRes
        var btnSelectorNeutral: Int = 0
        @DrawableRes
        var btnSelectorNegative: Int = 0

        var itemColor: Int = -1
        var regularFont: Typeface? = null

        var tag: Any? = null

        init {
            val materialBlue = DialogUtil.getColor(mContext!!, R.color.md_material_blue_600)

            // 检索默认的深颜色，在动作按钮和进度条上使用。
            this.widgetColor = DialogUtil.resolveColor(mContext!!, R.attr.colorAccent, materialBlue)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.widgetColor = DialogUtil.resolveColor(mContext!!, android.R.attr.colorAccent, this.widgetColor)
            }

            this.positiveColor = DialogUtil.getActionTextStateList(mContext!!, this.widgetColor)
            this.negativeColor = DialogUtil.getActionTextStateList(mContext!!, this.widgetColor)
            this.neutralColor = DialogUtil.getActionTextStateList(mContext!!, this.widgetColor)
            this.linkColor = DialogUtil.getActionTextStateList(mContext!!, DialogUtil.resolveColor(mContext!!, R.attr.md_link_color, this.widgetColor))

            var fallback = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fallback = DialogUtil.resolveColor(mContext!!, android.R.attr.colorControlHighlight)
            }
            this.buttonRippleColor = DialogUtil.resolveColor(
                    mContext!!,
                    R.attr.md_btn_ripple_color,
                    DialogUtil.resolveColor(mContext!!, R.attr.colorControlHighlight, fallback))

            // 根据活动主题的主色黑暗（更白或更黑）设置默认主题
            val primaryTextColor = DialogUtil.resolveColor(mContext!!, android.R.attr.textColorPrimary)
            this.theme = if (DialogUtil.isColorDark(primaryTextColor)) Theme.LIGHT else Theme.DARK

            // 加载主题值
            checkSingleton()

            this.titleGravity = DialogUtil.resolveGravityEnum(mContext!!, R.attr.md_title_gravity, this.titleGravity)
            this.contentGravity = DialogUtil.resolveGravityEnum(mContext!!, R.attr.md_content_gravity, this.contentGravity)
            this.btnStackedGravity = DialogUtil.resolveGravityEnum(mContext!!, R.attr.md_btnstacked_gravity, this.btnStackedGravity)
            this.itemsGravity = DialogUtil.resolveGravityEnum(mContext!!, R.attr.md_items_gravity, this.itemsGravity)
            this.buttonsGravity = DialogUtil.resolveGravityEnum(mContext!!, R.attr.md_buttons_gravity, this.buttonsGravity)

            val mediumFont = DialogUtil.resolveString(mContext!!, R.attr.md_medium_font)
            val regularFont = DialogUtil.resolveString(mContext!!, R.attr.md_regular_font)
            try {
                typeface(mediumFont, regularFont)
            } catch (ignored: Throwable) {
                ignored.printStackTrace()
            }

            if (this.mediumFont == null) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.mediumFont = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                    } else {
                        this.mediumFont = Typeface.create("sans-serif", Typeface.BOLD)
                    }
                } catch (ignored: Throwable) {
                    this.mediumFont = Typeface.DEFAULT_BOLD
                }
            }
            if (this.regularFont == null) {
                try {
                    this.regularFont = Typeface.create("sans-serif", Typeface.NORMAL)
                } catch (ignored: Throwable) {
                    this.regularFont = Typeface.SANS_SERIF
                    if (this.regularFont == null) {
                        this.regularFont = Typeface.DEFAULT
                    }
                }
            }
        }

        /**
         * 检查是否单选
         * */
        private fun checkSingleton() {
            if (ThemeSingleton[false] == null) {
                return
            }
            val s = ThemeSingleton.get()
            if (s != null) {
                if (s.darkTheme) {
                    this.theme = Theme.DARK
                }
                if (s.titleColor != 0) {
                    this.titleColor = s.titleColor
                }
                if (s.contentColor != 0) {
                    this.contentColor = s.contentColor
                }
                if (s.positiveColor != null) {
                    this.positiveColor = s.positiveColor
                }
                if (s.neutralColor != null) {
                    this.neutralColor = s.neutralColor
                }
                if (s.negativeColor != null) {
                    this.negativeColor = s.negativeColor
                }
                if (s.itemColor != 0) {
                    this.itemColor = s.itemColor
                }
                if (s.icon != null) {
                    this.icon = s.icon
                }
                if (s.backgroundColor != 0) {
                    this.backgroundColor = s.backgroundColor
                }
                if (s.dividerColor != 0) {
                    this.dividerColor = s.dividerColor
                }
                if (s.btnSelectorStacked != 0) {
                    this.btnSelectorStacked = s.btnSelectorStacked
                }
                if (s.listSelector != 0) {
                    this.listSelector = s.listSelector
                }
                if (s.btnSelectorPositive != 0) {
                    this.btnSelectorPositive = s.btnSelectorPositive
                }
                if (s.btnSelectorNeutral != 0) {
                    this.btnSelectorNeutral = s.btnSelectorNeutral
                }
                if (s.btnSelectorNegative != 0) {
                    this.btnSelectorNegative = s.btnSelectorNegative
                }
                if (s.widgetColor != 0) {
                    this.widgetColor = s.widgetColor
                }
                if (s.linkColor != null) {
                    this.linkColor = s.linkColor
                }
                this.titleGravity = s.titleGravity
                this.contentGravity = s.contentGravity
                this.btnStackedGravity = s.btnStackedGravity
                this.itemsGravity = s.itemsGravity
                this.buttonsGravity = s.buttonsGravity
            }
        }

        /**
         * 标题
         * */
        fun title(@StringRes titleRes: Int): Builder {
            title(mContext!!.getText(titleRes))
            return this
        }

        fun title(title: CharSequence): Builder {
            this.title = title
            return this
        }

        fun titleGravity(gravity: GravityEnum): Builder {
            this.titleGravity = gravity
            return this
        }

        fun titleColor(@ColorInt color: Int): Builder {
            this.titleColor = color
            this.titleColorSet = true
            return this
        }

        fun titleColorRes(@ColorRes colorRes: Int): Builder {
            return titleColor(DialogUtil.getColor(mContext!!, colorRes))
        }

        fun titleColorAttr(@AttrRes colorAttr: Int): Builder {
            return titleColor(DialogUtil.resolveColor(mContext!!, colorAttr))
        }

        /**
         * 按钮颜色
         * */
        fun buttonRippleColor(@ColorInt color: Int): Builder {
            this.buttonRippleColor = color
            return this
        }

        fun buttonRippleColorRes(@ColorRes colorRes: Int): Builder {
            return buttonRippleColor(DialogUtil.getColor(mContext!!, colorRes))
        }

        fun buttonRippleColorAttr(@AttrRes colorAttr: Int): Builder {
            return buttonRippleColor(DialogUtil.resolveColor(mContext!!, colorAttr))
        }

        /**
         * 设置对话框中使用的字体风格
         */
        fun typeface(medium: Typeface?, regular: Typeface?): Builder {
            this.mediumFont = medium
            this.regularFont = regular
            return this
        }

        /**
         * 设置对话框中使用的字体风格
         */
        fun typeface(medium: String?, regular: String?): Builder {
            if (medium != null && !medium.trim { it <= ' ' }.isEmpty()) {
                this.mediumFont = TypefaceHelper.get(mContext!!, medium)
                if (this.mediumFont == null) {
                    throw IllegalArgumentException("No font asset found for \"" + medium + "\"")
                }
            }
            if (regular != null && !regular.trim { it <= ' ' }.isEmpty()) {
                this.regularFont = TypefaceHelper.get(mContext!!, regular)
                if (this.regularFont == null) {
                    throw IllegalArgumentException("No font asset found for \"" + regular + "\"")
                }
            }
            return this
        }

        /**
         * 图标
         * */
        fun icon(icon: Drawable): Builder {
            this.icon = icon
            return this
        }

        fun iconRes(@DrawableRes icon: Int): Builder {
            this.icon = ResourcesCompat.getDrawable(mContext!!.resources, icon, null)
            return this
        }

        fun iconAttr(@AttrRes iconAttr: Int): Builder {
            this.icon = DialogUtil.resolveDrawable(mContext!!, iconAttr)
            return this
        }

        /**
         * 内容
         * */
        @JvmOverloads
        fun content(@StringRes contentRes: Int, html: Boolean = false): Builder {
            var text = mContext!!.getText(contentRes)
            if (html) {
                text = Html.fromHtml(text.toString().replace("\n", "<br/>"))
            }
            return content(text)
        }

        fun content(content: CharSequence): Builder {
            if (this.customView != null) {
                throw IllegalStateException("You cannot set content() " + "when you're using a custom view.")
            }
            this.content = content
            return this
        }

        fun content(@StringRes contentRes: Int, vararg formatArgs: Any): Builder {
            val str = String.format(mContext!!.getString(contentRes), *formatArgs).replace("\n", "<br/>")

            return content(Html.fromHtml(str))
        }

        fun contentColor(@ColorInt color: Int): Builder {
            this.contentColor = color
            this.contentColorSet = true
            return this
        }

        fun contentColorRes(@ColorRes colorRes: Int): Builder {
            contentColor(DialogUtil.getColor(mContext!!, colorRes))
            return this
        }

        fun contentColorAttr(@AttrRes colorAttr: Int): Builder {
            contentColor(DialogUtil.resolveColor(mContext!!, colorAttr))
            return this
        }

        fun contentGravity(gravity: GravityEnum):Builder {
            this.contentGravity = gravity
            return this
        }

        fun contentLineSpacing(multiplier: Float): Builder {
            this.contentLineSpacingMultiplier = multiplier
            return this
        }

        /**
         * 列表
         * */
        fun items(collection: Collection<*>): Builder {
            if (collection.isNotEmpty()) {
                val array: Array<CharSequence> = Array(collection.size, { "" })
                for ((i, obj) in collection.withIndex()) {
                    array[i] = obj.toString()
                }
                items(*array)
            } else if (collection.isEmpty()) {
                items = ArrayList()
            }
            return this
        }

        fun items(@ArrayRes itemsRes: Int): Builder {
            items(*mContext!!.resources.getTextArray(itemsRes))
            return this
        }

        fun items(vararg items: CharSequence): Builder {
            if (this.customView != null) {
                throw IllegalStateException("You cannot set items()" + " when you're using a custom view.")
            }
            this.items = ArrayList()
            Collections.addAll(this.items, *items)
            return this
        }

        fun itemsCallback(callback: ListCallback): Builder {
            this.listCallback = callback
            this.listCallbackSingleChoice = null
            this.listCallbackMultiChoice = null
            return this
        }

        fun itemsLongCallback(callback: ListLongCallback): Builder {
            this.listLongCallback = callback
            this.listCallbackSingleChoice = null
            this.listCallbackMultiChoice = null
            return this
        }

        fun itemsColor(@ColorInt color: Int): Builder {
            this.itemColor = color
            this.itemColorSet = true
            return this
        }

        fun itemsColorRes(@ColorRes colorRes: Int): Builder {
            return itemsColor(DialogUtil.getColor(mContext!!, colorRes))
        }

        fun itemsColorAttr(@AttrRes colorAttr: Int): Builder {
            return itemsColor(DialogUtil.resolveColor(mContext!!, colorAttr))
        }

        fun itemsGravity(gravity: GravityEnum): Builder {
            this.itemsGravity = gravity
            return this
        }

        fun itemsIds(idsArray: IntArray): Builder {
            this.itemIds = idsArray
            return this
        }

        fun itemsIds(@ArrayRes idsArrayRes: Int): Builder {
            return itemsIds(mContext!!.resources.getIntArray(idsArrayRes))
        }

        /**
         * 按钮显示位置
         * */
        fun buttonsGravity(gravity: GravityEnum): Builder {
            this.buttonsGravity = gravity
            return this
        }

        /**
         * 单选
         */
        fun itemsCallbackSingleChoice(selectedIndex: Int, callback: ListCallbackSingleChoice): Builder {
            this.selectedIndex = selectedIndex
            this.listCallback = null
            this.listCallbackSingleChoice = callback
            this.listCallbackMultiChoice = null
            return this
        }

        /**
         * 单选
         */
        fun alwaysCallSingleChoiceCallback(): Builder {
            this.alwaysCallSingleChoiceCallback = true
            return this
        }

        /**
         * 多选
         */
        fun itemsCallbackMultiChoice(selectedIndices: Array<Int>?, callback: ListCallbackMultiChoice): Builder {
            this.selectedIndices = selectedIndices
            this.listCallback = null
            this.listCallbackSingleChoice = null
            this.listCallbackMultiChoice = callback
            return this
        }

        /**
         * 未选中索引
         */
        fun itemsDisabledIndices(disabledIndices: Array<Int>): Builder {
            this.disabledIndices = disabledIndices
            return this
        }

        /**
         * 多选
         */
        fun alwaysCallMultiChoiceCallback(): Builder {
            this.alwaysCallMultiChoiceCallback = true
            return this
        }

        /**
         * 确认按钮
         * */
        fun positiveText(@StringRes positiveRes: Int): Builder {
            if (positiveRes == 0) {
                return this
            }
            positiveText(mContext!!.getText(positiveRes))
            return this
        }

        fun positiveText(message: CharSequence): Builder {
            this.positiveText = message
            return this
        }

        fun positiveColor(@ColorInt color: Int): Builder {
            return positiveColor(DialogUtil.getActionTextStateList(mContext!!, color))
        }

        fun positiveColorRes(@ColorRes colorRes: Int): Builder {
            return positiveColor(DialogUtil.getActionTextColorStateList(mContext!!, colorRes))
        }

        fun positiveColorAttr(@AttrRes colorAttr: Int): Builder {
            return positiveColor(DialogUtil.resolveActionTextColorStateList(mContext!!, colorAttr, null))
        }

        fun positiveColor(colorStateList: ColorStateList?): Builder {
            this.positiveColor = colorStateList
            this.positiveColorSet = true
            return this
        }

        fun positiveFocus(isFocusedDefault: Boolean): Builder {
            this.positiveFocus = isFocusedDefault
            return this
        }

        /**
         * 中立按钮
         * */
        fun neutralText(@StringRes neutralRes: Int): Builder {
            return if (neutralRes == 0) {
                this
            } else neutralText(mContext!!.getText(neutralRes))
        }

        fun neutralText(message: CharSequence): Builder {
            this.neutralText = message
            return this
        }

        fun neutralColor(@ColorInt color: Int): Builder {
            return neutralColor(DialogUtil.getActionTextStateList(mContext!!, color))
        }

        fun neutralColorRes(@ColorRes colorRes: Int): Builder {
            return neutralColor(DialogUtil.getActionTextColorStateList(mContext!!, colorRes))
        }

        fun neutralColorAttr(@AttrRes colorAttr: Int): Builder {
            return neutralColor(DialogUtil.resolveActionTextColorStateList(mContext!!, colorAttr, null))
        }

        fun neutralColor(colorStateList: ColorStateList?): Builder {
            this.neutralColor = colorStateList
            this.neutralColorSet = true
            return this
        }

        fun neutralFocus(isFocusedDefault: Boolean): Builder {
            this.neutralFocus = isFocusedDefault
            return this
        }

        /**
         * 取消按钮
         * */
        fun negativeColor(@ColorInt color: Int): Builder {
            return negativeColor(DialogUtil.getActionTextStateList(mContext!!, color))
        }

        fun negativeColorRes(@ColorRes colorRes: Int): Builder {
            return negativeColor(DialogUtil.getActionTextColorStateList(mContext!!, colorRes))
        }

        fun negativeColorAttr(@AttrRes colorAttr: Int): Builder {
            return negativeColor(DialogUtil.resolveActionTextColorStateList(mContext!!, colorAttr, null))
        }

        fun negativeColor(colorStateList: ColorStateList?): Builder {
            this.negativeColor = colorStateList
            this.negativeColorSet = true
            return this
        }

        fun negativeText(@StringRes negativeRes: Int): Builder {
            return if (negativeRes == 0) {
                this
            } else {
                negativeText(mContext!!.getText(negativeRes))
            }
        }

        fun negativeText(message: CharSequence): Builder {
            this.negativeText = message
            return this
        }

        fun negativeFocus(isFocusedDefault: Boolean): Builder {
            this.negativeFocus = isFocusedDefault
            return this
        }

        /**
         * 点击跳转连接
         * */
        fun linkColor(@ColorInt color: Int): Builder {
            return linkColor(DialogUtil.getActionTextStateList(mContext!!, color))
        }

        fun linkColorRes(@ColorRes colorRes: Int): Builder {
            return linkColor(DialogUtil.getActionTextColorStateList(mContext!!, colorRes))
        }

        fun linkColorAttr(@AttrRes colorAttr: Int): Builder {
            return linkColor(DialogUtil.resolveActionTextColorStateList(mContext!!, colorAttr, null))
        }

        fun linkColor(colorStateList: ColorStateList?): Builder {
            this.linkColor = colorStateList
            return this
        }

        fun listSelector(@DrawableRes selectorRes: Int): Builder {
            this.listSelector = selectorRes
            return this
        }

        fun btnSelectorStacked(@DrawableRes selectorRes: Int): Builder {
            this.btnSelectorStacked = selectorRes
            return this
        }

        fun btnSelector(@DrawableRes selectorRes: Int): Builder {
            this.btnSelectorPositive = selectorRes
            this.btnSelectorNeutral = selectorRes
            this.btnSelectorNegative = selectorRes
            return this
        }

        fun btnSelector(@DrawableRes selectorRes: Int, which: DialogAction): Builder {
            when (which) {
                DialogAction.NEUTRAL -> this.btnSelectorNeutral = selectorRes
                DialogAction.NEGATIVE -> this.btnSelectorNegative = selectorRes
                else -> this.btnSelectorPositive = selectorRes
            }
            return this
        }

        /**
         * 按钮内容位置
         */
        fun btnStackedGravity(gravity: GravityEnum): Builder {
            this.btnStackedGravity = gravity
            return this
        }

        /**
         * 复选框
         * */
        fun checkBoxPrompt(prompt: CharSequence, initiallyChecked: Boolean, checkListener: CompoundButton.OnCheckedChangeListener?): Builder {
            this.checkBoxPrompt = prompt
            this.checkBoxPromptInitiallyChecked = initiallyChecked
            this.checkBoxPromptListener = checkListener
            return this
        }

        fun checkBoxPromptRes(@StringRes prompt: Int, initiallyChecked: Boolean, checkListener: CompoundButton.OnCheckedChangeListener?): Builder {
            return checkBoxPrompt(mContext!!.resources.getText(prompt), initiallyChecked, checkListener)
        }

        /**
         * 自定义View
         * */
        fun customView(@LayoutRes layoutRes: Int, wrapInScrollView: Boolean): Builder {
            val li = LayoutInflater.from(mContext)
            return customView(li.inflate(layoutRes, null), wrapInScrollView)
        }

        fun customView(view: View, wrapInScrollView: Boolean): Builder {
            if (this.content != null) {
                throw IllegalStateException("You cannot use customView() when you have content set.")
            } else if (this.items != null) {
                throw IllegalStateException("You cannot use customView() when you have items set.")
            } else if (this.inputCallback != null) {
                throw IllegalStateException("You cannot use customView() with an input dialog")
            } else if (this.progress > -2 || this.indeterminateProgress) {
                throw IllegalStateException("You cannot use customView() with a progress dialog")
            }
            if (view.parent != null && view.parent is ViewGroup) {
                (view.parent as ViewGroup).removeView(view)
            }
            this.customView = view
            this.wrapCustomViewInScroll = wrapInScrollView
            return this
        }

        /**
         * 进度对话框
         */
        fun progress(indeterminate: Boolean, max: Int): Builder {
            if (this.customView != null) {
                throw IllegalStateException(
                        "You cannot set progress() when you're using a custom view.")
            }
            if (indeterminate) {
                this.indeterminateProgress = true
                this.progress = -2
            } else {
                this.indeterminateIsHorizontalProgress = false
                this.indeterminateProgress = false
                this.progress = -1
                this.progressMax = max
            }
            return this
        }

        /**
         * 进度对话框
         */
        fun progress(indeterminate: Boolean, max: Int, showMinMax: Boolean): Builder {
            this.showMinMax = showMinMax
            return progress(indeterminate, max)
        }

        /**
         * 改变文本显示的小电流和最大单位进步的格式，默认为%1d%2d
         */
        fun progressNumberFormat(format: String): Builder {
            this.progressNumberFormat = format
            return this
        }

        /**
         * 更改显示进度百分比的小文本的格式
         */
        fun progressPercentFormat(format: NumberFormat): Builder {
            this.progressPercentFormat = format
            return this
        }

        /**
         * 默认情况下，不确定的进度对话框将使用循环指示符。您可以将其更改为使用水平进度指示器。
         */
        fun progressIndeterminateStyle(horizontal: Boolean): Builder {
            this.indeterminateIsHorizontalProgress = horizontal
            return this
        }

        fun widgetColor(@ColorInt color: Int): Builder {
            this.widgetColor = color
            this.widgetColorSet = true
            return this
        }

        fun widgetColorRes(@ColorRes colorRes: Int): Builder {
            return widgetColor(DialogUtil.getColor(mContext!!, colorRes))
        }

        fun widgetColorAttr(@AttrRes colorAttr: Int): Builder {
            return widgetColor(DialogUtil.resolveColor(mContext!!, colorAttr))
        }

        fun choiceWidgetColor(colorStateList: ColorStateList?): Builder {
            this.choiceWidgetColor = colorStateList
            return this
        }

        /**
         * 线条
         * */
        fun dividerColor(@ColorInt color: Int): Builder {
            this.dividerColor = color
            this.dividerColorSet = true
            return this
        }

        fun dividerColorRes(@ColorRes colorRes: Int): Builder {
            return dividerColor(DialogUtil.getColor(mContext!!, colorRes))
        }

        fun dividerColorAttr(@AttrRes colorAttr: Int): Builder {
            return dividerColor(DialogUtil.resolveColor(mContext!!, colorAttr))
        }

        /**
         * 背景
         * */
        fun backgroundColor(@ColorInt color: Int):Builder {
            this.backgroundColor = color
            return this
        }

        fun backgroundColorRes(@ColorRes colorRes: Int): Builder {
            return backgroundColor(DialogUtil.getColor(mContext!!, colorRes))
        }

        fun backgroundColorAttr(@AttrRes colorAttr: Int): Builder {
            return backgroundColor(DialogUtil.resolveColor(mContext!!, colorAttr))
        }

        /**
         * 回调监听
         * */
        fun onPositive(callback: SingleButtonCallback): Builder {
            this.onPositiveCallback = callback
            return this
        }

        fun onNegative(callback: SingleButtonCallback): Builder {
            this.onNegativeCallback = callback
            return this
        }

        fun onNeutral(callback: SingleButtonCallback): Builder {
            this.onNeutralCallback = callback
            return this
        }

        fun onAny(callback: SingleButtonCallback): Builder {
            this.onAnyCallback = callback
            return this
        }

        /**
         * 主题
         * */
        fun theme(theme: Theme): Builder {
            this.theme = theme
            return this
        }

        fun cancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            this.canceledOnTouchOutside = cancelable
            return this
        }

        fun canceledOnTouchOutside(canceledOnTouchOutside: Boolean): Builder {
            this.canceledOnTouchOutside = canceledOnTouchOutside
            return this
        }

        /**
         * 默认为true。如果设置为false，当按下操作按钮时，对话框不会自动被删除，
         * 当用户选择一个列表项时，该对话框不会自动被删除。
         */
        fun autoDismiss(dismiss: Boolean): Builder {
            this.autoDismiss = dismiss
            return this
        }

        /**
         * 列表适配器
         */
        fun adapter(adapter: RecyclerView.Adapter<*>, layoutManager: RecyclerView.LayoutManager?): Builder {
            if (this.customView != null) {
                throw IllegalStateException("You cannot set adapter() when " + "you're using a custom view.")
            }
            if (layoutManager != null && layoutManager !is LinearLayoutManager && layoutManager !is GridLayoutManager) {
                throw IllegalStateException("You can currently only use LinearLayoutManager" + " and GridLayoutManager with this library.")
            }
            this.adapter = adapter
            this.layoutManager = layoutManager
            return this
        }

        /**
         * 限制一套图标的显示大小48dp。
         * */
        fun limitIconToDefaultSize(): Builder {
            this.limitIconToDefaultSize = true
            return this
        }

        fun maxIconSize(maxIconSize: Int): Builder {
            this.maxIconSize = maxIconSize
            return this
        }

        fun maxIconSizeRes(@DimenRes maxIconSizeRes: Int): Builder {
            return maxIconSize(mContext!!.resources.getDimension(maxIconSizeRes).toInt())
        }

        /**
         * 弹窗有关监听
         * */
        fun showListener(listener: DialogInterface.OnShowListener): Builder {
            this.showListener = listener
            return this
        }

        fun dismissListener(listener: DialogInterface.OnDismissListener): Builder {
            this.dismissListener = listener
            return this
        }

        fun cancelListener(listener: DialogInterface.OnCancelListener): Builder {
            this.cancelListener = listener
            return this
        }

        fun keyListener(listener: DialogInterface.OnKeyListener): Builder {
            this.keyListener = listener
            return this
        }

        fun stackingBehavior(behavior: StackingBehavior): Builder {
            this.stackingBehavior = behavior
            return this
        }

        /**
         * 输入框
         * */
        fun input(hint: CharSequence?, prefill: CharSequence?, allowEmptyInput: Boolean, callback: InputCallback): Builder {
            if (this.customView != null) {
                throw IllegalStateException("You cannot set content() when you're using a custom view.")
            }
            this.inputCallback = callback
            this.inputHint = hint
            this.inputPrefill = prefill
            this.inputAllowEmpty = allowEmptyInput
            return this
        }

        fun input(hint: CharSequence?, prefill: CharSequence?, callback: InputCallback): Builder {
            return input(hint, prefill, true, callback)
        }

        fun input(@StringRes hint: Int, @StringRes prefill: Int, allowEmptyInput: Boolean, callback: InputCallback): Builder {
            return input(
                    if (hint == 0) null else mContext!!.getText(hint),
                    if (prefill == 0) null else mContext!!.getText(prefill),
                    allowEmptyInput,
                    callback)
        }

        fun input(@StringRes hint: Int, @StringRes prefill: Int, callback: InputCallback): Builder {
            return input(hint, prefill, true, callback)
        }

        fun inputType(type: Int): Builder {
            this.inputType = type
            return this
        }

        @JvmOverloads
        fun inputRange(@IntRange(from = 0, to = Integer.MAX_VALUE.toLong()) minLength: Int,
                       @IntRange(from = -1, to = Integer.MAX_VALUE.toLong()) maxLength: Int,
                       @ColorInt errorColor: Int = 0): Builder {
            if (minLength < 0) {
                throw IllegalArgumentException("Min length for input dialogs cannot be less than 0.")
            }
            this.inputMinLength = minLength
            this.inputMaxLength = maxLength
            if (errorColor == 0) {
                this.inputRangeErrorColor = DialogUtil.getColor(mContext!!, R.color.md_edit_text_error)
            } else {
                this.inputRangeErrorColor = errorColor
            }
            if (this.inputMinLength > 0) {
                this.inputAllowEmpty = false
            }
            return this
        }

        fun inputRangeRes(@IntRange(from = 0, to = Integer.MAX_VALUE.toLong()) minLength: Int,
                          @IntRange(from = -1, to = Integer.MAX_VALUE.toLong()) maxLength: Int,
                          @ColorRes errorColor: Int): Builder {
            return inputRange(minLength, maxLength, DialogUtil.getColor(mContext!!, errorColor))
        }

        fun inputFilters(filters: Array<InputFilter>): Builder {
            this.inputFilters = filters
            return this
        }

        fun alwaysCallInputCallback(): Builder {
            this.alwaysCallInputCallback = true
            return this
        }

        fun tag(tag: Any?): Builder {
            this.tag = tag
            return this
        }

        @UiThread
        fun build(): MaterialDialog {
            return MaterialDialog(this)
        }

        @UiThread
        fun show(): MaterialDialog {
            val dialog = build()
            dialog.show()
            return dialog
        }
    }
}