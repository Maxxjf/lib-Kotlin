package com.qcloud.qclib.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * 类说明：统一管理所有的订阅生命周期
 * Author: Kuzan
 * Date: 2017/12/4 11:30.
 */
class CompositeDisposableUtil {
    private var mCompositeDisposable: CompositeDisposable? = null

    /**
     * 添加
     * */
    fun addDisposable(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }

    /**
     * 全部取消绑定
     * */
    fun dispose() {
        mCompositeDisposable?.dispose()
    }

    /**
     * 取消绑定
     * */
    fun removeDisposable(disposable: Disposable?) {
        disposable?.dispose()
    }
}