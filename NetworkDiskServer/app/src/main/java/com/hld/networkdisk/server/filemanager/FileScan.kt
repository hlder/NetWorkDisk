package com.hld.networkdisk.server.filemanager

import android.content.Context
import android.graphics.BitmapFactory
import com.hld.networkdisk.server.commons.Constants
import com.hld.networkdisk.server.commons.bitmapToBase64
import com.hld.networkdisk.server.commons.getFileSuffix
import com.hld.networkdisk.server.data.AppDatabase
import com.hld.networkdisk.server.data.PreviewDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileScan(private val context: Context) {
    private val basePath = Constants.baseFilePath(context)

    /**
     * 执行扫描
     */
    suspend fun doScan() = withContext(Dispatchers.IO){
        scan(File(basePath))
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
                bitmap?.let {
                    var bl = Math.min(bitmap.width / 120f, bitmap.height / 120f)
                    if (bl < 1) {
                        bl = 1F
                    }
                    println("======================width:${bitmap.width}  height:${bitmap.height}  bl：${bl} absolutePath:${file.absolutePath.replace(basePath, "")}")
                    options.inSampleSize = bl.toInt()
                    options.inJustDecodeBounds = false
                    val previewBase64 =
                        BitmapFactory.decodeFile(file.absolutePath, options).bitmapToBase64()
                    previewBase64?.let {
                        AppDatabase.getInstance(context).previewDao().insert(
                            PreviewDao.Bean(
                                fileAbsolutePath = file.absolutePath.replace(basePath, ""),
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