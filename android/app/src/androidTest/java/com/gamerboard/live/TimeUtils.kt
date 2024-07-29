package com.gamerboard.live

import java.util.Calendar
import java.util.Date
import java.util.TimeZone

fun nowWithAdd(seconds : Long) : Date {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.add(Calendar.SECOND , seconds.toInt())
    return calendar.time
}