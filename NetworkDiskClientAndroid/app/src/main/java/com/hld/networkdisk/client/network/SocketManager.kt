package com.hld.networkdisk.client.network

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException
import java.util.concurrent.atomic.AtomicInteger

class SocketManager(private val ip: String, private val port: Int) {
    private lateinit var socket: Socket
    private lateinit var printWriter: PrintWriter
    private var isNeedRestart = true
    private var restartCount = 0
    private val maxRestartCount = 5 // 最多重试5次

    private val gson: Gson = Gson()

    private val messageVersion = AtomicInteger(0)

    private val mapCallBack = mutableMapOf<Int, (String) -> Unit>()

    suspend fun start() {
        mapCallBack.clear()
        withContext(Dispatchers.IO) {
            try {
                socket = Socket(ip, port)
                printWriter = PrintWriter(socket.getOutputStream())
                receiveMessage(socket.getInputStream())
            } catch (e: UnknownHostException) {
                Log.e(TAG, "startError:${e.localizedMessage}")
            } catch (e: RuntimeException) {
                Log.e(TAG, "startError:${e.localizedMessage}")
            }
        }
        // flow停止了，说明socket断开了
        if (restartCount < maxRestartCount && isNeedRestart) {
            start()
            restartCount++
        }
    }

    private suspend fun receiveMessage(inputStream: InputStream) {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        flow<String> {
            var line = bufferedReader.readLine()
            while (line != null) {
                Log.d(TAG, "=========收到消息line:$line")
                emit(line)
                line = bufferedReader.readLine()
            }
        }.flowOn(Dispatchers.IO).collectLatest {
            Log.d(TAG, "=========collectLatest line:$it")
            // 收到一条消息
            val bean = gson.fromJson(it, BaseMessageBean::class.java)
            mapCallBack.remove(bean.version)?.invoke(bean.message)
        }
    }

    // 客户端先发消息
    suspend fun sendMessage(code: Int = 0, message: String, callBack: (String) -> Unit) =
        withContext(Dispatchers.IO) {
            val version = messageVersion.getAndIncrement()
            mapCallBack[version] = callBack
            val messageBean = BaseMessageBean(
                version = version,
                code = code,
                message = message
            )

            printWriter.println(gson.toJson(messageBean))
            printWriter.flush()
        }

    fun close() {
        isNeedRestart = false
        socket.close()
    }

    companion object {
        private const val TAG = "SocketManager"
    }
}

private data class BaseMessageBean(
    val version: Int,
    val code: Int,
    val message: String
)