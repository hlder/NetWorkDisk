package com.hld.networkdisk.server.networkImpls

import androidx.activity.ComponentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hld.networkdisk.server.beans.MessageBean
import com.hld.networkdisk.server.beans.MessageCodes
import com.hld.networkdisk.server.beans.RequestCopyFileBean
import com.hld.networkdisk.server.beans.RequestRenameFileBean
import com.hld.networkdisk.server.data.AppDatabase
import com.hld.networkdisk.server.data.PreviewDao
import com.hld.networkdisk.server.filemanager.FileManager
import com.hld.networkdisk.server.networkapis.MessageApi

class MessageApiImpl(private val activity: ComponentActivity) : MessageApi {
    private val gson = Gson()
    private val fileManager: FileManager = FileManager(activity)

    override fun onRequest(request: String): String {
        val messageBean = gson.fromJson(request, MessageBean::class.java)

        return when (messageBean.code) {
            MessageCodes.CODE_FILE_LIST -> queryFileList(messageBean) // 查询文件列表
            MessageCodes.CODE_QUERY_FILE_DIR_LIST -> queryFileDirList(messageBean) // 查询文件夹列表
            MessageCodes.CODE_DO_COPY_FILE -> doCopyFile(messageBean) // 执行复制文件
            MessageCodes.CODE_DO_MOVE_FILE -> doMoveFile(messageBean) // 执行移动文件
            MessageCodes.CODE_DO_RE_NAME_FILE -> doRenameFile(messageBean) // 执行重命名
            MessageCodes.CODE_DO_DELETE_FILE -> doDeleteFile(messageBean) // 执行删除文件
            else -> ""
        }
    }

    /**
     * 查询文件列表
     */
    private fun queryFileList(fromMessage: MessageBean): String {
        val listFileBean = fileManager.queryFileList(fromMessage.message)
        val listPreview = AppDatabase.getInstance(activity).previewDao().query(listFileBean.map { it.absolutePath }.toTypedArray())
        listFileBean.forEach { item ->
            val bean: PreviewDao.Bean? = listPreview?.find { it.fileAbsolutePath == item.absolutePath }
            item.previewImageBase64 = bean?.previewImageBase64
        }
        return resultSuccess(fromMessage, listFileBean)
    }

    private fun queryFileDirList(fromMessage: MessageBean): String {
        val listFileBean = fileManager.queryFileDirList(fromMessage.message)
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
    private fun doCopyFile(fromMessage: MessageBean): String {
        val bean = gson.fromJson(fromMessage.message, RequestCopyFileBean::class.java)
        val isCopySuccess = fileManager.doCopyFile(bean.fromFilePath, bean.toFilePath)
        return resultSuccess(fromMessage, "$isCopySuccess")
    }

    /**
     * 执行文件移动
     */
    private fun doMoveFile(fromMessage: MessageBean): String {
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
    private fun doRenameFile(fromMessage: MessageBean): String {
        val bean = gson.fromJson(fromMessage.message, RequestRenameFileBean::class.java)
        fileManager.reNameFileName(bean.filePath, bean.newFileName)
        return resultSuccess(fromMessage)
    }

    /**
     * 执行文件删除
     */
    private fun doDeleteFile(fromMessage: MessageBean): String {
        println("========================doDeleteFile:${fromMessage.message}")
        val listFilePath: List<String> = gson.fromJson(fromMessage.message, object : TypeToken<List<String>>() {}.type)
        listFilePath.forEach {
            fileManager.deleteFile(it)
        }
        return resultSuccess(fromMessage)
    }

    /**
     * 返回成功消息
     */
    private fun resultSuccess(fromMessage: MessageBean, obj: Any? = null): String {
        return gson.toJson(MessageBean(code = fromMessage.code, version = fromMessage.version, message = obj?.let { gson.toJson(it) } ?: ""))
    }
}