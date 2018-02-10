package com.qcloud.qclib.rxtask

import com.qcloud.qclib.rxtask.task.IOTask
import com.qcloud.qclib.rxtask.task.NewTask
import com.qcloud.qclib.rxtask.task.Task
import com.qcloud.qclib.rxtask.task.UITask
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 类说明：Rx线程
 * Author: Kuzan
 * Date: 2017/12/21 9:33.
 */
object RxScheduler {
    /**
     * IO线程执行
     * */
    fun <T> doOnIOThread(task: IOTask<T>) {
        Observable.just(task).observeOn(Schedulers.io()).subscribe { tioTask -> tioTask.doOnIOThread() }
    }

    /**
     * 新线程执行
     * */
    fun <T> doOnNewThread(task: NewTask<T>) {
        Observable.just(task).observeOn(Schedulers.io()).subscribe { tNewTask -> tNewTask.doOnNewThread() }
    }

    /**
     * ui线程执行
     * */
    fun <T> doOnUiThread(task: UITask<T>) {
        Observable.just(task).observeOn(AndroidSchedulers.mainThread()).subscribe { task.doOnUIThread() }
    }

    /**
     * 主线程和子线程有交互执行
     * */
    fun <T> doTask(task: Task<T>) {
        Observable.create(ObservableOnSubscribe<T> { e ->
            task.doOnIOThread()
            e.onNext(task.t)
            e.onComplete()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { task.doOnUIThread() }
    }
}