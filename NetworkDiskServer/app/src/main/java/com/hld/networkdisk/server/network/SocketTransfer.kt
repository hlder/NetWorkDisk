package com.hld.networkdisk.server.network

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.hld.networkdisk.server.commons.Constants.SERVER_PORT_FILE
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
    private val mapClient = mutableMapOf<String, Socket>()

    init {
        val messageServerSocketManager = ServerSocketManager( // 用于发送问你消息。
            activity, SERVER_PORT_MESSAGE,
            onCreateListener,
            object : ServerSocketManager.OnConnectedListener {
                override fun onConnected(socket: Socket) {
                    println("==================================message server收到连接localAddress:${socket.localAddress.hostName}  inetAddress:${socket.inetAddress.hostAddress}  port:${socket.port}  localPort:${socket.localPort}")
                    lifecycleScope.launch(Dispatchers.IO) {
                        receiveMessage(socket.getInputStream(), socket.getOutputStream()) // 接收消息和发送
                    }
                }
            }, SocketType.MESSAGE
        )
        val fileServerSocketManager = ServerSocketManager( // 用于传输文件
            activity, SERVER_PORT_FILE,
            onCreateListener,
            object : ServerSocketManager.OnConnectedListener {
                override fun onConnected(socket: Socket) {
                    println("==================================file server收到连接localAddress:${socket.localAddress.hostName}  inetAddress:${socket.inetAddress.hostAddress}  port:${socket.port}  localPort:${socket.localPort}")
                    mapClient["${socket.inetAddress.hostAddress}:${socket.port}"] = socket
                }
            }, SocketType.FILE
        )
        val previewImageSocketManager = ServerSocketManager( // 用于获取预览图
            activity, SERVER_PORT_PREVIEW_IMAGE,
            onCreateListener,
            object : ServerSocketManager.OnConnectedListener {
                override fun onConnected(socket: Socket) {
                    println("==================================preview server收到连接localAddress:${socket.localAddress.hostName}  inetAddress:${socket.inetAddress.hostAddress}  port:${socket.port}  localPort:${socket.localPort}")
                    lifecycleScope.launch(Dispatchers.IO) {
                        receivePreviewImage(
                            socket.getInputStream(),
                            socket.getOutputStream()
                        ) // 接收消息和发送
                    }
                }
            }, SocketType.PREVIEW
        )
    }

    /**
     * 收文件
     */
    fun getReceiveFileStream(address: String, port: Int): InputStream? {
        return mapClient["${address}:$port"]?.getInputStream()
    }

    /**
     * 发送文件
     */
    fun getSendFileStream(address: String, port: Int): OutputStream? {
        return mapClient["${address}:$port"]?.getOutputStream()
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
                emit(line)
                line = bufferedReader.readLine()
            }
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
}