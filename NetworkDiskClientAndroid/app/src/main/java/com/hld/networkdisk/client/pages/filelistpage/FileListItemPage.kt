package com.hld.networkdisk.client.pages.filelistpage

import android.graphics.Bitmap
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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hld.networkdisk.client.R
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.commons.suffixIsImg
import com.hld.networkdisk.client.utils.DateFormatUtil
import com.hld.networkdisk.client.utils.FileUtils

@Composable
fun FileListItemPage(
    viewModel: FileListViewModel = hiltViewModel(),
    item: FileBean,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            var bitmapState: State<Bitmap?>? = null
            val imgBase64 =
                viewModel.queryPreviewImage(item.absolutePath).collectAsState(initial = "")

            if (item.suffix.suffixIsImg() && imgBase64.value.isNotEmpty()) { // 需要去网络获取图片
                bitmapState =
                    viewModel.base64ToBitmap(imgBase64.value).collectAsState(initial = null)
            }
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
            ) {
                bitmapState.let {
                    val bitmap = it?.value
                    if (bitmap == null) {
                        val imagePainter = if (item.isDirectory) {
                            painterResource(id = R.mipmap.icon_folder)
                        } else {
                            painterResource(id = R.mipmap.icon_file_unknown)
                        }
                        Image(
                            painter = imagePainter, contentDescription = "", modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                        )
                    } else {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "",
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                        )
                    }
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
            Column(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
            ) {
                Text(text = item.name, color = Color(0xff070a1b), fontSize = 16.sp)
                Spacer(modifier = Modifier.height(5.dp))
                if (!item.isDirectory) {
                    val modifiedStr = DateFormatUtil.formatTimeStamp(item.lastModified)
                    val fileLength = FileUtils.getFileSizeStr(item.fileLength)
                    Text(text = "$modifiedStr    $fileLength", color = Color.Gray, fontSize = 13.sp)
                }
            }
            if(!item.isDirectory){
                Box(modifier = Modifier.padding(end = 10.dp)) {
                    val isLocalHave = viewModel.queryIsLocalHave(item)
                    if (isLocalHave) {// 本地存在，直接打开
                        Button(onClick = {

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
}