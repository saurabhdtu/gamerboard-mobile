package com.gamerboard.live.gamestatemachine.stateMachine

import com.gamerboard.live.models.db.Game
import com.gamerboard.live.models.db.GameInfo


/**
 * A builder pattern based light weight object with setter and getters to carry the filtered, corrected and validated input to the machine and
 * other places.
 * This is the output from the [MachineInputValidator] and source to [Handle.handle].
 * */
class MachineResult(
        //add private constructor if necessary
    val id: String?,
    val level: String?,
    var kill: String?,
    val gameInfo: GameInfo?,
    val rank: String?,
    var teamRank: String?,
    val accept: Boolean? = false,
    val inGame: Boolean? = false,
    val waiting: Boolean? = false,
    val login: Boolean? = false,
    val start: Boolean? = false,
    val onHome: Boolean? = false,
    val initialTier: String?,
    val finalTier:String?,
    val historyGame:Game?,
    val charId:String?,
    val metaInfoJson:String?,
    val hasLowConfidence:Boolean?,
    val noClearMajority:Boolean?,
    val framesReceived:Int?,
    var squadScoring: String?
) {

    private constructor(builder: Builder) : this(builder.id, builder.level, builder.kill, builder.gameInfo,
            builder.rank, builder.teamRank, builder.accept, builder.inGame, builder.waiting, builder.login,
            builder.start, builder.onHome, builder.initialTier, builder.finalTier, builder.historyGame, builder.charId,
            builder.metaInfoJson, builder.hasLowConfidence, builder.noClearMajority, builder.framesReceived, builder.squadScoring)

    class Builder {
        var id: String? = null
            private set
        var level: String? = null
            private set
        var kill: String? = null
        var gameInfo: GameInfo? = null
            private set
        var rank: String? = null
            private set
        var teamRank: String? = null
            private set
        var accept: Boolean = false
            private set
        var inGame: Boolean = false
            private set
        var waiting: Boolean = false
            private set
        var login: Boolean = false
            private set
        var start: Boolean = false
            private set
        var onHome: Boolean = false
            private set
        var initialTier: String? = null
            private set
        var finalTier: String? = null
            private set
        var historyGame: Game? = null
            private set
        var charId: String? = null
            private set
        var metaInfoJson: String? = null
            private set
        var hasLowConfidence: Boolean? = false
            private set
        var noClearMajority: Boolean? = false
            private set
        var framesReceived: Int? = null
            private set
        var squadScoring: String? = null
            private set


        fun setId(id: String) = apply { this.id = id }
        fun setLevel(level: String) = apply { this.level = level }
        fun setKill(kill: String) = apply { this.kill = kill }
        fun setGameInfo(gameInfo: GameInfo) = apply { this.gameInfo = gameInfo }
        fun setRank(rank: String) = apply { this.rank = rank }
        fun setTeamRank(teamRank: String) = apply { this.teamRank = teamRank }
        fun setAccepted() = apply { this.accept = true }
        fun setInGame() = apply { this.inGame = true }
        fun setWaiting() = apply { this.waiting = true }
        fun setLogin() = apply { this.login = true }
        fun setStart() = apply { this.start = true }
        fun setOnHome() = apply { this.onHome = true }
        fun setInitialTier(initialTier: String) = apply { this.initialTier = initialTier}
        fun setFinalTier(finalTier: String) = apply { this.finalTier = finalTier }
        fun setHistoryGame(game:Game) = apply { this.historyGame = game }
        fun setCharId(charId: String) = apply { this.charId = charId }
        fun setHasLowConfidence(hasLow:Boolean=false) = apply { this.hasLowConfidence = hasLow }
        fun setNoClearMajority(noMajority:Boolean=false) = apply { this.noClearMajority = noMajority }
        fun setFramesReceived(frames:Int) = apply { this.framesReceived = frames }
        fun setSquadScoring(squadScoring : String?) {
            this.squadScoring = squadScoring
        }

        fun build() = MachineResult(this)
    }
}