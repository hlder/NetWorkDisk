package com.hld.networkdisk.client.widgets

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun ColorWidget(@ColorRes resId:Int): Color {
    val c = LocalContext.current.getColor(resId)
    return Color(c)
}