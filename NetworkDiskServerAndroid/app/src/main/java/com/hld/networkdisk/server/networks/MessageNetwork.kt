package com.hld.networkdisk.server.networks

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class MessageNetwork(private val socket: Socket) {

    /**
     * 等待接收
     */
    suspend fun receiveMessage(block: (String) -> String) = withContext(Dispatchers.IO) {
        val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val printWriter = PrintWriter(socket.getOutputStream())
        flow<String> {
            var line = bufferedReader.readLine()
            while (line != null) {
                Log.i(TAG, "收到消息 line:${line}")
                emit(line)
                line = bufferedReader.readLine()
            }
            Log.i(TAG, "结束接收消息")
        }.flowOn(Dispatchers.IO).collectLatest {
            Log.i(TAG, "处理消息:${it}")
            // 收到一条消息
            val message = block(it)
            printWriter.println(message)
            printWriter.flush()
            Log.i(TAG, "返回处理结果:${message}")
        }
    }

    companion object {
        private const val TAG = "MessageNetwork"
    }
}