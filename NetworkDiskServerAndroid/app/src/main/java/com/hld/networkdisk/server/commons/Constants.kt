package com.hld.networkdisk.server.commons

import android.content.Context

object Constants {
    // 发消息的端口
    const val SERVER_PORT_MESSAGE = 20810
    // 传输文件的端口
    const val SERVER_PORT_FILE = 20811
    // 传输预览图得端口
    const val SERVER_PORT_PREVIEW_IMAGE = 20812


    /**
     * 获取basePath
     */
    fun baseFilePath(context: Context): String {
        return context.getExternalFilesDir("server")?.absolutePath ?: ""
    }
}