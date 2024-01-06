package com.hld.networkdisk.client.pages.filelistpage.childs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hld.networkdisk.client.R
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.commons.suffixIsImg
import com.hld.networkdisk.client.pages.filelistpage.FileListViewModel

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

        val isLocalHave = viewModel.queryIsLocalHave(item).collectAsState(initial = false)
        if (!item.isDirectory && !isLocalHave.value) {
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