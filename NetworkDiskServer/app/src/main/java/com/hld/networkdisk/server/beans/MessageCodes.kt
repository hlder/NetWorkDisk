package com.hld.networkdisk.server.beans

object MessageCodes {
    const val CODE_FILE_LIST = 1 // 获取文件列表
    const val CODE_CLIENT_RECEIVE_FROM_SERVER_FILE = 2 // 客户端收服务端的文件
    const val CODE_CLIENT_SEND_TO_SERVER_FILE = 3 // 客户端发文件给服务端
    const val CODE_QUERY_PREVIEW_IMAGE_BASE64 = 4 // 查询预览图的base64
    const val CODE_DO_COPY_FILE = 5 // 执行复制文件
    const val CODE_DO_DELETE_FILE = 6 // 执行删除文件
    const val CODE_DO_MOVE_FILE = 7 // 执行移动文件
    const val CODE_DO_RE_NAME_FILE = 8 // 执行文件重命名
    const val CODE_DO_CREATE_FILE_DIR = 9 // 创建一个文件夹
}