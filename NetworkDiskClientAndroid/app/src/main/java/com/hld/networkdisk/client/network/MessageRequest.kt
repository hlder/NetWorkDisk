package com.hld.networkdisk.client.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.commons.MessageCodes
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.TimeoutException

class MessageRequest(coroutineScope: CoroutineScope, ip: String, port: Int) {

    private val socketPoolManager = SocketPoolManager(coroutineScope, ip, port)

    private val gson = Gson()

    /**
     * 普通发送消息
     */

    /**
     * 查询列表
     */
    suspend fun queryFileList(filePath: String): List<FileBean>? {
        try {
            Log.i(TAG, "queryFileList filePath:${filePath}")
            val jsonStr = socketPoolManager.sendMessage(MessageCodes.CODE_FILE_LIST, filePath)
            Log.i(TAG, "queryFileList return:${jsonStr.length}")
            return gson.fromJson<List<FileBean>>(
                jsonStr, object : TypeToken<List<FileBean>>() {}.type
            )
        } catch (e: TimeoutException) {
            Log.e(TAG, "queryPreviewImageErr:${e.localizedMessage}")
        }
        return null
    }

    suspend fun queryFileDirList(filePath: String):List<FileBean>?{
        try {
            Log.i(TAG, "queryFileList filePath:${filePath}")
            val jsonStr = socketPoolManager.sendMessage(MessageCodes.CODE_QUERY_FILE_DIR_LIST, filePath)
            Log.i(TAG, "queryFileList return:${jsonStr.length}")
            return gson.fromJson<List<FileBean>>(
                jsonStr, object : TypeToken<List<FileBean>>() {}.type
            )
        } catch (e: TimeoutException) {
            Log.e(TAG, "queryPreviewImageErr:${e.localizedMessage}")
        }
        return null
    }

    suspend fun deleteFiles(listFilePath: List<String>) {
        socketPoolManager.sendMessage(MessageCodes.CODE_DO_DELETE_FILE, listFilePath)
    }

    companion object {
        private const val TAG = "MessageRequest"
    }
}