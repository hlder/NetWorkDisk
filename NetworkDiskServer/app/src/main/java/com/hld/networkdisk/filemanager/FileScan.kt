package com.hld.networkdisk.filemanager

import android.content.Context
import android.graphics.BitmapFactory
import com.hld.networkdisk.commons.Constants
import com.hld.networkdisk.commons.bitmapToBase64
import com.hld.networkdisk.commons.getFileSuffix
import com.hld.networkdisk.data.AppDatabase
import com.hld.networkdisk.data.PreviewDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File

class FileScan(private val context: Context) {
    /**
     * 执行扫描
     */
    suspend fun doScan() = coroutineScope {
        launch(Dispatchers.IO) {
            val path = Constants.baseFilePath(context)
            scan(File(path))
        }
    }

    private fun scan(file: File) {
        if (file.isDirectory) { // 文件夹
            file.listFiles()?.forEach {
                scan(it)
            }
        } else { // 文件
            val suffix = file.getFileSuffix() // 后缀
            if (suffix == "png" || suffix == "jpg" || suffix == "webp" || suffix == "jpeg") {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = false }
                val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
                println("======================fileScan:${bitmap} absolutePath:${file.absolutePath}")
                bitmap?.let {
                    var bl = Math.min(bitmap.width / 120f, bitmap.height / 120f)
                    if (bl < 1) {
                        bl = 1F
                    }
                    options.inSampleSize = bl.toInt()
                    options.inJustDecodeBounds = false
                    val previewBase64 =
                        BitmapFactory.decodeFile(file.absolutePath, options).bitmapToBase64()
                    previewBase64?.let {
                        AppDatabase.getInstance(context).previewDao().insert(
                            PreviewDao.Bean(
                                fileAbsolutePath = file.absolutePath,
                                previewImageBase64 = previewBase64
                            )
                        )
                    }
                    println("=====================FileScan 插入 path:${file.absolutePath}  base64Length:${previewBase64?.length}")
                }
            }
        }
    }
}