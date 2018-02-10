package com.qcloud.qclib.imageselect.utils

import android.app.Activity

import com.qcloud.qclib.imageselect.ImageSelectActivity
import com.qcloud.qclib.imageselect.ProcessImageActivity
import com.qcloud.qclib.imageselect.widget.CutImageView

/**
 * 类说明：文件选择工具类
 * Author: Kuzan
 * Date: 2017/5/25 14:58.
 */
object ImageSelectUtil {

    /**
     * 图片选择的结果
     */
    val SELECT_RESULT = "select_result"

    val RECT_TYPE = CutImageView.RECT_TYPE
    val CIRCLE_TYPE = CutImageView.CIRCLE_TYPE

    /**
     * 打开相册，选择图片,可多选，不可剪切。
     *
     * @param activity
     * @param requestCode
     * @param maxSelectCount 选择图片的最大数量
     */
    fun openPhoto(activity: Activity, requestCode: Int, maxSelectCount: Int) {
        ImageSelectActivity.openActivity(activity, requestCode, maxSelectCount)
    }

    /**
     * 打开相册，选择图片，单选，可剪切。
     *
     * @param activity
     * @param requestCode
     * @param isCut       是否要对图片进行剪切
     */
    fun openPhoto(activity: Activity, requestCode: Int, isCut: Boolean) {
        if (isCut) {
            ProcessImageActivity.openThisActivity(activity, requestCode, ProcessImageActivity.OPEN_PHOTO, isCut, RECT_TYPE, -1, -1)
        } else {
            ImageSelectActivity.openActivity(activity, requestCode)
        }
    }

    /**
     * 打开相册，选择图片，单选，可剪切。
     *
     * @param activity
     * @param requestCode
     * @param isCut       是否要对图片进行剪切
     * @param cutType     剪切类型，RECT_TYPE（正方形）、CIRCLE_TYPE（圆形）
     */
    fun openPhoto(activity: Activity, requestCode: Int, isCut: Boolean, cutType: Int) {
        if (isCut) {
            ProcessImageActivity.openThisActivity(activity, requestCode, ProcessImageActivity.OPEN_PHOTO, isCut, cutType, -1, -1)
        } else {
            ImageSelectActivity.openActivity(activity, requestCode)
        }
    }

    /**
     * 打开相册，选择图片，单选，可剪切。
     *
     * @param activity
     * @param requestCode
     * @param isCut       是否要对图片进行剪切
     * @param cutType     剪切类型，RECT_TYPE（正方形）、CIRCLE_TYPE（圆形）
     * @param cutSize     剪切大小，必须大于0，默认屏幕宽高最小值得1/2
     */
    fun openPhoto(activity: Activity, requestCode: Int, isCut: Boolean, cutType: Int, cutSize: Int) {
        if (isCut) {
            ProcessImageActivity.openThisActivity(activity, requestCode, ProcessImageActivity.OPEN_PHOTO, isCut, cutType, cutSize, cutSize)
        } else {
            ImageSelectActivity.openActivity(activity, requestCode)
        }
    }

    /**
     * 打开相册，选择图片，单选，矩阵剪切。
     *
     * @param activity
     * @param requestCode
     * @param width       剪切大小，必须大于0，默认屏幕宽高最小值得1/2
     * @param height      剪切大小，必须大于0，默认屏幕宽高最小值得1/2
     */
    fun openPhoto(activity: Activity, requestCode: Int, width: Int, height: Int) {
        ProcessImageActivity.openThisActivity(activity, requestCode, ProcessImageActivity.OPEN_PHOTO, true, RECT_TYPE, width, height)
    }

    /**
     * 调用相机拍照。
     *
     * @param activity
     * @param requestCode
     * @param isCut       是否要对图片进行剪切
     */
    fun startCamera(activity: Activity, requestCode: Int, isCut: Boolean) {
        ProcessImageActivity.openThisActivity(activity, requestCode, ProcessImageActivity.START_CAMERA, isCut, RECT_TYPE, -1, -1)
    }

    /**
     * 调用相机拍照。
     *
     * @param activity
     * @param requestCode
     * @param isCut       是否要对图片进行剪切
     * @param cutType     剪切类型，RECT_TYPE（正方形）、CIRCLE_TYPE（圆形）
     */
    fun startCamera(activity: Activity, requestCode: Int, isCut: Boolean, cutType: Int) {
        ProcessImageActivity.openThisActivity(activity, requestCode, ProcessImageActivity.START_CAMERA, isCut, cutType, -1, -1)
    }

    /**
     * 调用相机拍照。
     *
     * @param activity
     * @param requestCode
     * @param isCut       是否要对图片进行剪切
     * @param cutType     剪切类型，RECT_TYPE（正方形）、CIRCLE_TYPE（圆形）
     * @param cutSize     剪切大小，必须大于0，默认屏幕宽高最小值得1/2
     */
    fun startCamera(activity: Activity, requestCode: Int, isCut: Boolean, cutType: Int, cutSize: Int) {
        ProcessImageActivity.openThisActivity(activity, requestCode, ProcessImageActivity.START_CAMERA, isCut, cutType, cutSize, cutSize)
    }

    /**
     * 调用相机拍照，矩阵剪切。
     *
     * @param activity
     * @param requestCode
     * @param width       剪切大小，必须大于0，默认屏幕宽高最小值得1/2
     * @param height      剪切大小，必须大于0，默认屏幕宽高最小值得1/2；
     */
    fun startCamera(activity: Activity, requestCode: Int, width: Int, height: Int) {
        ProcessImageActivity.openThisActivity(activity, requestCode, ProcessImageActivity.START_CAMERA, true, RECT_TYPE, width, height)
    }
}
