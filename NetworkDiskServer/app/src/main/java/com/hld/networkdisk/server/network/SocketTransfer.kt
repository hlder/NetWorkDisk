package com.hld.networkdisk.server.network

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.hld.networkdisk.server.commons.Constants.SERVER_PORT_MESSAGE
import com.hld.networkdisk.server.commons.Constants.SERVER_PORT_PREVIEW_IMAGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket

/**
 * 传输
 */
class SocketTransfer(
    activity: ComponentActivity,
    onCreateListener: ServerSocketManager.OnCreateListener,
    private val onMessageRequestListener: OnRequestListener,
    private val onPreviewImageRequestListener: OnRequestListener,
) {
    private val lifecycleScope = activity.lifecycleScope

    init {
        val messageServerSocketManager = ServerSocketManager( // 用于发送问你消息。
            activity, SERVER_PORT_MESSAGE,
            onCreateListener,
            object : ServerSocketManager.OnConnectedListener {
                override fun onConnected(socket: Socket) {
                    Log.i(TAG , "message server收到连接inetAddress:${socket.inetAddress.hostAddress}  port:${socket.port}")
                    lifecycleScope.launch(Dispatchers.IO) {
                        receiveMessage(socket.getInputStream(), socket.getOutputStream()) // 接收消息和发送
                        Log.i(TAG , "链接断开message server inetAddress:${socket.inetAddress.hostAddress}  port:${socket.port}")
                    }
                }
            }, SocketType.MESSAGE
        )
        val previewImageSocketManager = ServerSocketManager( // 用于获取预览图
            activity, SERVER_PORT_PREVIEW_IMAGE,
            onCreateListener,
            object : ServerSocketManager.OnConnectedListener {
                override fun onConnected(socket: Socket) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        receivePreviewImage(
                            socket.getInputStream(),
                            socket.getOutputStream()
                        ) // 接收消息和发送
                    }
                }
            }, SocketType.PREVIEW
        )
        FileTransfer(activity, onCreateListener)
    }

    /**
     * 等待接收
     */
    private suspend fun receiveMessage(inputStream: InputStream, outPutStream: OutputStream) {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val printWriter = PrintWriter(outPutStream)
        flow<String> {
            var line = bufferedReader.readLine()
            while (line != null) {
                Log.i(TAG , "收到消息 line:${line}")
                emit(line)
                line = bufferedReader.readLine()
            }
            Log.i(TAG , "结束接收消息")
        }.flowOn(Dispatchers.IO).collectLatest {
            // 收到一条消息
            val message = onMessageRequestListener.onRequest(it)
            printWriter.println(message)
            printWriter.flush()
        }
    }

    /**
     * 等待接收
     */
    private suspend fun receivePreviewImage(inputStream: InputStream, outPutStream: OutputStream) {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val printWriter = PrintWriter(outPutStream)
        flow<String> {
            var line = bufferedReader.readLine()
            while (line != null) {
                emit(line)
                line = bufferedReader.readLine()
            }
        }.flowOn(Dispatchers.IO).collectLatest {
            // 收到一条消息
            val message = onPreviewImageRequestListener.onRequest(it)
            printWriter.println(message)
            printWriter.flush()
        }
    }

    /**
     * 接收到请求信息
     */
    interface OnRequestListener {
        suspend fun onRequest(data: String): String
    }

    companion object{
        private const val TAG = "SocketTransfer"
    }
}