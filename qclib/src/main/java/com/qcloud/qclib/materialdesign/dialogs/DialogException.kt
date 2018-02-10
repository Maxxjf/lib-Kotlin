package com.qcloud.qclib.materialdesign.dialogs

import android.view.WindowManager

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/2/9 13:40.
 */
class DialogException constructor(@SuppressWarnings("SameParameterValue") message: String): WindowManager.BadTokenException(message) {

}