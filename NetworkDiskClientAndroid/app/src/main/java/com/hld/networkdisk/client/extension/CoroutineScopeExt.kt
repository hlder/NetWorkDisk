package com.hld.networkdisk.client.extension

import android.util.Log
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeoutException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 扩展suspendCancellableCoroutine,让block拥有继承协程作用域
 */
public suspend inline fun <T> suspendCancellableCoroutineScope(
    crossinline block: suspend (CancellableContinuation<T>) -> Unit
) = coroutineScope {
    val block2: (CancellableContinuation<T>) -> Unit = {
        this.launch { block.invoke(it) }
    }
    suspendCancellableCoroutine(block2)
}

/**
 * 可以超时的
 */
public suspend inline fun <T> suspendTimeOutCoroutineScope(
    timeOut: Long = 10000,
    crossinline block: suspend (CancellableContinuation<T>) -> Unit
) = coroutineScope {
    val block2: (CancellableContinuation<T>) -> Unit = {
        launch {
            delay(timeOut)
            if (it.isActive) {
                it.cancel(TimeoutException("time out of ${timeOut}ms"))
            }
        }
        this.launch { block.invoke(it) }
    }
    suspendCancellableCoroutine(block2)
}

/**
 * 扩展suspendCoroutine，让block拥有继承协程作用域
 */
public suspend inline fun <T> suspendCoroutineScope(
    crossinline block: suspend (Continuation<T>) -> Unit
) = coroutineScope {
    val block2: (Continuation<T>) -> Unit = {
        this.launch { block.invoke(it) }
    }
    suspendCoroutine(block2)
}

/**
 * 监听当前协程作用域被cancel
 */
suspend fun onJobCancel(block: () -> Unit) = coroutineScope {
    try {
        this.coroutineContext.job.invokeOnCompletion {
            it?.let { block() } // 没有异常表示正常完成，有异常则表示异常(比如cancel)
        }
    } catch (e: IllegalStateException) {
        Log.d("dddd", "onJobCancel error:${e.localizedMessage}")
    }
}