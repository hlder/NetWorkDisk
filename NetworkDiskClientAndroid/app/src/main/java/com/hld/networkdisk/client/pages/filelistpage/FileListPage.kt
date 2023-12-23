package com.hld.networkdisk.client.pages.filelistpage

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.hld.networkdisk.client.MainViewModel
import com.hld.networkdisk.client.R
import com.hld.networkdisk.client.extension.activityViewModels
import com.hld.networkdisk.client.widgets.ActionTopBar
import com.hld.networkdisk.client.widgets.MenuItem

@Composable
fun FileListPage(
    viewModel: FileListViewModel = hiltViewModel(),
    onNavigateToFileList: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val mainViewModel by activityViewModels<MainViewModel>()

    LaunchedEffect(viewModel) {
        viewModel.mainViewModel = mainViewModel
    }

    val listFilesState = viewModel.doQueryList().collectAsState(initial = emptyList())

    Scaffold(topBar = {
        ActionTopBar(viewModel.getDirName(), onBackClick = onBackClick, menuContent = {

            MenuItem(Icons.Filled.AddCircle, "上传文件") {
                it.value = false
                mainViewModel.startFileSelect(viewModel.filePath)
            }
        })
    }) {
        Column(modifier = Modifier.padding(it)) {
            val appName = LocalContext.current.getString(R.string.app_name)
            Text(text = "${appName}${viewModel.filePath}")

            val listFiles = listFilesState.value
            if (listFiles != null) {
                if (listFiles.isEmpty()) { // 无数据
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column {
                            Text(text = "无数据")
                            Button(onClick = {

                            }) {
                                Text(text = "点击上传")
                            }
                        }
                    }
                } else { // 有数据
                    LazyColumn(content = {
                        items(listFiles.size) { index ->
                            val item = listFiles[index]
                            FileListItemPage(item = item) {
                                if (item.isDirectory) { // 点击跳转文件夹
                                    onNavigateToFileList(item.absolutePath)
                                } else { // 点击item

                                }
                            }
                        }
                    })
                }
            }
        }
    }
}
