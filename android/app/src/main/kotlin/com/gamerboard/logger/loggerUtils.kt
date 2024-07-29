package com.gamerboard.logger

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val UTC_FORMAT = "yyyy-MM-dd HH:mm:ss"
    fun formatTime(time: Long): String {
        return SimpleDateFormat(UTC_FORMAT, Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date(time))
    }
}

fun getLast(call: Int = 3): String {
    return Thread.currentThread().stackTrace.copyOfRange(
        3,
        Thread.currentThread().stackTrace.size / 2
    ).contentToString()
}
