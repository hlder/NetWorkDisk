package com.hld.networkdisk.client

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.gson.Gson
import com.hld.networkdisk.client.ui.ComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.doStart("127.0.0.1")
        setContent {
//            MaterialTheme{
//                ComposeApp()
//            }
            ComposeTheme {
                ComposeApp()
            }
        }
    }
}
