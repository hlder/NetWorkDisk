package com.hld.networkdisk

import android.content.Intent
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
import com.hld.networkdisk.network.SocketType
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
    private val portFileState: MutableState<Int> = mutableStateOf(-1)

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
            val textFile = remember {
                portFileState
            }
            Test("${text.value}","${textFile.value}") { click() }
        }

        ServerApi(this, object : ServerSocketManager.OnCreateListener {
            override fun onCreateSuccess(socketType: SocketType, port: Int) {
                println("=====================onCreateSuccess socketType:$socketType port:$port")
                if(socketType == SocketType.MESSAGE){
                    portState.value = port
                }else if(socketType == SocketType.FILE){
                    portFileState.value = port
                }
            }

            override fun onCreateError() {
                println("=====================onCreateError")
            }
        })
    }

    private fun click() {
        val intent = Intent(this, TestClientActivity::class.java)
        intent.putExtra("address", "127.0.0.1")
        intent.putExtra("messagePort", portState.value)
        intent.putExtra("filePort", portFileState.value)
        startActivity(intent)
//        lifecycleScope.launch(Dispatchers.IO) {
//            val socket = Socket("127.0.0.1", portState.value)
//
//            println("==================================localAddress:${socket.localAddress}  inetAddress:${socket.inetAddress}  port:${socket.port}  localPort:${socket.localPort}")
//
//            val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
//            val printWriter = PrintWriter(socket.getOutputStream())
//
//            launch {
//                for (i in 0 until 10){
//                    delay(1000)
//                    println("=========================client发送消息${i}")
//                    printWriter.println("=========================client发送消息${i}")
//                    printWriter.flush()
//                }
//            }
//
//            flow<String> {
//                var line = bufferedReader.readLine()
//                while (line != null) {
//                    emit(line)
//                    line = bufferedReader.readLine()
//                }
//            }.flowOn(Dispatchers.IO).collectLatest {
//                // 收到一条消息
//                println("=========================client收到消息:$it")
//            }
//        }

    }
    private fun uploadFile(file:File){

    }
    private fun downloadFile(){

    }

}

@Composable
fun Test(textStr:String,textFileStr:String, onClick: () -> Unit) {
    Column {
        Text(text = "消息端口:${textStr}")
        Text(text = "文件端口:${textFileStr}")
        Button(onClick = onClick) {
            Text(text = "点击")
        }
    }
}
