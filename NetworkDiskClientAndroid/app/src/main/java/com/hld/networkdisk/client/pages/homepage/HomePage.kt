package com.hld.networkdisk.client.pages.homepage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hld.networkdisk.client.widgets.ActionTopBar

@Composable
fun HomePage(viewModel: HomeViewModel = hiltViewModel(), onNavigateToFileList: () -> Unit) {
    Scaffold(
        topBar = { ActionTopBar("home") }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Button(onClick = {
                onNavigateToFileList()
            }) {
                Text(text = "点击")
            }

            Box {
                CircularProgressIndicator(
                    progress = 0.4f,
                    color = Color.Red,
                    strokeWidth = 2.dp,
                    trackColor = Color.Blue
                )
                Text(
                    text = "10%",
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

        }
    }
}