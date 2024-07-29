package com.gamerboard.logging.coroutine.executor

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext

abstract class MainIoExecutor : IExecutorScope, CoroutineScope, KoinComponent {

    private val mainDispatcher: MainDispatcher by inject()
    private val ioDispatcher: CoroutineDispatcher by inject()

    private var  job: CompletableJob = SupervisorJob()

    override val coroutineContext: CoroutineContext = job + ioDispatcher

    fun newJob(){
        job = SupervisorJob()
    }

    override fun cancel() {
        job.cancel()
    }
}