package com.hld.networkdisk.client.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.hld.networkdisk.client.commons.getFileSuffix
import java.io.File
import java.text.DecimalFormat


object FileUtils {
    private val decimalFormat = DecimalFormat("0.##")

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

    fun openFile(context: Context, filePath: String) {
        val suffix = filePath.getFileSuffix()
        val type = mapOpenFile[suffix]

        val photoURI = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName+".fileProvider",
            File(filePath)
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(photoURI, type)
        context.startActivity(intent)
    }

    private val mapOpenFile = mapOf(
        ".3gp" to "video/3gpp",
        ".apk" to "application/vnd.android.package-archive",
        ".asf" to "video/x-ms-asf",
        ".avi" to "video/x-msvideo",
        ".bin" to "application/octet-stream",
        ".bmp" to "image/bmp",
        ".c" to "text/plain",
        ".class" to "application/octet-stream",
        ".conf" to "text/plain",
        ".cpp" to "text/plain",
        ".doc" to "application/msword",
        ".docx" to "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        ".xls" to "application/vnd.ms-excel",
        ".xlsx" to "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        ".exe" to "application/octet-stream",
        ".gif" to "image/gif",
        ".gtar" to "application/x-gtar",
        ".gz" to "application/x-gzip",
        ".h" to "text/plain",
        ".htm" to "text/html",
        ".html" to "text/html",
        ".jar" to "application/java-archive",
        ".java" to "text/plain",
        ".jpeg" to "image/jpeg",
        ".jpg" to "image/jpeg",
        ".js" to "application/x-javascript",
        ".log" to "text/plain",
        ".m3u" to "audio/x-mpegurl",
        ".m4a" to "audio/mp4a-latm",
        ".m4b" to "audio/mp4a-latm",
        ".m4p" to "audio/mp4a-latm",
        ".m4u" to "video/vnd.mpegurl",
        ".m4v" to "video/x-m4v",
        ".mov" to "video/quicktime",
        ".mp2" to "audio/x-mpeg",
        ".mp3" to "audio/x-mpeg",
        ".mp4" to "video/mp4",
        ".mpc" to "application/vnd.mpohun.certificate",
        ".mpe" to "video/mpeg",
        ".mpeg" to "video/mpeg",
        ".mpg" to "video/mpeg",
        ".mpg4" to "video/mp4",
        ".mpga" to "audio/mpeg",
        ".msg" to "application/vnd.ms-outlook",
        ".ogg" to "audio/ogg",
        ".pdf" to "application/pdf",
        ".png" to "image/png",
        ".pps" to "application/vnd.ms-powerpoint",
        ".ppt" to "application/vnd.ms-powerpoint",
        ".pptx" to "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        ".prop" to "text/plain",
        ".rc" to "text/plain",
        ".rmvb" to "audio/x-pn-realaudio",
        ".rtf" to "application/rtf",
        ".sh" to "text/plain",
        ".tar" to "application/x-tar",
        ".tgz" to "application/x-compressed",
        ".txt" to "text/plain",
        ".wav" to "audio/x-wav",
        ".wma" to "audio/x-ms-wma",
        ".wmv" to "audio/x-ms-wmv",
        ".wps" to "application/vnd.ms-works",
        ".xml" to "text/plain",
        ".z" to "application/x-compress",
        ".zip" to "application/x-zip-compressed"
    )

}