package com.hld.networkdisk.server.networkImpls

import androidx.activity.ComponentActivity
import com.google.gson.Gson
import com.hld.networkdisk.server.beans.MessageTransferFileBean
import com.hld.networkdisk.server.filemanager.FileManager
import com.hld.networkdisk.server.networkapis.FileTransferApi
import java.io.InputStream
import java.io.OutputStream

class FileTransferApiImpl(private val activity: ComponentActivity) : FileTransferApi {
    private val gson = Gson()
    private val fileManager: FileManager = FileManager(activity)
    override fun onRequest(request: String, inputStream: InputStream, outputStream: OutputStream) {
        val bean = gson.fromJson(request, MessageTransferFileBean::class.java)
        if (bean.isClientSendToServer) { // 收
            fileManager.receiveFileFromStream(inputStream, bean)
        } else { // 发
            fileManager.sendFileWithStream(outputStream, bean)
        }
    }
}