package com.gamerboard.logger

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.os.Build
import android.os.Debug
import android.os.Environment
import android.provider.Settings
import androidx.work.*
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.RemoteConfigConstants
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.models.ImageSavingConfig
import com.gamerboard.live.service.screencapture.FileAndDataSync
import com.gamerboard.live.utils.logException
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.File
import kotlin.math.ceil

val LoggerModule = module {
    single {
        Logger(androidContext())
    }
}

@SuppressLint("HardwareIds")
@Deprecated("Use GBLog instead to log message. Helper functions can be found in LogHelper")
class Logger(val ctx: Context) {
    private var deviceId: String? = null
    private var appState: String = "Un-known"
    private var userId: String? = null
    private var lockCommit: Boolean = false
    private val json = Json { encodeDefaults = true }
    private val file = File(
        "${ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/logs",
        "logs.txt"
    )
    private val lock = Object()


    companion object {
        val loggingFlags: JSONObject by lazy {
            JSONObject(
                FirebaseRemoteConfig.getInstance().getString(RemoteConfigConstants.LOGGING_FLAG)
            )
        }
    }


    private val memoryUsageLog = File(
        "${ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/logs",
        "memory-usage.txt"
    )

    private var previousLog = -1

    private val primaryData = JSONObject()

    private var prefsHelper = (ctx as GamerboardApp).prefsHelper

    init {
        prefsHelper.getString(SharedPreferenceKeys.APP_STATE)?.let { appState = it }
        prefsHelper.getString(SharedPreferenceKeys.UUID) ?: Settings.Secure.getString(
            ctx.contentResolver,
            Settings.Secure.ANDROID_ID
        ).let { deviceId = it }
        primaryData.also {
            it.put("appV", BuildConfig.VERSION_NAME)
            it.put("vc", BuildConfig.VERSION_CODE)
            it.put("device", "${Build.DEVICE}  ${Build.MODEL}")
            it.put("api", Build.VERSION.SDK_INT.toString())
            it.put("deviceId", deviceId)
        }
    }


    // set commit log to true, that will send the logs to the server
    fun log(
        message: String,
        category: LogCategory = LogCategory.D,
        logToConsole: Boolean = true,
        commitLog: Boolean = false,
        platform: PlatformType = PlatformType.A
    ) {
        if (userId == null)
            userId = prefsHelper.getString(SharedPreferenceKeys.USER_ID)
        var game = ""
        if (MachineConstants.isGameInitialized())
            game += " ${MachineConstants.currentGame.gameName}"
        if (message.isNotEmpty()) {
            val log = Log(
                cat = category,
                uId = userId,
                msg = message,
                game = game,
                platformType = platform
            )
            logToFile(log, logToConsole, commitLog)
        }
    }

    fun oneTimeLog(
        tag: String,
        code: Int,
        message: String,
        metadata: String = "",
        category: LogCategory = LogCategory.D,
        logToConsole: Boolean = true
    ) {
        if (previousLog != code) {
            previousLog = code
            if (userId == null)
                userId = prefsHelper.getString(SharedPreferenceKeys.USER_ID)
            val log = Log(
                cat = category,
                uId = userId,
                msg = "$tag: $message",
                game = "",
                platformType = PlatformType.A
            )
            logToFile(log, logToConsole)
            if (logToConsole)
                android.util.Log.d(metadata, message)
        }
    }

    private fun logToFile(log: Log, logToConsole: Boolean = true, commitLog: Boolean = false) {
        synchronized(lock) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    file.also { file ->
                        file.parentFile.mkdirs()
                        if (!file.exists()) {
                            file.createNewFile()
                            file.appendText("$primaryData \n")
                        }
                        val l = json.encodeToString(log)
                        if (logToConsole)
                            android.util.Log.i("LOGGER", l)
                        file.appendText("$l\n")
                    }
                    if (file.length() > logFileSize * 1024 * 1024) {
                        renameLogFile()
                    }
                } catch (e: Exception) {
                    logException(e)
                }
                if (commitLog) {
                    commitLog()
                }
            }
        }
    }

    fun lock() {
        lockCommit = true
    }

    fun unlock() {
        lockCommit = false
    }

    suspend fun commitLog() {

        var fileSizeMb = (file.length() / (1024.0 * 1024.0) * 100).toInt() / 100.0
        fileSizeMb = ceil((fileSizeMb / 0.5)) * 0.5

        delay(200)
        var imageSavingValues = gson.fromJson(
            FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigConstants.SAVE_IMAGES), ImageSavingConfig::class.java
        )

        if (imageSavingValues.save) {
            FileAndDataSync.startZipSync(ctx)
        }
        //                if (BuildConfig.DEBUG)
        //                    FileAndDataSync.startZipSync(ctx)
        sendLogs()
    }

    private fun renameLogFile() {
        val dateTime = DateUtils.formatTime(System.currentTimeMillis())
        if (file.exists()) {
            val uploadFile = File(file.parent, "logs|$dateTime.txt")
            file.renameTo(uploadFile)
        }
        if (loggingFlags.optBoolean("memory_logs"))
            if (memoryUsageLog.exists()) {
                val uploadFile = File(file.parent, "memorylogs|$dateTime.txt")
                memoryUsageLog.renameTo(uploadFile)
            }
    }

    @SuppressLint("RestrictedApi")
    fun sendLogs() {
        renameLogFile()
        val data = Data.Builder()
        data.putString("deviceId", deviceId)
        val internetConstants =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val uploadLogsRequest =
            OneTimeWorkRequestBuilder<LogSyncWorker>().setInputData(data.build())
                .setConstraints(internetConstants).build()
        WorkManager.getInstance(ctx).enqueueUniqueWork(
            "com.gamerboard.live-sync-logs",
            ExistingWorkPolicy.KEEP,
            uploadLogsRequest
        )


        /*CoroutineScope(Dispatchers.IO).launch {
            if (file.exists()) {
                val dateTime = DateUtils.formatTime(System.currentTimeMillis())
                val uploadFile = File(file.parent, "logs-${dateTime.split(" ")[1]}.txt")
                file.renameTo(uploadFile)
                val fileUri = Uri.fromFile(uploadFile)
                // upload
                val storageRef = Firebase.storage.reference
                storageRef.child("logs/${deviceId}/${dateTime.split(" ")[0]}/${fileUri.lastPathSegment}")
                        .putFile(fileUri)
                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, "Sending Logs...", Toast.LENGTH_SHORT).show()
                }
                uploadFile.delete()
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, "No Logs recorded!", Toast.LENGTH_SHORT).show()
                }
            }
        }*/
    }

    fun writeMemoryUsageLog() {
        if (loggingFlags.optBoolean("memory_logs"))
            try {
                memoryUsageLog.parentFile.mkdirs()
                memoryUsageLog.also { file ->
                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    val mi = ActivityManager.MemoryInfo()
                    val activityManager =
                        ctx.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
                    activityManager.getMemoryInfo(mi)
                    val runtime = Runtime.getRuntime()
                    val freeMem = runtime.freeMemory()
                    val max = runtime.maxMemory()
                    val total = runtime.totalMemory()
                    val nativeHeap = Debug.getNativeHeapSize()
                    val nativeHeapFreeSize = Debug.getNativeHeapFreeSize()
                    val nativeHeapAllocated = Debug.getNativeHeapAllocatedSize()
                    val mb = 1024 * 1024
                    /*  Log.i("memory-info-distribution","""
                          nativePss: ${first.nativePss}
                          dalvikPss: ${first.dalvikPss}
                          otherPss: ${first.otherPss}
                          nativeShareDirty: ${first.nativeSharedDirty}
                          dalvikSharedDirty: ${first.dalvikSharedDirty}
                          otherSharedDirty: ${first.otherSharedDirty}
                          nativePrivateDirty: ${first.nativePrivateDirty}
                          dalvikPrivateDirty: ${first.dalvikPrivateDirty}
                          totalPrivateDirty: ${first.totalPrivateDirty}
                      """.trimIndent())*/
                    val j = JSONObject().apply {
                        put("timestamp", DateUtils.formatTime(System.currentTimeMillis()))
                        put("sys-avail", mi.availMem / mb)
                        put("sys-total", mi.totalMem / mb)
                        put("sys-thresh", mi.threshold / mb)
                        put("low-mem", mi.lowMemory)
                        put("runtime-freeMem", freeMem / mb)
                        put("runtime_max", max / mb)
                        put("runtime_total", total / mb)
                        put("native_heap", nativeHeap / mb)
                        put("native_heap_free", nativeHeapFreeSize / mb)
                        put("native_heap_alloc", nativeHeapAllocated / mb)
                    }
                    android.util.Log.i("memory-usage-log", j.toString())
                    if (file.length() > 5 * mb)
                        file.writeText("$j,")
                    else
                        file.appendText("$j,")

                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
    }
}


val gson = Gson()
