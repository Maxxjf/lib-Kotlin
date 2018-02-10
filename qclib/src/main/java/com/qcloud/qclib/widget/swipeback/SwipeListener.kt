package com.qcloud.qclib.widget.swipeback

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2018/1/17 17:47.
 */
interface SwipeListener {

    /**
     * @param state
     * @see #STATE_IDLE
     * @see #STATE_DRAGGING
     * @see #STATE_SETTLING
     *
     * @param scrollPercent
     * */
    fun onScrollStateChange(state: Int, scrollPercent: Float)

    /**
     * @param edgeFlag
     * @see #EDGE_LEFT
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     * */
    fun onEdgeTouch(edgeFlag: Int)

    fun onScrollOverThreshold()
}