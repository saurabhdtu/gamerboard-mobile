package com.gamerboard.live.utils

import android.os.Build
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.log
import com.gamerboard.logging.LoggingAgent
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Created by saurabh.lahoti on 16/08/21
 */
fun logException(e: Exception, logger: LoggingAgent? = null) {
    e.printStackTrace()
    log(logger){
        it.setMessage(e.toString() + e.stackTrace.toString())
        it.setCategory(LogCategory.E)
    }
    if (debugMachine != DEBUGGER.DIRECT_HANDLE) FirebaseCrashlytics.getInstance().recordException(e)
}

fun logMessage(message: String?) {
    if (debugMachine == DEBUGGER.DIRECT_HANDLE)
        println(message)
    else
        message?.let { FirebaseCrashlytics.getInstance().log(it) }
}

fun Float.memoryToGB(): Float {
    return (this / (1024 * 1024 * 1024))
}

fun isEmulator(): Boolean =
    (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) || Build.FINGERPRINT.startsWith(
        "generic"
    ) || Build.FINGERPRINT.startsWith("unknown") || Build.HARDWARE.contains("goldfish") || Build.HARDWARE.contains(
        "ranchu"
    ) || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains(
        "Android SDK built for x86"
    ) || Build.MANUFACTURER.contains("Genymotion") || Build.PRODUCT.contains("sdk_google") || Build.PRODUCT.contains(
        "google_sdk"
    ) || Build.PRODUCT.contains("sdk") || Build.PRODUCT.contains("sdk_x86") || Build.PRODUCT.contains(
        "sdk_gphone64_arm64"
    ) || Build.PRODUCT.contains("vbox86p") || Build.PRODUCT.contains("emulator") || Build.PRODUCT.contains(
        "simulator"
    )

fun Float.memoryToMB(): Float {
    return (this / (1024 * 1024))
}