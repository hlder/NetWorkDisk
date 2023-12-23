package com.hld.networkdisk.client.widgets

import androidx.annotation.ColorRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hld.networkdisk.client.R

@Composable
fun ActionTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    menuContent: (@Composable ColumnScope.(MutableState<Boolean>) -> Unit)? = null
) {
    val expandedState = remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center,
                color = getColor(colorId = R.color.action_top_bar_text_color),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable(onClick = onBackClick)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "back",
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }
        },
        actions = {
            if (menuContent != null) {
                Box(modifier = Modifier.padding(start = 10.dp)) {
                    IconButton(onClick = {
                        expandedState.value = !expandedState.value
                    }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More...")
                        if (expandedState.value)
                            MenuView(expandedState, menuContent)
                    }
                }
            }
        }
    )
}

@Composable
fun MenuView(
    expandedState: MutableState<Boolean>,
    menuContent: @Composable ColumnScope.(MutableState<Boolean>) -> Unit
) {
    DropdownMenu(
        expanded = expandedState.value,
        onDismissRequest = {
            expandedState.value = false
        }
    ) {
        menuContent(expandedState)
    }
}

@Composable
fun MenuItem(imageVector: ImageVector, text: String, onClick: () -> Unit) {
    DropdownMenuItem(
        onClick = onClick,
        leadingIcon = {
            Icon(imageVector = imageVector, contentDescription = text)
        },
        text = {
            Text(text = text, fontSize = 12.sp, modifier = Modifier.padding(end = 5.dp))
        }
    )
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