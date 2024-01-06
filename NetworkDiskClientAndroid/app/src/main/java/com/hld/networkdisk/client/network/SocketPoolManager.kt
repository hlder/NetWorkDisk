package com.hld.networkdisk.client.network

import com.google.gson.Gson
import com.hld.networkdisk.client.extension.suspendTimeOutCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class SocketPoolManager(private val coroutineScope: CoroutineScope, private val ip: String, private val port: Int) {
    private val inIdlePool = mutableListOf<SocketManager>()
    private val runningPool = mutableListOf<SocketManager>()

    @OptIn(DelicateCoroutinesApi::class)
    private val newContext = newSingleThreadContext("SocketPoolManager")
    private suspend fun getOne() = withContext(newContext) { getOneSocketManager() }
    private suspend fun getOneSocketManager(): SocketManager {
        if (inIdlePool.size > 0) {
            return inIdlePool[0].apply {
                inIdlePool.remove(this)
            }
        }
        while (runningPool.size > MAX_SOCKET_SIZE) { // 超过5了，等待前面运行完再运行
            delay(100)
        }
        val socketManager = SocketManager(ip, port)

        runningPool.add(socketManager)
        socketManager.create()
        coroutineScope.launch(Dispatchers.IO) {
            socketManager.start()
        }
        return socketManager
    }

    private suspend fun addOneIdlePool(socketManager: SocketManager) = withContext(newContext) {
        if ((inIdlePool.size + runningPool.size) > MAX_SOCKET_SIZE) {
            socketManager.close()
            inIdlePool.remove(socketManager)
            runningPool.remove(socketManager)
        } else {
            inIdlePool.add(socketManager)
            runningPool.remove(socketManager)
        }
    }

    suspend fun <T> sendMessage(code: Int, message: T) :String{
        return if (message is String) {
            sendMessageJson(code, message)
        } else {
            sendMessageJson(code, Gson().toJson(message))
        }
    }

    private suspend fun sendMessageJson(code: Int, message: String) = suspendTimeOutCoroutineScope { continuation -> // 超时10秒
        val socketManager = getOne()
        socketManager.sendMessage(code, message) { resultStr ->
            addOneIdlePool(socketManager)
            continuation.resume(resultStr)
        }
    }

    companion object {
        private const val MAX_SOCKET_SIZE = 5
    }
}