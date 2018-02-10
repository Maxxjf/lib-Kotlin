package com.qcloud.qclib.update

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import com.qcloud.qclib.R
import com.qcloud.qclib.rxtask.RxScheduler
import com.qcloud.qclib.rxtask.task.IOTask
import com.qcloud.qclib.toast.QToast
import com.qcloud.qclib.utils.FileUtil
import com.qcloud.qclib.utils.StringUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * 类说明：版本更新service
 * Author: Kuzan
 * Date: 2018/1/22 16:16.
 */
class UpdateService: Service() {

    private var mAppName: String? = null    // 应用名称
    private var mDownLoadUrl: String? = null    // apk 下载路径
    private var mLocalFilePath: String? = null  // apk 本地保存路径

    private var mNotificationManager: NotificationManager? = null   // 通知管理器
    private var mNotification: Notification? = null // 通知

    private var mIntent: Intent? = null

    private var mNotificationId: Int = 1

    private var mContentView: RemoteViews? = null

    private var isStop: Boolean = false
    private var isCancel: Boolean = false
    private var isLoading: Boolean = false

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(Companion.TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            mIntent = intent
            if (!isLoading && DOWN_ACTION_START == intent.action) {

                mAppName = UpdateUtil.getAppName(this)
                mDownLoadUrl = intent.getStringExtra("downLoadUrl")
                mLocalFilePath = UpdateUtil.getLocalFilePath(this)

                Log.e(TAG, "mDownLoadUrl = " + mDownLoadUrl!!)
                // 创建通知栏
                createNotification()
                // 启动下载线程
                startUpdate()
                isStop = false
                isLoading = true
            } else if (DOWN_ACTION_STOP == intent.action) {
                isStop = true
                isCancel = true
                isLoading = false
                // 结束service
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null) {
//            mIntent = intent;
//            if (!isLoading && DOWN_ACTION_START.equals(intent.getAction())) {
//
//                mAppName = UpdateUtil.getAppName(this);
//                mDownLoadUrl = intent.getStringExtra("downLoadUrl");
//                mLocalFilePath = UpdateUtil.getLocalFilePath(this);
//
//                Log.e(TAG, "mDownLoadUrl = " + mDownLoadUrl);
//                // 创建通知栏
//                createNotification();
//                // 启动下载线程
//                startUpdate();
//                isStop = false;
//                isLoading = true;
//            } else if (DOWN_ACTION_STOP.equals(intent.getAction())) {
//                isStop = true;
//                isCancel = true;
//                isLoading = false;
//                // 结束service
//                stopSelf();
//            }
//        }
//
//        return super.onStartCommand(intent, flags, startId);
//    }
//
    /***
     * 创建通知栏
     */
    fun createNotification() {
        /*
         * 自定义Notification视图
		 */
        mContentView = RemoteViews(packageName, R.layout.item_of_notification)
        mContentView!!.setTextViewText(R.id.notificationTitle, mAppName + "—"
                + getString(R.string.down_load_str001))
        mContentView!!.setTextViewText(R.id.notificationPercent, "0%")
        mContentView!!.setProgressBar(R.id.notificationProgress, 100, 0, false)

        // 初始化通知
        mNotification = NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(
                        getString(R.string.down_load_str002) + " " + mAppName)
                .build()
        mNotification!!.tickerText = (mAppName + " "
                + getString(R.string.down_load_str002))
        mNotification!!.flags = mNotification!!.flags or Notification.FLAG_NO_CLEAR
        mNotification!!.defaults = mNotification!!.defaults or Notification.DEFAULT_SOUND // 设置通知铃声和振动提醒
        mNotification!!.contentView = mContentView

        val launcherActivityName = UpdateUtil.getLauncherActivityName(this)
        if (StringUtil.isNotBlank(launcherActivityName)) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.component = ComponentName(this.packageName,
                    launcherActivityName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED//关键的一步，设置启动模式

            val mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            mNotification!!.contentIntent = mPendingIntent
        }

        // 发送通知
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager!!.notify(mNotificationId, mNotification)
        // 清除通知铃声
        mNotification!!.defaults = 0
    }

    @SuppressLint("HandlerLeak")
    private var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                DOWN_LOADING // apk 下载中
                -> {
                    val progress = msg.arg1
                    // 改变通知栏
                    mContentView!!.setTextViewText(R.id.notificationPercent, progress.toString() + "%")
                    mContentView!!.setProgressBar(R.id.notificationProgress, 100,
                            progress, false)
                    mNotificationManager!!.notify(mNotificationId, mNotification)
                    Log.e(TAG, "" + progress)
                    return
                }
                DOWN_OK // apk 下载完成
                -> {
                    mContentView!!.setTextViewText(R.id.notificationPercent, 100.toString() + "%")
                    mContentView!!.setProgressBar(R.id.notificationProgress, 100,
                            100, false)
                    // 添加通知声音
                    mNotification!!.defaults = mNotification!!.defaults or Notification.DEFAULT_SOUND
                    // 下载完成，点击安装
                    val uri = Uri.fromFile(File(mLocalFilePath!!))
                    // 安装应用意图
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "application/vnd.android.package-archive")
                    val mPendingIntent = PendingIntent.getActivity(this@UpdateService,
                            0, intent, 0)
                    mNotification!!.tickerText = (mAppName + " "
                            + getString(R.string.down_load_str003))

                    Log.e(TAG, "下载完成")
                    mContentView!!.setTextViewText(R.id.notificationTitle, mAppName + "—"
                            + getString(R.string.down_load_str003))
                    mNotification!!.contentIntent = mPendingIntent
                    mNotification!!.icon = android.R.drawable.stat_sys_download_done
                    mNotification!!.flags = Notification.FLAG_AUTO_CANCEL
                    mNotificationManager!!.notify(mNotificationId, mNotification)
                    isLoading = false
                }
                DOWN_ERROR // apk 下载失败
                -> {

                    if (!isCancel) {
                        val startIntent = Intent()
                        startIntent.action = "android.intent.action.VIEW"
                        val content_url = Uri.parse(mDownLoadUrl)
                        startIntent.data = content_url
                        val pendingIntent = PendingIntent.getActivity(this@UpdateService,
                                0, startIntent, 0)
                        mContentView!!.setTextViewText(R.id.notificationTitle, mAppName + "—"
                                + getString(R.string.down_load_str004))
                        mNotification!!.tickerText = (mAppName + " "
                                + getString(R.string.down_load_str004))
                        mNotification!!.contentIntent = pendingIntent
                        // 添加通知声音
                        mNotification!!.defaults = mNotification!!.defaults or Notification.DEFAULT_SOUND
                        mNotification!!.icon = android.R.drawable.stat_sys_download_done
                        mNotification!!.flags = Notification.FLAG_AUTO_CANCEL
                        mNotificationManager!!.notify(mNotificationId, mNotification)
                        //                        ToastUtils.ToastMessage(UpgradeService.this, "下载失败，启动浏览器下载!");
                    } else {
                        // 添加通知声音
                        mNotification!!.defaults = mNotification!!.defaults or Notification.DEFAULT_SOUND
                        mNotificationManager!!.cancel(mNotificationId)
                        QToast.show(this@UpdateService, "下载任务已取消!")
                    }
                    Log.e(TAG, "下载失败")
                    isLoading = false
                }
            }

            isCancel = false

            // 结束service
            stopService(mIntent)
            stopSelf()
        }
    }

    /***
     * 开启线程下载更新
     */
    private fun startUpdate() {
        // 启动线程下载更新
        RxScheduler.doOnIOThread(object : IOTask<Void> {
            override fun doOnIOThread() {
                try {
                    // 下载apk
                    downloadUpdateFile(mDownLoadUrl, mLocalFilePath, TIMEOUT,
                            handler)
                } catch (e: Exception) {
                    e.printStackTrace()
                    handler.sendMessage(handler.obtainMessage(DOWN_ERROR))
                }
            }
        })
    }

    /***
     * 下载文件
     *
     * @return
     * @throws MalformedURLException
     */
    @Throws(Exception::class)
    fun downloadUpdateFile(down_url: String?, file: String?, timeoutMillis: Int, handler: Handler) {
        var result = false
        var totalSize = 0 // 文件总大小
        var ins: InputStream? = null
        var fos: FileOutputStream? = null

        if (StringUtil.isNotBlank(file)) {
            FileUtil.deleteFile(file!!)
            FileUtil.createFile(file)
        }

        val url: URL
        try {
            url = URL(down_url)

            val httpURLConnection = url.openConnection() as HttpURLConnection

            httpURLConnection.connectTimeout = timeoutMillis

            httpURLConnection.readTimeout = timeoutMillis

            // 获取下载文件的size
            totalSize = httpURLConnection.contentLength
            Log.e("DownloadApkUtils", "totalSize = " + totalSize)

            if (httpURLConnection.responseCode == 404) {
                throw Exception("fail!")
            }

            ins = httpURLConnection.inputStream
            fos = FileOutputStream(file!!, false)
        } catch (e1: MalformedURLException) {
            e1.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var downloadCount = 0 // 已下载大小
        var updateProgress = 0 // 更新进度

        val buffer = ByteArray(64)
        var readsize = ins!!.read(buffer)

        try {
            while (readsize != -1) {

                fos!!.write(buffer, 0, readsize)

                // 计算已下载到的大小
                downloadCount += readsize

                readsize = ins.read(buffer)

                // 先计算已下载的百分比，然后跟上次比较是否有增加，有则更新通知进度
                val now = downloadCount * 100 / totalSize
                // Log.e(TAG, "now = " + now) ;
                if (updateProgress < now) {
                    updateProgress = now

                    val msg = Message()
                    msg.arg1 = updateProgress
                    msg.what = DOWN_LOADING
                    handler.sendMessage(msg)
                }

                if (isStop) {
                    break
                }
            }
            result = !isStop
        } catch (e: Exception) {
            e.printStackTrace()

        } finally {
            try {
                fos!!.close()
                ins.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (result) {
            handler.sendMessage(handler.obtainMessage(DOWN_OK))
        } else {
            handler.sendMessage(handler.obtainMessage(DOWN_ERROR))
        }
    }

    companion object {
        val TAG = "UpdateService"

        private val TIMEOUT = 10 * 60 * 1000    // 超时
        private val DOWN_OK = 1         // 下载成功标记
        private val DOWN_LOADING = 3    // 下载中。。。标记
        private val DOWN_ERROR = 0      // 下载失败标记

        val DOWN_ACTION_START = "ACTION_START"
        val DOWN_ACTION_STOP = "ACTION_STOP"
    }
}