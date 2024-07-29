package com.gamerboard.live.gamestatemachine.games

import com.gamerboard.live.common.PrefsHelper
import com.gamerboard.live.gamestatemachine.games.bgmi.BGMIInputValidator
import com.gamerboard.live.gamestatemachine.stateMachine.State
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachine
import com.gamerboard.live.repository.GameRepository
import com.gamerboard.live.repository.SubmitGameWorker
import com.gamerboard.live.service.screencapture.ImageProcessor
import com.gamerboard.live.service.screencapture.MLKitOCR
import com.gamerboard.live.service.screencapture.ui.UserGuideUI
import org.koin.java.KoinJavaComponent.inject
import java.util.LinkedList

object GameHelper {

    private val prefsHelper: PrefsHelper by inject(PrefsHelper::class.java)

    private val endGameProcesses: List<Pair<String, String?>?> =
        arrayListOf(
            Pair(MLKitOCR::class.java.name, "*"),
            Pair(ImageProcessor::class.java.name, "*"),
            Pair(
                BGMIInputValidator::class.java.name,
                BGMIInputValidator::validateRankKillGameInfo.name,
            ),
            Pair(
                BGMIInputValidator::class.java.name,
                BGMIInputValidator::validateResultForSoloGames.name,
            ),
            Pair(
                BGMIInputValidator::class.java.name,
                BGMIInputValidator::validateResultForTeamGames.name,
            ),
            Pair(
                BGMIInputValidator::class.java.name,
                BGMIInputValidator::validateRankRatingGameInfo.name,
            ),
            Pair(
                SubmitGameWorker::class.java.name,
                "*",
            ),
            Pair(
                UserGuideUI::class.java.name,
                UserGuideUI::gameFailed.name,
            ),
            Pair(
                UserGuideUI::class.java.name,
                UserGuideUI::showUserThatGameIsNotOverYet.name,
            ),
            Pair(GameRepository::class.java.name, GameRepository::pushGameOrProfile.name),
        )

    private var gameIdSlots: LinkedList<String> = LinkedList<String>()


    fun validateMethodAndClass(
        className: String,
        methodName: String
    ): Boolean {
        endGameProcesses.forEach { whiteListClassMethod ->
            val whiteListClassName: String? = whiteListClassMethod?.first
            val whiteListMethodName: String? = whiteListClassMethod?.second
            if (className == whiteListClassName && (whiteListMethodName == "*" || methodName == whiteListMethodName)) return true
        }
        return false
    }

    fun newGameStarted(gameId: String) {
        gameIdSlots.push(gameId)
    }

    fun onGameFinished(gameId: String?) {
        gameIdSlots.indexOfFirst { it == gameId }.also { index ->
            if (index >= 0) gameIdSlots.removeAt(index)
        }
    }

    fun getGameId(stackTrace: Array<StackTraceElement>): String? {
        val hasAny = stackTrace.any { validateMethodAndClass(it.className, it.methodName) }
        if (hasAny) {
            return gameIdSlots.peekFirst()
        }
        return gameIdSlots.peekLast()
    }

    fun gameIdOfPreviousGame() = gameIdSlots.peekFirst()
    fun getOriginalGameId(): String? {
        when (val currentState = StateMachine.machine.state) {
            is State.GameStarted -> {
                return currentState.gameStartInfo.gameId
            }

            is State.GameEnded -> {
                return currentState.gameStartInfo.gameId
            }

            is State.FetchResult -> {
                return currentState.gameResultDetails.gameStartInfo.gameId
            }

            is State.WarnedNotFinishedGame -> {
                return currentState.gameStartInfo.gameId
            }

            else -> {
                return null
            }
        }
    }

}