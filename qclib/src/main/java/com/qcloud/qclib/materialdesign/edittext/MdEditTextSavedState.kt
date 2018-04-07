package com.qcloud.qclib.materialdesign.edittext

import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * 类说明：保存EditText属性
 * Author: Kuzan
 * Date: 2018/4/7 11:34.
 */
class MdEditTextSavedState: View.BaseSavedState {
    var inputTextSize: Float? = null
    var inputColor: Int? = null
    var inputIconId: Int? = null
    var cleanIconId: Int? = null
    var underlineColor: Int? = null
    var cursorColor: Int? = null
    var hintText: String? = null
    var hintScale: Float? = null
    var hintColor: Int? = null
    var hintScaleColor: Int? = null
    var errorSize: Float? = null
    var errorColor: Int? = null
    var errorShow: Boolean = false
    var wordCountColor: Int? = null
    var maxLength: Int? = null
    var wordCountEnabled: Boolean = false

    constructor(superState: Parcelable) : super(superState)

    private constructor(source: Parcel) : super(source) {
        inputTextSize = source.readFloat()
        inputColor = source.readInt()
        inputIconId = source.readInt()
        cleanIconId = source.readInt()
        underlineColor = source.readInt()
        cursorColor = source.readInt()
        hintText = source.readString()
        hintScale = source.readFloat()
        hintColor = source.readInt()
        hintScaleColor = source.readInt()
        errorSize = source.readFloat()
        errorColor = source.readInt()
        errorShow = source.readByte().toInt() == 1
        wordCountColor = source.readInt()
        maxLength = source.readInt()
        wordCountEnabled = source.readByte().toInt() == 1
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeFloat(inputTextSize!!)
        out.writeInt(inputColor!!)
        out.writeInt(inputIconId!!)
        out.writeInt(cleanIconId!!)
        out.writeInt(underlineColor!!)
        out.writeInt(cursorColor!!)
        out.writeString(hintText)
        out.writeFloat(hintScale!!)
        out.writeInt(hintColor!!)
        out.writeInt(hintScaleColor!!)
        out.writeFloat(errorSize!!)
        out.writeInt(errorColor!!)
        out.writeByte((if (errorShow) 1 else 0).toByte())
        out.writeInt(wordCountColor!!)
        out.writeInt(maxLength!!)
        out.writeByte((if (wordCountEnabled) 1 else 0).toByte())
    }

    companion object {

        private val mCreator = object : Parcelable.Creator<MdEditTextSavedState> {
            override fun createFromParcel(source: Parcel): MdEditTextSavedState {
                return MdEditTextSavedState(source)
            }

            override fun newArray(size: Int): Array<MdEditTextSavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}