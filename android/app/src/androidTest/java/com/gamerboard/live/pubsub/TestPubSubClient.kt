package com.gamerboard.live.pubsub

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.data.TestGameLogMessage
import com.gamerboard.live.data.sampleLogMessage
import com.gamerboard.live.data.sampleLogMessage2
import com.gamerboard.live.data.sampleLogMessage3
import com.gamerboard.live.di.logModule
import com.gamerboard.live.di.repositoryModule
import com.gamerboard.live.koin.KoinTestRule
import com.gamerboard.live.nowWithAdd
import com.gamerboard.logger.LoggerModule
import com.gamerboard.logger.agent.LogPublisher
import com.gamerboard.logger.agent.PubSubClient
import com.gamerboard.logger.model.GameLogMessage
import com.gamerboard.logger.model.convertDate
import com.gamerboard.logging.GBLog
import com.gamerboard.logging.model.LogEntry
import com.google.pubsub.v1.Topic
import com.google.pubsub.v1.TopicName
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject


@RunWith(AndroidJUnit4::class)
@LargeTest
class TestPubSubClient {

    private val logPublisher: LogPublisher by inject(LogPublisher::class.java)

    private val instrumentedTestModule = module {
        single<LogPublisher> {
            PubSubClient(get(), TopicName.of(BuildConfig.GC_PROJECT_ID, "gamerboard_log_v2"))
        }
    }
    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            LoggerModule,
            logModule,
            repositoryModule,
            instrumentedTestModule
        )
    )
    @Before
    fun Setup(){
        logPublisher.prepare()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun sendMessage_successful(): Unit = runBlocking {
        val message =
            ProtoBuf.encodeToHexString(GameLogMessage.Builder().setMessage("Test").build())
        publishMessage(message)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun sendMessageFromJson_successful(): Unit = runBlocking {

        val json = sampleLogMessage
        sendMessage(json)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun sendMessageSample2FromJson_successful(): Unit = runBlocking {

        val json = sampleLogMessage2
        sendMessage(json)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun sendMessageSample3FromJson_successful(): Unit = runBlocking {

        val json = sampleLogMessage3
        sendMessage(json)
    }
      @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun sendMultipleMessage_successful(): Unit = runBlocking {

         val messages = (0 until  2000).map{
              val gameLog = Json.decodeFromString<TestGameLogMessage>(sampleLogMessage).apply {
                  timestamp = convertDate(nowWithAdd(it.toLong()))
              }
             LogEntry(identifier = "test", message = ProtoBuf.encodeToHexString(gameLog))
          }

          val publishedResults = logPublisher.publishMessages("staging", messages)
          assert(publishedResults.isEmpty())

    }

    private suspend fun sendMessage(json: String) {
        val gameLogMessage = Json.decodeFromString<GameLogMessage>(json)
        val message = ProtoBuf.encodeToHexString(gameLogMessage)
        publishMessage(message)
    }

    private suspend fun publishMessage(message: String) {
        val logEntry = LogEntry(identifier = "test", message = message)

        val publishedResults = logPublisher.publishMessages("staging", arrayListOf(logEntry))
        assert(publishedResults.isEmpty())
    }
}