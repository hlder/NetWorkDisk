package com.hld.networkdisk.client.pages.selectfiledirpage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hld.networkdisk.client.MainViewModel
import com.hld.networkdisk.client.beans.FileBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectFileDirViewModel @Inject constructor() : ViewModel() {
    var mainViewModel: MainViewModel? = null

    // 是否显示正在加载
    val isShowLoading = MutableLiveData(true)

    var listFiles: MutableList<FileBean>? = null

    /**
     * 查询列表
     */
    fun doQueryListFileDirs(filePath: String) = viewModelScope.launch(Dispatchers.IO) {
        isShowLoading.postValue(true)
        listFiles = mutableListOf()
        mainViewModel?.queryFileList("/$filePath")?.let {
            listFiles?.addAll(it)
        }
        isShowLoading.postValue(false)
    }


}