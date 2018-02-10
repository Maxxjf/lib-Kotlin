package com.qcloud.qclib.materialdesign.widget

import android.graphics.Point
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.annotation.LayoutRes
import android.support.annotation.StyleRes
import android.support.annotation.UiThread
import android.text.InputType
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import com.qcloud.qclib.R
import com.qcloud.qclib.materialdesign.dialogs.MaterialDialog
import com.qcloud.qclib.materialdesign.enums.DialogAction
import com.qcloud.qclib.materialdesign.enums.ListType
import com.qcloud.qclib.materialdesign.enums.Theme
import com.qcloud.qclib.utils.DialogUtil
import me.zhanghai.android.materialprogressbar.HorizontalProgressDrawable
import me.zhanghai.android.materialprogressbar.IndeterminateCircularProgressDrawable
import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable
import java.util.*

/**
 * 类说明：用material dialog在初始化对话框
 * Author: Kuzan
 * Date: 2018/2/9 11:23.
 */
object DialogHelper {

    /**
     * 获取主题
     * */
    @StyleRes
    fun getTheme(builder: MaterialDialog.Builder): Int {
        val darkTheme = DialogUtil.resolveBoolean(
                builder.mContext!!, R.attr.md_dark_theme, builder.theme == Theme.DARK)
        builder.theme = if (darkTheme) Theme.DARK else Theme.LIGHT
        return if (darkTheme) R.style.MD_Dark else R.style.MD_Light
    }

    /**
     * 获取布局
     * */
    @LayoutRes
    fun getInflateLayout(builder: MaterialDialog.Builder): Int {
        return if (builder.customView != null) {
            R.layout.md_dialog_custom
        } else if (builder.items != null || builder.adapter != null) {
            if (builder.checkBoxPrompt != null) {
                R.layout.md_dialog_list_check
            } else {
                R.layout.md_dialog_list
            }
        } else if (builder.progress > -2) {
            R.layout.md_dialog_progress
        } else if (builder.indeterminateProgress) {
            if (builder.indeterminateIsHorizontalProgress) {
                R.layout.md_dialog_progress_indeterminate_horizontal
            } else {
                R.layout.md_dialog_progress_indeterminate
            }
        } else if (builder.inputCallback != null) {
            if (builder.checkBoxPrompt != null) {
                R.layout.md_dialog_input_check
            } else {
                R.layout.md_dialog_input
            }
        } else if (builder.checkBoxPrompt != null) {
            R.layout.md_dialog_basic_check
        } else {
            R.layout.md_dialog_basic
        }
    }

    /**
     * 初始化弹窗
     * */
    @UiThread
    fun init(dialog: MaterialDialog) {
        val builder = dialog.builder

        // Set cancelable flag and dialog background color
        // 返回按钮
        dialog.setCancelable(builder.cancelable)
        // 点击弹窗外面
        dialog.setCanceledOnTouchOutside(builder.canceledOnTouchOutside)
        // 背景颜色
        if (builder.backgroundColor == 0) {
            builder.backgroundColor = DialogUtil.resolveColor(dialog.mView!!.context,
                    R.attr.md_background_color,
                    DialogUtil.resolveColor(dialog.context, R.attr.colorBackgroundFloating))
        }
        if (builder.backgroundColor != 0) {
            val drawable = GradientDrawable()
            drawable.cornerRadius = builder.mContext!!.resources.getDimension(R.dimen.padding_1)
            drawable.setColor(builder.backgroundColor)
            dialog.window.setBackgroundDrawable(drawable)
        }

        // 确认按钮颜色
        if (!builder.positiveColorSet) {
            builder.positiveColor = DialogUtil.resolveActionTextColorStateList(
                    builder.mContext!!, R.attr.md_positive_color, builder.positiveColor)
        }
        // 中立按钮颜色
        if (!builder.neutralColorSet) {
            builder.neutralColor = DialogUtil.resolveActionTextColorStateList(
                    builder.mContext!!, R.attr.md_neutral_color, builder.neutralColor)
        }
        // 取消按钮颜色
        if (!builder.negativeColorSet) {
            builder.negativeColor = DialogUtil.resolveActionTextColorStateList(
                    builder.mContext!!, R.attr.md_negative_color, builder.negativeColor)
        }
        if (!builder.widgetColorSet) {
            builder.widgetColor = DialogUtil.resolveColor(builder.mContext!!, R.attr.md_widget_color, builder.widgetColor)
        }

        // 标题颜色
        if (!builder.titleColorSet) {
            val titleColorFallback = DialogUtil.resolveColor(dialog.context, android.R.attr.textColorPrimary)
            builder.titleColor = DialogUtil.resolveColor(builder.mContext!!, R.attr.md_title_color, titleColorFallback)
        }
        // 提示内容颜色
        if (!builder.contentColorSet) {
            val contentColorFallback = DialogUtil.resolveColor(dialog.context, android.R.attr.textColorSecondary)
            builder.contentColor = DialogUtil.resolveColor(builder.mContext!!, R.attr.md_content_color, contentColorFallback)
        }
        // 列表颜色
        if (!builder.itemColorSet) {
            builder.itemColor = DialogUtil.resolveColor(builder.mContext!!, R.attr.md_item_color, builder.contentColor)
        }

        // 初始化控件
        dialog.titleView = dialog.mView!!.findViewById(R.id.md_title)
        dialog.iconView = dialog.mView!!.findViewById(R.id.md_icon)
        dialog.titleFrame = dialog.mView!!.findViewById(R.id.md_titleFrame)
        dialog.contentView = dialog.mView!!.findViewById(R.id.md_content)
        dialog.recyclerView = dialog.mView!!.findViewById(R.id.md_contentRecyclerView)
        dialog.checkBoxPrompt = dialog.mView!!.findViewById(R.id.md_promptCheckbox)

        dialog.positiveButton = dialog.mView!!.findViewById(R.id.md_buttonDefaultPositive)
        dialog.neutralButton = dialog.mView!!.findViewById(R.id.md_buttonDefaultNeutral)
        dialog.negativeButton = dialog.mView!!.findViewById(R.id.md_buttonDefaultNegative)

        // 让输入框不显示在输入对话框中。
        if (builder.inputCallback != null && builder.positiveText == null) {
            builder.positiveText = builder.mContext!!.getText(android.R.string.ok)
        }

        // 根据是否设置文本设置动作按钮的初始可见性。
        dialog.positiveButton?.visibility = if (builder.positiveText != null) View.VISIBLE else View.GONE
        dialog.neutralButton?.visibility = if (builder.neutralText != null) View.VISIBLE else View.GONE
        dialog.negativeButton?.visibility = if (builder.negativeText != null) View.VISIBLE else View.GONE

        // 设置动作按钮的焦点
        dialog.positiveButton?.isFocusable = true
        dialog.neutralButton?.isFocusable = true
        dialog.negativeButton?.isFocusable = true
        if (builder.positiveFocus) {
            dialog.positiveButton?.requestFocus()
        }
        if (builder.neutralFocus) {
            dialog.neutralButton?.requestFocus()
        }
        if (builder.negativeFocus) {
            dialog.negativeButton?.requestFocus()
        }

        // 设置图标
        if (builder.icon != null) {
            dialog.iconView?.visibility = View.VISIBLE
            dialog.iconView?.setImageDrawable(builder.icon)
        } else {
            val d = DialogUtil.resolveDrawable(builder.mContext!!, R.attr.md_icon)
            if (d != null) {
                dialog.iconView?.visibility = View.VISIBLE
                dialog.iconView?.setImageDrawable(d)
            } else {
                dialog.iconView?.visibility = View.GONE
            }
        }

        // 设置图标大小限制
        var maxIconSize = builder.maxIconSize
        if (maxIconSize == -1) {
            maxIconSize = DialogUtil.resolveDimension(builder.mContext!!, R.attr.md_icon_max_size)
        }
        if (builder.limitIconToDefaultSize || DialogUtil.resolveBoolean(builder.mContext!!, R.attr.md_icon_limit_icon_to_default_size)) {
            maxIconSize = builder.mContext!!.resources.getDimensionPixelSize(R.dimen.tab_height)
        }
        if (maxIconSize > -1) {
            dialog.iconView?.adjustViewBounds = true
            dialog.iconView?.maxHeight = maxIconSize
            dialog.iconView?.maxWidth = maxIconSize
            dialog.iconView?.requestLayout()
        }

        // 设置线条颜色
        if (!builder.dividerColorSet) {
            val dividerFallback = DialogUtil.resolveColor(dialog.context, R.attr.md_divider)
            builder.dividerColor = DialogUtil.resolveColor(builder.mContext!!, R.attr.md_divider_color, dividerFallback)
        }
        dialog.mView!!.setDividerColor(builder.dividerColor)

        // 设置标题和标题框
        if (dialog.titleView != null) {
            dialog.setTypeface(dialog.titleView!!, builder.mediumFont)
            dialog.titleView!!.setTextColor(builder.titleColor)
            dialog.titleView!!.gravity = builder.titleGravity.getGravityInt()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                dialog.titleView!!.textAlignment = builder.titleGravity.getTextAlignment()
            }

            if (builder.title == null) {
                dialog.titleFrame?.visibility = View.GONE
            } else {
                dialog.titleView!!.text = builder.title
                dialog.titleFrame?.visibility = View.VISIBLE
            }
        }

        // 设置内容
        if (dialog.contentView != null) {
            dialog.contentView!!.movementMethod = LinkMovementMethod()
            dialog.setTypeface(dialog.contentView!!, builder.regularFont)
            dialog.contentView!!.setLineSpacing(0f, builder.contentLineSpacingMultiplier)
            if (builder.linkColor == null) {
                dialog.contentView!!.setLinkTextColor(DialogUtil.resolveColor(dialog.context, android.R.attr.textColorPrimary))
            } else {
                dialog.contentView!!.setLinkTextColor(builder.linkColor)
            }
            dialog.contentView!!.setTextColor(builder.contentColor)
            dialog.contentView!!.gravity = builder.contentGravity.getGravityInt()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                dialog.contentView!!.textAlignment = builder.contentGravity.getTextAlignment()
            }

            if (builder.content != null) {
                dialog.contentView!!.text = builder.content
                dialog.contentView!!.visibility = View.VISIBLE
            } else {
                dialog.contentView!!.visibility = View.GONE
            }
        }

        // 设置是否选中提示框
        if (dialog.checkBoxPrompt != null) {
            dialog.checkBoxPrompt!!.text = builder.checkBoxPrompt
            dialog.checkBoxPrompt!!.isChecked = builder.checkBoxPromptInitiallyChecked
            dialog.checkBoxPrompt!!.setOnCheckedChangeListener(builder.checkBoxPromptListener)
            dialog.setTypeface(dialog.checkBoxPrompt!!, builder.regularFont)
            dialog.checkBoxPrompt!!.setTextColor(builder.contentColor)
            MDTintHelper.setTint(dialog.checkBoxPrompt!!, builder.widgetColor)
        }

        // 设置动作按钮
        dialog.mView!!.setButtonGravity(builder.buttonsGravity)
        dialog.mView!!.setButtonStackedGravity(builder.btnStackedGravity)
        dialog.mView!!.setStackingBehavior(builder.stackingBehavior)
        var textAllCaps: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            textAllCaps = DialogUtil.resolveBoolean(builder.mContext!!, android.R.attr.textAllCaps, true)
            if (textAllCaps) {
                textAllCaps = DialogUtil.resolveBoolean(builder.mContext!!, R.attr.textAllCaps, true)
            }
        } else {
            textAllCaps = DialogUtil.resolveBoolean(builder.mContext!!, R.attr.textAllCaps, true)
        }

        // 确认按钮
        if (dialog.positiveButton != null) {
            dialog.setTypeface(dialog.positiveButton!!, builder.mediumFont)
            dialog.positiveButton!!.setAllCapsCompat(textAllCaps)
            dialog.positiveButton!!.text = builder.positiveText
            dialog.positiveButton!!.setTextColor(builder.positiveColor)
            dialog.positiveButton!!.setStackedSelector(dialog.getButtonSelector(DialogAction.POSITIVE, true))
            dialog.positiveButton!!.setDefaultSelector(dialog.getButtonSelector(DialogAction.POSITIVE, false))
            dialog.positiveButton!!.tag = DialogAction.POSITIVE
            dialog.positiveButton!!.setOnClickListener(dialog)
        }

        // 中立按钮
        if (dialog.negativeButton != null) {
            dialog.setTypeface(dialog.negativeButton!!, builder.mediumFont)
            dialog.negativeButton!!.setAllCapsCompat(textAllCaps)
            dialog.negativeButton!!.text = builder.negativeText
            dialog.negativeButton!!.setTextColor(builder.negativeColor)
            dialog.negativeButton!!.setStackedSelector(dialog.getButtonSelector(DialogAction.NEGATIVE, true))
            dialog.negativeButton!!.setDefaultSelector(dialog.getButtonSelector(DialogAction.NEGATIVE, false))
            dialog.negativeButton!!.tag = DialogAction.NEGATIVE
            dialog.negativeButton!!.setOnClickListener(dialog)
        }

        // 取消按钮
        if (dialog.neutralButton != null) {
            dialog.setTypeface(dialog.neutralButton!!, builder.mediumFont)
            dialog.neutralButton!!.setAllCapsCompat(textAllCaps)
            dialog.neutralButton!!.text = builder.neutralText
            dialog.neutralButton!!.setTextColor(builder.neutralColor)
            dialog.neutralButton!!.setStackedSelector(dialog.getButtonSelector(DialogAction.NEUTRAL, true))
            dialog.neutralButton!!.setDefaultSelector(dialog.getButtonSelector(DialogAction.NEUTRAL, false))
            dialog.neutralButton!!.tag = DialogAction.NEUTRAL
            dialog.neutralButton!!.setOnClickListener(dialog)
        }

        // 设置列表对话框内容
        if (builder.listCallbackMultiChoice != null) {
            dialog.selectedIndicesList = ArrayList()
        }
        if (dialog.recyclerView != null) {
            if (builder.adapter == null) {
                if (builder.listCallbackSingleChoice != null) {
                    dialog.listType = ListType.SINGLE
                } else if (builder.listCallbackMultiChoice != null) {
                    dialog.listType = ListType.MULTI
                    if (builder.selectedIndices != null) {
                        dialog.selectedIndicesList = ArrayList(Arrays.asList<Int>(*builder.selectedIndices!!))
                        builder.selectedIndices = null
                    }
                } else {
                    dialog.listType = ListType.REGULAR
                }

                builder.adapter = DefaultRvAdapter(dialog, ListType.getLayoutForType(dialog.listType))
            } else if (builder.adapter is MDAdapter) {
                (builder.adapter as MDAdapter).setDialog(dialog)
            }
        }

        // 设置进度对话框
        initProgressDialog(dialog)

        // 设置输入对话框
        initInputDialog(dialog)

        // 设置自定义视图
        if (builder.customView != null) {
            (dialog.mView!!.findViewById(R.id.md_root) as MDRootLayout).noTitleNoPadding = true
            val frame = dialog.mView!!.findViewById<FrameLayout>(R.id.md_customViewFrame)
            dialog.customViewFrame = frame
            var innerView = builder.customView
            if (innerView!!.parent != null) {
                (innerView.parent as ViewGroup).removeView(innerView)
            }
            if (builder.wrapCustomViewInScroll) {
                val r = dialog.context.resources
                val framePadding = r.getDimensionPixelSize(R.dimen.padding_12)
                val sv = ScrollView(dialog.context)
                val paddingTop = r.getDimensionPixelSize(R.dimen.padding_4)
                val paddingBottom = r.getDimensionPixelSize(R.dimen.padding_4)
                sv.clipToPadding = false
                if (innerView is EditText) {
                    sv.setPadding(framePadding, paddingTop, framePadding, paddingBottom)
                } else {
                    sv.setPadding(0, paddingTop, 0, paddingBottom)
                    innerView.setPadding(framePadding, 0, framePadding, 0)
                }
                sv.addView(
                        innerView,
                        ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                innerView = sv
            }
            frame.addView(
                    innerView,
                    ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }

        // 设置监听
        if (builder.showListener != null) {
            dialog.setOnShowListener(builder.showListener!!)
        }
        if (builder.cancelListener != null) {
            dialog.setOnCancelListener(builder.cancelListener)
        }
        if (builder.dismissListener != null) {
            dialog.setOnDismissListener(builder.dismissListener)
        }
        if (builder.keyListener != null) {
            dialog.setOnKeyListener(builder.keyListener)
        }

        // 设置内部显示监听器
        dialog.setOnShowListenerInternal()

        // 其他内部初始化
        dialog.invalidateList()
        dialog.setViewInternal(dialog.mView!!)
        dialog.checkIfListInitScroll()

        // 设置弹窗大小
        val wm = dialog.window.windowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val windowWidth = size.x
        val windowHeight = size.y

        val windowVerticalPadding = builder.mContext!!.resources.getDimensionPixelSize(R.dimen.margin_10)
        val maxWidth = windowWidth * 4 / 5

        dialog.mView!!.maxHeight = windowHeight - windowVerticalPadding * 2
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window.attributes)
        lp.width = maxWidth
        dialog.window.attributes = lp
    }

    private fun fixCanvasScalingWhenHardwareAccelerated(pb: ProgressBar) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Canvas scaling when hardware accelerated results in artifacts on older API levels, so
            // we need to use software rendering
            if (pb.isHardwareAccelerated && pb.layerType != View.LAYER_TYPE_SOFTWARE) {
                pb.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            }
        }
    }

    /**
     * 初始化加载进度条
     * */
    private fun initProgressDialog(dialog: MaterialDialog) {
        val builder = dialog.builder
        if (builder.indeterminateProgress || builder.progress > -2) {
            dialog.progressBar = dialog.mView!!.findViewById(android.R.id.progress)
            if (dialog.progressBar == null) {
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (builder.indeterminateProgress) {
                    if (builder.indeterminateIsHorizontalProgress) {
                        val d = IndeterminateHorizontalProgressDrawable(builder.mContext)
                        d.setTint(builder.widgetColor)
                        dialog.progressBar?.progressDrawable = d
                        dialog.progressBar?.indeterminateDrawable = d
                    } else {
                        val d = IndeterminateCircularProgressDrawable(builder.mContext)
                        d.setTint(builder.widgetColor)
                        dialog.progressBar?.progressDrawable = d
                        dialog.progressBar?.indeterminateDrawable = d
                    }
                } else {
                    val d = HorizontalProgressDrawable(builder.mContext)
                    d.setTint(builder.widgetColor)
                    dialog.progressBar?.progressDrawable = d
                    dialog.progressBar?.indeterminateDrawable = d
                }
            } else {
                MDTintHelper.setTint(dialog.progressBar!!, builder.widgetColor)
            }

            if (!builder.indeterminateProgress || builder.indeterminateIsHorizontalProgress) {
                dialog.progressBar?.isIndeterminate = builder.indeterminateProgress && builder.indeterminateIsHorizontalProgress
                dialog.progressBar?.progress = 0
                dialog.progressBar?.max = builder.progressMax
                dialog.progressLabel = dialog.mView?.findViewById(R.id.md_label)
                if (dialog.progressLabel != null) {
                    dialog.progressLabel!!.setTextColor(builder.contentColor)
                    dialog.setTypeface(dialog.progressLabel!!, builder.mediumFont)
                    dialog.progressLabel!!.text = builder.progressPercentFormat.format(0)
                }
                dialog.progressMinMax = dialog.mView?.findViewById(R.id.md_minMax)
                if (dialog.progressMinMax != null) {
                    dialog.progressMinMax!!.setTextColor(builder.contentColor)
                    dialog.setTypeface(dialog.progressMinMax!!, builder.regularFont)

                    if (builder.showMinMax) {
                        dialog.progressMinMax!!.visibility = View.VISIBLE
                        dialog.progressMinMax!!.text = String.format(builder.progressNumberFormat, 0, builder.progressMax)
                        val lp = dialog.progressBar!!.layoutParams as ViewGroup.MarginLayoutParams
                        lp.leftMargin = 0
                        lp.rightMargin = 0
                    } else {
                        dialog.progressMinMax!!.visibility = View.GONE
                    }
                } else {
                    builder.showMinMax = false
                }
            }
        }

        if (dialog.progressBar != null) {
            fixCanvasScalingWhenHardwareAccelerated(dialog.progressBar!!)
        }
    }

    /**
     * 初始化输入框
     * */
    private fun initInputDialog(dialog: MaterialDialog) {
        val builder = dialog.builder
        dialog.inputEditText = dialog.mView?.findViewById(android.R.id.input)
        if (dialog.inputEditText == null) {
            return
        }
        dialog.setTypeface(dialog.inputEditText!!, builder.regularFont)
        if (builder.inputPrefill != null) {
            dialog.inputEditText!!.setText(builder.inputPrefill)
        }
        dialog.setInternalInputCallback()
        dialog.inputEditText!!.hint = builder.inputHint
        dialog.inputEditText!!.setSingleLine()
        dialog.inputEditText!!.setTextColor(builder.contentColor)
        dialog.inputEditText!!.setHintTextColor(DialogUtil.adjustAlpha(builder.contentColor, 0.3f))
        MDTintHelper.setTint(dialog.inputEditText!!, dialog.builder.widgetColor)

        if (builder.inputType != -1) {
            dialog.inputEditText!!.inputType = builder.inputType
            if (builder.inputType != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD && builder.inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                // If the flags contain TYPE_TEXT_VARIATION_PASSWORD, apply the password transformation
                // method automatically
                dialog.inputEditText!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        dialog.inputMinMax = dialog.mView?.findViewById(R.id.md_minMax)
        if (builder.inputMinLength > 0 || builder.inputMaxLength > -1) {
            dialog.invalidateInputMinMaxIndicator(
                    dialog.inputEditText!!.text.toString().length, !builder.inputAllowEmpty)
        } else {
            dialog.inputMinMax?.visibility = View.GONE
            dialog.inputMinMax = null
        }

        if (builder.inputFilters != null) {
            dialog.inputEditText!!.filters = builder.inputFilters
        }
    }
}