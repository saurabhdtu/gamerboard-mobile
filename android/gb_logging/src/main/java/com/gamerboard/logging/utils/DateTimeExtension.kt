package com.gamerboard.logging.utils

import java.util.Calendar
import java.util.Date
import java.util.TimeZone

fun now() : Date {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    return calendar.time
}

