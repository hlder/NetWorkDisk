package com.hld.networkdisk.server.networks

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class PreviewImageNetWork(private val socket: Socket)  {

    /**
     * 等待接收
     */
    suspend fun receiveMessage(block: (String) -> String) = withContext(Dispatchers.IO) {
        val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val printWriter = PrintWriter(socket.getOutputStream())
        flow<String> {
            var line = bufferedReader.readLine()
            while (line != null) {
                emit(line)
                line = bufferedReader.readLine()
            }
        }.flowOn(Dispatchers.IO).collectLatest {
            // 收到一条消息
            val message = block(it)
            printWriter.println(message)
            printWriter.flush()
        }
    }
}