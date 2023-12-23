package com.hld.networkdisk.client.extension

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel

fun ComponentActivity.startActivityForResult(intent:Intent, callBack:(Intent?)->Unit){
    val selectFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == AppCompatActivity.RESULT_OK){
            callBack.invoke(it.data)
        }
    }
    selectFileLauncher.launch(intent)
}

@Composable
inline fun <reified VM : ViewModel> activityViewModels(): Lazy<VM> {
    val activity = LocalContext.current as ComponentActivity
    return remember {
        activity.viewModels()
    }
}