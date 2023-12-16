package com.hld.networkdisk.client.pages.filelistpage

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.hld.networkdisk.client.MainViewModel
import com.hld.networkdisk.client.R
import com.hld.networkdisk.client.beans.FileBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
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

    fun queryPreviewImage(filePath: String) = flow {
        if (mapPreviewImage.contains(filePath)) {
            emit(mapPreviewImage[filePath]!!)
        } else {
            Log.d(TAG, "===============queryPreviewImage1  filePath:${filePath}")
            val imgBase64 = mainViewModel?.queryPreview(filePath)
            imgBase64?.let {
                if(imgBase64.length>101){
                    Log.d(TAG, "===============start${imgBase64.substring(0,100)}")
                    Log.d(TAG, "filePath:${filePath}===============end${imgBase64.substring(imgBase64.length-100,imgBase64.length-1)}")
                }
                mapPreviewImage[filePath] = imgBase64
                emit(imgBase64)
            }
        }
    }

    // 查询列表
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

    companion object{
        private const val TAG = "FileListViewModel"
    }
}