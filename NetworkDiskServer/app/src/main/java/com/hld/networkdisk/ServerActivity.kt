package com.hld.networkdisk

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import com.hld.networkdisk.network.ServerApi
import com.hld.networkdisk.network.ServerSocketManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

@AndroidEntryPoint
class ServerActivity : ComponentActivity() {
    private val portState: MutableState<Int> = mutableStateOf(-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = Uri.parse("qqqqq/aaaa?bbb=1&ccccc=8")
        println("=================uri path:${uri.path}")
        uri.queryParameterNames.forEach {
            println("=================uri key:$it  value:${uri.getQueryParameter(it)}")
        }

        println("=================uri:$uri")

        setContent {
            val text = remember {
                portState
            }
            Test("${text.value}") { click() }
        }

        ServerApi(this, object : ServerSocketManager.OnCreateListener {
            override fun onCreateSuccess(port: Int) {
                println("=====================onCreateSuccess port:$port")
                portState.value = port
            }

            override fun onCreateError() {
                println("=====================onCreateError")
            }
        })
    }

    private fun click() {
        lifecycleScope.launch(Dispatchers.IO) {
            val socket = Socket("127.0.0.1", portState.value)

            println("==================================localAddress:${socket.localAddress}  inetAddress:${socket.inetAddress}  port:${socket.port}  localPort:${socket.localPort}")

            val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val printWriter = PrintWriter(socket.getOutputStream())

            launch {
                for (i in 0 until 10){
                    delay(1000)
                    println("=========================client发送消息${i}")
                    printWriter.println("=========================client发送消息${i}")
                    printWriter.flush()
                }
            }

            flow<String> {
                var line = bufferedReader.readLine()
                while (line != null) {
                    emit(line)
                    line = bufferedReader.readLine()
                }
            }.flowOn(Dispatchers.IO).collectLatest {
                // 收到一条消息
                println("=========================client收到消息:$it")
            }
        }

    }
    private fun uploadFile(file:File){

    }
    private fun downloadFile(){

    }

}

@Composable
fun Test(textStr:String, onClick: () -> Unit) {
    Column {
        Text(text = "端口:${textStr}")
        Button(onClick = onClick) {
            Text(text = "点击")
        }
    }
}
