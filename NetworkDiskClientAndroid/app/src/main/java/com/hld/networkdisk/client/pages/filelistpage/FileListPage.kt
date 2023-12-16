package com.hld.networkdisk.client.pages.filelistpage

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.hld.networkdisk.client.widgets.ActionTopBar

@Composable
fun FileListPage(
    viewModel: FileListViewModel = hiltViewModel(), onNavigateToFileList: (String) -> Unit
) {
    val activity = (LocalContext.current as ComponentActivity)
    val mainViewModel: MainViewModel = hiltViewModel(activity)

    LaunchedEffect(viewModel) {
        viewModel.mainViewModel = mainViewModel
    }

    val listFiles = viewModel.doQueryList().collectAsState(initial = emptyList())

    Scaffold(topBar = {
        ActionTopBar(viewModel.getDirName())
    }) {
        Column(modifier = Modifier.padding(it)) {
            val appName = LocalContext.current.getString(R.string.app_name)
            Text(text = "${appName}${viewModel.filePath}")
            LazyColumn(content = {
                items(listFiles.value.size) { index ->
                    val item = listFiles.value[index]
                    FileListItemPage(item = item) {
                        if (item.isDirectory) {
                            onNavigateToFileList(item.absolutePath)
                        } else {

                        }
                    }
                }
            })
        }
    }
}
