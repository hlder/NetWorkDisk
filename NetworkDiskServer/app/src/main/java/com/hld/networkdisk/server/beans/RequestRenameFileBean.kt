package com.hld.networkdisk.server.beans

data class RequestRenameFileBean(
    val filePath: String,
    val newFileName: String
)