package com.hld.networkdisk.client.beans

/**
 * 需要收发文件时，客户端发来的message
 */
data class MessageTransferFileBean(
    val address: String,
    val port: Int,
    val filePath: String, // 在服务端的文件路径
    val isClientSendToServer: Boolean, // true表示客户端发送文件到服务端，false反之
    val fileLength: Long, // 文件大小
)