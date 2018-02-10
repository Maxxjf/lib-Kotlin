package com.qcloud.qclib.materialdesign.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.support.annotation.NonNull
import android.view.View
import android.view.ViewGroup
import com.qcloud.qclib.materialdesign.widget.MDRootLayout

/**
 * 类说明：基于Material Design 设计的Dialog
 * Author: Kuzan
 * Date: 2018/2/9 11:08.
 */
open class BaseDialog (protected val mContext: Context, theme: Int): Dialog(mContext, theme), DialogInterface.OnShowListener {

    open var mView: MDRootLayout? = null

    private var onShowListener: DialogInterface.OnShowListener? = null

    override fun <T : View?> findViewById(id: Int): T {
        return mView!!.findViewById<T>(id)
    }

    override fun setOnShowListener(@NonNull listener: DialogInterface.OnShowListener) {
        onShowListener = listener
    }

    fun setOnShowListenerInternal() {
        super.setOnShowListener(this)
    }

    fun setViewInternal(view: View) {
        super.setContentView(view)
    }

    override fun onShow(p0: DialogInterface?) {
        onShowListener?.onShow(p0)
    }

    @Deprecated("", ReplaceWith("throw IllegalAccessError(\"setContentView() is not supported in MaterialDialog. Specify a custom view in the Builder instead.\")"))
    @Throws(IllegalAccessError::class)
    override fun setContentView(layoutResID: Int) {
        throw IllegalAccessError(
                "setContentView() is not supported in MaterialDialog. Specify a custom view in the Builder instead.")
    }

    @Deprecated("", ReplaceWith("throw IllegalAccessError(\"setContentView() is not supported in MaterialDialog. Specify a custom view in the Builder instead.\")"))
    @Throws(IllegalAccessError::class)
    override fun setContentView(view: View) {
        throw IllegalAccessError(
                "setContentView() is not supported in MaterialDialog. Specify a custom view in the Builder instead.")
    }

    @Deprecated("", ReplaceWith("throw IllegalAccessError(\"setContentView() is not supported in MaterialDialog. Specify a custom view in the Builder instead.\")"))
    @Throws(IllegalAccessError::class)
    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        throw IllegalAccessError(
                "setContentView() is not supported in MaterialDialog. Specify a custom view in the Builder instead.")
    }
}