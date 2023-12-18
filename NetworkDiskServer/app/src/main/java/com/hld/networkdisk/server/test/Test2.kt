package com.hld.networkdisk.server.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

fun main() {
    runBlocking {
        launch { startServer() }
        launch { startClient() }
    }
}

suspend fun startClient() = withContext(Dispatchers.IO) {
    delay(1000)
    val s = Socket("127.0.0.1", 10088)
    val printWriter = PrintWriter(s.getOutputStream())
    printWriter.println("aaaaaaaaa1")
    printWriter.flush()
    delay(2000)
    printWriter.println("abcdefg")
    printWriter.flush()
    delay(2000)
    printWriter.println("hi啊jklmn")
    printWriter.flush()
    printWriter.close()
    s.close()
}

suspend fun startServer() = withContext(Dispatchers.IO) {
    println("====================================")
    val ss = ServerSocket(10088)
    val s = ss.accept()
    println("===================收到链接:${s}")
    val ins = s.getInputStream()

    launch(Dispatchers.IO) {
        val br = BufferedReader(InputStreamReader(ins))
        var line: String
        while ((br.readLine()).also { line = it } != null) {
            println("-------------------------line:${line}")
        }
        println("-------------------------over")
    }

    launch(Dispatchers.IO) {
        var value = -1
        while ((ins.read().also { value = it }) != -1) {
            println("=======================value:${value}   char:${Char(value)}")
        }
        println("===========================over")
    }
}