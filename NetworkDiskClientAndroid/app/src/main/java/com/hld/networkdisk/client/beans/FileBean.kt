package com.hld.networkdisk.client.beans

data class FileBean(
    val name: String, // 文件名
    val absolutePath: String, // 文件绝对路径
    val suffix: String, // 文件后缀名
    val isDirectory: Boolean, // 是否是文件夹
    val fileLength: Long, // 文件大小
    val lastModified: Long, // 最近修改的时间
    var previewImageBase64: String? = null // 预览的base64
)