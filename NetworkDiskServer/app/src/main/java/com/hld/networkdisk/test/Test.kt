package com.hld.networkdisk.test

import android.util.Log
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun main() {
    runBlocking {
        launch(Dispatchers.IO) { server() }
        launch(Dispatchers.IO) {
            delay(1000)
            client()
        }
    }

//    runBlocking {
////        val str = b(this)
////        println("str:$str")
//        val job = launch {
//            e()
//            f()
//        }
//        println("=======job:$job")
//        delay(100)
//        job.cancel()
//    }
}

suspend fun e() = coroutineScope {
    this.coroutineContext.job.invokeOnCompletion {
        println("======================e:it:$it")
    }
    delay(1000)
    println("======e:${this.coroutineContext.job}")
}

suspend fun f() = coroutineScope {
    delay(1000)
    println("======f:${this.coroutineContext.job}")
}

suspend fun b(scope: CoroutineScope) = suspendCancellableCoroutine<String> {
    scope.launch {
        it.resume("")
    }
}

suspend fun onJobCancel(block: () -> Unit) = coroutineScope {
    try {
        this.coroutineContext.job.invokeOnCompletion {
            block()
        }
    } catch (e: IllegalStateException) {
        Log.d("dddd", "error:${e.localizedMessage}")
    }
}

suspend fun d() = suspendCoroutine<String> {
    it.resume("")
}

public suspend inline fun <T> suspendCancellableCoroutineScope(
    crossinline block: suspend (CancellableContinuation<T>) -> Unit
) = coroutineScope {
    val block2: (CancellableContinuation<T>) -> Unit = {
        this.launch { block.invoke(it) }
    }
    suspendCancellableCoroutine<T>(block2)
}

public suspend inline fun <T> suspendCoroutineScope(crossinline block: suspend (Continuation<T>) -> Unit) =
    coroutineScope {
        val block2: (Continuation<T>) -> Unit = {
            this.launch { block.invoke(it) }
        }
        suspendCoroutine(block2)
    }


suspend fun c() = suspendCancellableCoroutineScope<String> {
    delay(1000)
    it.resume("")
    it.cancel()
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
        val sb = StringBuilder()
        sb.append("[")
        buffer.forEach { sb.append("${it},") }
        sb.append("]")
        println("${count++}-------------------------bytesRead:${bytesRead} buffer:${sb.toString()}")
        os.write(buffer, 0, bytesRead)
        os.flush()
    }
    ins.close()
    os.close()
    println("----------------------------发送完成")
}

suspend fun client() {
    val cs = Socket("127.0.0.1", 10008)
    val ins: InputStream = cs.getInputStream()
    val os = FileOutputStream(File("C:\\temp\\t\\test.img"))
    val buffer = ByteArray(1024)
    var bytesRead: Int

    var count = 0
    while (ins.read(buffer).also { bytesRead = it } != -1) {
        os.write(buffer, 0, bytesRead)
        val sb = StringBuilder()
        sb.append("[")
        buffer.forEach { sb.append("${it},") }
        sb.append("]")
        println("${count++}====================bytesRead:${bytesRead} buffer:${sb.toString()}")
        delay(1)
    }
    ins.close()
    os.close()
    println("============================接收完成")
}
