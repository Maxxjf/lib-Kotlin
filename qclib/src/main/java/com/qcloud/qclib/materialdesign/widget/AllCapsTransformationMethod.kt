package com.qcloud.qclib.materialdesign.widget

import android.content.Context
import android.graphics.Rect
import android.text.method.TransformationMethod
import android.view.View

/**
 * 类说明：文字字母转大写实现
 * Author: Kuzan
 * Date: 2018/2/8 17:33.
 */
class AllCapsTransformationMethod(context: Context): TransformationMethod {
    private var mLocale = context.resources.configuration.locale

    override fun onFocusChanged(p0: View?, p1: CharSequence?, p2: Boolean, p3: Int, p4: Rect?) {

    }

    override fun getTransformation(source: CharSequence?, view: View?): CharSequence? {
        return source?.toString()?.toUpperCase(mLocale)
    }
}