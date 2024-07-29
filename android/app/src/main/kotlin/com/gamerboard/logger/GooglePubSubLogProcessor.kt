package com.gamerboard.logger

import android.content.Context
import com.gamerboard.logging.model.LogEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.component.KoinComponent

class GooglePubSubLogProcessor(appContext: Context) :
    LogProcessor(), KoinComponent {


    companion object {
        private val TAG = GooglePubSubLogProcessor::class.java.simpleName
    }
    private val coroutineContext = Dispatchers.IO + SupervisorJob()


    init {

    }

    override suspend fun publish(
        messages: List<LogEntry>,
        onComplete: (ids: List<String>) -> Unit,
        failed: (ids: List<String>) -> Unit,
    ): Boolean {
        return true
    }
}