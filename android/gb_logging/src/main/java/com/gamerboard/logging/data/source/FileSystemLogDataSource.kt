package com.gamerboard.logging.data.source

import android.content.Context
import android.os.Environment
import com.gamerboard.logging.model.LogEntry
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.Calendar
import kotlin.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FileSystemLogDataSource(context: Context) : LogDataSource {
    private var parentFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "new_logs"
    ).also {
        try{
            if (!it.exists()) {
                it.mkdirs()
            }
        }catch (ex : Exception){
            ex.printStackTrace()
            FirebaseCrashlytics.getInstance().log(ex.stackTraceToString())
        }
    }

    private val mutex: Mutex = Mutex()

    private var logFile: File? = null

    private fun getLogFile(): File {
        if (logFile?.exists() == true) {
            return logFile!!
        }
        return createNewLogFile()
    }

    private fun createNewLogFile(): File {
        if (parentFile.exists()) {
            parentFile.mkdirs()
        }
        return File(parentFile, "${System.currentTimeMillis()}").apply {
            if (!exists()) {
                createNewFile()
            }
            logFile = this
        }
    }

    override suspend fun log(message: LogEntry) {
        mutex.withLock {
            try {
                getLogFile().appendText("${message.identifier}::${message.message}::${message.createdAt.time}\n")
            } catch (ex: Exception) {
                ex.printStackTrace()
                FirebaseCrashlytics.getInstance().log(ex.stackTraceToString())
            }
        }
    }

    override suspend fun rewriteFailedEntry(message: LogEntry) {
        log(message)
    }

    override suspend fun getLogs(): List<LogEntry> {
        mutex.withLock {
            try {
                return getLogFile().readLines().map {
                    parseLog(it)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                FirebaseCrashlytics.getInstance().log(ex.stackTraceToString())
            }
        }
        return emptyList()
    }

    private fun parseLog(it: String): LogEntry {
        val data = it.split("::")
        return LogEntry(
            identifier = data.firstOrNull()?.let{
                it.ifBlank { null }
            },
            message = data.getOrNull(1)?.toString()?.trim() ?: "",
            createdAt = Calendar.getInstance().apply {
                timeInMillis = data.getOrNull(2)?.toLong() ?: 0L
            }.time
        )
    }

    override suspend fun readInChunks(
        chunkSize: Int,
        callback: suspend (List<LogEntry>) -> List<LogEntry>,
    ) {
        val files = parentFile.listFiles()
        try {
            createNewLogFile()
            files?.forEach { file ->
                if (file.exists()) {
                    forEachLine(file, chunkSize, callback)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            FirebaseCrashlytics.getInstance().log(ex.stackTraceToString())
        }
    }

    private suspend fun forEachLine(file : File, chunkSize: Int, callback: suspend  (List<LogEntry>) -> List<LogEntry>)  = suspendCoroutine<List<LogEntry>?> { continuation ->
        val logs = arrayListOf<LogEntry>()
        file.forEachLine {
            logs.add(parseLog(it))
            if (logs.size == chunkSize) {
                runBlocking { callback(logs) }
                logs.clear()
            }
        }
        continuation.resume(null)
    }

    override suspend fun getCount(): Long {
        return 0
    }

    override suspend fun getLogsAndClear(): List<LogEntry> {
        return getLogs().also {
            delete()
        }
    }

    override suspend fun delete() {
        mutex.withLock {
            parentFile.listFiles()?.forEach {
                it.delete()
            }
        }
    }

    override suspend fun delete(ids: List<String>) {

    }

    override suspend fun markAllUnread() {
    }

    override suspend fun markUnread(ids: List<String>) {
    }

    override suspend fun markRead(ids: List<String>) {

    }
}