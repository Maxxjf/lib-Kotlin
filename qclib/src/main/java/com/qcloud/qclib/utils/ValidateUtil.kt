package com.qcloud.qclib.utils

import java.util.regex.Pattern

/**
 * 类说明：验证工具类
 * Author: Kuzan
 * Date: 2017/12/7 11:04.
 */
object ValidateUtil {
    /**
     * 验证手机号码是否合法
     *
     * @param number 需要做验证的手机号码
     * @return 返回true表示合法，false表示非法
     */
    fun isMobilePhone(number: String?): Boolean {
        /*
        * 国家号码段分配如下： 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188、147
        * 联通：130、131、132、152、155、156、185、186
        * 电信：133、153、177、180、189、（1349卫通）
        *
        * 注：手机号码必须是11位数
        */
        //"[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        val telRegex = "[1][34578]\\d{9}"

        return StringUtil.isNotBlank(number) && number!!.matches(telRegex.toRegex())
    }

    /**
     * 验证电话号码
     *          正确格式为：XXXX-XXXXXXX，XXXX-XXXXXXXX，XXX-XXXXXXX，XXX-XXXXXXXX，XXXXXXX，XXXXXXXX
     * @param telephone 电话号码
     *
     * @return 返回true表示合法，false表示非法
     * */
    fun isTelephone(telephone: String?): Boolean {
        return if (StringUtil.isNotBlank(telephone)) {
            val telRegex = "^(\\d3,4|\\d{3,4}-)?\\d{7,8}$"
            val pattern = Pattern.compile(telRegex)
            val matcher = pattern.matcher(telephone)
            return matcher.matches()
        } else {
            false
        }

    }

    /**
     * 验证邮箱是否正确
     *
     * @param email     邮箱
     *
     * @return 返回true表示合法，false表示非法
     * */
    fun isEmail(email: String?): Boolean {
        return if (StringUtil.isNotBlank(email)) {
            val emailRegex = "^([a-zA-Z0-9_\\-.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(]?)$"
            val pattern = Pattern.compile(emailRegex)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        } else {
            false
        }
    }

    /**
     * 验证是否是手机或邮箱
     *
     * @param contactWay 手机号码或邮箱地址
     *
     * @return 返回true表示合法，false表示非法
     * */
    fun isMobileOrEmail(contactWay: String?): Boolean = isMobilePhone(contactWay) || isEmail(contactWay)

    /**
     * 验证是否为6位数字的密码
     *
     * @param password 密码
     *
     * @return 返回true表示合法，false表示非法
     */
    fun isNumPasswordAndSix(password: String?): Boolean {
        return if (StringUtil.isNotBlank(password)) {
            val passwordRegex = "^\\d{6}$"
            val pattern = Pattern.compile(passwordRegex)
            val matcher = pattern.matcher(password)
            return matcher.matches()
        } else {
            false
        }
        //
    }

    /**
     * 验证用户密码(正确格式为：以字母开头，长度在6-18之间，只能包含字符、数字和下划线)
     *
     * @param password 密码
     *
     * @return 返回true表示合法，false表示非法
     * */
    fun isPassword(password: String?): Boolean {
        return if (StringUtil.isNotBlank(password)) {
            val passwordRegex = "^[a-zA-Z]\\w{5,17}$"
            val pattern = Pattern.compile(passwordRegex)
            val matcher = pattern.matcher(password)
            return matcher.matches()
        } else {
            false
        }
    }

    /**
     * 验证用户密码（数字和字母结合）
     *
     * @param password 密码
     *
     * @return 返回true表示合法，false表示非法
     * */
    fun isSimplePassword(password: String?): Boolean {
        return if (StringUtil.isNotBlank(password)) {
            // 复杂匹配
            val passwordRegex = "^(((?=.*[0-9].*)(?=.*[a-zA-Z].*))|((?=.*([^0-9a-zA-Z]).*)(?=.*[a-zA-Z].*))|((?=.*([^0-9a-zA-Z]).*)(?=.*[0-9].*))).{6,20}$"
            val pattern = Pattern.compile(passwordRegex)
            val matcher = pattern.matcher(password)
            return matcher.matches()
        } else {
            false
        }
    }

    /**
     * 验证是否为身份证号码（15位或18位数字）
     *
     * @param idCard 身份证号
     *
     * @return 返回true表示合法，false表示非法
     * */
    fun isIdCard(idCard: String?): Boolean {
        return if (StringUtil.isNotBlank(idCard)) {
            val idCardRegex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x|Y|y)$)"
            val pattern = Pattern.compile(idCardRegex)
            val matcher = pattern.matcher(idCard)
            return matcher.matches()
        } else {
            false
        }
    }

    /**
     * 设置手机为密码形式
     *
     * @param mobile 手机号
     * */
    fun setMobileToPassword(mobile: String?): String? {
        return if (StringUtil.isNotBlank(mobile) && mobile!!.length >= 7) {
            val sb = StringBuilder()
            for (i in 0 until mobile.length) {
                if (i in 3..7) {
                    sb.append('*')
                } else {
                    sb.append(mobile[i])
                }
            }
            return String(sb)
        } else {
            mobile
        }
    }

    /**
     * 设置手机为密码形式
     *
     * @param idCode 身份证号
     * */
    fun setIdCodeToPassword(idCode: String?): String? {
        return if (StringUtil.isNotBlank(idCode) && idCode!!.length >= 18) {
            val sb = StringBuilder()
            for (i in 0 until idCode.length) {
                if (i in 5..15) {
                    sb.append('*')
                } else {
                    sb.append(idCode[i])
                }
            }
            return String(sb)
        } else {
            idCode
        }
    }
}