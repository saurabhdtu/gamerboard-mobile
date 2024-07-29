package com.gamerboard.logging

import com.gamerboard.logging.di.coroutineModule
import com.gamerboard.logging.di.jsonModule
import com.gamerboard.logging.di.loggerModule
import com.gamerboard.logging.di.repositoryModule
import com.gamerboard.logging.model.LogEntry
import com.gamerboard.logging.serializer.LogMessage
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

private val modules = arrayListOf(loggerModule, repositoryModule, coroutineModule, jsonModule)

class GBLog( loggerFactory: LoggingAgent.Factory = Logger.Factory()) : KoinComponent {
    private var _current: LoggingAgent? = null
    private val loggers: HashMap<String?, LoggingAgent> = hashMapOf()
    private val manager: LogManager by inject()
    val current: LoggingAgent? = _current
    private var currentFactory: LoggingAgent.Factory = loggerFactory
    val factory  = currentFactory

    private operator fun invoke(loggerFactory: LoggingAgent.Factory = Logger.Factory()) {
        currentFactory = loggerFactory
        _current = currentFactory.create()
        loggers[_current!!.identifier!!] = _current!!
    }

    fun addAgent(identifier: String, factory: LoggingAgent.Factory){
        val agent = factory.create(identifier)
        loggers[identifier] = agent
    }

    fun setCustomFactory(factory: LoggingAgent.Factory){
        currentFactory = factory
        _current = currentFactory.create()
    }

    fun with(identifier: String?): LoggingAgent {
        return loggers.getOrPut(identifier) {
            currentFactory.create(identifier)
        }.also {
            _current = it
        }
    }

    fun log(message: LogMessage) {
        _current?.log(message)
    }


    fun markPendingMessagesUnread(){
        manager.markAllUnread()
    }

    fun getLogs(callback : (List<LogEntry>) -> Unit){

    }

    fun clear() {
        manager.clear()
    }

    companion object {
        private var _instance: GBLog? = null
        val instance : GBLog get() = _instance ?:throw Exception("GBLog is not initialized. Call GBLog.init(factory) to initialize the logger")
        fun init(loggerFactory: LoggingAgent.Factory? = null) {
            if (_instance == null) {
                _instance = GBLog(loggerFactory ?: Logger.Factory())
            }
            _instance.also {
                loadKoinModules(modules)
            }
        }

        fun terminate() {
            unloadKoinModules(modules)
            _instance?.let{
                it.loggers.clear()
                it._current = null
            }
        }
    }
}