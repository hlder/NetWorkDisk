package com.hld.networkdisk.filemanager

import android.content.Context
import com.hld.networkdisk.beans.FileBean
import com.hld.networkdisk.beans.MessageTransferFileBean
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.LinkedList

class FileManager(context: Context) {
    private val baseFilePath = context.getExternalFilesDir("server")?.absolutePath ?: ""

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
                    absolutePath = file.absolutePath.replace(baseFilePath, ""),
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
     * 接收客户端发来的文件
     */
    fun receiveFileFromStream(inputStream: InputStream, bean: MessageTransferFileBean): Boolean {
        var fileOutputStream: FileOutputStream? = null
        try {
            val outFile = fileReName(baseFilePath + "/" + bean.filePath) // 文件已存在，那么换个名字
            if(outFile.parentFile?.exists() != true){
                outFile.parentFile?.mkdirs()
            }
            outFile.createNewFile()
            println("=================================receiveFileFromStream outFile:${outFile.absolutePath}")
            fileOutputStream = FileOutputStream(outFile)
            println("=================================receiveFileFromStream 开始")

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                fileOutputStream.write(buffer, 0, length)
                buffer.apply { // ==================================================
                    val sb = StringBuilder()
                    sb.append("[")
                    buffer.forEach { sb.append("${it},") }
                    sb.append("]")
                    println("=================================receiveFileFromStream bytesRead:${length} buffer:${sb.toString()}")
                }
            }
            println("=================================receiveFileFromStream 结束")
            return true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: java.lang.RuntimeException) {
            e.printStackTrace()
        } finally {
            inputStream.close()
            fileOutputStream?.close()
        }
        return false
    }

    /**
     * 发送文件
     */
    fun sendFileWithStream(outputStream: OutputStream, bean: MessageTransferFileBean): Boolean {
        val file = File(baseFilePath + bean.filePath)
        if (file.exists()) {
            val fileInputStream = FileInputStream(file)
            try {
                val buffer = ByteArray(1024)
                var length: Int
                while (fileInputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                return true
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: java.lang.RuntimeException) {
                e.printStackTrace()
            } finally {
                fileInputStream.close()
                outputStream.close()
            }
        }
        return false
    }

    /**
     * 文件换名字
     */
    private fun fileReName(filePath: String): File {
        val file = File(filePath)
        if (file.exists()) { // 已存在，需要换名
            val suffix = getFileSuffix(file)
            val newFilePath = filePath.replace(".$suffix", "(1).$suffix")
            return fileReName(newFilePath)
        }
        return file
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