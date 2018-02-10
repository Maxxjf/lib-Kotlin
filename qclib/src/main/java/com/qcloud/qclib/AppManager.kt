package com.qcloud.qclib

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.support.annotation.NonNull
import java.util.*

/**
 * 类说明：Activity管理
 * Author: Kuzan
 * Date: 2017/11/22 15:27.
 */
class AppManager private constructor() {

    /**
     * 添加Activity到堆栈
     * */
    fun addActivity(@NonNull activity: Activity) {
        mActivityStack.add(activity)
    }

    /**
     * 获取栈顶Activity（堆栈中最后一个压入的）
     * */
    fun getTopActivity(): Activity? = mActivityStack.lastElement()

    /**
     * 获取指定类名的Activity
     * */
    fun getActivity(cls: Class<*>): Activity? = mActivityStack.firstOrNull { it.javaClass == cls }

    /**
     * 干掉栈顶Activity（堆栈中最后一个压入的）
     * */
    fun killTopActivity() {
        killActivity(mActivityStack.lastElement())
    }

    /**
     * 结束指定的Activity
     * */
    fun killActivity(activity: Activity?) {
        if (activity != null) {
            mActivityStack.remove(activity)
            activity.finish()
        }
    }

    /**
     * 结束指定类名的Activity
     * */
    fun killActivity(cls: Class<*>) {
        mActivityStack.filter { it.javaClass == cls }
                .forEach { killActivity(it) }
    }

    /**
     * 结束所有Activity
     */
    fun killAllActivity() {
        mActivityStack.asSequence()
                .filterNotNull()
                .forEach { it.finish() }
        mActivityStack.clear()
    }


    /**
     * 退出应用程序
     */
    @SuppressLint("MissingPermission")
    fun appExit(@NonNull context: Context) {
        try {
            killAllActivity()
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            manager.killBackgroundProcesses(context.packageName)
            System.exit(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取栈内activity数量
     * */
    fun getCount(): Int = mActivityStack.size

    /**
     * 静态内部类
     * 一个ClassLoader下同一个类只会加载一次，保证了并发时不会得到不同的对象
     * */
    object AppHolder {
        var instance: AppManager = AppManager()
    }

    // 类似于static
    companion object {
        private val mActivityStack: Stack<Activity> = Stack()

        /**
         * 实现懒加载
         * 在调用getInstance()方法时才会去初始化mInstance
         */
        val instance: AppManager
            get() = AppHolder.instance
    }
}