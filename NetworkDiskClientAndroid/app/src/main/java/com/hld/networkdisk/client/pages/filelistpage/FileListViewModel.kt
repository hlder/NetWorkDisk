package com.hld.networkdisk.client.pages.filelistpage

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.hld.networkdisk.client.MainViewModel
import com.hld.networkdisk.client.MainViewModel.Companion.DOWNLOAD_STATUS_INIT
import com.hld.networkdisk.client.R
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.commons.Constants
import com.hld.networkdisk.client.commons.base64ToBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileListViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    val filePath: String = savedStateHandle["filePath"] ?: ""

    var mainViewModel: MainViewModel? = null
        set(value) {
            field = value
        }

    private var listData: List<FileBean>? = null

    private val mapPreviewImage = mutableMapOf<String, String>()

    private val mapDownLoadLiveData = mutableMapOf<String, MutableLiveData<Float>>()

    /**
     * 查询是否本地有该文件
     */
    fun queryIsLocalHave(fileBean: FileBean) = flow {
        val baseFilePath = Constants.baseFilePath(getApplication())
        val file = File(baseFilePath + fileBean.absolutePath)
        emit(file.exists() && file.length() == fileBean.fileLength)
    }

    /**
     * 获取当前文件夹的名字
     */
    fun getDirName(): String {
        val arr = filePath.split("/")
        var name = ""
        if (arr.isNotEmpty()) {
            name = arr[arr.size - 1]
        }
        if (name.isEmpty()) {
            name = getApplication<Application>().getString(R.string.app_name)
        }
        return name
    }

    /**
     * 查询文件的预览图片
     */
    fun queryPreviewImage(filePath: String) = flow {
        if (mapPreviewImage.contains(filePath)) {
            emit(mapPreviewImage[filePath]!!)
        } else {
            Log.d(TAG, "===============queryPreviewImage1  filePath:${filePath}")
            val imgBase64 = mainViewModel?.queryPreview(filePath)
            imgBase64?.let {
                if (imgBase64.length > 101) {
                    Log.d(TAG, "===============start${imgBase64.substring(0, 100)}")
                    Log.d(
                        TAG, "filePath:${filePath}===============end${
                            imgBase64.substring(
                                imgBase64.length - 100, imgBase64.length - 1
                            )
                        }"
                    )
                }
                mapPreviewImage[filePath] = imgBase64
                emit(imgBase64)
            }
        }
    }

    /**
     * base64转bitmap
     */
    fun base64ToBitmap(base64: String) = flow {
        emit(base64.base64ToBitmap())
    }

    /**
     * 查询列表
     */
    fun doQueryList() = flow {
        var list = listData
        if (list != null) {
            this.emit(list)
        } else {
            list = mainViewModel?.queryFileList("/$filePath")
            listData = list
            list?.let {
                emit(list)
            }
        }
    }

    /**
     * 获取下载状态的liveData
     */
    fun getDownloadLiveData(fileBean: FileBean): MutableLiveData<Float> {
        val filePath = fileBean.absolutePath
        var ld = mapDownLoadLiveData[filePath]
        if (ld == null) {
            ld = mainViewModel?.mapDownloadLiveData?.get(filePath)
        }
        if (ld == null) {
            ld = MutableLiveData(DOWNLOAD_STATUS_INIT)
        }
        mapDownLoadLiveData[filePath] = ld
        return ld
    }

    /**
     * 执行下载动作
     */
    fun downLoad(fileBean: FileBean) {
        val liveData = getDownloadLiveData(fileBean)
        mainViewModel?.downLoadFile(
            fileBean.absolutePath,
            fileBean.fileLength,
            liveData
        )
    }

    companion object {
        private const val TAG = "FileListViewModel"

    }
}