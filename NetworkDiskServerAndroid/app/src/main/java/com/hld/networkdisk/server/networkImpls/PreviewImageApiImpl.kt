package com.hld.networkdisk.server.networkImpls

import androidx.activity.ComponentActivity
import com.google.gson.Gson
import com.hld.networkdisk.server.beans.MessageBean
import com.hld.networkdisk.server.data.AppDatabase
import com.hld.networkdisk.server.networkapis.PreviewImageApi

class PreviewImageApiImpl(private val activity: ComponentActivity) : PreviewImageApi {
    private val gson = Gson()

    override fun onRequest(request: String): String {
        val messageBean = gson.fromJson(request, MessageBean::class.java)
        val path = messageBean.message
        val previewBean = AppDatabase.getInstance(activity).previewDao().query(path)
        return resultSuccess(messageBean, previewBean?.previewImageBase64 ?: "")
    }

    /**
     * 返回成功消息
     */
    private fun resultSuccess(fromMessage: MessageBean, obj: Any? = null): String {
        return gson.toJson(
            MessageBean(
                code = fromMessage.code,
                version = fromMessage.version,
                message = obj?.let { gson.toJson(it) } ?: ""
            )
        )
    }
}