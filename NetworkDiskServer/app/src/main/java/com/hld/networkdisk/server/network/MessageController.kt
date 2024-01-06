package com.hld.networkdisk.server.network

import androidx.activity.ComponentActivity
import com.google.gson.Gson
import com.hld.networkdisk.server.beans.MessageBean
import com.hld.networkdisk.server.beans.RequestCopyFileBean
import com.hld.networkdisk.server.beans.RequestDeleteFileBean
import com.hld.networkdisk.server.beans.RequestRenameFileBean
import com.hld.networkdisk.server.data.AppDatabase
import com.hld.networkdisk.server.data.PreviewDao
import com.hld.networkdisk.server.filemanager.FileManager

class MessageController(
    private val activity: ComponentActivity,
) {
    private val fileManager: FileManager = FileManager(activity)
    private val gson = Gson()

    /**
     * 查询文件列表
     */
    fun queryFileList(fromMessage: MessageBean): String {
        val listFileBean = fileManager.queryFileList(fromMessage.message)
        val listPreview = AppDatabase.getInstance(activity).previewDao().query(listFileBean.map { it.absolutePath }.toTypedArray())
        listFileBean.forEach { item ->
            val bean: PreviewDao.Bean? = listPreview?.find { it.fileAbsolutePath == item.absolutePath }
            item.previewImageBase64 = bean?.previewImageBase64
        }
        return resultSuccess(fromMessage, listFileBean)
    }

    /**
     * 执行文件复制
     */
    fun doCopyFile(fromMessage: MessageBean): String {
        val bean = gson.fromJson(fromMessage.message, RequestCopyFileBean::class.java)
        val isCopySuccess = fileManager.doCopyFile(bean.fromFilePath, bean.toFilePath)
        return resultSuccess(fromMessage, "$isCopySuccess")
    }

    /**
     * 执行文件移动
     */
    fun doMoveFile(fromMessage: MessageBean): String {
        val bean = gson.fromJson(fromMessage.message, RequestCopyFileBean::class.java)
        val isCopySuccess = fileManager.doCopyFile(bean.fromFilePath, bean.toFilePath)
        if (isCopySuccess) {
            fileManager.deleteFile(bean.fromFilePath)
        }
        return resultSuccess(fromMessage, "$isCopySuccess")
    }

    /**
     * 执行文件重命名
     */
    fun doRenameFile(fromMessage: MessageBean): String {
        val bean = gson.fromJson(fromMessage.message, RequestRenameFileBean::class.java)
        fileManager.reNameFileName(bean.filePath, bean.newFileName)
        return resultSuccess(fromMessage)
    }

    /**
     * 执行文件删除
     */
    fun doDeleteFile(fromMessage: MessageBean): String {
        val bean = gson.fromJson(fromMessage.message, RequestDeleteFileBean::class.java)
        fileManager.deleteFile(bean.filePath)
        return resultSuccess(fromMessage)
    }

    /**
     * 返回成功消息
     */
    private fun resultSuccess(fromMessage: MessageBean, obj: Any? = null): String {
        return gson.toJson(MessageBean(code = fromMessage.code, version = fromMessage.version, message = obj?.let { gson.toJson(it) } ?: ""))
    }
}