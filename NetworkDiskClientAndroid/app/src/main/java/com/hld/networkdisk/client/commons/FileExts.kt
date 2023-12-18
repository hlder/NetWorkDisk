package com.hld.networkdisk.client.commons

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

fun String.getFileSuffix():String{
    val str = this.split(".")
    return if (str.isNotEmpty()) {
        str[str.size - 1]
    } else {
        ""
    }
}

// 判断后缀名是否是图片
fun String.suffixIsImg():Boolean{
    if (this == "png" || this == "jpg" || this == "webp" || this == "jpeg") {
        return true
    }
    return false
}