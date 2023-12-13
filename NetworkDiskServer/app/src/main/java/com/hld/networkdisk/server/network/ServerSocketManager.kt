package com.hld.networkdisk.server.network

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

/**
 * socket链接管理
 */
class ServerSocketManager(
    activity: ComponentActivity,
    private val onCreateListener: OnCreateListener,
    private val onConnectedListener: OnConnectedListener,
    private val socketType: SocketType = SocketType.MESSAGE
) {
    private val lifecycleScope = activity.lifecycleScope
    private var serverSocket: ServerSocket? = null

    init {
        serverSocket = try {
            createServerSocket()
        } catch (e: RuntimeException) {
            createServerSocket()
        } catch (e: IOException) {
            createServerSocket()
        }

        if (serverSocket == null) {
            onCreateListener.onCreateError()
        } else {
            onCreateListener.onCreateSuccess(socketType, serverSocket!!.localPort)
            lifecycleScope.launch(Dispatchers.IO) {
                doAccept()
            }
        }
    }

    private fun doAccept() {
        lifecycleScope.launch(Dispatchers.IO) {
            serverSocket?.let {
                while (true) {
                    val socket = it.accept()
                    onConnectedListener.onConnected(socket)
                }
            }
        }
    }

    private fun createServerSocket(): ServerSocket? {
        try {
            return ServerSocket(1000 + ((Math.random()) * 60000).toInt())
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    interface OnCreateListener {
        // 创建成功
        fun onCreateSuccess(socketType: SocketType, port: Int)

        // 创建失败
        fun onCreateError()
    }

    interface OnConnectedListener {
        // 接收到链接
        fun onConnected(socket: Socket)
    }
}

enum class SocketType {
    MESSAGE, FILE
}