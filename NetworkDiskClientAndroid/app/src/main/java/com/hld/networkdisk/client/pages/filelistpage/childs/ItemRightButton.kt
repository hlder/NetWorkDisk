package com.hld.networkdisk.client.pages.filelistpage.childs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.commons.Constants
import com.hld.networkdisk.client.pages.filelistpage.FileListViewModel
import com.hld.networkdisk.client.utils.FileUtils

@Composable
fun ItemRightButton(viewModel: FileListViewModel = hiltViewModel(), item: FileBean) {
    Box(modifier = Modifier.width(80.dp)) {
        if (viewModel.selectedStatus.observeAsState().value == true) { // 属于选择状态，显示radioButton
            ItemRadioButton(item = item)
        } else { // 显示正常状态
            if (!item.isDirectory) {
                val status = viewModel.getDownloadLiveData(item).observeAsState().value ?: 0f
                if (status in 0.0..100.0) { //正在下载
                    ItemRightInDownloading(status)
                } else { // 不在下载
                    val isLocalHave = viewModel.queryIsLocalHave(item).collectAsState(initial = false)
                    val context = LocalContext.current
                    ItemRightShowButton(isLocalHave.value, downloadClick = {
                        viewModel.downLoad(item)
                    }, openClick = {
                        FileUtils.openFile(
                            context, Constants.baseFilePath(context) + item.absolutePath
                        )
                    })
                }
            }
        }
    }
}

@Composable
fun BoxScope.ItemRightInDownloading(status: Float) {
    Box(modifier = Modifier.align(Alignment.Center)) {
        Text(
            modifier = Modifier.align(Alignment.Center), fontSize = 12.sp, text = "${(status * 100).toInt()}%"
        )
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center), progress = status, color = Color.Red, strokeWidth = 2.dp, trackColor = Color.Blue
        )
    }
}

@Composable
fun BoxScope.ItemRightShowButton(isLocalHave: Boolean, downloadClick: () -> Unit, openClick: () -> Unit) {
    Box(modifier = Modifier.align(Alignment.Center)) {
        if (isLocalHave) {// 本地存在，直接打开
            Button(onClick = openClick) {
                Text(text = "打开")
            }
        } else { // 本地不存在该文件，需要下载
            Button(onClick = downloadClick) {
                Text(text = "下载")
            }
        }
    }
}

@Composable
fun BoxScope.ItemRadioButton(viewModel: FileListViewModel = hiltViewModel(), item: FileBean) {
    viewModel.selectedListSize.observeAsState().value // 监听选中的size变化
    val selected = viewModel.selectedList.contains(item)
    RadioButton(modifier = Modifier.align(Alignment.Center), selected = selected, onClick = {
        viewModel.addOrRemoveSelected(item)
    })
}

@Preview
@Composable
fun PreviewCheckBox() {
    Column {
        RadioButton(selected = true, onClick = { /*TODO*/ })
        RadioButton(selected = false, onClick = { /*TODO*/ })
    }

}

@Preview
@Composable
private fun PreviewItemRightButtonInDownloading() {
    Box(
        modifier = Modifier.width(80.dp)
    ) {
        ItemRightInDownloading(0.4f)
    }
}

@Preview
@Composable
private fun PreviewItemRightButtonShowButton() {
    Box(
        modifier = Modifier.width(80.dp)
    ) {
        ItemRightShowButton(true, downloadClick = {}, openClick = {})
    }
}