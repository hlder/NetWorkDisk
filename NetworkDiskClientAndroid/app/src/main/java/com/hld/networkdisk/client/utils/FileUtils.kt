package com.hld.networkdisk.client.utils

import java.text.DecimalFormat

object FileUtils {
    private val decimalFormat= DecimalFormat("0.##")

    fun getFileSizeStr(fileLength: Long): String {
        val B: Long = fileLength
        if (B < 1024f) {
            return "${decimalFormat.format(B)}B"
        }
        val KB = B / 1024f
        if (KB < 1024f) {
            return "${decimalFormat.format(KB)}KB"
        }
        val MB = KB / 1024f
        if (MB < 1024f) {
            return "${decimalFormat.format(MB)}MB"
        }
        val GB = MB / 1024f
        return "${decimalFormat.format(GB)}GB"
    }
}