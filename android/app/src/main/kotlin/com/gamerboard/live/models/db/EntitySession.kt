package com.gamerboard.live.models.db

import androidx.room.*

/**
 * Created by saurabh.lahoti on 25/08/21
 */
@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey val id: Int = 0,
    var sessionId: String?,
    val createdAt: Long,
)

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session)

    @Update
    suspend fun updateSession(session: Session)

    @Query("SELECT * FROM sessions WHERE sessionId IS NOT null ORDER BY createdAt DESC LIMIT 1")
    suspend fun getCurrentSession():List<Session>

    @Query("SELECT sessionId FROM sessions WHERE sessionId IS NOT null ORDER BY createdAt DESC LIMIT 1")
    suspend fun getSessionId():String?

}


