package com.hld.networkdisk.server.beans

data class MessageBean(
    val version: Int,
    val code: Int,
    val message: String
)