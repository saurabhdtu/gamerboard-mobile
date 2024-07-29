package com.gamerboard.live.repository

import android.util.Log
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.models.db.AppDatabase
import com.gamerboard.live.models.db.Game
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.logWithIdentifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object GameManager:KoinComponent {
    private val db : AppDatabase by inject()

    fun saveGame(game: Game) {
        CoroutineScope(Dispatchers.IO).launch {
            db.getGamesDao().insertGame(game)
        }
    }

     fun clearAllGames(){
        CoroutineScope(Dispatchers.IO).launch {
            db.getGamesDao().clearAllGameData()
        }
    }

    suspend fun updateGame(game: Game) {
        Log.d("debug-scoring", game.toString())
        val games = db.getGamesDao().getGameById(game.gameId!!)
        var existingGame: Game? = null
        if (games.isNotEmpty())
            existingGame = games.first()
        /*   val squadScoring = MachineInputValidator.getBestFromTeamScoreArray(MachineInputValidator.getSquadScoringArray(
                game.squadScoring!!
            ),
                existingGame?.squadScoring?.let {
                    MachineInputValidator.getSquadScoringArray(
                        it
                    )
                })*/
        val squadScoring = if (game.squadScoring != null) {
            try {
                if ((MachineConstants.machineInputValidator.getSquadScoringArray(game.squadScoring!!).size) >= ((existingGame?.squadScoring?.let {
                        MachineConstants.machineInputValidator.getSquadScoringArray(
                            it
                        ).size
                    }) ?: 0)) {
                    game.squadScoring
                } else {
                    existingGame?.squadScoring
                }
            } catch (ex: Exception) {
                game.squadScoring ?: existingGame?.squadScoring
            }
        } else {
            existingGame?.squadScoring
        }
        game.squadScoring = squadScoring


        logWithIdentifier(game.gameId){
            it.setMessage("Updating game with id:${game.gameId} for user ${game.userId}, game:$game")
            it.addContext("game_id", game.gameId)
            it.addContext("game_user_id", game.userId)
            it.addContext("game", game)
            it.setCategory(LogCategory.ENGINE)
        }

        db.getGamesDao().updateGame(
            gameId = game.gameId,
            MachineConstants.machineLabelUtils.getCorrectNumberFromTwoValues(
                game.kills,
                existingGame?.kills
            ),
            MachineConstants.machineLabelUtils.getCorrectGameInfo(
                game.gameInfo,
                existingGame?.gameInfo
            ),
            MachineConstants.machineLabelUtils.getCorrectNumberFromTwoValues(
                game.teamRank,
                existingGame?.teamRank
            ),
            MachineConstants.machineLabelUtils.getCorrectNumberFromTwoValues(
                game.rank,
                existingGame?.rank
            ),
            game.initialTier ?: existingGame?.initialTier,
            game.finalTier ?: existingGame?.finalTier,
            game.metaInfoJson ?: existingGame?.metaInfoJson,
            game.squadScoring
        )
    }

}