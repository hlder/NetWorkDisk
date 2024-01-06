package com.hld.networkdisk.client.pages.filelistpage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.hld.networkdisk.client.MainViewModel
import com.hld.networkdisk.client.R
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.extension.activityViewModels
import com.hld.networkdisk.client.pages.filelistpage.childs.FileListItemPage
import com.hld.networkdisk.client.pages.filelistpage.childs.FileListPageFooterActionButton
import com.hld.networkdisk.client.widgets.ActionTopBar
import com.hld.networkdisk.client.widgets.MenuItem

@Composable
fun FileListPage(
    onNavigateToFileList: (String) -> Unit, onBackClick: () -> Unit
) {
    val viewModel: FileListViewModel = hiltViewModel()
    val mainViewModel by activityViewModels<MainViewModel>()

    LaunchedEffect(viewModel) {
        viewModel.mainViewModel = mainViewModel
    }
    Scaffold(topBar = {
        ActionTopBar(viewModel.getDirName(), onBackClick = onBackClick, menuContent = {
            MenuItem(Icons.Filled.AddCircle, "上传文件") {
                it.value = false
                mainViewModel.startFileSelect(viewModel.filePath)
            }
        })
    }) {
        FileListBody(Modifier.padding(it), onNavigateToFileList)
    }

    // 全屏覆盖在界面上正在加载等待框
    if (viewModel.isShowDialogLoading.observeAsState().value == true) {
        BackHandler(true) {} // 拦截返回键
        Box(modifier = Modifier.fillMaxSize().background(color = colorResource(id = R.color.bg_dialog_loading)).clickable {}) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun FileListBody(modifier: Modifier, onNavigateToFileList: (String) -> Unit, viewModel: FileListViewModel = hiltViewModel()) {
    val mainViewModel by activityViewModels<MainViewModel>()
    LaunchedEffect(viewModel) {
        viewModel.mainViewModel = mainViewModel
        viewModel.doQueryListFiles()
    }

    Column(modifier = modifier) {
        val appName = LocalContext.current.getString(R.string.app_name)
        Text(text = "${appName}${viewModel.filePath}")

        Box(modifier = Modifier.weight(1f, true)) {
            if (viewModel.isShowLoading.observeAsState().value == true) { // 显示正在加载
                FileListLoading()
            } else { // 显示数据
                val listFiles = viewModel.listFiles
                if (listFiles != null) {
                    if (listFiles.isEmpty()) { // 无数据
                        FileListNoData()
                    } else { // 有数据
                        if (viewModel.selectedStatus.observeAsState().value == true) { // 如果是选择状态，则拦截返回键
                            BackHandler(true) { // 拦截返回键
                                viewModel.selectedStatus.value = false
                            }
                        }
                        ShowFileList(listFiles, onNavigateToFileList)
                    }
                }
            }
        }
        // 选择状态
        if (viewModel.selectedStatus.observeAsState().value == true) {
            FileListPageFooterActionButton()
        }
    }

}

@Composable
private fun FileListLoading() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun FileListNoData() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Text(text = "无数据")
            Button(onClick = {

            }) {
                Text(text = "点击上传")
            }
        }
    }
}

@Composable
private fun ShowFileList(listFiles: List<FileBean>, onNavigateToFileList: (String) -> Unit, viewModel: FileListViewModel = hiltViewModel()) {
    LazyColumn(content = {
        items(listFiles.size) { index ->
            val item = listFiles[index]
            FileListItemPage(item = item, onClick = {
                if (viewModel.selectedStatus.value == true) { // 执行选择
                    viewModel.addOrRemoveSelected(item)
                } else { // 执行跳转
                    if (item.isDirectory) { // 点击跳转文件夹
                        onNavigateToFileList(item.absolutePath)
                    } else { // 点击item

                    }
                }
            }, onLongClick = {
                if (viewModel.selectedStatus.value != true) {
                    viewModel.selectedStatus.value = true
                    viewModel.selectedList.clear()
                    viewModel.selectedListSize.value = 0
                    viewModel.addOrRemoveSelected(item)
                }
            })
        }
    })
}