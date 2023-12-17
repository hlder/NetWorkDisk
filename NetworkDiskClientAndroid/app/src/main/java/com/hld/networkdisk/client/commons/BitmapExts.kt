package com.hld.networkdisk.client.commons

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * bitmap转为base64
 * @return
 */
fun Bitmap.bitmapToBase64(): String? {
    var result: String? = null
    var baos: ByteArrayOutputStream? = null
    try {
        baos = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        baos.flush()
        baos.close()
        val bitmapBytes = baos.toByteArray()
        result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            if (baos != null) {
                baos.flush()
                baos.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return result
}

/**
 * base64转为bitmap
 * @return
 */
fun String.base64ToBitmap(): Bitmap? {
    try {
        val bytes = Base64.decode(this, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: IllegalArgumentException) {
        Log.e("dddd", "base64ToBitmap err:${e.localizedMessage}")
    }
    return null
}