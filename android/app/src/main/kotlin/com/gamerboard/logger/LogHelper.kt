package com.gamerboard.logger

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.app.Service
import android.content.Context
import android.graphics.Bitmap
import android.os.Debug
import android.os.Environment
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.RemoteConfigConstants
import com.gamerboard.live.gamestatemachine.games.GameHelper
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.models.ImageSavingConfig
import com.gamerboard.live.service.screencapture.FileAndDataSync
import com.gamerboard.live.utils.logException
import com.gamerboard.logger.agent.OldLoggerAgent
import com.gamerboard.logger.model.GameLogMessage
import com.gamerboard.logger.model.OcrInfoMessage
import com.gamerboard.logging.GBLog
import com.gamerboard.logging.LoggingAgent
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File


class LogHelper private constructor(val context: Context) : KoinComponent {

    private val logger: Logger by inject()

    companion object {

        @SuppressLint("HardwareIds")
        fun createInstance(context: Context): LogHelper {

            return LogHelper(context)
        }
    }

    fun completeLogging() {
        if (GBLog.instance.factory is OldLoggerAgent.Factory) {
            CoroutineScope(Dispatchers.IO).launch {
                logger.commitLog()
            }
        } else {
            val imageSavingValues = gson.fromJson(
                FirebaseRemoteConfig.getInstance().getString(RemoteConfigConstants.SAVE_IMAGES),
                ImageSavingConfig::class.java
            )
            if (imageSavingValues.save) {
                FileAndDataSync.startZipSync(context)
            }
            UploadLogWorker.start(context)

        }

    }

    fun logImage(gameId: String?, image: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tempFile = File.createTempFile(gameId ?: "unknown", ".png")
                image.compress(Bitmap.CompressFormat.PNG, 100, tempFile.outputStream())
                val firebasePath =
                    "visionImagesV2/android/${MachineConstants.currentGame.packageName}/${GamerboardApp.sessionId}/${gameId}"
                val ref =
                    FirebaseStorage.getInstance().getReference(firebasePath).child(tempFile.name)
                ref.putStream(tempFile.inputStream()).await()
                val downloadUrl = ref.downloadUrl.await()
                logWithIdentifier(gameId) {
                    it.setMessage("Query Ocr")
                    it.setOcrInfo(
                        OcrInfoMessage(
                            vision = OcrInfoMessage.Vision(
                                success = false, visionImage = downloadUrl.toString()
                            )
                        )
                    )
                    it.setCategory(LogCategory.AUTOML_IMAGE)
                }
                tempFile.delete()
            } catch (ex: Exception) {
                logException(ex, loggerWithIdentifier(gameId))
            }
        }
    }

    fun getMemoryInfo(): MemoryInfo {
        val mi = MemoryInfo()
        val activityManager = context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        return mi
    }

    fun logMemoryUsage() {
       try {
            val mi = getMemoryInfo()
            val runtime = Runtime.getRuntime()
            val freeMem = runtime.freeMemory()
            val max = runtime.maxMemory()
            val total = runtime.totalMemory()
            val nativeHeap = Debug.getNativeHeapSize()
            val nativeHeapFreeSize = Debug.getNativeHeapFreeSize()
            val nativeHeapAllocated = Debug.getNativeHeapAllocatedSize()
            val mb = 1024 * 1024
            log {
                it.setMessage("Write Memory usage log")
                it.addContext("timestamp", DateUtils.formatTime(System.currentTimeMillis()))
                it.addContext("sys-avail", mi.availMem / mb)
                it.addContext("sys-total", mi.totalMem / mb)
                it.addContext("sys-thresh", mi.threshold / mb)
                it.addContext("low-mem", mi.lowMemory)
                it.addContext("runtime-freeMem", freeMem / mb)
                it.addContext("runtime_max", max / mb)
                it.addContext("runtime_total", total / mb)
                it.addContext("native_heap", nativeHeap / mb)
                it.addContext("native_heap_free", nativeHeapFreeSize / mb)
                it.addContext("native_heap_alloc", nativeHeapAllocated / mb)
                it.addContext("cpu_core", runtime.availableProcessors())
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

fun loggerWithIdentifier(identifier: String?): LoggingAgent {
    if (identifier?.contains(":") == true) {
        return GBLog.instance.with(identifier.split(":").firstOrNull())
    }
    return GBLog.instance.with(identifier)
}

fun logger(): LoggingAgent {

    return GBLog.instance.with(null)
}

fun log(message: String) {
    logger().apply {
        val logMessage = GameLogMessage.Builder(identifier).setMessage(message).build()
        log(logMessage)
    }
}

private fun Array<StackTraceElement>?.buildStackTraceString(): String {
    val sb = StringBuilder()
    if (!this.isNullOrEmpty()) {
        for (element in this) {
            sb.append(element.toString() + "\n")
        }
    }
    return sb.toString()
}

fun logFlutter(message: String) {

    loggerWithIdentifier(GameHelper.getGameId(stackTrace())).apply {
        val logMessage =
            GameLogMessage.Builder(identifier).setMessage(message).setPlatform(PlatformType.F)
                .build()
        log(logMessage)
    }
}

fun logWithCategory(logger: LoggingAgent?, message: String, category: LogCategory) {

    logger?.log(
        GameLogMessage.Builder(logger.identifier).setCategory(category).setMessage(message).build()
    )
}

fun logWithCategory(message: String, category: LogCategory) {

    loggerWithIdentifier(GameHelper.getGameId(stackTrace())).apply {
        log(GameLogMessage.Builder(identifier).setCategory(category).setMessage(message).build())
    }
}

fun log(builder: (GameLogMessage.Builder) -> GameLogMessage.Builder) {
    log(loggerWithIdentifier(GameHelper.getGameId(stackTrace())), builder)
}

fun stackTrace(): Array<StackTraceElement> = (Exception()).stackTrace

fun logWithIdentifier(
    identifier: String?,
    message: String,
) {
    log(loggerWithIdentifier(identifier)) {
        it.setMessage(message)
    }
}

fun logWithIdentifier(
    identifier: String?,
    builder: (GameLogMessage.Builder) -> GameLogMessage.Builder,
) {
    log(loggerWithIdentifier(identifier), builder)
}

fun log(logger: LoggingAgent?, builder: (GameLogMessage.Builder) -> GameLogMessage.Builder) {
    logger?.log(builder(GameLogMessage.Builder(logger.identifier)).build())
}

fun log(logger: LoggingAgent?, message: String) {

    log(logger) {
        it.setMessage(message)
    }
}
