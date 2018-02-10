package com.qcloud.qclib.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.FloatRange

/**
 * 类说明：颜色工具类
 * Author: Kuzan
 * Date: 2017/12/4 9:31.
 */
object ColorUtil {
    /**
     * 获取颜色的RGB值
     *
     * @param color 颜色值 Color.BLACK
     * */
    fun toHexFromColor(color: Int): String {
        val sb = StringBuilder()

        var r: String = Integer.toHexString(Color.red(color))
        var g: String = Integer.toHexString(Color.green(color))
        var b: String = Integer.toHexString(Color.blue(color))

        r = if (r.length == 1) "0" + r else r
        g = if (g.length == 1) "0" + g else g
        b = if (b.length == 1) "0" + b else b

        r = r.toUpperCase()
        g = g.toUpperCase()
        b = b.toUpperCase()

        sb.append("0x")
        sb.append(r)
        sb.append(g)
        sb.append(b)
        // 0x000000
        return sb.toString()
    }

    /**
     * 获取两个颜色的过渡色
     *
     * @param color1 开始的颜色
     * @param color2 结束的颜色
     * @param count  开始的颜色到结束的颜色的过渡色分为几份
     * @param index 获取第几个过渡色，总共分为count个过渡色，i表示取其中的第i个过渡色
     *
     * @return 取得的过渡色
     * */
    fun getColorGradient(color1: Int, color2: Int, count: Int, index: Int): Int {
        // 开始的颜色的a，r，g，b值
        val color1Values = toColorValue(color1)
        // 结束的颜色的a，r，g，b值
        val color2Values = toColorValue(color2)

        val result = IntArray(4)
        for (j in color2Values.indices) {
            result[j] = (color1Values[j] + (color2Values[j] - color1Values[j]) * 1.0 / count * index).toInt()
        }
        return Color.argb(result[0], result[1], result[2], result[3])
    }

    /**
     * 获取颜色的a，r，g，b值
     *
     * @param color 颜色值
     * @return IntArray
     * */
    fun toColorValue(color: Int): IntArray {
        val values = IntArray(4)
        values[0] = Color.alpha(color)
        values[1] = Color.red(color)
        values[2] = Color.green(color)
        values[3] = Color.blue(color)

        return values
    }

    /**
     * 调整颜色亮度
     *
     * @param color 颜色值
     * @param value 亮度值
     * */
    fun toDarkenColor(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) value: Float = 0.8F): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        //HSV指Hue、Saturation、Value，即色调、饱和度和亮度，此处表示修改亮度
        hsv[2] *= value
        return Color.HSVToColor(hsv)
    }

    /**
     * 转换为6位十六进制颜色代码，不含“#”
     *
     * @param color 颜色值
     * @param isAlpha 是否包含透明
     * */
    fun toColorString(@ColorInt color: Int, isAlpha: Boolean = false): String {
        var alpha = Integer.toHexString(Color.alpha(color))
        var red = Integer.toHexString(Color.red(color))
        var green = Integer.toHexString(Color.green(color))
        var blue = Integer.toHexString(Color.blue(color))

        if (alpha.length == 1) {
            alpha = "0" + alpha
        }
        if (red.length == 1) {
            red = "0" + red
        }
        if (green.length == 1) {
            green = "0" + green
        }
        if (blue.length == 1) {
            blue = "0" + blue
        }

        return if (isAlpha) {
            alpha + red + green + blue
        } else {
            red + green + blue
        }
    }

    /**
     * 将16进制color转为int类型
     *
     * @param colorString 16进制颜色值
     * */
    fun parseColor(colorString: String): Int {
        val colorStr = when {
            colorString.startsWith("#") -> colorString
            colorString.startsWith("0x") -> StringUtil.replace(colorString, "0x", "#")
            colorString.startsWith("0X") -> StringUtil.replace(colorString, "0X", "#")
            else -> "#" + colorString
        }
        return Color.parseColor(colorStr)
    }

    /**
     * 对TextView、Button等设置不同状态时其文字颜色。
     * 参见：http://blog.csdn.net/sodino/article/details/6797821
     * Modified by liyujiang at 2015.08.13
     */
    fun toColorStateList(@ColorRes normalColor: Int, @ColorRes pressedColor: Int, @ColorRes focusedColor: Int, @ColorRes unableColor: Int): ColorStateList {
        val colors = intArrayOf(pressedColor, focusedColor, normalColor, focusedColor, unableColor, normalColor)
        val states = arrayOfNulls<IntArray>(6)
        states[0] = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
        states[1] = intArrayOf(android.R.attr.state_enabled, android.R.attr.state_focused)
        states[2] = intArrayOf(android.R.attr.state_enabled)
        states[3] = intArrayOf(android.R.attr.state_focused)
        states[4] = intArrayOf(android.R.attr.state_window_focused)
        states[5] = intArrayOf()
        return ColorStateList(states, colors)
    }

    /**
     * 对TextView、Button等设置不同状态时其文字颜色。
     * 参见：http://blog.csdn.net/sodino/article/details/6797821
     * Modified by liyujiang at 2015.08.13
     */
    fun toColorStateList(@ColorRes normalColor: Int, @ColorRes pressedColor: Int): ColorStateList = toColorStateList(normalColor, pressedColor, pressedColor, normalColor)

    /**
     * 随机颜色
     * @return: Int
     */
    fun random() : Int {
        return if (Build.VERSION.SDK_INT >= 26)
            Color.argb(MathUtil.randomNum(1, 0).toFloat(),
                    MathUtil.randomNum(255, 0).toFloat(),
                    MathUtil.randomNum(255, 0).toFloat(),
                    MathUtil.randomNum(255, 0).toFloat())
        else
            Color.rgb(MathUtil.randomNum(255, 0),
                    MathUtil.randomNum(255, 0),
                    MathUtil.randomNum(255, 0))
    }

    /**
     * 自定义颜色随机
     * @return: Int
     */
    fun randomCustom(colors : IntArray) : Int = colors[MathUtil.randomNum(colors.size - 1, 0)]
}