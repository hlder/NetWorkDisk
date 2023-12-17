package com.hld.networkdisk.client.pages.filelistpage

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.hld.networkdisk.client.MainViewModel
import com.hld.networkdisk.client.R
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.commons.Constants
import com.hld.networkdisk.client.commons.base64ToBitmap
import com.hld.networkdisk.client.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileListViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    val filePath: String = savedStateHandle["filePath"] ?: ""

    var mainViewModel: MainViewModel? = null

    private var listData: List<FileBean>? = null

    private val mapPreviewImage = mutableMapOf<String, String>()

    private val mapChildFile = mutableMapOf<String, File>()

    init {
        initMap()
    }

    private fun initMap() {
        val baseFilePath = Constants.baseFilePath(getApplication())
        val path = "${baseFilePath}/${filePath}"
        val file = File(path)
        if (file.exists()) {
            file.listFiles()?.forEach { item ->
                val key = item.absolutePath.replace(baseFilePath, "")
                mapChildFile[key] = item
            }
        }
    }

    /**
     * 查询是否本地有该文件
     */
    fun queryIsLocalHave(fileBean: FileBean): Boolean {
        val localFile = mapChildFile[fileBean.absolutePath]
        return if (localFile == null) {
            false
        } else localFile.length() == fileBean.fileLength
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

    fun downLoad(fileBean: FileBean) {
        viewModelScope.launch {
            mainViewModel?.downLoadFile(
                fileBean.absolutePath,
                fileBean.fileLength,
                onProgress = {
                    Log.d(TAG, "=========onDownLoadProgress:${fileBean.absolutePath}   progress:$it")
                },
                onSuccess = {
                    Log.d(TAG, "=========下载成功:${fileBean.absolutePath}")
                },
                onError = {
                    Log.d(TAG, "=========下载错误${fileBean.absolutePath}")
                }
            )
        }
    }

    companion object {
        private const val TAG = "FileListViewModel"
    }
}