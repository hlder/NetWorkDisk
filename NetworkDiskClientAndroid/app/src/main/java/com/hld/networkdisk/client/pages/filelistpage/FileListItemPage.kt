package com.hld.networkdisk.client.pages.filelistpage

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hld.networkdisk.client.R
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.commons.Constants
import com.hld.networkdisk.client.commons.base64ToBitmap
import com.hld.networkdisk.client.commons.suffixIsImg
import com.hld.networkdisk.client.utils.DateFormatUtil
import com.hld.networkdisk.client.utils.FileUtils
import com.hld.networkdisk.client.widgets.ColorWidget

@Composable
fun FileListItemPage(
    item: FileBean,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            PreviewImage(item = item)
            ItemCenter(modifier = Modifier.weight(1f), item)
            ItemRightButton(item = item)
        }
    }
}

@Composable
fun ItemCenter(modifier: Modifier, item: FileBean){
    Column(
        modifier = Modifier
            .then(modifier)
            .padding(start = 10.dp)
    ) {
        Text(
            text = item.name,
            color = ColorWidget(R.color.text_title_name),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(5.dp))
        if (!item.isDirectory) {
            val modifiedStr = DateFormatUtil.formatTimeStamp(item.lastModified)
            val fileLength = FileUtils.getFileSizeStr(item.fileLength)
            Text(text = "$modifiedStr    $fileLength", color = Color.Gray, fontSize = 13.sp)
        }
    }
}

@Composable
fun ItemRightButton(viewModel: FileListViewModel = hiltViewModel(), item: FileBean) {
    if (!item.isDirectory) {
        val downloadStatus = viewModel.getDownloadLiveData(item).observeAsState()
        val status = downloadStatus.value ?: 0f
        if (status in 0.0..100.0) {//正在下载
            Text(text = "${status * 100}%")
        } else { // 不在下载
            Box(modifier = Modifier.padding(end = 10.dp)) {
                val context = LocalContext.current
                val isLocalHave =
                    viewModel.queryIsLocalHave(item).collectAsState(initial = false)
                if (isLocalHave.value) {// 本地存在，直接打开
                    Button(onClick = {
                        FileUtils.openFile(
                            context,
                            Constants.baseFilePath(context) + item.absolutePath
                        )
                    }) {
                        Text(text = "打开")
                    }
                } else { // 本地不存在该文件，需要下载
                    Button(onClick = {
                        viewModel.downLoad(item)
                    }) {
                        Text(text = "下载")
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewImage(viewModel: FileListViewModel = hiltViewModel(), item: FileBean) {
    Box(
        modifier = Modifier
            .width(50.dp)
            .height(50.dp)
    ) {
        val defImagePainter = if (item.isDirectory) {
            painterResource(id = R.mipmap.icon_folder)
        } else {
            painterResource(id = R.mipmap.icon_file_unknown)
        }
        if (item.suffix.suffixIsImg()) { // 判断是否是image类型
            val base64State = viewModel.queryPreviewImage(item.absolutePath)
                .collectAsState(initial = null).value
            val imageBitmap = base64State?.run { viewModel.getBitmapFromCache(this) }?.asImageBitmap()
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "",
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                )
            } else {
                Image(
                    painter = defImagePainter, contentDescription = "", modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                )
            }
        } else {
            Image(
                painter = defImagePainter, contentDescription = "", modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
            )
        }

        if (!item.isDirectory) {
            Image(
                painter = painterResource(id = R.mipmap.icon_file_right_yun),
                contentDescription = "icon_right",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(20.dp)
                    .height(20.dp)
            )
        }
    }
}