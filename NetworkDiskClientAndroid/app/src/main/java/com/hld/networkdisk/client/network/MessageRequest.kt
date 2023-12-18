package com.hld.networkdisk.client.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.commons.MessageCodes
import com.hld.networkdisk.client.extension.suspendTimeOutCoroutineScope
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resume

class MessageRequest(private val ip: String, private val port: Int) {

    private val socketManager = SocketManager(ip, port)

    private val gson = Gson()

    suspend fun start() {
        socketManager.create()
        socketManager.start()
    }

    /**
     * 普通发送消息
     */
    private suspend fun sendMessage(code: Int, message: String) =
        suspendTimeOutCoroutineScope { continuation ->
            socketManager.sendMessage(code, message) { resultStr -> // 超时10秒
                continuation.resume(resultStr)
            }
        }

    /**
     * 查询列表
     */
    suspend fun queryFileList(filePath: String): List<FileBean>? {
        try {
            Log.i(TAG, "queryFileList filePath:${filePath}")
            val jsonStr = sendMessage(MessageCodes.CODE_FILE_LIST, filePath)
            Log.i(TAG, "queryFileList return:${jsonStr.length}")
            return gson.fromJson<List<FileBean>>(
                jsonStr,
                object : TypeToken<List<FileBean>>() {}.type
            )
        } catch (e: TimeoutException) {
            Log.e(TAG, "queryPreviewImageErr:${e.localizedMessage}")
        }
        return null
    }

    companion object {
        private const val TAG = "MessageRequest"
    }
}