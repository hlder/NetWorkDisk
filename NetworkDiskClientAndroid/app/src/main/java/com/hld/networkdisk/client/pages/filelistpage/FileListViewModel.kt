package com.hld.networkdisk.client.pages.filelistpage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hld.networkdisk.client.MainViewModel
import com.hld.networkdisk.client.beans.FileBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class FileListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val filePath: String = savedStateHandle["filePath"] ?: ""

    var mainViewModel: MainViewModel? = null

    private val gson = Gson()

    private var listData: List<FileBean>? = null

    // 查询列表
    fun doQueryList() = flow {
        var list = listData
        if (list != null) {
            this.emit(list)
        } else {
            list = mainViewModel?.queryFileList(filePath)
            listData = list
            list?.let { emit(list) }
        }
    }
}