package com.hld.networkdisk.client.servers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.hld.networkdisk.client.R
import com.hld.networkdisk.client.UploadFileInterface
import com.hld.networkdisk.client.commons.Constants.SERVER_PORT_FILE
import com.hld.networkdisk.client.network.FileTransferRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStream


class UploadFileServer : LifecycleService() {

    private val listFlow = mutableListOf<StateFlow<Float>>()

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return UploadFileInterface.Default().asBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val ip = intent.getStringExtra("ip")
            val filePath = intent.getStringExtra("filePath")
            val fileLength = intent.getLongExtra("fileLength", 0)

            val inputStream: InputStream? = intent.data?.let { uri ->
                contentResolver.openInputStream(uri)
            }
            if (ip != null && fileLength > 0 && filePath != null && inputStream != null) {
                startUploadFile(ip, fileLength, filePath, inputStream)
            }
        }

        showNotify()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun showNotify(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "description"
            // 你可以在这里设置更多的通知频道属性，如声音、振动等。
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)

            // 设置通知的其他属性，如标题、内容、图标等。
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentTitle("显示标题")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentText("显示内容.aaaaaaabbbbbbbb")
                .setWhen(System.currentTimeMillis())
                .build()
            startForeground(FOREGROUND_ID, notification)
        }
    }

    private fun startUploadFile(
        ip: String,
        fileLength: Long,
        filePath: String,
        inputStream: InputStream,
    ) {
        val stateFlow = MutableStateFlow(-1f)
        listFlow.add(stateFlow)

        lifecycleScope.launch(Dispatchers.IO) {
            FileTransferRequest.create(ip, SERVER_PORT_FILE).doSendFile(ins = inputStream,
                fileLength = fileLength,
                yunFilePath = filePath,
                onProgress = {
                    stateFlow.emit(it)
                },
                onSuccess = {
                    stateFlow.emit(200f)
                    listFlow.remove(stateFlow)
                    if(listFlow.size == 0){
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    }
                })
        }
    }

    companion object {
        private const val TAG = "UploadFileServer"
        private const val CHANNEL_ID = "UploadFileServerChannelId"

        private const val FOREGROUND_ID = 10808

    }
}