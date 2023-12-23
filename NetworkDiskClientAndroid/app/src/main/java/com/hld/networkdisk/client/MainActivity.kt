package com.hld.networkdisk.client

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hld.networkdisk.client.servers.UploadFileServer
import com.hld.networkdisk.client.ui.ComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val selectFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.data?.let { uri ->
                    val intent = Intent(this@MainActivity, UploadFileServer::class.java)
                    intent.data = uri
                    intent.putExtra("ip", mainViewModel.ip)
                    val (name, length) = queryUriFileLength(uri)
                    intent.putExtra("filePath", "${mainViewModel.selectedYunFilePath}/$name")
                    intent.putExtra("fileLength", length)
                    startForegroundService(intent)
                }
            }
        }

    private fun queryUriFileLength(uri: Uri): Pair<String, Long> {
        val mVideoCursor: Cursor? =
            contentResolver.query(uri, null, null, null, MediaStore.Files.FileColumns.DATA)
        var name = ""
        var length = 0L
        if (mVideoCursor != null && mVideoCursor.moveToFirst()) {
            val index = mVideoCursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)
            length = mVideoCursor.getLong(index)

            val nameIndex = mVideoCursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
            name = mVideoCursor.getString(nameIndex)
        }
        mVideoCursor?.close()
        return name to length
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.doStart("127.0.0.1")
        setContent {
//            MaterialTheme{
//                ComposeApp()
//            }
            ComposeTheme {
                ComposeApp()
            }
        }
        init()
    }

    private fun bindServer() {
        val intent = Intent(this@MainActivity, UploadFileServer::class.java)
        bindService(intent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }, BIND_AUTO_CREATE)
    }

    private fun init() {
        mainViewModel.startFileSelect.observe(this) {
            if (it) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "*/*"
                selectFileLauncher.launch(intent)
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
