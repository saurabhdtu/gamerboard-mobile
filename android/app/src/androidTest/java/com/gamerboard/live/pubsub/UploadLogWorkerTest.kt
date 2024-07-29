package com.gamerboard.live.pubsub

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.gamerboard.live.data.TestGameLogMessage
import com.gamerboard.live.data.sampleLogMessage
import com.gamerboard.live.di.logModule
import com.gamerboard.live.di.repositoryModule
import com.gamerboard.live.koin.KoinTestRule
import com.gamerboard.live.nowWithAdd
import com.gamerboard.logger.LoggerModule
import com.gamerboard.logger.UploadLogWorker
import com.gamerboard.logger.agent.DebugLogger
import com.gamerboard.logger.agent.LogPublisher
import com.gamerboard.logger.agent.PubSubClient
import com.gamerboard.logger.agent.PublishResult
import com.gamerboard.logger.model.GameLogMessage
import com.gamerboard.logger.model.convertDate
import com.gamerboard.logging.GBLog
import com.gamerboard.logging.LogManager
import com.gamerboard.logging.model.LogEntry
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.random.Random


@OptIn(ExperimentalSerializationApi::class)
@RunWith(AndroidJUnit4::class)
class UploadLogWorkerTest {
    private lateinit var context: Context
    private lateinit var executor: Executor
    private val logManager: LogManager by inject(LogManager::class.java)

    companion object {
        private val TAG = UploadLogWorker::class.java.simpleName
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        executor = Executors.newSingleThreadExecutor()

        GBLog.init(DebugLogger.Factory())

        for (i in 0 until 2000) {
            val gameLog = Json.decodeFromString<TestGameLogMessage>(sampleLogMessage).apply {
                timestamp = convertDate(nowWithAdd(0))
            }
            GBLog.instance.with("9999").log(gameLog)
        }

    }

    private val instrumentedTestModule = module {
        factory<LogPublisher> { DebugFailedLogPublisher(get()) }
    }

    class DebugFailedLogPublisher(context: Context) : PubSubClient(context) {
        private val collectedMessage = arrayListOf<GameLogMessage>()

        private fun distinctMessage(it: Map.Entry<String, List<GameLogMessage>>) =
            it.value.groupBy { it.message }.filter { it.value.isNotEmpty() }.also {
                println("Messages ${it}")
            }.isEmpty()

        override suspend fun publishMessages(
            env: String,
            messages: List<LogEntry>
        ): List<PublishResult> {
            val gameLogMessages =
                messages.map { ProtoBuf.decodeFromHexString<GameLogMessage>(it.message) }

            collectedMessage.addAll(gameLogMessages)
            val groupMessagesByTimestamp =
                collectedMessage.groupBy { it.timestamp }.mapValues { it.value }

            val messagesWithCount2 =
                groupMessagesByTimestamp.filter { it.value.size > 1 && distinctMessage(it) }

            if (messagesWithCount2.isNotEmpty()) {
                return messages.map { PublishResult(it, "") }
            }

/*
            val failedMessages = arrayListOf<PublishResult>()
            for (message in messages) {
                if(Random.nextInt() % 2 == 0){
                    failedMessages.add(PublishResult(message, ""))
                }
            }*/
            return  super.publishMessages(env, messages).also {
                println("Messages ${it.map { it.error }}")
            }
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


    @Test
    fun uploadLogs_shouldNotUploadDuplicateLog() = runBlocking {

        val worker = TestListenableWorkerBuilder<UploadLogWorker>(
            context = context,
        ).build()

        val result = worker.doWork()

        assert(result is (ListenableWorker.Result.Success))
    }


}