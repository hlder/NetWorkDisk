package com.hld.networkdisk.client.pages.filelistpage.childs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hld.networkdisk.client.beans.FileBean
import com.hld.networkdisk.client.pages.filelistpage.FileListViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListItemPage(
    viewModel: FileListViewModel = hiltViewModel(),
    item: FileBean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    Column(modifier = Modifier.combinedClickable(
        onClick = onClick,
        onLongClick = onLongClick
    )) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            PreviewImage(item = item) // 左边的icon预览
            ItemCenter(modifier = Modifier.weight(1f), item)
            ItemRightButton(viewModel, item = item)
        }
    }
}
