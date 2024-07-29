package com.gamerboard.live.gamestatemachine.processor

import com.gamerboard.live.gamestatemachine.stateMachine.Event
import com.gamerboard.live.gamestatemachine.stateMachine.GameEndInfo
import com.gamerboard.live.gamestatemachine.stateMachine.GameStartInfo
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.MachineMessageBroadcaster
import com.gamerboard.live.gamestatemachine.stateMachine.MachineResult
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachine
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants.UNKNOWN
import com.gamerboard.live.models.db.GameInfo

object Handle {
    /**
     *Here the events are actually triggered based on what we received from the input after the input was validated,
     *here the [input] is the MachineResult object which contains the detailed information.
     */
    fun handle(
        input: MachineResult,
        curLabels: List<Int> = MachineConstants.gameConstants.unknownScreenBucket()
    ) {
        val machine = StateMachine.machine
        when (curLabels) {
            MachineConstants.gameConstants.homeScreenBucket() -> {
                machine.transition(
                    Event.CanRecordNewGame(
                        true,
                        "User visits home screen!"
                    )
                )
                machine.transition(Event.ReturnedToHomeAfterWarnedToVerify(reason = "User returned to home screen after warned to verify!"))
                machine.transition(Event.OnHomeScreenDirectlyFromLobby(reason = "User moved to home screen from lobby!"))
                machine.transition(Event.OnHomeScreenDirectlyFromGameStarted(reason = "User moved to home screen from game!"))
                machine.transition(Event.GameCompleted("User visits home screen!"))
            }

            MachineConstants.gameConstants.waitingScreenBucket() -> {
                if (input.waiting != true)
                    return

                machine.transition(
                    Event.CanRecordNewGame(
                        true,
                        "User visits home screen!"
                    )
                )
                machine.transition(Event.EnteredLobby())
                machine.transition(Event.GameCompleted("User visits waiting, screen!"))
            }

            MachineConstants.gameConstants.gameScreenBucket() -> {
                if (input.inGame != true)
                    return
                machine.transition(
                    Event.CanRecordNewGame(
                        true,
                        "User visits game screen!"
                    )
                )
                val gameStartInfo = GameStartInfo(
                    startTimeStamp = "${System.currentTimeMillis()}",
                    gameId = "${System.currentTimeMillis()}"
                )
                machine.transition(Event.EnteredGame(gameStartInfo))
            }

            MachineConstants.gameConstants.gameEndScreen() -> {
                if (input.accept == true) {
                    val gameEndInfo = GameEndInfo(
                        endTimeStamp = "${System.currentTimeMillis()}",
                    )
                    if (MachineConstants.machineLabelProcessor.firstGameEndScreen == 1) {
                        MachineMessageBroadcaster.invoke()?.firstGameEndScreen()
                    }
                    machine.transition(Event.GameEnded(gameEndInfo))
                }
            }

            MachineConstants.gameConstants.resultRankRating() -> {
                val gameEndInfo = GameEndInfo(
                    endTimeStamp = "${System.currentTimeMillis()}",
                )
                machine.transition(Event.GameEnded(gameEndInfo))
                machine.transition(Event.OnGameResultScreen())


                val initialTier = input.initialTier ?: UNKNOWN
                val finalTier = input.finalTier ?: UNKNOWN
                machine.transition(Event.GotTier(initialTier, finalTier))

                val gameInfo = input.gameInfo ?: GameInfo(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN)
                machine.transition(Event.GotGameInfo(gameInfo = gameInfo))

                val rank = input.rank ?: UNKNOWN
                machine.transition(Event.GotRank(rank = rank))

                val teamRank = input.teamRank ?: UNKNOWN
                machine.transition(Event.GotTeamRank(teamRank = teamRank))
            }

            MachineConstants.gameConstants.resultRankKills() -> {
                val gameEndInfo = GameEndInfo(
                    endTimeStamp = "${System.currentTimeMillis()}",
                )
                machine.transition(Event.GameEnded(gameEndInfo))
                machine.transition(Event.OnGameResultScreen())

                machine.transition(
                    Event.CanRecordNewGame(
                        true,
                        "User visits kills screen!"
                    )
                )

                val rank = input.rank ?: UNKNOWN
                val teamRank = input.teamRank ?: UNKNOWN
                val kill = input.kill ?: UNKNOWN
                val gameInfo = input.gameInfo ?: GameInfo(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN)
                val squadScoring = input.squadScoring ?: UNKNOWN

                machine.transition(Event.GotRank(rank))
                machine.transition(Event.GotTeamRank(teamRank))
                machine.transition(Event.GotKill(kill))
                machine.transition(Event.GotGameInfo(gameInfo))
                machine.transition(Event.GotSquadKill(squadScoring))
            }

            MachineConstants.gameConstants.loginScreenBucket() -> {
                if (input.login != true)
                    return
                machine.transition(Event.GameCompleted("User visits login screen!"))
                machine.transition(Event.UnVerifyUser(reason = "BGMI login screen was detected!"))
            }

            else -> {

            }
        }
    }
}

