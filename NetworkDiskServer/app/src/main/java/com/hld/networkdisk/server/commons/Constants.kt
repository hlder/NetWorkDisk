package com.hld.networkdisk.server.commons

import android.content.Context

object Constants {
    const val SERVER_PORT_MESSAGE = 20810
    const val SERVER_PORT_FILE = 20811
    const val SERVER_PORT_PREVIEW_IMAGE = 20812


    /**
     * 获取basePath
     */
    fun baseFilePath(context: Context): String {
        return context.getExternalFilesDir("server")?.absolutePath ?: ""
    }
}