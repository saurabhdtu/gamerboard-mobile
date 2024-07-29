package com.gamerboard.logger.agent

import android.content.Context
import android.os.Environment
import android.util.Log
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.R
import com.gamerboard.live.utils.EventUtils
import com.gamerboard.live.utils.Events
import com.gamerboard.logger.model.GameLogMessage
import com.gamerboard.logging.model.LogEntry
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.protobuf.ByteString
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Date

class PubSubRestClient(val context: Context) : LogPublisher{
    private var accessToken: String
    private var credentials =
        GoogleCredentials.fromStream(context.resources.openRawResource(R.raw.credentials))
    private val lock = Mutex()
    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        encodeDefaults = true
        explicitNulls = false
        ignoreUnknownKeys = false
    }

    companion object {
        private val TAG = PubSubRestClient::class.java.simpleName
    }

    init {
        accessToken = getAccessToken()
    }

    private fun sendStringMessages(env: String, messages: List<String>): Boolean {
        return try {
            publishStringMessageOnPubSub(env, messages = messages)
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

    fun sendMessages(env: String, messages: List<GameLogMessage>): Boolean {
        val files = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val testFile = File(files, "${env}_sample_output_${Date()}.txt").apply {
            createNewFile()
        }
        testFile.appendText(Gson().toJson(messages))
        return try {
            publishStringMessageOnPubSub(env, messages = messages.map { json.encodeToString(it) })
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

    suspend fun sendMessage(env: String, messages: String): Boolean {
        return lock.withLock {
            try {
                publishMessageOnPubSub(env, messages)
                true
            } catch (ex: Exception) {
                ex.printStackTrace()
                false
            }
        }
    }


    private fun publishStringMessageOnPubSub(env: String, messages: List<String>) {
        publish(messages, env)
    }

    private fun publishMessageOnPubSub(env: String, json: String) {
        publish(arrayListOf(json), env)
    }


    private fun publish(messages: List<String>, env: String) {
        if (messages.isEmpty()) return
        val contentType = "application/json; charset=utf-8".toMediaType()

        val messageArray = JSONArray()
        for (message in messages) {
            val messageObject = JSONObject()
            messageObject.put("data", ByteString.copyFromUtf8(message))

            val attributesObject = JSONObject()
            attributesObject.put("env", env) // Add attribute "env" with value "staging"

            messageObject.put("attributes", attributesObject)
            messageArray.put(messageObject)
        }

        val requestBody = JSONObject().apply {
            put("messages", messageArray)
        }.toString().toRequestBody(contentType)


        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://pubsub.googleapis.com/v1/projects/${BuildConfig.GC_PROJECT_ID}/topics/${BuildConfig.GC_PUB_SUB_TOPIC}:publish")
            .header("Authorization", "Bearer ${accessToken}")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            println("Failed to publish message. Error: ${response.code} - ${response.body?.string()}")
            throw Exception("Failed to publish message. Error: ${response.code} - ${response.body?.string()}")
        }
        println("Message published successfully. Response ${response.body?.string()}")
    }

    private fun getAccessToken(): String {
        val scopedCredentials =
            credentials.createScoped(listOf("https://www.googleapis.com/auth/pubsub"))
        return scopedCredentials.refreshAccessToken().tokenValue
    }

    fun stop() {

    }

    override fun prepare() {

    }

    override fun clean() {
    }

    override suspend fun publishMessages(env: String, messages: List<LogEntry>): List<PublishResult> {
        val messageToPublish = arrayListOf<String>()

        val failedLogs = arrayListOf<PublishResult>()
        for (logEntry in messages) {
            if (logEntry.message.isNotEmpty()) {
                try {
                    val message: GameLogMessage = ProtoBuf.decodeFromHexString(logEntry.message)
                    val data = json.encodeToString(message)
                    messageToPublish.add(data)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    FirebaseCrashlytics.getInstance().recordException(ex)
                    failedLogs.add(PublishResult(logEntry = logEntry, error = ex.stackTraceToString()))
                }
            }
        }
        sendStringMessages(env = env, messages = messageToPublish)
        return failedLogs
    }
}