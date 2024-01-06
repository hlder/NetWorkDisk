package com.hld.networkdisk.server.networkapis

interface MessageApi {
    fun onRequest(request: String): String
}