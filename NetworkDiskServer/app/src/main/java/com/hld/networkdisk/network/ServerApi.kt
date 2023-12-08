package com.hld.networkdisk.network

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.hld.networkdisk.beans.MessageBean
import com.hld.networkdisk.beans.MessageCodes
import com.hld.networkdisk.beans.MessageTransferFileBean
import com.hld.networkdisk.filemanager.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 服务端提供接口
 */
class ServerApi(
    activity: ComponentActivity,
    onCreateListener: ServerSocketManager.OnCreateListener
) {
    private val lifecycleScope = activity.lifecycleScope
    private val fileManager: FileManager = FileManager(activity)
    private val gson = Gson()

    private val socketTransfer =
        SocketTransfer(activity, onCreateListener, object : SocketTransfer.OnRequestListener {
            override suspend fun onRequest(data: String): String {
                println("=========================server收到消息：$data")
                val messageBean = gson.fromJson(data, MessageBean::class.java)
                return parseMessage(messageBean)
            }
        })

    /**
     * 解析消息
     */
    private fun parseMessage(fromMessage: MessageBean): String {
        when (fromMessage.code) {
            MessageCodes.CODE_FILE_LIST -> { // 查询文件列表
                val listFileBean = fileManager.queryFileList(fromMessage.message)
                return resultSuccess(fromMessage, listFileBean)
            }

            MessageCodes.CODE_CLIENT_RECEIVE_FROM_SERVER_FILE -> { // 客户端收服务端的文件
                lifecycleScope.launch(Dispatchers.IO) {
                    val bean = gson.fromJson(fromMessage.message, MessageTransferFileBean::class.java)
                    val outputStream = socketTransfer.getSendFile(bean.address, bean.port)
                    outputStream?.let { fileManager.sendFileWithStream(outputStream, bean) }
                }
                return resultSuccess(fromMessage)
            }

            MessageCodes.CODE_CLIENT_SEND_TO_SERVER_FILE -> { // 客户端发文件给服务端
                lifecycleScope.launch(Dispatchers.IO) {
                    val bean = gson.fromJson(fromMessage.message, MessageTransferFileBean::class.java)
                    val inputStream = socketTransfer.getReceiveFileStream(bean.address, bean.port)
                    println("====================================address:${bean.address} port:${bean.port} inputStream：${inputStream} parseMessage:${fromMessage.message}")
                    inputStream?.let { fileManager.receiveFileFromStream(inputStream, bean) }
                }
                return resultSuccess(fromMessage)
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
}