package com.qcloud.qclib.widget.swipeback

import android.support.annotation.NonNull
import android.view.View

/**
 * 类说明：回调接口
 * Author: Kuzan
 * Date: 2018/1/17 15:01.
 */
abstract class SwipeCallback {
    /**
     * 拖动状态改变时调用
     *
     * @param state
     * @see #STATE_IDLE
     * @see #STATE_DRAGGING
     * @see #STATE_SETTLING
     * */
    open fun onViewDragStateChanged(state: Int) {

    }

    /**
     * 当视图的位置因拖动改变时调用
     *
     * @param changedView 位置改变的视图
     * @param left 视图左边缘的新x坐标
     * @param top 视图顶部边缘的新y坐标
     * @param dx 最后一次改变x的位置
     * @param dy 最后一次改变y的位置
     * */
    open fun onViewPositionChanged(@NonNull changedView: View, left: Int, top: Int, dx: Int, dy: Int) {

    }

    /**
     * 在拖动或结算时捕获子视图时调用。
     *
     * @param capturedChild 子视图
     * @param activePointerId 跟踪子视图的id
     * */
    open fun onViewCaptured(@NonNull capturedChild: View, activePointerId: Int) {

    }

    /**
     * 当子视图不再被主动拖动时调用
     *
     * @param releasedChild 子视图
     * @param xvel 在屏幕上X方向以每秒像素为单位的速度。
     * @param yvel 在屏幕上Y方向以每秒像素为单位的速度。
     * */
    open fun onViewReleased(@NonNull releasedChild: View, xvel: Float, yvel: Float) {

    }

    /**
     * 当父视图中的一个已订阅的边被用户触摸时调用，而且当前未捕获子视图。
     *
     * @param edgeFlags 描述当前触摸边缘的边缘标志的组合。
     * @param pointerId 指向所述边缘的指针的id
     * @see #EDGE_LEFT
     * @see #EDGE_TOP
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     * */
    open fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {

    }

    /**
     * 当给定的边缘可能被锁定时调用。
     *
     * @param edgeFlags 描述边缘锁定的边缘标志的组合。
     * */
    open fun onEdgeLock(edgeFlags: Int): Boolean {
        return false
    }

    /**
     * 当用户从父视图中的一个订阅边缘开始拖动时调用，而且当前未捕获子视图。
     *
     * @param edgeFlags 描述当前触摸边缘的边缘标志的组合。
     * @param pointerId 指向所述边缘的指针的id
     * @see #EDGE_LEFT
     * @see #EDGE_TOP
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     * */
    open fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {

    }

    /**
     * 获取子视图的位置。
     *
     * @param index 子视图的标签
     * */
    open fun getOrderedChildIndex(index: Int): Int {
        return index
    }

    /**
     * 返回拖动子视图的水平拖动范围的大小。对于不能横向移动的视图，此方法应该返回0。
     *
     * @param child 子视图
     * */
    open fun getViewHorizontalDragRange(@NonNull child: View): Int {
        return 0
    }

    /**
     * 返回拖动子视图的垂直拖动范围的大小。对于不能纵向移动的视图，此方法应该返回0。
     *
     * @param child 子视图
     * */
    open fun getViewVerticalDragRange(@NonNull child: View): Int {
        return 0
    }

    /**
     * 限制拖动的子视图沿水平轴的运动。
     *      默认不允许实现水平运动；扩展类必须重写此方法
     *
     * @param child 子视图
     * @param left 沿X轴的运动
     * @param dx 向左更改的位置
     * */
    open fun clampViewPositionHorizontal(@NonNull child: View, left: Int, dx: Int): Int {
        return 0
    }

    /**
     * 限制拖动的子视图沿垂直轴的运动。
     *      默认不允许实现垂直运动；扩展类必须重写此方法
     *
     * @param child 子视图
     * @param top 沿Y轴的运动
     * @param dy 向下更改的位置
     * */
    open fun clampViewPositionVertical(@NonNull child: View, top: Int, dy: Int): Int {
        return 0
    }

    /**
     * 当用户操作表明他们想捕捉所指示的 pointerId 子视图调用。
     *      如果允许用户用指定的指针拖动给指定的子视图，则回调应该返回true。
     *
     * @param child 子视图
     * @param pointerId
     * */
    abstract fun tryCaptureView(@NonNull child: View, pointerId: Int): Boolean
}