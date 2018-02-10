package com.qcloud.qclib.adapter.abslistview

import android.annotation.SuppressLint
import android.content.Context
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.*

/**
 * 类说明：通用的ViewHolder
 * Author: Kuzan
 * Date: 2018/1/17 11:45.
 */
class ViewHolder(var mContext: Context,
                 parent: ViewGroup,
                 var layoutId: Int,
                 var position: Int) {

    private val mViews: SparseArray<View> = SparseArray()
    val mConvertView: View = LayoutInflater.from(mContext).inflate(layoutId, parent, false)

    init {
        mConvertView.tag = this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: View> getView(@IdRes viewId: Int): T {
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
    fun setBackgroundColor(@IdRes viewId: Int, color: Int): ViewHolder {
        val view = getView<View>(viewId)
        view.setBackgroundColor(color)
        return this
    }

    fun setBackgroundRes(@IdRes viewId: Int, @DrawableRes backgroundRes: Int): ViewHolder {
        val view = getView<View>(viewId)
        view.setBackgroundResource(backgroundRes)
        return this
    }

    /**
     * ImageView有关
     * */
    fun setImageResource(@IdRes viewId: Int, @DrawableRes resId: Int): ViewHolder {
        val image = getView<ImageView>(viewId)
        image.setImageResource(resId)
        return this
    }

    fun setImageBitmap(@IdRes viewId: Int, @NonNull bitmap: Bitmap): ViewHolder {
        val image = getView<ImageView>(viewId)
        image.setImageBitmap(bitmap)
        return this
    }

    fun setImageDrawable(@IdRes viewId: Int, @NonNull drawable: Drawable): ViewHolder {
        val image = getView<ImageView>(viewId)
        image.setImageDrawable(drawable)
        return this
    }

    /**
     * TextView有关
     * */
    fun setText(@IdRes viewId: Int, text: String): ViewHolder {
        val tv = getView<TextView>(viewId)
        tv.text = text
        return this
    }

    fun setTextColor(@IdRes viewId: Int, color: Int): ViewHolder {
        val tv = getView<TextView>(viewId)
        tv.setTextColor(color)
        return this
    }

    fun setTextSize(@IdRes viewId: Int, size: Float): ViewHolder {
        val tv = getView<TextView>(viewId)
        tv.textSize = size
        return this
    }

    fun setTypeface(@NonNull typeface: Typeface, @IdRes vararg viewIds: Int): ViewHolder {
        for (viewId in viewIds) {
            val tv = getView<TextView>(viewId)
            tv.typeface = typeface
            tv.paintFlags = tv.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        }
        return this
    }

    fun linkify(@IdRes viewId: Int): ViewHolder {
        val tv = getView<TextView>(viewId)
        Linkify.addLinks(tv, Linkify.ALL)
        return this
    }

    /**
     * ProgressBar 有关
     * */
    fun setProgress(@IdRes viewId: Int, progress: Int): ViewHolder {
        val bar = getView<ProgressBar>(viewId)
        bar.progress = progress
        return this
    }

    fun setProgress(@IdRes viewId: Int, progress: Int, max: Int): ViewHolder {
        val bar = getView<ProgressBar>(viewId)
        bar.max = max
        bar.progress = progress
        return this
    }

    fun setProgressMax(@IdRes viewId: Int, max: Int): ViewHolder {
        val bar = getView<ProgressBar>(viewId)
        bar.max = max
        return this
    }

    /**
     * RatingBar 有关
     * */
    fun setRating(@IdRes viewId: Int, rating: Float): ViewHolder {
        val bar = getView<RatingBar>(viewId)
        bar.rating = rating
        return this
    }

    fun setRating(@IdRes viewId: Int, rating: Float, max: Int): ViewHolder {
        val bar = getView<RatingBar>(viewId)
        bar.max = max
        bar.rating = rating
        return this
    }

    fun setRatingMax(@IdRes viewId: Int, max: Int): ViewHolder {
        val bar = getView<RatingBar>(viewId)
        bar.max = max
        return this
    }

    /**
     * RadioButton 有关
     * */
    fun setRadioButton(@IdRes viewId: Int, isChecked: Boolean): ViewHolder {
        val btn = getView<RadioButton>(viewId)
        btn.isChecked = isChecked
        return this
    }

    /**
     * RadioGroup 有关
     * */
    fun setRadioGroup(@IdRes viewId: Int, @IdRes selectId: Int): ViewHolder {
        val group = getView<RadioGroup>(viewId)
        group.check(selectId)
        return this
    }

    /**
     * CheckBox 有关
     * */
    fun setCheckBox(@IdRes viewId: Int, isChecked: Boolean): ViewHolder {
        val box = getView<CheckBox>(viewId)
        box.isChecked = isChecked
        return this
    }

    /**
     * 设置标签
     * */
    fun setTag(@IdRes viewId: Int, tag: Any): ViewHolder {
        val view = getView<View>(viewId)
        view.tag = tag
        return this
    }

    fun setTag(@IdRes viewId: Int, key: Int, tag: Any): ViewHolder {
        val view = getView<View>(viewId)
        view.setTag(key, tag)
        return this
    }

    /**
     * 设置是否可见
     * */
    fun setVisible(@IdRes viewId: Int, isVisible: Boolean): ViewHolder {
        val view = getView<View>(viewId)
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
        return this
    }

    fun setVisible(@IdRes viewId: Int, visible: Int): ViewHolder {
        val view = getView<View>(viewId)
        view.visibility = visible
        return this
    }

    /**
     * 设置透明度
     * */
    @SuppressLint("ObsoleteSdkInt")
    fun setAlpha(@IdRes viewId: Int, value: Float): ViewHolder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView<View>(viewId).alpha = value
        } else {
            val alpha = AlphaAnimation(value, value)
            alpha.duration = 0
            alpha.fillAfter = true
            getView<View>(viewId).startAnimation(alpha)
        }
        return this
    }

    /**
     * ******************关于事件的 *********************
     * */
    fun setOnClickListener(viewId: Int, listener: View.OnClickListener): ViewHolder {
        val view = getView<View>(viewId)
        view.setOnClickListener(listener)
        return this
    }

    fun setOnTouchListener(viewId: Int, listener: View.OnTouchListener): ViewHolder {
        val view = getView<View>(viewId)
        view.setOnTouchListener(listener)
        return this
    }

    fun setOnLongClickListener(viewId: Int, listener: View.OnLongClickListener): ViewHolder {
        val view = getView<View>(viewId)
        view.setOnLongClickListener(listener)
        return this
    }

    companion object {
        fun get(@NonNull context: Context, convertView: View?, parent: ViewGroup, layoutId: Int, position: Int): ViewHolder {
            return if (convertView == null) {
                ViewHolder(context, parent, layoutId, position)
            } else {
                val holder: ViewHolder = convertView.tag as ViewHolder
                holder.position = position
                holder
            }
        }
    }
}