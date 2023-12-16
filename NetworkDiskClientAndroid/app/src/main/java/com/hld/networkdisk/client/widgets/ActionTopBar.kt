package com.hld.networkdisk.client.widgets

import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hld.networkdisk.client.R


@Composable
fun ActionTopBar(title: String) {
    Column {
        Box(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .background(color = getColor(colorId = R.color.action_top_bar_bg_color))
        ) {
            Text(
                text = title,
                modifier = Modifier.align(Alignment.Center),
                color = getColor(colorId = R.color.action_top_bar_text_color),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Divider(color = getColor(colorId = R.color.divider_color))
    }
}


@Composable
fun getColor(@ColorRes colorId: Int): Color {
    val colorInt = LocalContext.current.getColor(colorId)
    val a = android.graphics.Color.alpha(colorInt)
    val r = android.graphics.Color.red(colorInt)
    val g = android.graphics.Color.green(colorInt)
    val b = android.graphics.Color.blue(colorInt)
    return Color(r, g, b, a)
}