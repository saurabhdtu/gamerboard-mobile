package com.gamerboard.logger

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.gamerboard.live.BuildConfig
import com.gamerboard.logger.agent.LogPublisher
import com.gamerboard.logger.agent.PublishResult
import com.gamerboard.logger.model.GameLogMessage
import com.gamerboard.logging.LogManager
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.koin.java.KoinJavaComponent.inject
import java.util.Date

class UploadLogWorker(var context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    private val pubSubRestClient: LogPublisher by inject(LogPublisher::class.java)
    private val logManager: LogManager by inject(LogManager::class.java)

    companion object {
        fun start(context: Context) {
            val internetConstants =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val uploadLogsRequest =
                OneTimeWorkRequestBuilder<UploadLogWorker>()
                    .setConstraints(internetConstants).build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                TAG,
                ExistingWorkPolicy.KEEP,
                uploadLogsRequest
            )
        }

        val TAG = "com.gamerboard.live.${UploadLogWorker::class.java.simpleName}"
    }

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = false
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun doWork(): Result {

        logManager.markAllUnread()
        pubSubRestClient.prepare()

        var failed = false
        logManager.readInChunks(200, callback = { logEntries ->

            logEntries.chunked(20).forEach {
                logManager.markRead(it.map { it.id })
            }

            val failedEntries = pubSubRestClient.publishMessages(
                BuildConfig.FLAVOR,
                logEntries
            )
            val failedIds = failedEntries.map { it.logEntry.id }
            failed = failed || failedIds.isNotEmpty()
            return@readInChunks logEntries.filter { failedIds.contains(it.id).not() }
        })

        logManager.clear()

        pubSubRestClient.clean()

        if (failed) {
            return Result.failure()
        }
        return Result.success()
    }

}