package com.qcloud.qclib.permission

import android.app.Activity
import android.os.Build
import android.support.annotation.NonNull
import android.support.annotation.RequiresApi
import android.text.TextUtils
import com.qcloud.qclib.beans.PermissionBean
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.PublishSubject

/**
 * 类说明：权限管理工具，使用RxJava2实现
 * Author: Kuzan
 * Date: 2018/1/11 19:19.
 */
class PermissionsManager(@NonNull mActivity: Activity) {

    private val mPermissionsFragment: PermissionsFragment

    init {
        mPermissionsFragment = getPermissionsFragment(mActivity)
    }

    /**
     * 实例化
     * */
    private fun getPermissionsFragment(@NonNull activity: Activity): PermissionsFragment {
        var permissionsFragment: PermissionsFragment? = findPermissionsFragment(activity)

        return if (permissionsFragment == null) {
            permissionsFragment = PermissionsFragment()
            val fragmentManager = activity.fragmentManager
            fragmentManager
                    .beginTransaction()
                    .add(permissionsFragment, TAG)
                    .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()

            permissionsFragment
        } else {
            permissionsFragment
        }
    }

    /**
     * 单例模式
     * */
    private fun findPermissionsFragment(@NonNull activity: Activity): PermissionsFragment? {
        val fragment = activity.fragmentManager.findFragmentByTag(TAG)
        return if (fragment != null) {
            fragment as PermissionsFragment
        } else {
            null
        }
    }

    /**
     * 如果所有权限都被授予，则返回true，如果没有，则为false
     * <p>
     * 如果没有请求到一个或多个权限，请调用相关的框架方法来询问用户是否允许权限。
     */
    fun <T> ensure(vararg permissions: String): ObservableTransformer<T, Boolean> {
        return ObservableTransformer { observer ->
            request(observer, *permissions)
                    .buffer(permissions.size)
                    .flatMap { permissions ->
                        if (permissions.isEmpty()) {
                            return@flatMap Observable.empty<Boolean>()
                        }
                        // 如果所有权限都被授予，则返回true
                        permissions
                                .filterNot { it.granted }
                                .forEach { return@flatMap Observable.just(false) }
                        Observable.just(true)
                    }

        }
    }

    /**
     * 一个一个权限顺序申请
     * <p>
     * 如果没有请求到一个或多个权限，请调用相关的框架方法来询问用户是否允许权限。
     */
    fun <T> ensureEach(@NonNull vararg permissions: String): ObservableTransformer<T, PermissionBean> {
        return ObservableTransformer { o -> request(o, *permissions) }
    }

    /**
     * 申请权限
     *      在应用程序初始化阶段调用请求权限。
     *      最好在onCreate()中请求
     */
    fun requestArray(permissions: Array<String>): Observable<Boolean> {
        return Observable.just(TRIGGER).compose(ensure(*permissions))
    }

    /**
     * 申请权限
     *      在应用程序初始化阶段调用请求权限。
     *      最好在onCreate()中请求
     */
    fun request(vararg permissions: String): Observable<Boolean> {
        return Observable.just(TRIGGER).compose(ensure(*permissions))
    }

    /**
     * 在应用程序初始化阶段调用请求权限。
     */
    fun requestEach(vararg permissions: String): Observable<PermissionBean> {
        return Observable.just(TRIGGER).compose(ensureEach(*permissions))
    }

    /**
     * 请求权限
     * */
    private fun request(trigger: Observable<*>?, @NonNull vararg permissions: String): Observable<PermissionBean> {
        if (permissions.isEmpty()) {
            throw IllegalArgumentException("PermissionsManager.request/requestEach requires at least one input permission")
        }
        return oneOf(trigger, pending(*permissions))
                .flatMap { requestImplementation(*permissions) }
    }

    /**
     * 等待用户去申请的权限
     * */
    private fun pending(@NonNull vararg permissions: String): Observable<*> {
        return if (permissions.none { mPermissionsFragment.containsByPermission(it) }) Observable.empty<Any>() else Observable.just(TRIGGER)
    }

    /**
     * 请求一个权限
     * */
    private fun oneOf(trigger: Observable<*>?, pending: Observable<*>): Observable<*> {
        if (trigger == null) {
            return Observable.just(TRIGGER)
        }
        return Observable.merge(trigger, pending)
    }

    /**
     * 请求权限执行
     * */
    fun requestImplementation(vararg permissions: String): Observable<PermissionBean> {
        val list = ArrayList<Observable<PermissionBean>>(permissions.size)
        val unrequestedPermissions = ArrayList<String>()

        // 在多重权限的情况下，我们为每一个创建一个可观察到的权限。
        // 最后，观测值组合起来具有独特的响应。
        for (permission in permissions) {
            mPermissionsFragment.log("Requesting permission $permission")
            if (isGranted(permission)) {
                // 已经申请到的权限或者android 6.0以下
                // 返回已授予权限的对象。
                list.add(Observable.just(PermissionBean(permission, true, false)))
                continue
            }
            if (isRevoked(permission)) {
                // 拒绝权限，返回一个拒绝权限对象。
                list.add(Observable.just(PermissionBean(permission, false, false)))
                continue
            }
            var subject = mPermissionsFragment.getSubjectByPermission(permission)
            // 如果不存在，创建一个新的
            if (subject == null) {
                unrequestedPermissions.add(permission)
                subject = PublishSubject.create()
                mPermissionsFragment.setSubjectForPermission(permission, subject)
                list.add(subject)
            } else {
                list.add(subject)
            }
        }
        if (unrequestedPermissions.isNotEmpty()) {
            val unrequestedPermissionsArray = unrequestedPermissions.toTypedArray()
            requestPermissionsFromFragment(unrequestedPermissionsArray)
        }
        return Observable.concat(Observable.fromIterable(list))
    }

    fun shouldShowRequestPermissionRationale(@NonNull activity: Activity, @NonNull permissions: String): Observable<Boolean> {
        if (!isMarshmallow()) {
            return Observable.just(false)
        }
        return Observable.just(shouldShowRequestPermissionRationaleImplementation(activity, permissions))
    }

    fun shouldShowRequestPermissionRationaleImplementation(@NonNull activity: Activity, @NonNull vararg permissions: String): Boolean {
        return permissions.none { !isGranted(it) && !activity.shouldShowRequestPermissionRationale(it) }
    }

    /**
     * Fragment页面请求权限
     * */
    fun requestPermissionsFromFragment(@NonNull permissions: Array<String>) {
        mPermissionsFragment.log("requestPermissionsFromFragment " + TextUtils.join(", ", permissions))
        mPermissionsFragment.requestPermissions(permissions)
    }

    /**
     * 是否已授予权限
     * 如果已授予权限，则返回true。
     * <p>
     * android 6.0以下，返回true
     */
    fun isGranted(@NonNull permission: String): Boolean {
        return !isMarshmallow() || mPermissionsFragment.isGranted(permission)
    }

    /**
     * 是否拒绝权限
     * 如果拒绝，返回true
     * <p>
     * android 6.0以下，返回false
     */
    fun isRevoked(@NonNull permission: String): Boolean {
        return isMarshmallow() && mPermissionsFragment.isRevoked(permission)
    }

    /**
     * 是否需要请求权限
     * */
    fun isMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    /**
     * 请求回调
     * */
    fun onRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray) {
        mPermissionsFragment.onRequestPermissionsResult(permissions, grantResults, BooleanArray(permissions.size))
    }

    /**
     * 打印日志
     * */
    fun setLogging(logging: Boolean = true) {
        mPermissionsFragment.mLogging = logging
    }

    companion object {
        val TAG = PermissionsManager::class.java.simpleName
        val TRIGGER = Any()
    }
}