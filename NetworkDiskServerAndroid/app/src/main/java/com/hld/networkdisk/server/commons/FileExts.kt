package com.hld.networkdisk.server.commons

import java.io.File

// 获取文件的后缀
fun File.getFileSuffix(): String {
    val str = this.absolutePath.split(".")
    return if (str.isNotEmpty()) {
        str[str.size - 1]
    } else {
        ""
    }
}