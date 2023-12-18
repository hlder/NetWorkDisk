package com.hld.networkdisk.client.network

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.hld.networkdisk.client.beans.MessageTransferFileBean
import com.hld.networkdisk.client.commons.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.PrintWriter
import java.lang.Exception
import java.net.Socket

class FileTransferRequest private constructor(ip: String, port: Int) {
    private val socket = Socket(ip, port)
    private val gson = Gson()

    suspend fun doSendFile(
        file: File,
        yunFilePath: String,
        onProgress: ((progress: Float) -> Unit)? = null,
        onSuccess: (() -> Unit)? = null,
    ) = withContext(Dispatchers.IO) {
        val bean = MessageTransferFileBean(
            address = socket.localAddress.hostAddress ?: "",
            port = socket.localPort,
            filePath = yunFilePath,
            isClientSendToServer = true,
            fileLength = file.length()
        )
        val printWriter = PrintWriter(socket.getOutputStream())
        printWriter.println(gson.toJson(bean))
        printWriter.flush()
        uploadFile(file, onProgress, onSuccess)
    }

    suspend fun doDownloadFile(
        context: Context,
        yunFilePath: String,
        fileSize: Long,
        onProgress: ((progress: Float) -> Unit)? = null,
        onSuccess: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) = withContext(Dispatchers.IO) {
        val bean = MessageTransferFileBean(
            address = socket.localAddress.hostAddress ?: "",
            port = socket.localPort,
            filePath = yunFilePath,
            isClientSendToServer = false,
            fileLength = fileSize
        )

        val printWriter = PrintWriter(socket.getOutputStream())
        printWriter.println(gson.toJson(bean))
        printWriter.flush()
        launch(Dispatchers.IO) {
            downLoadFile(
                Constants.baseFilePath(context) + yunFilePath,
                fileSize, onProgress, onSuccess, onError
            )
        }
    }

    private suspend fun uploadFile(
        file: File,
        onProgress: ((progress: Float) -> Unit)? = null,
        onSuccess: (() -> Unit)? = null,
    ) = coroutineScope {
        launch(Dispatchers.IO) {
            // 开始发送
            val ins: InputStream = FileInputStream(file)
            val os = socket.getOutputStream()
            val buffer = ByteArray(1024)
            var bytesRead: Int

            var count = 0
            val max = file.length()
            onProgress?.invoke(0f)
            while (ins.read(buffer).also { bytesRead = it } != -1) {
                os.write(buffer, 0, bytesRead)
                count += buffer.size
                onProgress?.invoke(count.toFloat() / max)
            }
            onSuccess?.invoke()
            ins.close()
            os.close()
        }
    }

    private fun downLoadFile(
        filePath: String,
        fileSize: Long,
        onProgress: ((progress: Float) -> Unit)? = null,
        onSuccess: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ){

        Log.d(TAG, "downLoadFile filePath:${filePath}")
        val file = File(filePath)
        if (file.parentFile?.exists() != true) {
            file.parentFile?.mkdirs()
        }
        Log.d(TAG, "downLoadFile===================================1")
        if (!file.exists()) {
            Log.d(TAG, "downLoadFile===================================2")
            if (!file.createNewFile()) {
                onError?.invoke(FileNotFoundException("can not create file"))
                return
            }
        }
        Log.d(TAG, "downLoadFile===================================3")

        // 开始下载
        val ins: InputStream = socket.getInputStream()
        val os = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var bytesRead: Int

        var count = 0
        onProgress?.invoke(0f)
        Log.d(TAG, "downLoadFile===================================4")
        while (ins.read(buffer).also { bytesRead = it } != -1) {
            Log.d(TAG, "downLoadFile===================================5 count:${count}")
            os.write(buffer, 0, bytesRead)
            count += buffer.size
            onProgress?.invoke(count.toFloat() / fileSize)
        }
        Log.d(TAG, "downLoadFile===================================6")
        onSuccess?.invoke()
        ins.close()
        os.close()
    }

    companion object {
        private const val TAG = "FileTransferRequest"
        fun create(ip: String, port: Int) = FileTransferRequest(ip, port)
    }
}
