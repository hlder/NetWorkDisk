package com.hld.networkdisk.client.pages.filelistpage

import android.app.Application
import android.graphics.Bitmap
import android.util.LruCache
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.hld.networkdisk.client.MainViewModel
import com.hld.networkdisk.client.MainViewModel.Companion.DOWNLOAD_STATUS_INIT
import com.hld.networkdisk.client.R
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.commons.Constants
import com.hld.networkdisk.client.commons.base64ToBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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

    // 选择状态，false表示点击状态，true表示选择状态
    val selectedStatus = MutableLiveData(false)

    // 选择状态时，选中的item列表
    val selectedList = mutableListOf<FileBean>()

    // 选中的size
    val selectedListSize = MutableLiveData(0)

    // 是否显示正在加载
    val isShowLoading = MutableLiveData(true)

    // 是否显示正在加载框
    val isShowDialogLoading = MutableLiveData(false)

    var listFiles: MutableList<FileBean>? = null

    private val mapPreviewImage = mutableMapOf<String, String>()

    private val mapDownLoadLiveData = mutableMapOf<String, MutableLiveData<Float>>()

    private val lruCache = object : LruCache<String, Bitmap>(200 * 1024 * 1024) {
        override fun sizeOf(key: String?, value: Bitmap?): Int {
            return value?.byteCount ?: 0
        }
    }

    /**
     * 选择状态时。添加或移除
     */
    fun addOrRemoveSelected(item: FileBean) {
        if (selectedList.contains(item)) {
            selectedList.remove(item)
        } else {
            selectedList.add(item)
        }
        selectedListSize.value = selectedList.size
    }

    fun getBitmapFromCache(base64Image: String): Bitmap? {
        var cacheBitmap = lruCache.get(base64Image)
        if (cacheBitmap == null) {
            cacheBitmap = base64Image.base64ToBitmap()
            cacheBitmap?.let {
                lruCache.put(base64Image, cacheBitmap)
            }
        }
        return cacheBitmap
    }

    /**
     * 查询是否本地有该文件
     */
    fun queryIsLocalHave(fileBean: FileBean) = flow {
        val baseFilePath = Constants.baseFilePath(getApplication())
        val file = File(baseFilePath + fileBean.absolutePath)
        emit(file.exists() && file.length() == fileBean.fileLength)
    }.flowOn(Dispatchers.IO)

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
        val base64: String?
        if (mapPreviewImage.contains(filePath)) {
            base64 = mapPreviewImage[filePath]
        } else {
            base64 = mainViewModel?.queryPreview(filePath)
            base64?.let { mapPreviewImage[filePath] = base64 }
        }
        emit(base64)
    }.flowOn(Dispatchers.IO)

    /**
     * 查询列表
     */
    fun doQueryListFiles() = viewModelScope.launch(Dispatchers.IO) {
        isShowLoading.postValue(true)
        listFiles = mutableListOf()
        mainViewModel?.queryFileList("/$filePath")?.let {
            listFiles?.addAll(it)
        }
        isShowLoading.postValue(false)
    }

    /**
     * 删除选中的文件
     */
    fun doDeleteFiles() = viewModelScope.launch(Dispatchers.IO) {
        isShowDialogLoading.postValue(true)
        val baseFilePath = Constants.baseFilePath(getApplication())
        val listFilePath: List<String> = selectedList.map {
            deleteFileOrDir(File(baseFilePath + it.absolutePath))
            it.absolutePath
        }
        mainViewModel?.deleteFiles(listFilePath)
        listFiles?.removeAll(selectedList)
        selectedList.clear()
        selectedListSize.postValue(0)
        selectedStatus.postValue(false)
        isShowDialogLoading.postValue(false)
        withContext(Dispatchers.Main) {
            Toast.makeText(getApplication(), getApplication<Application>().getString(R.string.toast_delete_files_success), Toast.LENGTH_SHORT).show()
        }
    }

    // 递归删除文件和文件夹
    private fun deleteFileOrDir(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                deleteFileOrDir(it)
            }
        }
        if (file.exists()) {
            file.delete()
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
    fun downLoad(fileBean: FileBean) = mainViewModel?.downLoadFile(fileBean.absolutePath, fileBean.fileLength, getDownloadLiveData(fileBean))

    companion object {
        private const val TAG = "FileListViewModel"

    }
}