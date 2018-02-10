package com.qcloud.qclib.widget.customview.verticalviewpager

import android.os.Parcel
import android.os.Parcelable
import android.support.v4.os.ParcelableCompat
import android.support.v4.os.ParcelableCompatCreatorCallbacks
import android.view.View

/**
 * 类说明：ViewPager的持续状态
 * Author: Kuzan
 * Date: 2018/1/20 16:57.
 */
class SavedState : View.BaseSavedState {
    var position: Int = 0
    var adapterState: Parcelable? = null
    var mLoader: ClassLoader? = null

    constructor(superState: Parcelable) : super(superState)

    constructor(parcel: Parcel, loader: ClassLoader?) : super(parcel) {
        var newLoader = loader
        if (newLoader == null) {
            newLoader = javaClass.classLoader
        }
        position = parcel.readInt()
        adapterState = parcel.readParcelable(newLoader)
        this.mLoader = newLoader
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(position)
        out.writeParcelable(adapterState, flags)
    }

    override fun toString(): String {
        return ("FragmentPager.SavedState{"
                + Integer.toHexString(System.identityHashCode(this))
                + " position=" + position + "}")
    }

    companion object {

        val CREATOR: Parcelable.Creator<SavedState> = ParcelableCompat.newCreator(object : ParcelableCompatCreatorCallbacks<SavedState> {
            override fun createFromParcel(parcel: Parcel, loader: ClassLoader): SavedState {
                return SavedState(parcel, loader)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        })
    }
}