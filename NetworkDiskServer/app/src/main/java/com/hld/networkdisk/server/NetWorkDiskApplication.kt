package com.hld.networkdisk.server

import android.app.Application
import android.util.Log
import com.hld.networkdisk.server.commons.Constants
import com.hld.networkdisk.server.network.ServerSocketManager
import com.hld.networkdisk.server.network.SocketTransfer
import com.hld.networkdisk.server.network.SocketType
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.Socket

@HiltAndroidApp
class NetWorkDiskApplication : Application() {
    override fun onCreate() {
        super.onCreate()


    }
}