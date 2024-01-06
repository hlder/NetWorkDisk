package com.hld.networkdisk.server.beans

data class RequestCopyFileBean(
    val fromFilePath: String,
    val toFilePath: String
)