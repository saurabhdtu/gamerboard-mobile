package com.gamerboard.logger.agent

import android.content.Context
import android.util.Log
import androidx.work.WorkManager
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.R
import com.gamerboard.logger.UploadLogWorker
import com.gamerboard.logger.model.GameLogMessage
import com.gamerboard.logger.model.convertDate
import com.gamerboard.logger.stackTrace
import com.gamerboard.logging.model.LogEntry
import com.gamerboard.logging.utils.now
import com.google.api.core.ApiFuture
import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.api.gax.batching.BatchingSettings
import com.google.api.gax.batching.FlowControlSettings
import com.google.api.gax.batching.FlowController
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.api.gax.rpc.ApiException
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.pubsub.v1.Publisher
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.protobuf.ByteString
import com.google.pubsub.v1.PubsubMessage
import com.google.pubsub.v1.TopicName
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.koin.java.KoinJavaComponent.inject
import org.threeten.bp.Duration
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


open class PubSubClient(val context: Context, private val  customTopicName: TopicName? = null) : LogPublisher {
    private lateinit var batchSettings: BatchingSettings
    private lateinit var flowControl: FlowControlSettings
    private var credentials =
        GoogleCredentials.fromStream(context.resources.openRawResource(R.raw.credentials))
    private val topicName: TopicName =
        customTopicName ?: TopicName.of(BuildConfig.GC_PROJECT_ID, BuildConfig.GC_PUB_SUB_TOPIC)
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = false
    }

    companion object {
        private val TAG = PubSubClient::class.java.simpleName
    }

    init {

    }

    override fun prepare() {
        flowControl = FlowControlSettings.getDefaultInstance().toBuilder()
            .setMaxOutstandingRequestBytes(10 * 1024 * 1024)
            .setMaxOutstandingElementCount(200)
            .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block)
            .build()

        batchSettings = BatchingSettings.newBuilder()
            .setIsEnabled(true)
            .setFlowControlSettings(flowControl)
            .setElementCountThreshold(200)
            .setDelayThreshold(Duration.ofSeconds(10))
            .build()
    }

    override fun clean() {
        //Do nothing
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Throws(IOException::class, InterruptedException::class)
    override suspend fun publishMessages(
        env: String,
        messages: List<LogEntry>
    ): List<PublishResult> {
        val failedEntries = arrayListOf<PublishResult>()

        val publisher = Publisher.newBuilder(topicName)
            .setEnableCompression(true)
            .setBatchingSettings(batchSettings)
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build()
        val messageIdFutures: ArrayList<Result.Success> = ArrayList()
        try {
            for (logEntry in messages) {
                if (logEntry.message.isNotEmpty()) {
                    try {
                        val message: GameLogMessage = ProtoBuf.decodeFromHexString(logEntry.message)

                        if(message.message.isNullOrEmpty()) continue

                        addAdditionalInfo(message, logEntry)

                        val data = ByteString.copyFromUtf8(json.encodeToString(message))
                        val pubsubMessage = PubsubMessage.newBuilder()
                            .setData(data)
                            .putAttributes("env", env)
                            .build()

                        val result = publisher.publish(pubsubMessage)
                        messageIdFutures.add(Result.Success(result, logEntry))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        FirebaseCrashlytics.getInstance().recordException(ex)
                        Log.e(TAG, "Error for  ${logEntry.message}")
                        failedEntries.add(
                            PublishResult(
                                logEntry = logEntry,
                                error = ex.stackTraceToString()
                            )
                        )
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(ex)
        } finally {

            try {
                failedEntries.addAll(messageIdFutures.failedEntries())
            } catch (ex: Exception) {
                FirebaseCrashlytics.getInstance().recordException(ex)
            }

            try {
                publisher.shutdown()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return failedEntries

    }

    private fun addAdditionalInfo(
        message: GameLogMessage,
        logEntry: LogEntry
    ) {
        message.context.add(
            hashMapOf(
                Pair("key", "pt"), Pair(
                    "value",
                    convertDate(
                        now()
                    )
                )
            )
        )

        message.context.add(
            hashMapOf(
                Pair("key", "logEntryTimestamp"),
                Pair("value", convertDate(logEntry.createdAt))
            )
        )
    }

    private fun ArrayList<Result.Success>.failedEntries(): List<PublishResult> {
        val failedEntries = arrayListOf<PublishResult>()
        forEach {
            try {
                it.future.get()
            } catch (ex: Exception) {
                System.out.println("This message is not published. ${ex.stackTraceToString()}")
                failedEntries.add(
                    PublishResult(
                        logEntry = it.logEntry,
                        error = "This message is not published. ${ex.stackTraceToString()}"
                    )
                )
            }
        }
        return failedEntries
    }

    private suspend fun publishMessage(
        publisher: Publisher,
        pubsubMessage: PubsubMessage?
    ) = suspendCoroutine { continuation ->
        val future: ApiFuture<String> = publisher.publish(pubsubMessage)
        ApiFutures.addCallback(
            future,
            object : ApiFutureCallback<String> {
                override fun onFailure(throwable: Throwable) {
                    if (throwable is ApiException) {
                        val apiException: ApiException = throwable as ApiException
                        // details on the API exception
                        System.out.println(apiException.getStatusCode().getCode())
                        System.out.println(apiException.isRetryable())
                    }
                    println("Error publishing message : $pubsubMessage")
                    continuation.resume(Result.Error(throwable))
                    Firebase.crashlytics.recordException(throwable)
                }

                override fun onSuccess(messageId: String) {
                    // Once published, returns server-assigned message ids (unique within the topic)
                    continuation.resume(Result.Error(Exception("Message Sent ${messageId}")))
                    println("DoWork publishing message : $messageId")
                }
            },
            MoreExecutors.directExecutor()
        )
    }


}