package com.gamerboard.live.gamestatemachine.stateMachine

import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.gamestatemachine.games.*
import com.gamerboard.live.gamestatemachine.games.bgmi.BGMIInputValidator
import com.gamerboard.live.gamestatemachine.games.bgmi.BGMILabelProcessor
import com.gamerboard.live.gamestatemachine.games.bgmi.BGMILabelUtils
import com.gamerboard.live.gamestatemachine.games.freefire.FreeFireInputValidator
import com.gamerboard.live.gamestatemachine.games.freefire.FreeFireLabelProcessor
import com.gamerboard.live.gamestatemachine.games.freefire.FreeFireLabelUtils


/**
 * Game Labels in the same order as we will see them from the model.
 * The position here (0 indexed) will be returned from the model.
 * classic:all:kills
global:login
classic:all:gameplay
classic:all:waiting
profile:self
classic:start
profile:id
classic_rating
rank
gameInfo
classic_rank_gameInfod
 * */

object MachineConstants {
    lateinit var gameConstants: GameConstants
    lateinit var machineLabelProcessor: MachineLabelProcessor
    lateinit var machineInputValidator: MachineInputValidator
    lateinit var machineLabelUtils: MachineLabelUtils
    lateinit var currentGame: SupportedGames

    fun isGameInitialized() = ::currentGame.isInitialized
    fun loadConstants(packageName: String) {
        updateGameIdActive = 0
        when (packageName.lowercase()) {
            SupportedGames.BGMI.packageName -> {
                currentGame = SupportedGames.BGMI
//                gameConstants = BGMIConstants()
                machineLabelProcessor = BGMILabelProcessor()
                machineInputValidator = BGMIInputValidator()
                machineLabelUtils = BGMILabelUtils()
            }
            SupportedGames.FREEFIRE.packageName -> {
                currentGame = SupportedGames.FREEFIRE
//                gameConstants = FreeFireConstants()
                machineLabelProcessor = FreeFireLabelProcessor()
                machineInputValidator = FreeFireInputValidator()
                machineLabelUtils = FreeFireLabelUtils()
            }
        }
    }

    enum class ScreenName{
        RATING,
        KILLS,
        PERFORMANCE,
        OTHER
    }

}


//Docs: https://www.notion.so/gamerboard/V1-Test-automation-Framework-ad631040659e497f9170a050a9ddef57#ee3a086394d24beeb93d11be79943931

enum class DEBUGGER {
    DISABLED,  // Run with the production app code, check `test()` method below and it's use to get the idea.
    DIRECT_HANDLE, // To run unit tests/ Old machine, directly pass to machine handler
    RUN_WITH_IMAGES, // Run with pictures/Images of game play.
    UNIT_TEST // Run Unit tests, check `test()` method below and it's use to get the idea.
}

var debugMachine = DEBUGGER.DISABLED


/**
 *check the flag returned by `test()` to run production/test code
 * */
fun test(): Boolean {
    if (debugMachine == DEBUGGER.DISABLED || debugMachine == DEBUGGER.RUN_WITH_IMAGES)
        return false
    return true
}

/** Maximum limit of the buffer size for the array of labels of continuous coming screens*/
const val MAX_BUFFER_LIMIT = 30
const val TIMER_COUNT_RATING_RANK = 10
const val TIMER_COUNT_RANK_KILLS = 15
const val TIMER_COUNT_PROFILE_VERIFY = 10
const val UNKNOWN_TO_STOP_LOADER = 300

fun getTierUrl(sport: String, tier: String): String =
    "https://storage.googleapis.com/gb-app/assets/${sport.lowercase()}/${tier.lowercase()}.png"

val minimumTruePositives: Map<String, Int> = mapOf(
    "start" to 1,
    "in-game" to 4,
    "waiting" to 1,
    "level" to 1,
    "id" to 1,
    "char-id" to 1,
    "rank" to 1,
    "team-rank" to 1,
    "kill" to 1,
    "game-info" to 1,
    "login" to 1,
    "initial-tier" to 1,
    "final-tier" to 1
)

object StateMachineStringConstants {
    const val UNKNOWN = "Un-Known"
}