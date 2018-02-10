package com.qcloud.qclib.adapter.recyclerview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.NonNull
import android.text.util.Linkify
import android.util.SparseArray
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.*

/**
 * 类说明：复用ViewHolder, 绑定 RecyclerView item 的控件
 * Author: Kuzan
 * Date: 2018/1/15 16:48.
 */
class BaseViewHolder private constructor(val mConvertView: View) {

    private val mViews: SparseArray<View> = SparseArray()

    init {
        mConvertView.tag = mViews
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: View> get(@IdRes viewId: Int): T {
        var view: View? = mViews[viewId]
        if (view == null) {
            view = mConvertView.findViewById(viewId)
            mViews.put(viewId, view)
        }
        return view as T
    }

    /***************** 关于值的设置 ***************/
    /**
     * 背景有关
     * */
    fun setBackgroundColor(@IdRes viewId: Int, color: Int): BaseViewHolder {
        val view = get<View>(viewId)
        view.setBackgroundColor(color)
        return this
    }

    fun setBackgroundRes(@IdRes viewId: Int, @DrawableRes backgroundRes: Int): BaseViewHolder {
        val view = get<View>(viewId)
        view.setBackgroundResource(backgroundRes)
        return this
    }

    /**
     * ImageView有关
     * */
    fun setImageResource(@IdRes viewId: Int, @DrawableRes resId: Int): BaseViewHolder {
        val image = get<ImageView>(viewId)
        image.setImageResource(resId)
        return this
    }

    fun setImageBitmap(@IdRes viewId: Int, @NonNull bitmap: Bitmap): BaseViewHolder {
        val image = get<ImageView>(viewId)
        image.setImageBitmap(bitmap)
        return this
    }

    fun setImageDrawable(@IdRes viewId: Int, @NonNull drawable: Drawable): BaseViewHolder {
        val image = get<ImageView>(viewId)
        image.setImageDrawable(drawable)
        return this
    }

    /**
     * TextView有关
     * */
    fun setText(@IdRes viewId: Int, text: String?): BaseViewHolder {
        val tv = get<TextView>(viewId)
        tv.text = text
        return this
    }

    fun setTextColor(@IdRes viewId: Int, color: Int): BaseViewHolder {
        val tv = get<TextView>(viewId)
        tv.setTextColor(color)
        return this
    }

    fun setTextSize(@IdRes viewId: Int, size: Float): BaseViewHolder {
        val tv = get<TextView>(viewId)
        tv.textSize = size
        return this
    }

    fun setTypeface(@NonNull typeface: Typeface, @IdRes vararg viewIds: Int): BaseViewHolder {
        for (viewId in viewIds) {
            val tv = get<TextView>(viewId)
            tv.typeface = typeface
            tv.paintFlags = tv.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        }
        return this
    }

    fun linkify(@IdRes viewId: Int): BaseViewHolder {
        val tv = get<TextView>(viewId)
        Linkify.addLinks(tv, Linkify.ALL)
        return this
    }

    /**
     * ProgressBar 有关
     * */
    fun setProgress(@IdRes viewId: Int, progress: Int): BaseViewHolder {
        val bar = get<ProgressBar>(viewId)
        bar.progress = progress
        return this
    }

    fun setProgress(@IdRes viewId: Int, progress: Int, max: Int): BaseViewHolder {
        val bar = get<ProgressBar>(viewId)
        bar.max = max
        bar.progress = progress
        return this
    }

    fun setProgressMax(@IdRes viewId: Int, max: Int): BaseViewHolder {
        val bar = get<ProgressBar>(viewId)
        bar.max = max
        return this
    }

    /**
     * RatingBar 有关
     * */
    fun setRating(@IdRes viewId: Int, rating: Float): BaseViewHolder {
        val bar = get<RatingBar>(viewId)
        bar.rating = rating
        return this
    }

    fun setRating(@IdRes viewId: Int, rating: Float, max: Int): BaseViewHolder {
        val bar = get<RatingBar>(viewId)
        bar.max = max
        bar.rating = rating
        return this
    }

    fun setRatingMax(@IdRes viewId: Int, max: Int): BaseViewHolder {
        val bar = get<RatingBar>(viewId)
        bar.max = max
        return this
    }

    /**
     * RadioButton 有关
     * */
    fun setRadioButton(@IdRes viewId: Int, isChecked: Boolean): BaseViewHolder {
        val btn = get<RadioButton>(viewId)
        btn.isChecked = isChecked
        return this
    }

    /**
     * RadioGroup 有关
     * */
    fun setRadioGroup(@IdRes viewId: Int, @IdRes selectId: Int): BaseViewHolder {
        val group = get<RadioGroup>(viewId)
        group.check(selectId)
        return this
    }

    /**
     * CheckBox 有关
     * */
    fun setCheckBox(@IdRes viewId: Int, isChecked: Boolean): BaseViewHolder {
        val box = get<CheckBox>(viewId)
        box.isChecked = isChecked
        return this
    }

    /**
     * 设置标签
     * */
    fun setTag(@IdRes viewId: Int, tag: Any): BaseViewHolder {
        val view = get<View>(viewId)
        view.tag = tag
        return this
    }

    fun setTag(@IdRes viewId: Int, key: Int, tag: Any): BaseViewHolder {
        val view = get<View>(viewId)
        view.setTag(key, tag)
        return this
    }

    /**
     * 设置是否可见
     * */
    fun setVisible(@IdRes viewId: Int, isVisible: Boolean): BaseViewHolder {
        val view = get<View>(viewId)
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
        return this
    }

    fun setVisible(@IdRes viewId: Int, visible: Int): BaseViewHolder {
        val view = get<View>(viewId)
        view.visibility = visible
        return this
    }


    /**
     * 设置透明度
     * */
    @SuppressLint("ObsoleteSdkInt")
    fun setAlpha(@IdRes viewId: Int, value: Float): BaseViewHolder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            get<View>(viewId).alpha = value
        } else {
            val alpha = AlphaAnimation(value, value)
            alpha.duration = 0
            alpha.fillAfter = true
            get<View>(viewId).startAnimation(alpha)
        }
        return this
    }

    companion object {
        fun getViewHolder(@NonNull view: View): BaseViewHolder {
            var baseViewHolder: BaseViewHolder? = if (view.tag != null) {
                view.tag as BaseViewHolder
            } else {
                null
            }
            if (baseViewHolder == null) {
                baseViewHolder = BaseViewHolder(view)
                view.tag = baseViewHolder
            }
            return baseViewHolder
        }
    }
}