package com.hld.networkdisk.server.networkapis

interface PreviewImageApi {
    fun onRequest(request: String): String
}