package com.hld.networkdisk.client.pages.selectfiledirpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hld.networkdisk.client.MainViewModel
import com.hld.networkdisk.client.R
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.extension.activityViewModels
import com.hld.networkdisk.client.pages.filelistpage.childs.FileListItemPage

@Composable
fun SelectFileDirPage(baseFilePath: String) {
    val viewModel: SelectFileDirViewModel = hiltViewModel()
    val mainViewModel by activityViewModels<MainViewModel>()
    LaunchedEffect(viewModel) {
        viewModel.mainViewModel = mainViewModel
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bg_dialog_loading))
    ) {
        if (viewModel.isShowLoading.observeAsState().value == true) { // 正在加载
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else { // 显示数据
            Box(modifier = Modifier.padding(top = 100.dp, bottom = 100.dp, start = 20.dp, end = 20.dp)) {

            }
        }
        viewModel.listFiles
    }

}

@Composable
fun showFileDirList(listFiles: List<FileBean>) {
    LazyColumn(content = {
        items(listFiles.size) { index ->
            val item = listFiles[index]
            FileListItemPage(item = item, onClick = {

            })
        }
    })
}