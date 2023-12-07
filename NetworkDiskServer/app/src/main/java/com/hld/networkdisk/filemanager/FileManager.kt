package com.hld.networkdisk.filemanager

import android.content.Context
import com.hld.networkdisk.beans.FileBean
import com.hld.networkdisk.beans.MessageTransferFileBean
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.LinkedList

class FileManager(context: Context) {
    private val baseFilePath = context.getExternalFilesDir("server")?.absolutePath

    /**
     * 查询文件列表
     */
    fun queryFileList(path: String): List<FileBean> {
        val baseFile = File(baseFilePath + path)
        val list = LinkedList<FileBean>()
        baseFile.listFiles()?.forEach { file ->
            list.add(
                FileBean(
                    name = file.name,
                    absolutePath = file.absolutePath,
                    suffix = getFileSuffix(file),
                    isDirectory = file.isDirectory,
                    fileLength = file.length(),
                    lastModified = file.lastModified(),
                )
            )
        }
        return list
    }

    /**
     * 接收文件
     */
    fun receiveFileFromStream(inputStream: InputStream, bean: MessageTransferFileBean) {
        val outFile = fileReName(bean.filePath) // 文件已存在，那么换个名字
        val fileOutputStream = FileOutputStream(outFile)

        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            fileOutputStream.write(buffer, 0, length)
        }
    }

    /**
     * 文件换名字
     */
    private fun fileReName(filePath:String):File{
        val file = File(filePath)
        if(file.exists()){ // 已存在，需要换名
            val suffix = getFileSuffix(file)
            val newFilePath = filePath.replace(".$suffix","(1).$suffix")
            return fileReName(newFilePath)
        }
        return file
    }

    /**
     * 发送文件
     */
    fun sendFileWithStream(outputStream: OutputStream, bean: MessageTransferFileBean) {

    }

    // 获取文件的后缀
    private fun getFileSuffix(file: File): String {
        val str = file.absolutePath.split(".")
        return if (str.isNotEmpty()) {
            str[str.size - 1]
        } else {
            ""
        }
    }
}