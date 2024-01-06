package com.hld.networkdisk.server.networks

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

/**
 * socket链接管理
 */
class ServerSocketManager(
    private val coroutineScope: CoroutineScope,
    private val port: Int,
    private val onConnectedListener: OnConnectedListener,
) {
    private var serverSocket: ServerSocket? = null

    init {
        serverSocket = try {
            createServerSocket()
        } catch (e: RuntimeException) {
            createServerSocket()
        } catch (e: IOException) {
            createServerSocket()
        }

        if (serverSocket != null) {
            coroutineScope.launch(Dispatchers.IO) {
                doAccept()
            }
        }
    }

    fun getLocalPort(): Int {
        return serverSocket?.localPort ?: -1
    }

    private fun doAccept() {
        coroutineScope.launch(Dispatchers.IO) {
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
            return ServerSocket(port)
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    interface OnConnectedListener {
        // 接收到链接
        fun onConnected(socket: Socket)
    }
}
