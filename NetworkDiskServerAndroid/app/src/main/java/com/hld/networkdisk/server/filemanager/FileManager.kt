package com.hld.networkdisk.server.filemanager

import android.content.Context
import android.util.Log
import com.hld.networkdisk.server.beans.FileBean
import com.hld.networkdisk.server.beans.MessageTransferFileBean
import com.hld.networkdisk.server.commons.Constants
import com.hld.networkdisk.server.commons.getFileSuffix
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.LinkedList

class FileManager(context: Context) {
    private val baseFilePath = Constants.baseFilePath(context)

    /**
     * 重命名
     */
    fun reNameFileName(filePath: String, newName: String) {
        val file = File(baseFilePath + filePath)
        if (file.exists()) {
            file.renameTo(File(file.absolutePath.replace(file.name, newName)))
        }
    }

    /**
     * 删除文件
     */
    fun deleteFile(filePath: String) {
        val file = File(baseFilePath + filePath)
        deleteFileOrDir(file)
    }

    // 递归删除文件和文件夹
    private fun deleteFileOrDir(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                deleteFileOrDir(it)
            }
        }
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * 执行文件复制
     */
    fun doCopyFile(fromPath: String, toPath: String): Boolean {
        val fromFile = File(baseFilePath + fromPath)
        if (!fromFile.exists()) {
            return false
        }
        if (fromPath == toPath) {
            return true
        }
        val toFile = File(baseFilePath + toPath)
        if (toFile.parentFile?.exists() != true) {
            toFile.parentFile?.mkdirs()
        }
        if (!toFile.exists()) {
            toFile.createNewFile()
        }
        val fileInputStream = FileInputStream(fromFile)
        val fileOutputStream = FileOutputStream(toFile)
        try {
            val buffer = ByteArray(1024)
            var length: Int
            while (fileInputStream.read(buffer).also { length = it } > 0) {
                fileOutputStream.write(buffer, 0, length)
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
            fileOutputStream.close()
        }
        return true
    }

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
                    suffix = file.getFileSuffix(),
                    isDirectory = file.isDirectory,
                    fileLength = file.length(),
                    lastModified = file.lastModified(),
                )
            )
        }
        return list
    }

    /**
     * 只查询文件夹列表
     */
    fun queryFileDirList(path: String): List<FileBean> {
        val baseFile = File(baseFilePath + path)
        val list = LinkedList<FileBean>()
        baseFile.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                list.add(
                    FileBean(
                        name = file.name,
                        absolutePath = file.absolutePath.replace(baseFilePath, ""),
                        suffix = file.getFileSuffix(),
                        isDirectory = file.isDirectory,
                        fileLength = file.length(),
                        lastModified = file.lastModified(),
                    )
                )
            }
        }
        return list
    }

    /**
     * 接收客户端发来的文件
     */
    fun receiveFileFromStream(inputStream: InputStream, bean: MessageTransferFileBean): Pair<Boolean, String> {
        var fileOutputStream: FileOutputStream? = null
        try {
            Log.i(TAG, "=================================receiveFileFromStream :${baseFilePath + "/" + bean.filePath}")
            val outFile = fileReName(baseFilePath + "/" + bean.filePath) // 文件已存在，那么换个名字
            if (outFile.parentFile?.exists() != true) {
                outFile.parentFile?.mkdirs()
            }
            Log.i(TAG, "=================================receiveFileFromStream outFile:${outFile.absolutePath}")
            outFile.createNewFile()
            fileOutputStream = FileOutputStream(outFile)
            Log.i(TAG, "=================================receiveFileFromStream 开始")

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                fileOutputStream.write(buffer, 0, length)
                buffer.apply { // ==================================================
                    val sb = StringBuilder()
                    sb.append("[")
                    buffer.forEach { sb.append("${it},") }
                    sb.append("]")
                    Log.i(TAG, "=================================receiveFileFromStream bytesRead:${length} buffer:${sb.toString()}")
                }
            }
            Log.i(TAG, "=================================receiveFileFromStream 结束")
            return Pair(true, outFile.absolutePath)
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
        return Pair(false, "")
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
            val suffix = file.getFileSuffix()
            val newFilePath = filePath.replace(".$suffix", "(1).$suffix")
            return fileReName(newFilePath)
        }
        return file
    }

    companion object {
        private const val TAG = "FileManager"
    }
}