package com.gamerboard.live.utils

import android.app.ActivityManager
import android.app.Service
import android.content.Context

fun Context.getTotalMemory(total : Boolean) : Long{
    val actManager = getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    actManager.getMemoryInfo(memInfo)
    return (if (total) memInfo.totalMem else memInfo.availMem)
}