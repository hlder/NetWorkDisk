package com.hld.networkdisk.client.pages.filelistpage.childs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hld.networkdisk.client.R
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.utils.DateFormatUtil
import com.hld.networkdisk.client.utils.FileUtils
import com.hld.networkdisk.client.widgets.ColorWidget

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