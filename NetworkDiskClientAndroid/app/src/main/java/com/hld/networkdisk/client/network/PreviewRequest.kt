package com.hld.networkdisk.client.network

import android.util.Log
import com.hld.networkdisk.client.extension.suspendTimeOutCoroutineScope
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resume

class PreviewRequest(private val ip: String, private val port: Int) {

    /**
     * 查询预览图
     */
    suspend fun queryPreviewImage(filePath: String): String? {
        val socketManager = SocketManager(ip, port)
        socketManager.start()
        var preViewBase64: String? = null
        try {
            preViewBase64 = doHttp(socketManager, filePath)
        } catch (e: TimeoutException) {
            Log.e(TAG, "queryPreviewImageErr:${e.localizedMessage}")
        }

        return preViewBase64
    }

    private suspend fun doHttp(socketManager: SocketManager, filePath: String) =
        suspendTimeOutCoroutineScope { continuation ->
            socketManager.sendMessage(message = filePath) { resultStr ->
                continuation.resume(resultStr)
                socketManager.close()
            }
        }

    companion object {
        private const val TAG = "PreviewRequest"
    }
}