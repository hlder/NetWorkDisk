package com.hld.networkdisk.server.networks

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket

class FileTransferNetwork(private val socket: Socket) {

    suspend fun receiveMessage(block: (String, InputStream, OutputStream) -> Unit) = withContext(Dispatchers.IO) {
        val inputStream = socket.getInputStream()
        val outPutStream = socket.getOutputStream()
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val line = bufferedReader.readLine()
        Log.i(TAG, "receiveMessage line:${line}")
        if (line != null) {
            block(line, inputStream, outPutStream)
        }
    }

    companion object {
        private const val TAG = "MessageNetwork"
    }
}