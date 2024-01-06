package com.hld.networkdisk.client.pages.filelistpage.childs

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hld.networkdisk.client.R
import com.hld.networkdisk.client.pages.filelistpage.FileListViewModel
import com.hld.networkdisk.client.utils.DialogFactory

@Composable
fun FileListPageFooterActionButton() {
    val context = LocalContext.current
    val viewModel = hiltViewModel<FileListViewModel>()
    val size = viewModel.selectedListSize.observeAsState().value
    Row(modifier = Modifier.background(color = Color.White)) {
        FooterActionItem(R.mipmap.icon_file_list_action_copy, text = stringResource(id = R.string.file_list_footer_action_button_copy)) {}
        FooterActionItem(R.mipmap.icon_file_list_action_move, text = stringResource(id = R.string.file_list_footer_action_button_move)) {}
        if (size == 1) {
            FooterActionItem(R.mipmap.icon_file_list_action_rename, text = stringResource(id = R.string.file_list_footer_action_button_rename)) {}
        } else {
            FooterActionItem(R.mipmap.icon_file_list_action_rename, text = stringResource(id = R.string.file_list_footer_action_button_rename))
        }
        if (size == 0) {
            FooterActionItem(R.mipmap.icon_file_list_action_delete, text = stringResource(id = R.string.file_list_footer_action_button_delete))
        } else {
            FooterActionItem(R.mipmap.icon_file_list_action_delete, text = stringResource(id = R.string.file_list_footer_action_button_delete)) {
                DialogFactory.showTwoButtonAlert(context = context, message = context.getString(R.string.dialog_delete_files), onSureClick = {
                    viewModel.doDeleteFiles()
                })
            }
        }
    }
}

@Composable
private fun RowScope.FooterActionItem(@DrawableRes resId: Int, text: String, onClick: (() -> Unit)? = null) {
    val color = if (onClick != null) {
        colorResource(id = R.color.footer_action_text_color)
    } else {
        colorResource(id = R.color.footer_action_text_color_cannot_click)
    }
    Box(
        modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .clickable(onClick = onClick ?: {})
    ) {
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            Image(
                painter = painterResource(id = resId), contentDescription = "", modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(25.dp)
                    .height(25.dp)
            )
            Text(
                text = text, color = color, modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 5.dp), fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_XL, showSystemUi = true)
@Composable
fun TestPreview() {
    Box {
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            FileListPageFooterActionButton()
        }
    }
}