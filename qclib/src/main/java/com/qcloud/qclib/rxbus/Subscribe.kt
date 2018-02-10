package com.qcloud.qclib.rxbus

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 类说明：
 * Author: Kuzan
 * Date: 2017/8/12 9:46.
 */
@Suppress("DEPRECATED_JAVA_ANNOTATION")
@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Subscribe
