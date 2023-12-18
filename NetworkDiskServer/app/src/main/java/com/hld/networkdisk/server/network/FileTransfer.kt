package com.hld.networkdisk.server.network

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.hld.networkdisk.server.beans.MessageTransferFileBean
import com.hld.networkdisk.server.commons.Constants
import com.hld.networkdisk.server.filemanager.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket

class FileTransfer(
    activity: ComponentActivity,
    onCreateListener: ServerSocketManager.OnCreateListener,
) {
    private val lifeCycleScope = activity.lifecycleScope

    private val fileManager: FileManager = FileManager(activity)

    private val gson = Gson()

    init {
        val fileServerSocketManager = ServerSocketManager( // 用于传输文件
            activity, Constants.SERVER_PORT_FILE,
            onCreateListener,
            object : ServerSocketManager.OnConnectedListener {
                override fun onConnected(socket: Socket) {
                    Log.i(TAG, "onConnected:inetAddress:${socket.inetAddress.hostAddress}  port:${socket.port}")
                    receiveMessage(socket.getInputStream(), socket.getOutputStream())
                }
            }, SocketType.FILE
        )
    }

    private fun receiveMessage(inputStream: InputStream, outPutStream: OutputStream) {
        lifeCycleScope.launch(Dispatchers.IO) {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val line = bufferedReader.readLine()
            Log.i(TAG, "receiveMessage line:${line}")
            if (line != null) {
                val bean = gson.fromJson(line, MessageTransferFileBean::class.java)
                if (bean.isClientSendToServer) { // 收
                    fileManager.receiveFileFromStream(inputStream, bean)
                } else { // 发
                    fileManager.sendFileWithStream(outPutStream, bean)
                }
            }
        }
    }
    companion object{
        private const val TAG = "FileTransfer"
    }
}
