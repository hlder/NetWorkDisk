package com.hld.networkdisk.client.utils

import android.icu.util.Calendar
import android.text.format.DateFormat

object DateFormatUtil {
    fun formatTimeStamp(timeStamp: Long): String {
        val today = Calendar.getInstance()
        val todayStr = DateFormat.format("yyyy-MM-dd", today.timeInMillis)
        val yesTodayStr = DateFormat.format("yyyy-MM-dd", today.timeInMillis - 24 * 60 * 60 * 1000)
        val timeStr = DateFormat.format("yyyy-MM-dd", timeStamp)

        return if (todayStr == timeStr) { // 今天的
            DateFormat.format("今天 kk:mm", timeStamp).toString()
        } else if(yesTodayStr == timeStr){ // 昨天的
            DateFormat.format("昨天 kk:mm", timeStamp).toString()
        } else {
            timeStr.toString()
        }
    }
}