package com.hld.networkdisk.client.network

import android.util.Log
import com.google.gson.Gson
import com.hld.networkdisk.client.extension.suspendCancellableCoroutineScope
import com.hld.networkdisk.client.extension.suspendTimeOutCoroutineScope
import com.hld.networkdisk.client.pages.filelistpage.FileListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resume

class PreviewRequest(private val ip: String, private val port: Int) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    /**
     * 查询预览图
     */
    suspend fun queryPreviewImage(filePath: String): String? {
        val socketManager = SocketManager(ip, port)
        socketManager.create()
        coroutineScope.launch {
            socketManager.start()
        }
        var preViewBase64: String? = null
        try {
            Log.d(TAG, "===============PreviewRequest1 filePath:${filePath}")
            preViewBase64 = doHttp(socketManager, filePath)
            Log.d(TAG, "===============PreviewRequest2 filePath:${filePath}")
        } catch (e: TimeoutException) {
            Log.e(TAG, "queryPreviewImageErr:${e.localizedMessage}")
        }

        return preViewBase64?.replace("\\u003d", "=")?.replace("\\n","")
    }

    private suspend fun doHttp(socketManager: SocketManager, filePath: String) =
        suspendCancellableCoroutineScope { continuation ->
            Log.d(TAG, "===============PreviewRequest1 doHttp1")
            socketManager.sendMessage(message = filePath) { resultStr ->
                Log.d(TAG, "===============PreviewRequest2 doHttp2")
                continuation.resume(resultStr)
                socketManager.close()
            }
        }

    companion object {
        private const val TAG = "PreviewRequest"
    }
}