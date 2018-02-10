package com.qcloud.qclib.imageselect

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.qcloud.qclib.R
import com.qcloud.qclib.imageselect.adapter.ImageSelectAdapter
import com.qcloud.qclib.imageselect.beans.FolderBean
import com.qcloud.qclib.imageselect.utils.ImageSelectUtil
import com.qcloud.qclib.imageselect.window.FolderPop
import com.qcloud.qclib.toast.QToast
import com.qcloud.qclib.utils.StringUtil
import java.io.File
import java.util.*

/**
 * 类说明：图片选择页面
 * Author: Kuzan
 * Date: 2017/5/25 14:58.
 */
class ImageSelectActivity : Activity() {

    private var imageList: RecyclerView? = null
    private var btnConfirm: Button? = null
    private var btnBack: ImageView? = null
    private var tvFolderName: TextView? = null

    private var mAdapter: ImageSelectAdapter? = null

    private var folders: ArrayList<FolderBean>? = null

    private var mMaxCount: Int = 0
    private val CODE_FOR_WRITE_PERMISSION = 0x00001234 // 读取外置sd卡权限

    private var isToSettings = false
    private var mPop: FolderPop? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_image_select)

        initView()

        mMaxCount = intent.getIntExtra(MAX_SELECT_COUNT, 0)

        imageList!!.post {
            imageList!!.layoutManager = GridLayoutManager(this@ImageSelectActivity, 3)
            mAdapter = ImageSelectAdapter(this@ImageSelectActivity, imageList!!.width / 3, mMaxCount)
            imageList!!.adapter = mAdapter
            if (folders != null && !folders!!.isEmpty()) {
                setFolder(folders!![0])
            }

            mAdapter!!.setOnImageSelectListener(object : ImageSelectAdapter.OnImageSelectListener {
                override fun OnImageSelect(image: String, isSelect: Boolean, selectCount: Int) {
                    if (mMaxCount == 0) {
                        confirm()
                    } else {
                        setSelectCount(selectCount)
                    }
                }
            })
        }

        if (mMaxCount == 0) {
            btnConfirm!!.visibility = View.GONE
        } else {
            setSelectCount(0)
        }

        getImages()
    }

    override fun onStart() {
        super.onStart()
        if (isToSettings) {
            isToSettings = false
            getImages()
        }
    }

    private fun initView() {
        imageList = findViewById<View>(R.id.image_list) as RecyclerView
        btnConfirm = findViewById<View>(R.id.commit) as Button
        btnBack = findViewById<View>(R.id.btn_back) as ImageView
        tvFolderName = findViewById<View>(R.id.tv_folder_name) as TextView

        btnConfirm!!.setOnClickListener { confirm() }

        btnBack!!.setOnClickListener { finish() }

        tvFolderName!!.setOnClickListener {
            if (folders != null) {
                if (mPop == null) {
                    mPop = FolderPop(this@ImageSelectActivity)
                    mPop!!.setFolders(folders!!)
                    mPop!!.setOnSelectListener(object : FolderPop.OnSelectListener {
                        override fun onSelect(folder: FolderBean) {
                            setFolder(folder)
                        }
                    })
                }
                mPop!!.showAsDropDown(tvFolderName!!)
            }
        }
    }

    private fun setFolder(folder: FolderBean?) {
        if (folder != null && mAdapter != null) {
            tvFolderName!!.text = folder.name
            imageList!!.scrollToPosition(0)
            mAdapter!!.refresh(folder.images!!)
        }
    }

    /**
     * 发生没有权限等异常时，显示一个提示dialog.
     */
    private fun showExceptionDialog() {

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_exception, null, false)

        val dialog = AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view)
                .create()

        view.findViewById<View>(R.id.btn_confirm).setOnClickListener {
            dialog.cancel()
            startAppSettings()
            isToSettings = true
        }

        view.findViewById<View>(R.id.btn_cancel).setOnClickListener {
            dialog.cancel()
            finish()
        }

        dialog.show()
    }

    /**
     * 启动应用的设置
     */
    private fun startAppSettings() {
        val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + packageName)
        startActivity(intent)
    }

    private fun setSelectCount(count: Int) {
        if (mMaxCount > 0) {
            btnConfirm!!.text = "确定($count/$mMaxCount)"
        } else {
            btnConfirm!!.text = "确定($count)"
        }
    }

    fun confirm() {

        if (mAdapter == null) {
            return
        }

        val selectImages = mAdapter!!.selectImages
        if (selectImages.isEmpty()) {
            QToast.show(this, "请选择图片")
            return
        }

        val intent = Intent()
        intent.putStringArrayListExtra(ImageSelectUtil.SELECT_RESULT, selectImages)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CODE_FOR_WRITE_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGetImageThread()
            } else {
                showExceptionDialog()
            }
        }
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描
     */
    private fun getImages() {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            QToast.show(this, "没有图片")
            return
        }
        val hasWriteContactsPermission = ContextCompat.checkSelfPermission(application, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {
            startGetImageThread()
        } else {
            ActivityCompat.requestPermissions(this@ImageSelectActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), CODE_FOR_WRITE_PERMISSION)
        }
    }

    private fun startGetImageThread() {
        Thread(Runnable {
            val mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val mContentResolver = contentResolver

            val mCursor = mContentResolver.query(mImageUri, arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media._ID), null, null,
                    MediaStore.Images.Media.DATE_ADDED)

            //                images.clear();

            val images = ArrayList<String>()

            while (mCursor!!.moveToNext()) {
                // 获取图片的路径
                val path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA))

                images.add(path)
            }
            mCursor.close()
            Collections.reverse(images)
            folders = splitFolder(images)
            runOnUiThread {
                if (folders != null && !folders!!.isEmpty()) {
                    //                            mAdapter.refresh(images);
                    setFolder(folders!![0])
                }
            }
        }).start()
    }

    private fun splitFolder(images: ArrayList<String>?): ArrayList<FolderBean> {
        val folders = ArrayList<FolderBean>()
        folders.add(FolderBean("全部", images!!))

        if (!images.isEmpty()) {
            val size = images.size
            for (i in 0 until size) {
                val path = images[i]
                val name = getFolderName(path)
                if (StringUtil.isNotBlank(name)) {
                    val folder = getFolder(name, folders)
                    if (folder != null) {
                        folder.addImage(path)
                    } else {
                        val newFolder = FolderBean(name)
                        newFolder.addImage(path)
                        folders.add(newFolder)
                    }
                }
            }
        }
        return folders
    }

    private fun getFolderName(path: String): String {
        if (StringUtil.isNotBlank(path)) {
            val strings = path.split(File.separator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (strings.size >= 2) {
                return strings[strings.size - 2]
            }
        }
        return ""
    }

    private fun getFolder(name: String, folders: List<FolderBean>?): FolderBean? {
        if (folders != null && !folders.isEmpty()) {
            val size = folders.size
            (0 until size)
                    .map { folders[it] }
                    .filter { name == it.name }
                    .forEach { return it }
        }
        return null
    }

    companion object {

        /**
         * 最大的图片选择数
         */
        private val MAX_SELECT_COUNT = "max_select_count"

        /**
         * 启动图片选择器
         *
         * @param activity
         * @param requestCode
         * @param maxSelectCount 最大的图片选择数 小与0则不限制数量 等于0单选
         */
        fun openActivity(activity: Activity, requestCode: Int, maxSelectCount: Int) {
            val intent = Intent(activity, ImageSelectActivity::class.java)
            intent.putExtra(MAX_SELECT_COUNT, maxSelectCount)
            activity.startActivityForResult(intent, requestCode)
        }

        /**
         * 启动图片选择器 （不传最大的图片选择数，默认为单选）
         *
         * @param activity
         * @param requestCode
         */
        fun openActivity(activity: Activity, requestCode: Int) {
            val intent = Intent(activity, ImageSelectActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
        }
    }
}
