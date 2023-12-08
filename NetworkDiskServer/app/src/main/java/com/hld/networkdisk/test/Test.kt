package com.hld.networkdisk.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket

fun main() {
    runBlocking {
        launch(Dispatchers.IO) { server() }
        launch(Dispatchers.IO) {
            delay(1000)
            client()
        }
    }
//    println("=========================main")
//    val ins: InputStream = FileInputStream(File("C:\\temp\\f\\a.zip"))
//    val os= FileOutputStream(File("C:\\temp\\t\\test.img"))
//    val buffer = ByteArray(1024)
//    var bytesRead: Int
//
//    while (ins.read(buffer).also { bytesRead = it } != -1) {
//        os.write(buffer, 0, bytesRead)
//        val sb = StringBuilder()
//        sb.append("[")
//        buffer.forEach { sb.append("${it},") }
//        sb.append("]")
//        println("====================bytesRead:${bytesRead} buffer:${sb.toString()}")
//    }
//    ins.close()
//    os.close()
//    println("====================结束")
}

fun server() {
    val ss = ServerSocket(10008)
    val s = ss.accept()
    val ins: InputStream = FileInputStream(File("C:\\temp\\f\\a.zip"))
//    val ins: InputStream = FileInputStream(File("C:\\temp\\f\\system.img"))
    val os = s.getOutputStream()
    val buffer = ByteArray(1024)
    var bytesRead: Int

    var count = 0
    while (ins.read(buffer).also { bytesRead = it } != -1) {
        os.write(buffer, 0, bytesRead)
        val sb = StringBuilder()
        sb.append("[")
        buffer.forEach { sb.append("${it},") }
        sb.append("]")
        println("${count++}-------------------------bytesRead:${bytesRead} buffer:${sb.toString()}")
    }
    ins.close()
    os.close()
    println("----------------------------发送完成")
}

suspend fun client() {
    val cs = Socket("127.0.0.1",10008)
    val ins: InputStream = cs.getInputStream()
    val os= FileOutputStream(File("C:\\temp\\t\\test.img"))
    val buffer = ByteArray(1024)
    var bytesRead: Int

    var count = 0
    while (ins.read(buffer).also { bytesRead = it } != -1) {
        os.write(buffer, 0, bytesRead)
        val sb = StringBuilder()
        sb.append("[")
        buffer.forEach { sb.append("${it},") }
        sb.append("]")
//        println("${count++}====================bytesRead:${bytesRead} buffer:${sb.toString()}")
        delay(1)
    }
    ins.close()
    os.close()
    println("============================接收完成")
}
