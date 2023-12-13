package com.hld.networkdisk.server.commons

import android.content.Context

object Constants {
    /**
     * 获取basePath
     */
    fun baseFilePath(context: Context): String {
        return context.getExternalFilesDir("server")?.absolutePath ?: ""
    }
}