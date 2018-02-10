package com.qcloud.qclib.imageselect

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.qcloud.qclib.R
import com.qcloud.qclib.imageselect.utils.ImageSelectUtil
import com.qcloud.qclib.imageselect.utils.ImageUtil
import com.qcloud.qclib.imageselect.widget.CutImageView
import com.qcloud.qclib.toast.QToast
import com.qcloud.qclib.utils.FileUtil
import com.qcloud.qclib.utils.StringUtil
import java.io.File
import java.util.*


/**
 * 类说明：图片处理界面
 * Author: Kuzan
 * Date: 2017/5/25 14:58.
 */
class ProcessImageActivity : Activity() {

    private var btnConfirm: Button? = null
    private var btnBack: ImageView? = null
    private var imageView: CutImageView? = null
    private val TAKE_PHOTO = 110

    private var mType = OPEN_PHOTO
    private var mIsCut = false

    private var mRequestCode: Int = 0
    private val CODE_FOR_CAMERA_PERMISSION = 0x00000235
    private val CODE_FOR_WRITE_PERMISSION = 0x00001234 // 读取外置sd卡权限

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_process_img)

        val intent = intent

        mType = intent.getIntExtra("type", OPEN_PHOTO)
        mIsCut = intent.getBooleanExtra("isCut", false)
        mRequestCode = intent.getIntExtra("requestCode", 0)
        if (mType == START_CAMERA) {
            val hasWriteContactsPermission = ContextCompat.checkSelfPermission(application, Manifest.permission.CAMERA)
            if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {
                val imageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //                imageUri = Uri.parse(getCacheDir().getPath() + File.separator + System.currentTimeMillis() + ".jpg");
                imageUri = Uri.parse("file:///sdcard/" + System.currentTimeMillis() + ".jpg")
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(imageIntent, TAKE_PHOTO)
            } else {
                ActivityCompat.requestPermissions(this@ProcessImageActivity, arrayOf(Manifest.permission.CAMERA), CODE_FOR_CAMERA_PERMISSION)
            }
        } else {
            ImageSelectActivity.openActivity(this, mRequestCode)
        }

        initView()
    }

    private fun initView() {
        imageView = findViewById<View>(R.id.process_img) as CutImageView
        btnConfirm = findViewById<View>(R.id.commit) as Button
        btnBack = findViewById<View>(R.id.btn_back) as ImageView

        btnConfirm!!.setOnClickListener {
            btnConfirm!!.isEnabled = false
            confirm(imageView!!.clipImage())
        }
        btnBack!!.setOnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == TAKE_PHOTO) {
            if (imageUri != null) {
                val url = ImageUtil.getRealFilePath(this, imageUri)
                if (StringUtil.isNotBlank(url)) {
                    val hasWriteContactsPermission = ContextCompat.checkSelfPermission(application, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {
                        getCameraImage()
                    } else {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), CODE_FOR_WRITE_PERMISSION)
                    }
                } else {
                    QToast.show(this, "获取图片失败")
                    finish()
                }
            }
        } else if (data != null && requestCode == mRequestCode) {
            val images = data.getStringArrayListExtra(ImageSelectUtil.SELECT_RESULT)
            if (mIsCut) {
                val bitmap = ImageUtil.decodeSampledBitmapFromResource(images[0], 720, 1080)
                if (bitmap != null) {
                    imageView!!.setBitmapData(bitmap, intent.getIntExtra("cutType", CutImageView.RECT_TYPE),
                            intent.getIntExtra("width", -1), intent.getIntExtra("height", -1))
                } else {
                    QToast.show(this, "获取图片失败")
                    finish()
                }
            } else {
                val intent = Intent()
                intent.putStringArrayListExtra(ImageSelectUtil.SELECT_RESULT, images)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        } else {
            finish()
        }
    }

    private fun getCameraImage() {
        if (imageUri != null) {
            val url = ImageUtil.getRealFilePath(this, imageUri)
            if (StringUtil.isNotBlank(url)) {
                val bitmap = ImageUtil.decodeSampledBitmapFromResource(url!!, 720, 1080)
                if (bitmap != null) {
                    if (mIsCut) {
                        imageView!!.setBitmapData(bitmap, intent.getIntExtra("cutType", CutImageView.RECT_TYPE),
                                intent.getIntExtra("width", -1), intent.getIntExtra("height", -1))
                    } else {
                        confirm(bitmap)
                    }
                    deleteTempImage()
                } else {
                    QToast.show(this, "获取图片失败")
                    finish()
                }
            } else {
                QToast.show(this, "获取图片失败")
                finish()
            }
        } else {
            finish()
        }
    }

    private fun deleteTempImage() {
        if (imageUri != null) {
            val url = ImageUtil.getRealFilePath(this, imageUri)
            if (StringUtil.isNotBlank(url)) {
                FileUtil.deleteFile(url!!)
            }
        }
    }

    fun confirm(bitmap: Bitmap?) {
        var newBitmap = bitmap

        var imagePath: String? = null

        if (newBitmap != null) {
            imagePath = ImageUtil.saveImage(newBitmap, cacheDir.path + File.separator + "image_select")
            newBitmap.recycle()
        }

        if (StringUtil.isNotBlank(imagePath)) {
            val selectImages = ArrayList<String>()
            selectImages.add(imagePath!!)
            val intent = Intent()
            intent.putStringArrayListExtra(ImageSelectUtil.SELECT_RESULT, selectImages)
            setResult(Activity.RESULT_OK, intent)
        }

        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CODE_FOR_CAMERA_PERMISSION) {
            if (permissions[0] == Manifest.permission.CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意使用CAMERA
                startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_PHOTO)
            } else {
                //用户不同意，向用户展示该权限作用
                finish()
            }
        } else if (requestCode == CODE_FOR_WRITE_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCameraImage()
            } else {
                QToast.show(this, "获取图片失败")
                finish()
            }
        }
    }

    companion object {

        val START_CAMERA = 1
        val OPEN_PHOTO = 2

        fun openThisActivity(context: Activity, requestCode: Int, type: Int, isCut: Boolean, cutType: Int, width: Int, height: Int) {
            val intent = Intent(context, ProcessImageActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("isCut", isCut)
            intent.putExtra("requestCode", requestCode)
            intent.putExtra("cutType", cutType)
            intent.putExtra("width", width)
            intent.putExtra("height", height)
            context.startActivityForResult(intent, requestCode)
        }
    }

}
