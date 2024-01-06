package com.hld.networkdisk.server.networkapis

import java.io.InputStream
import java.io.OutputStream

interface FileTransferApi {
    fun onRequest(request: String, inputStream: InputStream, outputStream: OutputStream): Unit
}