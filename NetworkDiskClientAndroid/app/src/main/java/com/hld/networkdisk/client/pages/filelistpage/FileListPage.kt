package com.hld.networkdisk.client.pages.filelistpage

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import com.hld.networkdisk.client.MainViewModel

@Composable
fun FileListPage(
    viewModel: FileListViewModel = hiltViewModel(),
    onNavigateToFileList: (String) -> Unit
) {
    val activity = (LocalContext.current as ComponentActivity)
    val mainViewModel: MainViewModel = hiltViewModel(activity)

    LaunchedEffect(viewModel) {
        viewModel.mainViewModel = mainViewModel
    }

    val listFiles = viewModel.doQueryList().collectAsState(initial = emptyList())

    Scaffold {
        LazyColumn(modifier = Modifier.padding(it), content = {
            items(listFiles.value.size) {
                val item = listFiles.value[it]
                Text(text = "iiiiii:${item.name}")
            }
        })
    }


}
