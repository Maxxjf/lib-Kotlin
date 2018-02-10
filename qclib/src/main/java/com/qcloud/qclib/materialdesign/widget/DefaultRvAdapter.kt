package com.qcloud.qclib.materialdesign.widget

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.qcloud.qclib.R
import com.qcloud.qclib.materialdesign.dialogs.MaterialDialog
import com.qcloud.qclib.materialdesign.enums.GravityEnum
import com.qcloud.qclib.materialdesign.enums.ListType
import com.qcloud.qclib.utils.DialogUtil

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/2/9 13:52.
 */
open class DefaultRvAdapter(
        private val dialog: MaterialDialog,
        @LayoutRes private val layout: Int) : RecyclerView.Adapter<DefaultRvAdapter.DefaultVH>() {

    private val itemGravity: GravityEnum = dialog.builder.itemsGravity
    private var callback: InternalListCallback? = null

    fun setCallback(callback: InternalListCallback) {
        this.callback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultVH {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        DialogUtil.setBackgroundCompat(view, dialog.listSelector)
        return DefaultVH(view, this)
    }

    override fun onBindViewHolder(holder: DefaultVH, index: Int) {
        val view = holder.itemView
        val disabled = DialogUtil.isIn(index, dialog.builder.disabledIndices)
        val itemTextColor = if (disabled)
            DialogUtil.adjustAlpha(dialog.builder.itemColor, 0.4f)
        else
            dialog.builder.itemColor
        holder.itemView.isEnabled = !disabled

        when (dialog.listType) {
            ListType.SINGLE -> {
                @SuppressLint("CutPasteId")
                val radio = holder.control as RadioButton
                val selected = dialog.builder.selectedIndex == index
                if (dialog.builder.choiceWidgetColor != null) {
                    MDTintHelper.setTint(radio, dialog.builder.choiceWidgetColor!!)
                } else {
                    MDTintHelper.setTint(radio, dialog.builder.widgetColor)
                }
                radio.isChecked = selected
                radio.isEnabled = !disabled
            }
            ListType.MULTI -> {
                @SuppressLint("CutPasteId")
                val checkbox = holder.control as CheckBox
                val selected = dialog.selectedIndicesList!!.contains(index)
                if (dialog.builder.choiceWidgetColor != null) {
                    MDTintHelper.setTint(checkbox, dialog.builder.choiceWidgetColor!!)
                } else {
                    MDTintHelper.setTint(checkbox, dialog.builder.widgetColor)
                }
                checkbox.isChecked = selected
                checkbox.isEnabled = !disabled
            }
        }

        holder.title.text = dialog.builder.items!![index]
        holder.title.setTextColor(itemTextColor)
        dialog.setTypeface(holder.title, dialog.builder.regularFont)

        setupGravity(view as ViewGroup)

        if (dialog.builder.itemIds != null) {
            if (index < dialog.builder.itemIds!!.size) {
                view.setId(dialog.builder.itemIds!![index])
            } else {
                view.setId(-1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (view.childCount == 2) {
                // Remove circular selector from check boxes and radio buttons on Lollipop
                if (view.getChildAt(0) is CompoundButton) {
                    view.getChildAt(0).background = null
                } else if (view.getChildAt(1) is CompoundButton) {
                    view.getChildAt(1).background = null
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (dialog.builder.items != null) dialog.builder.items!!.size else 0
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun setupGravity(view: ViewGroup) {
        val itemRoot = view as LinearLayout
        val gravityInt = itemGravity.getGravityInt()
        itemRoot.gravity = gravityInt or Gravity.CENTER_VERTICAL

        if (view.getChildCount() == 2) {
            if (itemGravity === GravityEnum.END
                    && !isRTL(view.getContext())
                    && view.getChildAt(0) is CompoundButton) {
                val first = view.getChildAt(0) as CompoundButton
                view.removeView(first)

                val second = view.getChildAt(0) as TextView
                view.removeView(second)
                second.setPadding(
                        second.paddingRight,
                        second.paddingTop,
                        second.paddingLeft,
                        second.paddingBottom)

                view.addView(second)
                view.addView(first)
            } else if (itemGravity === GravityEnum.START
                    && isRTL(view.getContext())
                    && view.getChildAt(1) is CompoundButton) {
                val first = view.getChildAt(1) as CompoundButton
                view.removeView(first)

                val second = view.getChildAt(0) as TextView
                view.removeView(second)
                second.setPadding(
                        second.paddingRight,
                        second.paddingTop,
                        second.paddingRight,
                        second.paddingBottom)

                view.addView(first)
                view.addView(second)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun isRTL(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return false
        }
        val config = context.resources.configuration
        return config.layoutDirection == View.LAYOUT_DIRECTION_RTL
    }

    interface InternalListCallback {
        fun onItemSelected(dialog: MaterialDialog, itemView: View, position: Int, text: CharSequence?, longPress: Boolean): Boolean
    }

     class DefaultVH(itemView: View, val adapter: DefaultRvAdapter) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        val control: CompoundButton = itemView.findViewById(R.id.md_control)
        val title: TextView = itemView.findViewById(R.id.md_title)

        init {
            itemView.setOnClickListener(this)
            if (adapter.dialog.builder.listLongCallback != null) {
                itemView.setOnLongClickListener(this)
            }
        }

        override fun onClick(view: View) {
            if (adapter.callback != null && adapterPosition != RecyclerView.NO_POSITION) {
                var text: CharSequence? = null
                if (adapter.dialog.builder.items != null && adapterPosition < adapter.dialog.builder.items!!.size) {
                    text = adapter.dialog.builder.items!![adapterPosition]
                }
                adapter.callback!!.onItemSelected(adapter.dialog, view, adapterPosition, text, false)
            }
        }

        override fun onLongClick(view: View): Boolean {
            if (adapter.callback != null && adapterPosition != RecyclerView.NO_POSITION) {
                var text: CharSequence? = null
                if (adapter.dialog.builder.items != null && adapterPosition < adapter.dialog.builder.items!!.size) {
                    text = adapter.dialog.builder.items!![adapterPosition]
                }
                return adapter.callback!!.onItemSelected(
                        adapter.dialog, view, adapterPosition, text, true)
            }
            return false
        }
    }
}
