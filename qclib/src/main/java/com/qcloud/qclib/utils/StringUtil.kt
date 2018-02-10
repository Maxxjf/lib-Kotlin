package com.qcloud.qclib.utils

import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList

/**
 * 类说明：字符串工具类
 * Author: Kuzan
 * Date: 2017/11/22 16:01.
 */
object StringUtil {
    /**
     * 判断字符串是否为空，为空的标准是 str==null或str.length()==0
     *
     * @param str
     * @return true空字符串
     * */
    fun isEmpty(str: String?): Boolean = str.isNullOrEmpty()

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return true非空字符串
     * */
    fun isNotEmpty(str: String?): Boolean = !isEmpty(str)

    /**
     * 判断某字符串是否为空或长度为0或由空白符(whitespace) 构成
     *      对于制表符、换行符、换页符和回车符（\t \n \f \r）均识为空白符
     * @param str
     * @return true空字符串
     * */
    fun isBlank(str: String?): Boolean = str.isNullOrBlank()

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return true非空字符串
     * */
    fun isNotBlank(str: String?): Boolean = !isBlank(str)

    /**
     * 判断对象是否为空
     *
     * @param obj
     * @return true 空对象
     * */
    fun isBlankObject(obj: Any?): Boolean = obj == null || obj.toString().isBlank()

    /**
     * 检查两个字符串是否一致 .
     *
     * @param str1
     * @param str2
     * @return true两字符串一致
     * */
    fun isEquals(str1: String?, str2: String?): Boolean {
        return if (isEmpty(str1) || isEmpty(str2)) {
            false
        } else {
            str1.equals(str2)
        }
    }

    /**
     * 去除字符串中的制表符、换行符、换页符和回车符（\t \n \r）
     *
     * @param str
     * @return 去掉制表符、换行符、换页符和回车符后的字符串
     * */
    fun replaceBlank(str: String?): String {
        var dest = ""
        if (str != null) {
            val p: Pattern = Pattern.compile("\\s*|\t|\r|\n")
            val m: Matcher = p.matcher(str)
            dest = m.replaceAll("")
        }
        return dest
    }

    /**
     * 此方法将给出的字符串source使用splitStr划分为单词数组.
     *
     * @param source    需要进行划分的原字符串
     * @param splitStr  单词的分隔字符串，默认为','，可不传
     * @return 划分以后的数组，如果source为null的时候返回以source为唯一元素的数组
     */
    fun split(source: String?, splitStr: String = ","): Array<String?> {
        if (isEmpty(source)) {
            val wordLists = arrayOfNulls<String>(1)
            wordLists[0] = ""
            return wordLists
        }

        val str = StringTokenizer(source, splitStr)
        val total = str.countTokens()
        val wordLists = arrayOfNulls<String>(total)
        for (i in 0 until total) {
            wordLists[i] = str.nextToken()
        }
        return wordLists
    }

    /**
     * 此方法将给出的字符串source使用splitStr划分为单词集合.
     *
     * @param source
     * @param splitStr ，默认为','，可不传
     * @return
     * */
    fun split2List(source: String?, splitStr: String = ","): MutableList<String> {
        val wordList: MutableList<String> = ArrayList()
        if (isEmpty(source)) {
            wordList.add("")
        } else {
            val str = StringTokenizer(source, splitStr)
            val total = str.countTokens()
            for (i in 0 until total) {
                wordList.add(str.nextToken())
            }
        }
        return wordList
    }

    /**
     * 将set的所有记录并成一个以 splitStr 分隔的字符串.
     *
     * @param set
     * @param splitStr ，默认为','，可不传
     * @return
     * */
    fun combineSet(set: Set<*>?, splitStr: String = ","): String {
        if (set == null || set.isEmpty()) {
            return ""
        }
        val sb = StringBuffer()
        val it = set.iterator()
        while (it.hasNext()) {
            sb.append(it.next())
            sb.append(splitStr)
        }
        if (sb.length >= splitStr.length) {
            sb.delete(sb.length - splitStr.length, sb.length)
        }
        return sb.toString()
    }

    /**
     * 将List的所有记录并成一个以 splitStr 分隔的字符串.
     *
     * @param list
     * @param splitStr ，默认为','，可不传
     * @return
     * */
    fun combineList(list: List<*>?, splitStr: String = ","): String {
        if (list == null || list.isEmpty()) {
            return ""
        }
        val sb = StringBuffer()
        val it = list.iterator()
        while (it.hasNext()) {
            sb.append(it.next())
            sb.append(splitStr)
        }
        if (sb.length >= splitStr.length) {
            sb.delete(sb.length - splitStr.length, sb.length)
        }
        return sb.toString()
    }

    /**
     * 将数组合并成一个以 splitStr 号分隔的字符串.
     *
     * @param array
     * @param splitStr，默认为','，可不传
     * @return
     * */
    fun combineArray(array: Array<*>?, splitStr: String = ","): String {
        if (array == null || array.isEmpty()) {
            return ""
        }
        val sb = StringBuffer()
        val it = array.iterator()
        while (it.hasNext()) {
            sb.append(it.next())
            sb.append(splitStr)
        }
        if (sb.length >= splitStr.length) {
            sb.delete(sb.length - splitStr.length, sb.length)
        }
        return sb.toString()
    }

    /**
     * 字符串数组中是否包含指定的字符串.
     *
     * @param array
     * @param str
     * @param isIgnoreCase 是否大小写敏感，默认为true(不区分大小写)，可不传
     *
     * @return 包含时返回true，否则返回false
     * */
    fun contains(array: Array<String>?, str: String?, isIgnoreCase: Boolean = true): Boolean {
        if (array == null || array.isEmpty() || isEmpty(str)) {
            return false
        }
        return array.any { it.equals(str, isIgnoreCase) }
    }

    /**
     * 字符串集合中是否包含指定的字符串.
     *
     * @param list
     * @param str
     * @param isIgnoreCase 是否大小写敏感，默认为true(不区分大小写)，可不传
     *
     * @return 包含时返回true，否则返回false
     * */
    fun contains(list: List<String>?, str: String?, isIgnoreCase: Boolean = true): Boolean {
        if (list == null || list.isEmpty() || isEmpty(str)) {
            return false
        }
        return list.any { it.equals(str, isIgnoreCase) }
    }

    /**
     * 整型集合中是否包含指定的数.
     *
     * @param list
     * @param value
     *
     * @return 包含时返回true，否则返回false
     * */
    fun contains(list: List<Int>?, value: Int): Boolean {
        if (list == null || list.isEmpty()) {
            return false
        }
        return list.any { it == value }
    }

    /**
     * 不区分大小写判定字符串数组中是否包含指定的字符串.
     *
     * @param array
     * @param str
     * @return
     * */
    fun containsIgnoreCase(array: Array<String>?, str: String?) = contains(array, str, false)

    /**
     * 字符串中是否包含另一个指定的字符串.
     *
     * @param source
     * @param str
     * @return
     * */
    fun contains(source: String?, str: String?): Boolean {
        return if (isBlank(source) || isBlank(str)) {
            false
        } else {
            source?.indexOf(str!!) != -1
        }
    }

    /**
     * 字符串替换.
     *
     * @param source  搜索字符串
     * @param beStr   要查找字符串
     * @param toStr   要替换字符串
     * @return
     */
    fun replace(source: String, beStr: String, toStr: String): String? {
        var i = source.indexOf(beStr, 0)
        if (i >= 0) {
            val ac = source.toCharArray()
            val ac1 = toStr.toCharArray()
            val j = beStr.length
            val sb = StringBuffer(ac.size)
            sb.append(ac, 0, i).append(ac1)
            i += j
            var k: Int = i
            i = source.indexOf(beStr, i)
            while (i > 0) {
                sb.append(ac, k, i - k).append(ac1)
                i += j
                k = i
                i = source.indexOf(beStr, i)
            }

            sb.append(ac, k, ac.size - k)
            return sb.toString()
        } else {
            return source
        }
    }

    /**
     * 取指定字符串的指定长度子字串.
     *
     * @param source
     * @param start 开始位置
     * @param end   结束位置
     * @return
     * */
    fun subStr(source: String?, start: Int, end: Int): String {
        var result = ""
        if (isNotEmpty(source)) {
            if (source!!.length >= end) {
                try {
                    result = source.substring(start, end)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                result = source
            }
        }
        return result
    }

    /**
     * 取指定字符串的指定长度子字串.
     *
     * @param source
     * @param len
     * @return
     * */
    fun subStr(source: String?, len: Int): String = subStr(source, 0, len)

    /**
     * 获取两字符之间的字符串.
     *
     * @param source    原字符串
     * @param startStr  要截字符串的开始字符串
     * @param endStr    要截字符串的结束字符串
     * @return String
     */
    fun subMiddleStr(source: String, startStr: String, endStr: String): String {
        val start = source.indexOf(startStr)
        val end = source.indexOf(endStr)
        return if (isEquals(startStr, endStr) || start == -1 || end == -1) {
            ""
        } else {
            source.substring(start + startStr.length, end)
        }
    }

    /**
     * 判断对象是否为纯数字
     *
     * @param numberStr
     * @return
     * */
    fun isNumberStr(numberStr: Any?): Boolean {
        if (isBlankObject(numberStr)) {
            return false
        }
        val pattern = Pattern.compile("[0-9]*")
        val isNum = pattern.matcher(numberStr.toString())
        return isNum.matches()
    }

    /**
     * 判断字符串是否为纯中文
     *
     * @param chineseStr
     * @return true 纯中文 false 有一个不是中文
     * */
    fun isChineseStr(chineseStr: String?): Boolean {
        if (isBlank(chineseStr)) {
            return false
        }
        for (c in chineseStr!!) {
            if (!isChinese(c)) {
                return false
            }
        }

        return true
    }

    /**
     * 判断字符串是否包含中文
     *
     * @param chineseStr
     * @return true 是不包含中文 false包含中文
     * */
    fun isNotHasChinease(chineseStr: String?): Boolean {
        if (isBlank(chineseStr)) {
            return false
        }
        for (c in chineseStr!!) {
            if (isChinese(c)) {
                return false
            }
        }
        return true
    }

    /**
     * 判断字符是否为中文
     *
     * @param c
     * @return true 中文字符
     * */
    fun isChinese(c: Char): Boolean {
        val ub = Character.UnicodeBlock.of(c)
        return (ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)
    }
}