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
        messageRequest = MessageRequest(ip, Constants.SERVER_PORT_MESSAGE)
        previewRequest = PreviewRequest(ip, Constants.SERVER_PORT_PREVIEW_IMAGE)
        viewModelScope.launch {
            messageRequest.start()
        }
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
    suspend fun queryFileList(filePath: String): List<FileBean>? =
        messageRequest.queryFileList(filePath)

    /**
     * 查询预览
     */
    suspend fun queryPreview(filePath: String) = previewRequest.queryPreviewImage(filePath)

    /**
     * 下载文件
     */
    fun downLoadFile(
        filePath: String,
        fileSize: Long,
        inLiveData: MutableLiveData<Float>?
    ): LiveData<Float> {
        var liveData: MutableLiveData<Float>? = mapDownloadLiveData[filePath]
        if (liveData != null) {
            return liveData
        }
        liveData = inLiveData ?: MutableLiveData(0.0f)
        mapDownloadLiveData[filePath] = liveData
        Log.i(TAG, "========================================1")

        viewModelScope.launch(Dispatchers.IO) {
            Log.i(TAG, "========================================2")
            FileTransferRequest.create(ip, portFile).doDownloadFile(
                context = getApplication(),
                yunFilePath = filePath,
                fileSize = fileSize,
                onProgress = {
                    Log.i(TAG, "========================================4")
                    liveData.postValue(it)
                },
                onSuccess = {
                    Log.i(TAG, "========================================5")
                    liveData.postValue(ON_DOWNLOAD_SUCCESS)
                    mapDownloadLiveData.remove(filePath)
                },
                onError = {
                    Log.i(TAG, "========================================6")
                    liveData.postValue(ON_DOWNLOAD_ERROR)
                    mapDownloadLiveData.remove(filePath)
                }
            )
            Log.i(TAG, "========================================3")
        }
        return liveData
    }

    companion object {
        private const val TAG = "MainViewModel"
        const val DOWNLOAD_STATUS_INIT = -999f
        const val ON_DOWNLOAD_SUCCESS = -10f
        const val ON_DOWNLOAD_ERROR = -5f
    }
}
