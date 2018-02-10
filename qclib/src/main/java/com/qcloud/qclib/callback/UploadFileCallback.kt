package com.qcloud.qclib.callback

import com.google.gson.reflect.TypeToken
import com.lzy.okgo.callback.AbsCallback
import com.qcloud.qclib.beans.BaseResponse
import com.qcloud.qclib.beans.UploadFileBean
import com.qcloud.qclib.enums.RequestStatusEnum
import com.qcloud.qclib.network.JsonConvert
import com.qcloud.qclib.utils.StringUtil
import okhttp3.Response
import org.json.JSONObject

/**
 * 类说明：上传文件回调
 *          这个是根据不同框架定义的
 * Author: Kuzan
 * Date: 2018/1/16 11:34.
 */
abstract class UploadFileCallback: AbsCallback<UploadFileBean>() {
    private val convert:JsonConvert<BaseResponse<UploadFileBean>>

    init {
        val type = object: TypeToken<JSONObject>() {

        }.type
        convert = JsonConvert(type)
    }

    @Throws(Throwable::class)
    override fun convertResponse(response: Response): UploadFileBean? {
        val bean: BaseResponse<UploadFileBean>? = convert.convertResponse(response)
        response.close()

        if (bean != null) {
            if (bean.status == RequestStatusEnum.SUCCESS.status) {
                return bean.data
            } else {
                throw IllegalStateException(if (StringUtil.isBlank(bean.message)) "上传文件失败" else bean.message)
            }
        } else {
            throw IllegalStateException("上传文件失败")
        }
    }
}