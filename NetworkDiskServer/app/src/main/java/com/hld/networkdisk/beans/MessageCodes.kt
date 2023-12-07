package com.hld.networkdisk.beans

object MessageCodes {
    const val CODE_FILE_LIST = 1 // 获取文件列表
    const val CODE_CLIENT_RECEIVE_FROM_SERVER_FILE = 2 // 客户端收服务端的文件
    const val CODE_CLIENT_SEND_TO_SERVER_FILE = 3 // 客户端发文件给服务端
}