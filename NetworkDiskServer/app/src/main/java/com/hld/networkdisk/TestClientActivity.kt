package com.hld.networkdisk

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.hld.networkdisk.beans.MessageBean
import com.hld.networkdisk.beans.MessageCodes
import com.hld.networkdisk.beans.MessageTransferFileBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger

class TestClientActivity : AppCompatActivity() {
    private lateinit var ms: Socket
    private lateinit var address: String
    private var messagePort: Int = 0
    private var filePort: Int = 0
    private lateinit var messageTransfer: TestTransfer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Button(onClick = { clickSend() }) {
                    Text(text = "发送")
                }
                Button(onClick = { clickDownload() }) {
                    Text(text = "下载")
                }
                Button(onClick = { clickTest() }) {
                    Text(text = "测试")
                }
            }
        }
        address = intent.getStringExtra("address") ?: ""
        messagePort = intent.getIntExtra("messagePort", 0)
        filePort = intent.getIntExtra("filePort", 0)
        lifecycleScope.launch(Dispatchers.IO) {
            ms = Socket(address, messagePort)
            messageTransfer = TestTransfer(this@TestClientActivity, ms)
        }
    }

    private fun clickDownload() = lifecycleScope.launch(Dispatchers.IO) {
        val fs = Socket(address, filePort)
        val bean = MessageTransferFileBean(
            address = fs.localAddress.toString(),
            port = fs.localPort,
            "aaa",
            isClientSendToServer = false,
            10000
        )
        messageTransfer.sendMessage(
            MessageCodes.CODE_CLIENT_SEND_TO_SERVER_FILE,
            Gson().toJson(bean)
        ) {
            // 开始下载
        }
    }
    private fun clickTest(){
        lifecycleScope.launch(Dispatchers.IO) {
            println("=============================准备发送1")
            val baseFilePath = getExternalFilesDir("client")?.absolutePath ?: ""
            // 开始发送
            val ins: InputStream = FileInputStream(File("${baseFilePath}/sunflower-main.zip"))
            val buffer = ByteArray(1024)
            var bytesRead: Int

            var count = 0
            while (ins.read(buffer).also { bytesRead = it } != -1) {
//                    os.write(buffer, 0, bytesRead)
                println("${count++}====================bytesRead:${bytesRead}")
            }
            println("===========================================发送完成1")
            ins.close()
            println("===========================================发送完成2")
        }
    }

    private fun clickSend() = lifecycleScope.launch(Dispatchers.IO) {
        val fs = Socket(address, filePort)
        val bean = MessageTransferFileBean(
            address = fs.localAddress.hostAddress?:"",
            port = fs.localPort,
            "aaa/sunflower-main.zip",
            isClientSendToServer = true,
            10000
        )
        messageTransfer.sendMessage(MessageCodes.CODE_CLIENT_SEND_TO_SERVER_FILE, Gson().toJson(bean)) {
            lifecycleScope.launch(Dispatchers.IO){
                println("=============================准备发送1")
                val baseFilePath = getExternalFilesDir("client")?.absolutePath ?: ""
                // 开始发送
                val ins: InputStream = FileInputStream(File("${baseFilePath}/sunflower-main.zip"))
                val os = fs.getOutputStream()
                val buffer = ByteArray(1024)
                var bytesRead: Int

                var count = 0
                while (ins.read(buffer).also { bytesRead = it } != -1) {
                    os.write(buffer, 0, bytesRead)
                    println("${count++}====================bytesRead:${bytesRead}")
                }
                println("===========================================发送完成1")
                ins.close()
                os.close()
                println("===========================================发送完成2")
            }
        }
    }
}

class TestTransfer(activity: AppCompatActivity, private val s: Socket) {
    private val printWriter = PrintWriter(s.getOutputStream())

    init {
        activity.lifecycleScope.launch {
            receiveMessage(s.getInputStream())
        }
    }

    private var version = AtomicInteger(0)

    private val map = mutableMapOf<Int, (MessageBean) -> Unit>()

    fun sendMessage(
        code: Int,
        message: String,
        callBack: (MessageBean) -> Unit
    ) {
        val temp = version.getAndIncrement()
        map[temp] = callBack

        printWriter.println(
            Gson().toJson(
                MessageBean(
                    code = code,
                    version = temp,
                    message = message
                )
            )
        )
        printWriter.flush()
    }

    private suspend fun receiveMessage(inputStream: InputStream) {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        flow<String> {
            var line = bufferedReader.readLine()
            while (line != null) {
                emit(line)
                line = bufferedReader.readLine()
            }
        }.flowOn(Dispatchers.IO).collectLatest {
            // 收到一条消息
            val messageBean = Gson().fromJson(it, MessageBean::class.java)
            map[messageBean.version]?.invoke(messageBean)
        }
    }

}