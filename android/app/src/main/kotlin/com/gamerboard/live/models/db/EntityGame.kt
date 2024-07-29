package com.gamerboard.live.models.db

import androidx.room.*
import kotlinx.serialization.Serializable


@Serializable
@Entity(tableName = "user_games")
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var userId: String?,
    var valid: Boolean?,
    var rank: String?,
    var gameInfo: String?,
    var kills: String?,
    var teamRank: String?,
    var initialTier: String?,
    var finalTier: String?,
    var endTimestamp: String?,
    var startTimeStamp: String?,
    var gameId: String?,
    var synced: Int? = 0,
    var metaInfoJson: String?,
    var serverGameId: Int? = null,
    var serverUserId: Int? = null,
    var squadScoring: String? = null
)   // game ID

@Dao
interface GameDao {
    @Query("SELECT * FROM user_games")
    suspend fun getListOfAllGames(): List<Game>

    @Query("DELETE FROM user_games")
    suspend fun clearAllGameData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    @Query("UPDATE user_games SET kills=(:kills), gameInfo=(:gameInfo), teamRank=(:teamRank), rank=(:rank), initialTier=(:initialTier), finalTier=(:finalTier), metaInfoJson=(:metaInfoJson), squadScoring=(:squadScoring)  WHERE gameId = (:gameId)")
    suspend fun updateGame(
        gameId: String?,
        kills: String?,
        gameInfo: String?,
        teamRank: String?,
        rank: String?,
        initialTier: String?,
        finalTier: String?,
        metaInfoJson: String?,
        squadScoring: String?
    )

    @Query("UPDATE user_games SET serverGameId=(:serverGameId), serverUserId=(:serverUserId), synced= 1 WHERE gameId = (:gameId)")
    suspend fun mapServerGameToLocal(gameId: String?, serverGameId: Int?, serverUserId: Int?)

    @Query("SELECT * FROM user_games WHERE gameId=(:gameId)")
    suspend fun getGameById(gameId: String): List<Game>

    @Query("SELECT * FROM user_games WHERE synced=0")
    suspend fun getUnSyncedGames(): List<Game>

    @Query("UPDATE user_games SET synced=1 WHERE id=(:id)")
    suspend fun setSynced(id: Int)
}

@Serializable
@Entity(tableName = "user")
data class User(@PrimaryKey var id: String, var skillLevel: String?, var games: ArrayList<Game>)

@Serializable
data class GameInfo(var type: String, var view: String, var group: String, var mode: String)
