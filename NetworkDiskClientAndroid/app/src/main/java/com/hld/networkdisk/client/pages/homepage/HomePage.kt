package com.hld.networkdisk.client.pages.homepage

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.hld.networkdisk.client.MainViewModel

@Composable
fun HomePage(viewModel: HomeViewModel = hiltViewModel(), onNavigateToFileList: () -> Unit) {
    val activity = (LocalContext.current as ComponentActivity)
    val mainViewModel: MainViewModel = hiltViewModel(activity)
    LaunchedEffect(viewModel){
        mainViewModel.doStart("127.0.0.1")
    }
    Scaffold {
        Column(modifier = Modifier.padding(it)) {
            Button(onClick = {
                onNavigateToFileList()
            }) {
                Text(text = "点击")
            }
        }
    }
}