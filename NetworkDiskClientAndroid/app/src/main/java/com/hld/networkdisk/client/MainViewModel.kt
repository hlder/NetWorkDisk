package com.hld.networkdisk.client

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.beans.MessageTransferFileBean
import com.hld.networkdisk.client.commons.Constants
import com.hld.networkdisk.client.commons.MessageCodes
import com.hld.networkdisk.client.network.FileTransferRequest
import com.hld.networkdisk.client.network.MessageRequest
import com.hld.networkdisk.client.network.PreviewRequest
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private lateinit var messageRequest: MessageRequest
    private lateinit var previewRequest: PreviewRequest
    private var ip: String = ""
    private var portFile: Int = Constants.SERVER_PORT_FILE

    private val gson: Gson = Gson()

    fun doStart(ip: String) {
        this.ip = ip
        messageRequest = MessageRequest(ip, Constants.SERVER_PORT_MESSAGE)
        previewRequest = PreviewRequest(ip, Constants.SERVER_PORT_PREVIEW_IMAGE)
        viewModelScope.launch {
            messageRequest.start()
        }
    }

    /**
     * 查询文件列表
     */
    suspend fun queryFileList(filePath: String): List<FileBean>? =
        messageRequest.queryFileList(filePath)

    /**
     * 查询预览
     */
    suspend fun queryPreview(filePath: String) = previewRequest.queryPreviewImage(filePath)

    /**
     * 上传文件
     */
    suspend fun uploadFile(
        file: File,
        filePath: String,
        onProgress: ((progress: Float) -> Unit)? = null,
        onSuccess: (() -> Unit)? = null,
    ) = withContext(Dispatchers.IO) {
        val fileTransferRequest = FileTransferRequest.create(ip, portFile)
        val bean = MessageTransferFileBean(
            address = fileTransferRequest.socket.localAddress.hostAddress ?: "",
            port = fileTransferRequest.socket.localPort,
            filePath = filePath,
            isClientSendToServer = true,
            fileLength = file.length()
        )
        val resultStr = messageRequest.sendMessage(
            MessageCodes.CODE_CLIENT_SEND_TO_SERVER_FILE,
            gson.toJson(bean)
        )
        fileTransferRequest.uploadFile(
            file = file,
            onProgress = onProgress,
            onSuccess = onSuccess
        )
    }

    /**
     * 下载文件
     */
    suspend fun downLoadFile(
        filePath: String,
        fileSize: Long,
        onProgress: ((progress: Float) -> Unit)? = null,
        onSuccess: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) = withContext(Dispatchers.IO) {
        val fileTransferRequest = FileTransferRequest.create(ip, portFile)
        val bean = MessageTransferFileBean(
            address = fileTransferRequest.socket.localAddress.hostAddress ?: "",
            port = fileTransferRequest.socket.localPort,
            filePath = filePath,
            isClientSendToServer = false,
            fileLength = fileSize
        )
        val resultStr = messageRequest.sendMessage(
            MessageCodes.CODE_CLIENT_RECEIVE_FROM_SERVER_FILE,
            gson.toJson(bean)
        )
        fileTransferRequest.downLoadFile(
            filePath = Constants.baseFilePath(getApplication()) + filePath,
            fileSize = fileSize,
            onProgress = onProgress,
            onSuccess = onSuccess
        )
    }
}