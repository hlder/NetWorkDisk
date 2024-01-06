package com.hld.networkdisk.server

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.hld.networkdisk.server.databinding.ActivityServerBinding
import com.hld.networkdisk.server.networkImpls.FileTransferApiImpl
import com.hld.networkdisk.server.networkImpls.MessageApiImpl
import com.hld.networkdisk.server.networkImpls.PreviewImageApiImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServerActivity : ComponentActivity() {
    private lateinit var binding: ActivityServerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        NetWorkManager.onConnectListener = {
            lifecycleScope.launch(Dispatchers.Main) {
                binding.textReceiveAddress.text = "hostAddress:${it.hostAddress}  hostName:${it.hostName}  address:${it.address} "
            }
        }
        lifecycleScope.launch {
            NetWorkManager.setMessageApi(MessageApiImpl(this@ServerActivity))
            NetWorkManager.setPreviewImageApi(PreviewImageApiImpl(this@ServerActivity))
            NetWorkManager.setFileTransferApi(FileTransferApiImpl(this@ServerActivity))

            launch(Dispatchers.IO) {
                binding.textPortMessage.text = "消息：${NetWorkManager.messagePort}"
                binding.textPortPreviewImage.text = "预览：${NetWorkManager.previewImagePort}"
                binding.textPortFileTransfer.text = "文件：${NetWorkManager.fileTransferPort}"
            }
        }
    }
}
