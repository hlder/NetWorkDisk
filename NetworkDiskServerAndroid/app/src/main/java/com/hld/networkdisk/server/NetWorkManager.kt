package com.hld.networkdisk.server

import com.hld.networkdisk.server.commons.Constants
import com.hld.networkdisk.server.networks.ServerSocketManager
import com.hld.networkdisk.server.networkapis.FileTransferApi
import com.hld.networkdisk.server.networkapis.MessageApi
import com.hld.networkdisk.server.networkapis.PreviewImageApi
import com.hld.networkdisk.server.networks.FileTransferNetwork
import com.hld.networkdisk.server.networks.MessageNetwork
import com.hld.networkdisk.server.networks.PreviewImageNetWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.Socket

object NetWorkManager {
    var messagePort = -1
    var previewImagePort = -1
    var fileTransferPort = -1

    var onConnectListener: ((InetAddress) -> Unit)? = null

    private var messageApi: MessageApi? = null
    private var previewImageApi: PreviewImageApi? = null
    private var fileTransferApi: FileTransferApi? = null

    private val workScope = CoroutineScope(Dispatchers.IO)

    // 处理文本消息
    private val messageConnectedListener = object : ServerSocketManager.OnConnectedListener {
        override fun onConnected(socket: Socket) {
            workScope.launch {
                onConnectListener?.invoke(socket.inetAddress)
                MessageNetwork(socket).receiveMessage {
                    messageApi?.onRequest(it) ?: ""
                }
            }
        }
    }

    // 处理图片预览消息
    private val previewImageConnectedListener = object : ServerSocketManager.OnConnectedListener {
        override fun onConnected(socket: Socket) {
            workScope.launch {
                PreviewImageNetWork(socket).receiveMessage {
                    previewImageApi?.onRequest(it) ?: ""
                }
            }
        }
    }

    // 处理文件传输
    private val fileTransferConnectedListener = object : ServerSocketManager.OnConnectedListener {
        override fun onConnected(socket: Socket) {
            workScope.launch {
                FileTransferNetwork(socket).receiveMessage { s, inputStream, outputStream -> fileTransferApi?.onRequest(s, inputStream, outputStream) }
            }
        }
    }

    init {
        val messageSocketManager = ServerSocketManager(workScope, Constants.SERVER_PORT_MESSAGE, messageConnectedListener)
        val previewImageSocketManager = ServerSocketManager(workScope, Constants.SERVER_PORT_PREVIEW_IMAGE, previewImageConnectedListener)
        val fileTransferSocketManager = ServerSocketManager(workScope, Constants.SERVER_PORT_FILE, fileTransferConnectedListener)

        messagePort = messageSocketManager.getLocalPort()
        previewImagePort = previewImageSocketManager.getLocalPort()
        fileTransferPort = fileTransferSocketManager.getLocalPort()
    }

    fun setMessageApi(messageApi: MessageApi) {
        this.messageApi = messageApi
    }

    fun setPreviewImageApi(previewImageApi: PreviewImageApi) {
        this.previewImageApi = previewImageApi
    }

    fun setFileTransferApi(fileTransferApi: FileTransferApi) {
        this.fileTransferApi = fileTransferApi
    }


}