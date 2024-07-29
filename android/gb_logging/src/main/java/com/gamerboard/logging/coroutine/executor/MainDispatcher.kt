package com.gamerboard.logging.coroutine.executor

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class MainDispatcher {
    val dispatcher: CoroutineDispatcher = Dispatchers.Main
}