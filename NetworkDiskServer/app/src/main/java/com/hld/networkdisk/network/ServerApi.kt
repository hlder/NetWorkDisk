package com.hld.networkdisk.network

import androidx.activity.ComponentActivity

/**
 * 服务端提供接口
 */
class ServerApi(
    activity: ComponentActivity,
    onCreateListener: ServerSocketManager.OnCreateListener
) {
    init {
        SocketTransfer(activity, onCreateListener, object : SocketTransfer.OnRequestListener {
            override suspend fun onRequest(data: String): String {
                println("=========================server收到消息：$data")

                return "aslsadklq888888"
            }
        })
    }
}
