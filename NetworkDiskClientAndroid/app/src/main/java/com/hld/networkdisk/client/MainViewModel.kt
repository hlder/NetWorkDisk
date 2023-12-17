package com.hld.networkdisk.client

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.beans.MessageTransferFileBean
import com.hld.networkdisk.client.commons.Constants
import com.hld.networkdisk.client.commons.MessageCodes
import com.hld.networkdisk.client.network.FileTransferRequest
import com.hld.networkdisk.client.network.MessageRequest
import com.hld.networkdisk.client.network.PreviewRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private lateinit var messageRequest: MessageRequest
    private lateinit var previewRequest: PreviewRequest
    private var ip: String = ""
    private var portFile: Int = Constants.SERVER_PORT_FILE

    private val gson: Gson = Gson()

    val mapDownloadLiveData = mutableMapOf<String, MutableLiveData<Float>>()

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
    fun downLoadFile(
        filePath: String,
        fileSize: Long,
        inLiveData:MutableLiveData<Float>?
    ): LiveData<Float> {
        var liveData: MutableLiveData<Float>? = mapDownloadLiveData[filePath]
        if (liveData != null) {
            return liveData
        }
        liveData = inLiveData ?: MutableLiveData(0.0f)
        mapDownloadLiveData[filePath] = liveData

        viewModelScope.launch(Dispatchers.IO) {
            val fileTransferRequest = FileTransferRequest.create(ip, portFile)

            if(!fileTransferRequest.socket.isConnected){
                return@launch
            }

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
                onProgress = {
                    liveData.postValue(it)
                },
                onSuccess = {
                    liveData.postValue(ON_DOWNLOAD_SUCCESS)
                    mapDownloadLiveData.remove(filePath)
                },
                onError = {
                    liveData.postValue(ON_DOWNLOAD_ERROR)
                    mapDownloadLiveData.remove(filePath)
                }
            )
        }
        return liveData
    }

    companion object {
        const val DOWNLOAD_STATUS_INIT = -999f
        const val ON_DOWNLOAD_SUCCESS = -10f
        const val ON_DOWNLOAD_ERROR = -5f
    }
}
