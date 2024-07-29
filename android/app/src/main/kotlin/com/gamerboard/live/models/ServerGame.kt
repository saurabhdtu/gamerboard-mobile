package com.gamerboard.live.models

import androidx.annotation.Keep
import com.gamerboard.live.GetGameScoringQuery
import kotlinx.serialization.Serializable
import com.gamerboard.live.fragment.GameResponse

/**
 * Created by saurabh.lahoti on 30/12/21
 */

@Keep
data class CustomGameResponse(
    val serverGame: GameResponse,
    val scoring: GetGameScoringQuery.Data
)


@Keep
@Serializable
data class Scoring(
        val killPoints: Int,
        val rankPoints: ArrayList<RankPoint>
)

@Keep
@Serializable
data class RankPoint(
    val points: Int,
    val rank: Int
)





@Keep
@Serializable
data class ServerGame(
    val id: Int,
    val level: String,
    val rank: Int,
    val score: Double,
    val userId: Int,
    val metadata: ServerGameMetaData
)

@Keep
@Serializable
data class ServerGameMetaData(
        val finalTier: Int,
        val initialTier: Int,
        val kills: Int,
)

@Keep
@Serializable
data class TournamentResponse(
    val isAdded: Boolean,
    val isTop: Boolean,
    val tournament: Tournament,
    val exclusionReason:String?
)

@Keep
@Serializable
data class Tournament(
    val id: Int,
    val name: String
)