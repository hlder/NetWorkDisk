package com.hld.networkdisk.client

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.commons.Constants
import com.hld.networkdisk.client.network.FileTransferRequest
import com.hld.networkdisk.client.network.MessageRequest
import com.hld.networkdisk.client.network.PreviewRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    val mapDownloadLiveData = mutableMapOf<String, MutableLiveData<Float>>()

    val startFileSelect = MutableLiveData(false)

    var selectedYunFilePath: String = ""

    private lateinit var messageRequest: MessageRequest
    private lateinit var previewRequest: PreviewRequest
    var ip: String = ""
    private var portFile: Int = Constants.SERVER_PORT_FILE

    fun doStart(ip: String) {
        this.ip = ip
        messageRequest = MessageRequest(viewModelScope, ip, Constants.SERVER_PORT_MESSAGE)
        previewRequest = PreviewRequest(ip, Constants.SERVER_PORT_PREVIEW_IMAGE)
    }

    /**
     * 启动文件选择
     */
    fun startFileSelect(yunFilePath: String) {
        selectedYunFilePath = yunFilePath
        startFileSelect.postValue(true)
    }

    /**
     * 查询文件列表
     */
    suspend fun queryFileList(filePath: String): List<FileBean>? = messageRequest.queryFileList(filePath)

    /**
     * 查询文件夹列表
     */
    suspend fun queryFileDirList(filePath: String): List<FileBean>? = messageRequest.queryFileDirList(filePath)

    /**
     * 刪除文件
     */
    suspend fun deleteFiles(listFilePath: List<String>) = messageRequest.deleteFiles(listFilePath)

    /**
     * 查询预览
     */
    suspend fun queryPreview(filePath: String) = previewRequest.queryPreviewImage(filePath)

    /**
     * 下载文件
     */
    fun downLoadFile(filePath: String, fileSize: Long, inLiveData: MutableLiveData<Float>?): LiveData<Float> {
        var liveData: MutableLiveData<Float>? = mapDownloadLiveData[filePath]
        if (liveData != null) {
            return liveData
        }
        liveData = inLiveData ?: MutableLiveData(0.0f)
        mapDownloadLiveData[filePath] = liveData

        viewModelScope.launch(Dispatchers.IO) {
            FileTransferRequest.create(ip, portFile).doDownloadFile(context = getApplication(), yunFilePath = filePath, fileSize = fileSize, onProgress = {
                liveData.postValue(it)
            }, onSuccess = {
                liveData.postValue(ON_DOWNLOAD_SUCCESS)
                mapDownloadLiveData.remove(filePath)
            }, onError = {
                liveData.postValue(ON_DOWNLOAD_ERROR)
                mapDownloadLiveData.remove(filePath)
            })
        }
        return liveData
    }

    companion object {
        const val DOWNLOAD_STATUS_INIT = -999f
        const val ON_DOWNLOAD_SUCCESS = -10f
        const val ON_DOWNLOAD_ERROR = -5f
    }
}
