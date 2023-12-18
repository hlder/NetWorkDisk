package com.hld.networkdisk.server.network

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.hld.networkdisk.server.beans.MessageBean
import com.hld.networkdisk.server.beans.MessageCodes
import com.hld.networkdisk.server.beans.MessageTransferFileBean
import com.hld.networkdisk.server.data.AppDatabase
import com.hld.networkdisk.server.data.PreviewDao
import com.hld.networkdisk.server.filemanager.FileManager
import com.hld.networkdisk.server.filemanager.FileScan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * 服务端提供接口
 */
class ServerApi(
    private val activity: ComponentActivity,
    onCreateListener: ServerSocketManager.OnCreateListener
) {
    private val lifecycleScope = activity.lifecycleScope
    private val fileManager: FileManager = FileManager(activity)
    private val gson = Gson()

    private val socketTransfer =
        SocketTransfer(activity, onCreateListener, object : SocketTransfer.OnRequestListener {
            override suspend fun onRequest(data: String): String {
                Log.i(TAG, "server收到消息：$data")
                val messageBean = gson.fromJson(data, MessageBean::class.java)
                return parseMessage(messageBean).apply {
                    Log.i(TAG, "返回数据：$data")
                }
            }
        }, object : SocketTransfer.OnRequestListener {
            override suspend fun onRequest(data: String): String {
                val messageBean = gson.fromJson(data, MessageBean::class.java)
                val path = messageBean.message
                val previewBean = AppDatabase.getInstance(activity).previewDao().query(path)
                return resultSuccess(messageBean, previewBean?.previewImageBase64?:"")
            }
        })

    /**
     * 解析消息
     */
    private fun parseMessage(fromMessage: MessageBean): String {
        when (fromMessage.code) {
            MessageCodes.CODE_FILE_LIST -> { // 查询文件列表
                val listFileBean = fileManager.queryFileList(fromMessage.message)
                val listPreview = AppDatabase.getInstance(activity).previewDao().query(listFileBean.map { it.absolutePath }.toTypedArray())
                listFileBean.forEach { item->
                    val bean: PreviewDao.Bean? = listPreview?.find { it.fileAbsolutePath == item.absolutePath }
                    item.previewImageBase64 = bean?.previewImageBase64
                }
                return resultSuccess(fromMessage, listFileBean)
            }
        }
        return ""
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

    companion object{
        private const val TAG = "ServerApi"
    }
}
