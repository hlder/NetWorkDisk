package com.hld.networkdisk.server.network

import android.util.Log
import androidx.activity.ComponentActivity
import com.google.gson.Gson
import com.hld.networkdisk.server.beans.MessageBean
import com.hld.networkdisk.server.beans.MessageCodes
import com.hld.networkdisk.server.data.AppDatabase

/**
 * 服务端提供接口
 */
class ServerApi(
    private val activity: ComponentActivity,
    onCreateListener: ServerSocketManager.OnCreateListener
) {
    private val gson = Gson()

    private val messageController = MessageController(activity)

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
                return messageController.queryFileList(fromMessage)
            }
            MessageCodes.CODE_DO_COPY_FILE -> { // 执行复制文件
                return messageController.doCopyFile(fromMessage)
            }
            MessageCodes.CODE_DO_MOVE_FILE -> { // 执行移动文件
                return messageController.doMoveFile(fromMessage)
            }
            MessageCodes.CODE_DO_RE_NAME_FILE -> { // 执行重命名
                return messageController.doRenameFile(fromMessage)
            }
            MessageCodes.CODE_DO_DELETE_FILE -> { // 执行删除文件
                return messageController.doDeleteFile(fromMessage)
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
