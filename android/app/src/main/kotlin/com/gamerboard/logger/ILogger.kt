package com.gamerboard.logger

interface ILogger {
    fun log(
        message: String,
        category: LogCategory = LogCategory.D,
        logToConsole: Boolean = true,
        commitLog: Boolean = false,
        platform: PlatformType = PlatformType.A
    )
    fun sendLogs()
    fun writeMemoryUsageLog()
    fun unlock()
    fun lock()
    suspend fun commitLog()
}