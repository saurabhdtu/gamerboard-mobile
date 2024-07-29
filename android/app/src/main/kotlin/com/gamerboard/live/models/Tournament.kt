package com.gamerboard.live.models

import androidx.annotation.Keep
import com.gamerboard.live.GetGameScoringQuery
import com.gamerboard.live.fragment.UserTournament
import com.gamerboard.live.type.BgmiMaps
import com.gamerboard.live.type.BgmiModes
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.internal.immutableListOf
import java.text.SimpleDateFormat

@Keep
@Serializable
data class ServerTournamentElement(
    val joinedAt: String,
    val rank: Long,
    val score: Long,
    val tournament: TournamentDetails

) {
    companion object {
        fun getServerTournamentElement(userTournament: UserTournament): ServerTournamentElement {
            val dateTimeFormatter =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var serverTournamentElement = ServerTournamentElement(
                joinedAt = dateTimeFormatter.format(userTournament.joinedAt!!),
                rank = userTournament.rank?.toLong() ?: 0,
                score = userTournament.score!!.toLong(),
                tournament = TournamentDetails(
                    id = userTournament.tournament.tournament.id.toLong(),
                    name = userTournament.tournament.tournament.name,
                    maxPrize = userTournament.tournament.tournament.maxPrize.toLong(),
                    userCount = userTournament.tournament.tournament.userCount.toLong(),
                    startTime = dateTimeFormatter.format(userTournament.tournament.tournament.startTime),
                    endTime = dateTimeFormatter.format(userTournament.tournament.tournament.endTime),
                    rules = Rules(
                        minUsers = userTournament.tournament.tournament.rules.minUsers.toLong(),
                        maxLevel = userTournament.tournament.tournament.rules.onBGMIRules?.bgmiMaxLevel?.name?: userTournament.tournament.tournament.rules.onFFMaxRules?.ffMaxLevel?.name?:"",
                        maxUsers = userTournament.tournament.tournament.rules.maxUsers.toLong(),
                        allowedGroups = userTournament.tournament.tournament.rules.onBGMIRules?.bgmiAllowedGroups?.name?:userTournament.tournament.tournament.rules.onFFMaxRules?.ffAllowedGroups?.name?:"",
                        allowedMaps = userTournament.tournament.tournament.rules.onBGMIRules?.bgmiAllowedMaps?.map { it.name }?.toList()?:userTournament.tournament.tournament.rules.onFFMaxRules?.ffAllowedMaps?.map { it.name }?.toList()?: immutableListOf(),
                        allowedModes = userTournament.tournament.tournament.rules.onBGMIRules?.bgmiAllowedModes?.map { it.name }?.toList()?: userTournament.tournament.tournament.rules.onFFMaxRules?.ffAllowedModes?.map { it.name }?.toList()?:immutableListOf(),
                        minLevel = userTournament.tournament.tournament.rules.onBGMIRules?.bgmiMinLevel?.name?:userTournament.tournament.tournament.rules.onFFMaxRules?.ffMinLevel?.name?:"",
                        typename = userTournament.tournament.tournament.rules.__typename,
                    )
                )
            )
            return serverTournamentElement
        }
    }
}

@Keep
@Serializable
data class TournamentDetails(
    val id: Long,
    val name: String,
    val maxPrize: Long,
    val userCount: Long,
    val rules: Rules,
    val startTime: String,
    val endTime: String
)

@Keep
@Serializable
data class Rules(
    @SerializedName("__typename")
    val typename: String,
    val minUsers: Long,
    val maxUsers: Long,
    val allowedGroups: String,
    val allowedMaps: List<String>,
    val allowedModes: List<String>,
    val maxLevel: String,
    val minLevel: String
)

@Keep
@Serializable
data class LeaderBoardElement(
    val tournamentId: Int,
    val name: String,
    val score: Long,
    val behindBy: Long,
    val topGames: TopGames? = null,
    val rank: Long,
    val matchesPlayed: Long? = null,
    val myId: String? = null,
    val myPhoto: String? = null
)

@Serializable
class TopGames(val gameResults: List<GameResult>?)

@Serializable
class GameResult(
    val rank: Int,
    val score: Int
)

class LeaderBoardScoring(
    val leaderBoard: List<LeaderBoardElement>,
    val score: GetGameScoringQuery.Scoring,
    val tournamentId: Int,
)
