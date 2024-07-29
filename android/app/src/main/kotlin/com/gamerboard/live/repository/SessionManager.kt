package com.gamerboard.live.repository

import android.util.Log
import com.gamerboard.live.models.db.AppDatabase
import com.gamerboard.live.models.db.Session
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


object SessionManager: KoinComponent {
    val db : AppDatabase by inject()
    val tag = "SessionManager"
    suspend fun clearSession() {
        Log.d(tag, "clearSession()")
        val sessions = db.sessionDao().getCurrentSession()
        if (sessions.isNotEmpty()) {
            Log.d(tag, "clearSession(): $sessions")
            val session = sessions.first()
            session.let {
                it.sessionId = null
                db.sessionDao().updateSession(it)
            }
        }
    }

    suspend fun createSession(sessionId: String) {
        val sessionsTable = db.sessionDao()
        clearSession()
        val morphSessionId = "$sessionId:${System.currentTimeMillis()}"
        Log.d(tag, "createSession($morphSessionId)")
        sessionsTable.insertSession(
            Session(
                sessionId = morphSessionId,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun getSessionID():String? {
        val sessionId = db.sessionDao().getSessionId()
        Log.d(tag, "getSessionID=>$sessionId")
        return sessionId
    }


    suspend fun updateSession(sessionId: String) {
        val sessionsTable = db.sessionDao()
        val morphSessionId  = "$sessionId:${System.currentTimeMillis()}"
        val sessions = sessionsTable.getCurrentSession()
        Log.d(tag, "updateSession($morphSessionId); $sessions")
        if(sessions.isNotEmpty()){
            sessions.first().let {
                it.sessionId = morphSessionId
                sessionsTable.updateSession(it)
            }
        }
    }
}