package com.qcloud.qclib.permission

import android.annotation.TargetApi
import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.annotation.RequiresApi
import android.util.Log
import com.qcloud.qclib.beans.PermissionBean
import io.reactivex.subjects.PublishSubject

/**
 * 类说明：权限申请处理
 * Author: Kuzan
 * Date: 2018/1/12 9:15.
 */
class PermissionsFragment: Fragment() {

//    private static final int PERMISSIONS_REQUEST_CODE = 42;
//
    /**包含所有当前权限请求，一旦被批准或拒绝，他们就从中移除*/
    val mSubjects = HashMap<String, PublishSubject<PermissionBean>>()

    var mLogging: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    /**
     * 请求权限组
     * */
    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissions(@NonNull permissions: Array<String>) {
        requestPermissions(permissions, Companion.PERMISSIONS_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != PERMISSIONS_REQUEST_CODE) {
            return
        }
        val shouldShowRequestPermissionRationale = BooleanArray(permissions.size)
        for (i in permissions.indices) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i])
        }
        onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale)
    }

    /**
     * 请求权限返回处理
     * */
    fun onRequestPermissionsResult(@NonNull permissions: Array<String>, @NonNull grantResults: IntArray, @NonNull shouldShowRequestPermissionRationale: BooleanArray) {
        for (i in permissions.indices) {
            log("onRequestPermissionsResult  " + permissions[i])
            // 找到相应的权限
            val subject = mSubjects[permissions[i]]
            if (subject == null) {
                // 没有对应的权限
                Log.e(PermissionsManager.TAG, "PermissionsManager.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
                return
            }
            mSubjects.remove(permissions[i])
            val granted = grantResults[i] == PackageManager.PERMISSION_GRANTED
            subject.onNext(PermissionBean(permissions[i], granted, shouldShowRequestPermissionRationale[i]))
            subject.onComplete()
        }
    }

    /**
     * 是否已开启权限
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    fun isGranted(@NonNull permission: String): Boolean {
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 是否拒绝该权限
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    fun isRevoked(@NonNull permission: String): Boolean {
        return activity.packageManager.isPermissionRevokedByPolicy(permission, activity.packageName)
    }

    /**
     * 获取权限信息
     * */
    fun getSubjectByPermission(@NonNull permission: String): PublishSubject<PermissionBean>? {
        return mSubjects[permission]
    }

    /**
     * 判断是否包含该权限
     * */
    fun containsByPermission(@NonNull permission: String): Boolean {
        return mSubjects.containsKey(permission)
    }

    /**
     * 设定权限
     * */
    fun setSubjectForPermission(@NonNull permission: String, @NonNull subject: PublishSubject<PermissionBean>): PublishSubject<PermissionBean>? {
        return mSubjects.put(permission, subject)
    }

    /**
     * 打印日志
     * */
    fun log(message: String) {
        if (mLogging) {
            Log.d(PermissionsManager.TAG, message)
        }
    }

    companion object {
        val PERMISSIONS_REQUEST_CODE = 42
    }
}