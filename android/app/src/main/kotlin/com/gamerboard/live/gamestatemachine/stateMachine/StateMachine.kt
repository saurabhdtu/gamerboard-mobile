package com.gamerboard.live.gamestatemachine.stateMachine

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.gamestatemachine.RPFetchResult
import com.gamerboard.live.gamestatemachine.RPGameEnded
import com.gamerboard.live.gamestatemachine.RPGameStarted
import com.gamerboard.live.gamestatemachine.RPIdle
import com.gamerboard.live.gamestatemachine.RPLobby
import com.gamerboard.live.gamestatemachine.RPVerified
import com.gamerboard.live.gamestatemachine.RPWarnedDidNotPlayGame
import com.gamerboard.live.gamestatemachine.RPWarnedNotFinishedGame
import com.gamerboard.live.gamestatemachine.RPWarnedToVerify
import com.gamerboard.live.gamestatemachine.SavedMachineState
import com.gamerboard.live.gamestatemachine.StateEnum
import com.gamerboard.live.gamestatemachine.games.GameHelper
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.games.updateGameIdActive
import com.gamerboard.live.gamestatemachine.processor.Handle
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants.UNKNOWN
import com.gamerboard.live.models.TFResult
import com.gamerboard.live.models.db.AppDatabase
import com.gamerboard.live.models.db.Game
import com.gamerboard.live.models.db.GameInfo
import com.gamerboard.live.repository.GameManager
import com.gamerboard.live.repository.GameRepository
import com.gamerboard.live.service.screencapture.AutoMLQueryHelper
import com.gamerboard.live.service.screencapture.ui.OnBoardingStep
import com.gamerboard.live.type.BgmiGroups
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.LogHelper
import com.gamerboard.logger.Logger
import com.gamerboard.logger.log
import com.gamerboard.logger.logWithCategory
import com.gamerboard.logger.logWithIdentifier
import com.tinder.StateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject

interface OnBoardingInfoProvider {
    var onBoarding: Boolean?
}

interface UserDetails {
    val unVerifiedUserDetails: UnVerifiedUserDetails
}

interface VerifiedUser : UserDetails {
    val verifiedUserDetails: VerifiedUserDetails
    override val unVerifiedUserDetails: UnVerifiedUserDetails
        get() = UnVerifiedUserDetails(
            verifiedUserDetails.originalGameId,
            verifiedUserDetails.gameCharId,
            verifiedUserDetails.onBoarding
        )
}

interface AutoMlCall {
    val callAutoMl: Boolean
}

interface GamesProvider {
    val gamesPlayed: ArrayList<Game>
}

interface CurrentGameProvider {
    val activeGame: Game
}

// User
@Serializable
data class UnVerifiedUserDetails(
    val originalGameId: String,
    val originalGameUserName: String?,
    val onBoarding: Boolean,
)

@Serializable
data class VerifiedUserDetails(
    val userId: String,
    val onBoarding: Boolean,
    val originalGameId: String,
    val gameCharId: String,
    var canStartGame: Boolean,
    override val gamesPlayed: ArrayList<Game>,
) : UserDetails, GamesProvider {
    override val unVerifiedUserDetails: UnVerifiedUserDetails
        get() = UnVerifiedUserDetails(userId, gameCharId, onBoarding)
}

@Serializable
data class GameResult(
    var kills: String,
    var autoMlKills: Boolean = false,

    var rank: String,
    var autoMlRank: Boolean = false,

    var teamRank: String,
    var autoMlTeamRank: Boolean = false,

    var initialTier: String,
    var finalTier: String,
    var autoMlTier: Boolean = false,

    var gameInfo: GameInfo,
    var autoMlGameInfo: Boolean = false,
    var squadScoring: String? = null
)

@Serializable
data class GameResultDetails(
    val gameStartInfo: GameStartInfo,
    val gameEndInfo: GameEndInfo,
    val gameResult: GameResult,
)

// Game
@Serializable
data class GameStartInfo(val startTimeStamp: String, val gameId: String)

@Serializable
data class GameEndInfo(val endTimeStamp: String)

sealed class State {
    // Main engine states
    class UnInitialized : State(), OnBoardingInfoProvider {
        override var onBoarding: Boolean? = null
        var originalGameId: String? = null
        var originalUserName: String? = null
    }

    class Idle(override val unVerifiedUserDetails: UnVerifiedUserDetails) : State(), UserDetails
    class Verified(
        override val verifiedUserDetails: VerifiedUserDetails
    ) :
        State(),
        VerifiedUser

    class Lobby(
        override val verifiedUserDetails: VerifiedUserDetails
    ) :
        State(),
        VerifiedUser

    class GameStarted(
        override val verifiedUserDetails: VerifiedUserDetails,
        val gameStartInfo: GameStartInfo
    ) : State(), VerifiedUser

    class GameEnded(
        override val verifiedUserDetails: VerifiedUserDetails,
        val gameStartInfo: GameStartInfo,
        val gameEndInfo: GameEndInfo
    ) : State(), VerifiedUser

    class FetchResult(
        override val verifiedUserDetails: VerifiedUserDetails,
        val gameResultDetails: GameResultDetails
    ) : State(), VerifiedUser, CurrentGameProvider {
        override val activeGame: Game
            get() = getGameFromDetails(gameResultDetails, verifiedUserDetails)
    }

    // Secondary states
    class WarnedToVerify(override val unVerifiedUserDetails: UnVerifiedUserDetails) : State(),
        UserDetails

    class WarnedDidNotPlayGame(
        override val verifiedUserDetails: VerifiedUserDetails
    ) :
        State(), VerifiedUser

    class WarnedNotFinishedGame(
        override val verifiedUserDetails: VerifiedUserDetails,
        val gameStartInfo: GameStartInfo
    ) :
        State(), VerifiedUser
}

sealed class Event {
    class SetOnBoarding(val onBoarding: Boolean?) : Event()
    class SetOriginalGameProfile(val originalGameId: String?, val originalGameUserName: String?) :
        Event()

    class UnInitializedUser(val reason: String) : Event()
    class ReturnedToHomeAfterWarnedToVerify(val reason: String) : Event()

    class VerifyUser(val gameProfileId: String, val gameCharId: String) : Event()
    class UnVerifyUser(val reason: String) : Event()

    class EnteredLobby : Event()
    class EnteredGame(val gameStartInfo: GameStartInfo) : Event()

    class OnGameResultScreen : Event()

    class GotRank(val rank: String) : Event()
    class GotTeamRank(val teamRank: String) : Event()
    class GotKill(val kills: String) : Event()
    class GotSquadKill(val squadScoring: String?) : Event()

    class GotGameInfo(val gameInfo: GameInfo) : Event()

    class GotTier(
        val initialTier: String,
        val finalTier: String
    ) : Event()

    class CanRecordNewGame(val canRecord: Boolean, val reason: String) : Event()

    class GameEnded(val gameEndInfo: GameEndInfo) : Event()
    class GameCompleted(val reason: String, val executeInBackground: Boolean = false) : Event()

    class OnHomeScreenDirectlyFromGameStarted(val reason: String) : Event()
    class OnHomeScreenDirectlyFromGameEnd(val reason: String) : Event()
    class OnHomeScreenDirectlyFromLobby(val reason: String) : Event()

    // System events
    class ServiceStopped(val stoppedVia: String) : Event()
    class OnRestoreMachine(val savedMachineState: SavedMachineState) : Event()
}


sealed class Effect {
    class OnMachineInitialized(val unVerifiedUserDetails: UnVerifiedUserDetails) : Effect()
    class OnMachineUnInitialized(val reason: String) : Effect()

    class OnUserVerified(
        val verifiedUserDetails: VerifiedUserDetails,
    ) : Effect()

    class OnUserUnVerified(val reason: String) : Effect()

    class OnEnteredLobby(
        val verifiedUserDetails: VerifiedUserDetails,
    ) : Effect()

    class OnEnteredGameFromLobby(val gameStartInfo: GameStartInfo) : Effect()
    class OnEnteredGame(val gameStartInfo: GameStartInfo) : Effect()

    class OnFetchingResults(
        val verifiedUserDetails: VerifiedUserDetails,
        val gameStartInfo: GameStartInfo,
        val gameEndInfo: GameEndInfo
    ) : Effect()

    class OnReceivedRank(val rank: String) : Effect()
    class OnReceivedKills(val kills: String) : Effect()
    class OnReceivedSquadKills(val squadScoring: String?) : Effect()
    class OnReceivedGameInfo(val gameInfo: GameInfo) : Effect()
    class OnReceivedTier(val initialTier: String, val finalTier: String) : Effect()
    class OnReceivedTeamRank(val teamRank: String) : Effect()

    class OnCanRecordNewGame(val reason: String) : Effect()

    class OnGameEnded(val gameEndInfo: GameEndInfo) : Effect()
    class OnGameCompleted(
        val reason: String,
        val executeInBackground: Boolean,
        val gameResultDetails: GameResultDetails
    ) : Effect()

    class OnGameStartWithoutVerify(
        val unVerifiedUserDetails: UnVerifiedUserDetails,
        val reason: String
    ) : Effect()

    class OnHomeScreenDirectlyFromGameEnd(val reason: String) : Effect()
    class OnHomeScreenDirectlyFromGameStarted(val reason: String) : Effect()
    class OnHomeScreenDirectlyFromLobby(val reason: String) : Effect()
    class OnReturnedToHomeAfterVerified(val reason: String) : Effect()

    // System events, effects
    class OnServiceStopped(val stoppedVia: String, val reason: String) : Effect()
    class OnMachineRestored(val toState: String, timestamp: String) : Effect()

}

fun getGameFromDetails(
    gameResultDetails: GameResultDetails,
    verifiedUserDetails: VerifiedUserDetails
): Game {
    val result = gameResultDetails.gameResult
    val start = gameResultDetails.gameStartInfo
    val end = gameResultDetails.gameEndInfo
    val gameInfoJson = Json.encodeToString(result.gameInfo)

    return Game(
        gameId = start.gameId,
        userId = verifiedUserDetails.userId,
        startTimeStamp = start.startTimeStamp,
        endTimestamp = end.endTimeStamp,

        rank = result.rank,
        kills = result.kills,
        teamRank = result.teamRank,
        gameInfo = gameInfoJson,
        initialTier = result.initialTier,
        finalTier = result.finalTier,

        valid = true,
        metaInfoJson = UNKNOWN,
        squadScoring = result.squadScoring
    )
}

fun saveGameInDatabase(game: Game) {
    if (test()) return

    logWithIdentifier(game.gameId.toString()) {
        it.setCategory(LogCategory.ENGINE)
        it.setMessage("Saving Game")
        it.addContext("game", game)
    }
    GameManager.saveGame(game)
}

fun updateGameInDatabase(game: Game) {
    Log.e("updateGameInDatabase>>", game.squadScoring ?: "")
    if (test()) return
    CoroutineScope(Dispatchers.IO).launch {
        updateGameInDB(game)
    }
}

suspend fun updateGameInDB(game: Game) {
    GameManager.updateGame(game)
}

fun unVerifyUserOnLogin() {
    updateGameIdActive = 0
    if (test()) return
    logWithCategory("Un Verified user on login user!", category = LogCategory.ENGINE)


    logWithIdentifier(GameHelper.getOriginalGameId()) {
        it.addContext("reason", GameRepository.GameFailureReason.USER_UNVERIFIED_LOGIN)
        it.setMessage("Unverified on BGMI Login")
        it.setCategory(LogCategory.ICM)
    }


    MachineMessageBroadcaster.invoke()?.newLoginBroadcast()
    MachineMessageBroadcaster.invoke()?.setProfileVerification(false, "", "")
    Handle.handle(
        MachineResult.Builder().setLogin().build(),
        curLabels = MachineConstants.gameConstants.loginScreenBucket()
    )
}

fun userIsVerified(numericId: String, characterId: String) {
    if (test()) return
    MachineMessageBroadcaster.invoke()?.setProfileVerification(true, numericId, characterId)
}

fun sendGameForAutoMl(
    game: Game,
    visionImages: ArrayList<String>,
    visionLabels: ArrayList<ArrayList<TFResult>>
) {
    if (test()) return

    logWithIdentifier(game.gameId) {
        it.setMessage("Sending game for AutoML ID: ")
        it.setCategory(LogCategory.ENGINE)
    }

    val state = com.gamerboard.live.gamestatemachine.stateMachine.StateMachine.machine.state
    if (state !is VerifiedUser) {
        logWithIdentifier(game.gameId) {
            it.setMessage("Could not send for auto ml, user was un verified!")
            it.addContext(
                "reason",
                GameRepository.GameFailureReason.USER_NOT_VERIFIED_ON_GAME_SUBMIT
            )
            it.addContext("game", game)
            it.setCategory(LogCategory.CME)
        }
        return
    }
    val characterId = (state as VerifiedUser).verifiedUserDetails.gameCharId
    val gameProfileId = (state as VerifiedUser).verifiedUserDetails.originalGameId

    AutoMLQueryHelper().runQuery(
        ctx = (GamerboardApp.instance as Context),
        imagePaths = visionImages,
        game = game,
        labels = visionLabels,

        // these are required to pass to the job, it can be used for squad games.
        originalBGBICharacterID = characterId,
        originalBGMIId = gameProfileId
    )

    //Keep this for testing. It stops the service via code when it shows kill screen
    /* Log.e(com.gamerboard.live.gamestatemachine.stateMachine.StateMachine::class.java.simpleName, "Stopping Manually")
     Intent(BroadcastFilters.SERVICE_COM).also { intent ->
         intent.putExtra("action", "stop")
         (MachineMessageBroadcaster.invoke()?.ctx)?.sendBroadcast(intent)
         CoroutineScope(Dispatchers.IO).launch {
             val pendingWorkers =
                 MachineMessageBroadcaster.invoke()?.ctx?.let {
                     WorkManager.getInstance(it).getWorkInfos(
                         WorkQuery.Builder.fromStates(
                             arrayListOf(
                                 WorkInfo.State.ENQUEUED,
                                 WorkInfo.State.RUNNING
                             )
                         ).build()).await()
                 }
             Log.i(com.gamerboard.live.gamestatemachine.stateMachine.StateMachine::class.java.simpleName, "Pending Workers ${pendingWorkers?.map { it.id.toString() + " " + it.tags.toString() }}")
         }
     }*/
}

fun sendGameToServer(
    game: Game,
    gameResultDetails: GameResultDetails
) {
    if (test()) return


    logWithIdentifier(game.gameId) {
        it.setMessage("Send game to server")
        it.addContext("game_id", game.gameId)
        it.addContext("user_id", game.userId)
        it.addContext("details", gameResultDetails)
        it.setCategory(LogCategory.ENGINE)
    }

    MachineMessageBroadcaster.invoke()?.gameEndedBroadcast(game.gameId!!)
}

fun showIncompleteGameUi(reason: String) {
    if (test()) return

    logWithIdentifier(GameHelper.getOriginalGameId()) {
        it.setMessage("Incomplete game, reason:")
        it.addContext("reason", reason)
        it.setCategory(LogCategory.ENGINE)
    }
    MachineMessageBroadcaster.invoke()?.finishedIncompleteGame(reason)
}

fun warnUserToVerifyBeforeStartingGame(reason: String) {
    if (test()) return

    logWithIdentifier(GameHelper.getOriginalGameId()) {
        it.setMessage("Incomplete game, reason:")
        it.addContext("reason", reason)
        it.setCategory(LogCategory.ENGINE)
    }
    MachineMessageBroadcaster.invoke()?.warnUserToVerifyBeforeStart()
}

fun startRecordingGame(gameId: String) {
    if (test()) return

    logWithIdentifier(gameId) {
        it.setMessage("Starting recording game play video")
        it.addContext("game_id", gameId)
        it.setCategory(LogCategory.ENGINE)
    }
    MachineMessageBroadcaster.invoke()?.startRecordingGameVideo(gameId)
}

fun finishRecordingGameVideo() {
    if (test()) return
    logWithCategory("Stopping recording game play video", category = LogCategory.ENGINE)
    MachineMessageBroadcaster.invoke()?.stopRecordingGameVideo()
}

fun removeRecordingGameVideo() {
    if (test()) return
    logWithCategory("Stopping recording game play video", category = LogCategory.ENGINE)
    MachineMessageBroadcaster.invoke()?.removeGamePlayVideo()
}


private fun uploadLogs(reason: String = "", stoppedVia: String = "") {
    if (test()) return

    logWithIdentifier(GameHelper.getOriginalGameId()) {
        it.setMessage("Upload logs")
        it.addContext("reason", reason)
        it.addContext("stopped_via", stoppedVia)
    }
    logWithIdentifier(GameHelper.getOriginalGameId()) {
        it.setMessage("Upload logs")
        it.addContext("reason", GameRepository.GameFailureReason.CLOSED_SERVICE)
        it.setCategory(LogCategory.ICM)
    }

}

// Timer to finish the game after the kills screen is detected!
private const val GAME_END_DELAY = 10L // seconds
private var gameEndTimer: CountDownTimer? = null
fun startGameEndTimer() {
    if (test()) return
    Handler(Looper.getMainLooper()).post {
        if (gameEndTimer == null) {
            gameEndTimer = object : CountDownTimer(GAME_END_DELAY * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    log("Game end Timer of ${GAME_END_DELAY}s expired!")

                    val machine =
                        com.gamerboard.live.gamestatemachine.stateMachine.StateMachine.machine
                    machine.transition(Event.GameCompleted(reason = "Timer of ${GAME_END_DELAY}s expired, finish the game"))
                }
            }
        }
        gameEndTimer!!.cancel()
        gameEndTimer!!.start()
    }
}

private fun clearOnBoardingPreferences() {
    if (test()) return
    GamerboardApp.instance.prefsHelper.putString(
        SharedPreferenceKeys.RUN_TUTORIAL,
        "${OnBoardingStep.NOT_STARTED}"
    )
    GamerboardApp.instance.prefsHelper.putString(
        SharedPreferenceKeys.RUN_TUTORIAL_IN_GAME,
        "${OnBoardingStep.NOT_STARTED}"
    )
}

fun restoreStateMachineWithInfo(saved: SavedMachineState): Pair<State, Effect> {

    var stateToRestore: State? = null
    var effectOfRestore: Effect? = null

    when (saved.restoreState) {
        StateEnum.RSUnInitialized -> {
            if (saved.restoreSMParams is RPIdle) {
                stateToRestore = State.UnInitialized()
            }
        }

        StateEnum.RSIdle -> {
            if (saved.restoreSMParams is RPIdle) {
                stateToRestore =
                    State.Idle(unVerifiedUserDetails = saved.restoreSMParams.unVerifiedUserDetails)
            }
        }

        StateEnum.RSVerified -> {
            if (saved.restoreSMParams is RPVerified) {
                stateToRestore =
                    State.Verified(verifiedUserDetails = saved.restoreSMParams.verifiedUserDetails)
            }
        }

        StateEnum.RSLobby -> {
            if (saved.restoreSMParams is RPLobby) {
                stateToRestore =
                    State.Lobby(verifiedUserDetails = saved.restoreSMParams.verifiedUserDetails)
            }
        }

        StateEnum.RSGameStarted -> {
            if (saved.restoreSMParams is RPGameStarted) {
                stateToRestore =
                    State.GameStarted(
                        verifiedUserDetails = saved.restoreSMParams.verifiedUserDetails,
                        gameStartInfo = saved.restoreSMParams.gameStartInfo
                    )
            }
        }

        StateEnum.RSGameEnded -> {
            if (saved.restoreSMParams is RPGameEnded) {
                stateToRestore =
                    State.GameEnded(
                        verifiedUserDetails = saved.restoreSMParams.verifiedUserDetails,
                        gameStartInfo = saved.restoreSMParams.gameStartInfo,
                        gameEndInfo = saved.restoreSMParams.gameEndInfo
                    )
            }
        }

        StateEnum.RSFetchResult -> {
            if (saved.restoreSMParams is RPFetchResult) {
                stateToRestore =
                    State.FetchResult(
                        verifiedUserDetails = saved.restoreSMParams.verifiedUserDetails,
                        gameResultDetails = saved.restoreSMParams.gameResultDetails
                    )
            }
        }

        StateEnum.RSWarnedToVerify -> {
            if (saved.restoreSMParams is RPWarnedToVerify) {
                stateToRestore =
                    State.WarnedToVerify(unVerifiedUserDetails = saved.restoreSMParams.unVerifiedUserDetails)
            }
        }

        StateEnum.RSWarnedDidNotPlayGame -> {
            if (saved.restoreSMParams is RPWarnedDidNotPlayGame) {
                stateToRestore =
                    State.WarnedDidNotPlayGame(verifiedUserDetails = saved.restoreSMParams.verifiedUserDetails)
            }
        }

        StateEnum.RSWarnedNotFinishedGame -> {
            if (saved.restoreSMParams is RPWarnedNotFinishedGame) {
                stateToRestore =
                    State.WarnedNotFinishedGame(
                        verifiedUserDetails = saved.restoreSMParams.verifiedUserDetails,
                        gameStartInfo = saved.restoreSMParams.gameStartInfo
                    )
            }
        }
    }

    if (stateToRestore == null)
        return Pair(
            State.UnInitialized(),
            Effect.OnMachineUnInitialized(reason = "Failed to restore!")
        )

    effectOfRestore = Effect.OnMachineRestored(
        "State Machine restored to ${saved.restoreState}",
        timestamp = "${System.currentTimeMillis()}"
    )

    if (stateToRestore is VerifiedUser)
        MachineMessageBroadcaster.invoke()?.setProfileVerification(
            true,
            (stateToRestore as VerifiedUser).verifiedUserDetails.originalGameId,
            (stateToRestore as VerifiedUser).verifiedUserDetails.gameCharId
        )
    return Pair(stateToRestore, effectOfRestore)
}

/**
 * The heart of the Game Engine the Game State Machine. This machine is responsible for calling for the actions based on the input
 * and record the games and handle all the cases & types of games (Complete, Valid, In Complete).
 * */
object StateMachine{
    var machine = StateMachine.create<State, Event, Effect> {
        // default is un initialized, we wait to receive
        // the onBoarding flag and user Id from the flutter end
        initialState(State.UnInitialized())

        state<State.UnInitialized> {
            //User is initialized
            on<Event.SetOriginalGameProfile> { event ->
                originalGameId = event.originalGameId
                originalUserName = event.originalGameUserName

                if (originalGameId.isNullOrBlank() || onBoarding == null)
                    return@on transitionTo(this)

                val unverifiedUser = UnVerifiedUserDetails(
                    originalGameId = originalGameId!!,
                    onBoarding = onBoarding!!,
                    originalGameUserName = originalUserName
                )

                transitionTo(
                    State.Idle(unverifiedUser),
                    Effect.OnMachineInitialized(unverifiedUser),
                )
            }

            on<Event.SetOnBoarding> { event ->
                onBoarding = event.onBoarding

                if (originalGameId.isNullOrBlank() || onBoarding == null)
                    return@on transitionTo(this)

                val unverifiedUser = UnVerifiedUserDetails(
                    originalGameId = originalGameId!!,
                    onBoarding = onBoarding!!,
                    originalGameUserName = originalUserName
                )

                transitionTo(
                    State.Idle(unverifiedUser),
                    Effect.OnMachineInitialized(unverifiedUser),
                )
            }
            on<Event.UnInitializedUser> { event ->
                transitionTo(
                    State.UnInitialized(),
                    Effect.OnMachineUnInitialized(event.reason)
                )
            }
            on<Event.OnRestoreMachine> { event ->
                val restored = restoreStateMachineWithInfo(event.savedMachineState)
                transitionTo(restored.first, restored.second)
            }
        }

        state<State.Idle> {
            // Verify the user with received
            // gameProfileId and gameCharId form event
            on<Event.VerifyUser> { event ->

                if (event.gameCharId.isBlank())
                    return@on transitionTo(this)

                if (event.gameCharId.isBlank())
                    return@on transitionTo(this)

                // This will store the games played by user
                val gamesPlayed = arrayListOf<Game>()

                val verifiedUser = VerifiedUserDetails(
                    userId = unVerifiedUserDetails.originalGameId,
                    onBoarding = unVerifiedUserDetails.onBoarding,
                    originalGameId = event.gameProfileId,
                    gameCharId = event.gameCharId,
                    // Default true
                    canStartGame = true,
                    gamesPlayed = gamesPlayed
                )

                transitionTo(
                    State.Verified(verifiedUser),
                    Effect.OnUserVerified(verifiedUser)
                )
            }
            // Can not start game if not verified
            // warn to verify if not verified
            on<Event.EnteredGame> { event ->
                val unverifiedUser = UnVerifiedUserDetails(
                    originalGameId = unVerifiedUserDetails.originalGameId,
                    onBoarding = unVerifiedUserDetails.onBoarding,
                    originalGameUserName = unVerifiedUserDetails.originalGameUserName
                )
                transitionTo(
                    State.WarnedToVerify(unverifiedUser),
                    Effect.OnGameStartWithoutVerify(
                        unverifiedUser,
                        "User was warned to verify before starting game!"
                    )
                )
            }
            // Show warning when in lobby, he can quickly exit!
            on<Event.EnteredLobby> { event ->
                val unverifiedUser = UnVerifiedUserDetails(
                    originalGameId = unVerifiedUserDetails.originalGameId,
                    onBoarding = unVerifiedUserDetails.onBoarding,
                    originalGameUserName = unVerifiedUserDetails.originalGameUserName
                )
                transitionTo(
                    State.WarnedToVerify(unverifiedUser),
                    Effect.OnGameStartWithoutVerify(
                        unverifiedUser,
                        "User was warned to verify before starting game on lobby!, $event"
                    )
                )
            }
            //
            on<Event.UnInitializedUser> { event ->
                transitionTo(
                    State.UnInitialized(),
                    Effect.OnMachineUnInitialized(event.reason)
                )
            }

            // system events
            on<Event.ServiceStopped> { event ->
                transitionTo(
                    State.UnInitialized(),
                    Effect.OnServiceStopped(
                        stoppedVia = event.stoppedVia,
                        reason = "User was Idle, initialized so un initialized"
                    )
                )
            }
            on<Event.OnRestoreMachine> { event ->
                val restored = restoreStateMachineWithInfo(event.savedMachineState)
                transitionTo(restored.first, restored.second)
            }
        }

        state<State.WarnedToVerify> {
            // we have warned to verify
            // he can be verified
            on<Event.VerifyUser> { event ->
                // This will store the games played by user
                val gamesPlayed = arrayListOf<Game>()

                val verifiedUser = VerifiedUserDetails(
                    userId = unVerifiedUserDetails.originalGameId,
                    onBoarding = unVerifiedUserDetails.onBoarding,
                    originalGameId = event.gameProfileId,
                    gameCharId = event.gameCharId,
                    // Default true
                    canStartGame = true,
                    gamesPlayed = gamesPlayed
                )

                transitionTo(
                    State.Verified(verifiedUser),
                    Effect.OnUserVerified(verifiedUser)
                )
            }
            on<Event.UnVerifyUser> { event ->
                transitionTo(
                    State.Idle(unVerifiedUserDetails),
                    Effect.OnUserUnVerified(event.reason)
                )
            }
            on<Event.ReturnedToHomeAfterWarnedToVerify> { event ->
                transitionTo(
                    State.Idle(unVerifiedUserDetails),
                    Effect.OnReturnedToHomeAfterVerified(event.reason)
                )
            }

            // system events
            on<Event.ServiceStopped> { event ->
                transitionTo(
                    State.UnInitialized(),
                    Effect.OnServiceStopped(
                        stoppedVia = event.stoppedVia,
                        reason = "User was Warned to verify, initialized so un initialized"
                    )
                )
            }
            on<Event.OnRestoreMachine> { event ->
                val restored = restoreStateMachineWithInfo(event.savedMachineState)
                transitionTo(restored.first, restored.second)
            }
        }
        state<State.Verified> {
            // User can be unverified on bgmi login,
            // Or for any reason, received via event.reason
            on<Event.UnVerifyUser> { event ->
                val unverifiedUser = UnVerifiedUserDetails(
                    originalGameId = verifiedUserDetails.userId,
                    onBoarding = verifiedUserDetails.onBoarding,
                    originalGameUserName = verifiedUserDetails.gameCharId
                )
                transitionTo(State.Idle(unverifiedUser), Effect.OnUserUnVerified(event.reason))
            }
            // He moves to waiting lobby
            on<Event.EnteredLobby> {
                transitionTo(
                    State.Lobby(verifiedUserDetails),
                    Effect.OnEnteredLobby(verifiedUserDetails)
                )
            }
            // He can start a new game
            on<Event.EnteredGame> { event ->
                GameHelper.newGameStarted(event.gameStartInfo.gameId)
               // Log.i(StateMachine::class.java.simpleName, "EnteredGame2 ${event.gameStartInfo.gameId}")
                transitionTo(
                    State.GameStarted(
                        verifiedUserDetails,
                        gameStartInfo = event.gameStartInfo
                    ),
                    Effect.OnEnteredGame(event.gameStartInfo)
                )
            }
            on<Event.CanRecordNewGame> { event ->
                // after this he can record new game,
                // solves spectate issue
                verifiedUserDetails.canStartGame = event.canRecord
                transitionTo(this, Effect.OnCanRecordNewGame(reason = event.reason))
            }

            // system events
            on<Event.ServiceStopped> { event ->
                transitionTo(
                    State.UnInitialized(),
                    Effect.OnServiceStopped(
                        stoppedVia = event.stoppedVia,
                        reason = "User was Verified, initialized so un initialized"
                    )
                )
            }
            on<Event.OnRestoreMachine> { event ->
                val restored = restoreStateMachineWithInfo(event.savedMachineState)
                transitionTo(restored.first, restored.second)
            }
        }
        state<State.Lobby> {
            on<Event.UnVerifyUser> { event ->
                val unverifiedUser = UnVerifiedUserDetails(
                    originalGameId = verifiedUserDetails.userId,
                    onBoarding = verifiedUserDetails.onBoarding,
                    originalGameUserName = verifiedUserDetails.gameCharId
                )
                transitionTo(State.Idle(unverifiedUser), Effect.OnUserUnVerified(event.reason))
            }
            // He can start a new game
            on<Event.EnteredGame> { event ->
                GameHelper.newGameStarted(event.gameStartInfo.gameId)
               // Log.i(StateMachine::class.java.simpleName, "EnteredGame1 ${event.gameStartInfo.gameId}")
                transitionTo(
                    State.GameStarted(
                        verifiedUserDetails,
                        gameStartInfo = event.gameStartInfo
                    ),
                    Effect.OnEnteredGameFromLobby(event.gameStartInfo)
                )
            }
            on<Event.CanRecordNewGame> { event ->
                verifiedUserDetails.canStartGame = event.canRecord
                transitionTo(this, Effect.OnCanRecordNewGame(reason = event.reason))
            }
            // User exits the lobby in between!
            on<Event.OnHomeScreenDirectlyFromLobby> { event ->
                transitionTo(
                    State.Verified(verifiedUserDetails),
                    Effect.OnHomeScreenDirectlyFromLobby(event.reason)
                )
            }
            // system events
            on<Event.ServiceStopped> { event ->
                transitionTo(
                    State.UnInitialized(),
                    Effect.OnServiceStopped(
                        stoppedVia = event.stoppedVia,
                        reason = "User was in lobby, initialized & verified so un initialized"
                    )
                )
            }
        }

        state<State.GameStarted> {
            // User enters game only if verified,
            // He may stop service, so un verify
            on<Event.UnVerifyUser> { event ->
                val unverifiedUser = UnVerifiedUserDetails(
                    originalGameId = verifiedUserDetails.userId,
                    onBoarding = verifiedUserDetails.onBoarding,
                    originalGameUserName = verifiedUserDetails.gameCharId
                )
                transitionTo(State.Idle(unverifiedUser), Effect.OnUserUnVerified(event.reason))
            }
            // The user is out of Game now
            on<Event.GameEnded> { event ->
                transitionTo(
                    State.GameEnded(
                        verifiedUserDetails = verifiedUserDetails,
                        gameStartInfo = gameStartInfo,
                        gameEndInfo = event.gameEndInfo
                    ), Effect.OnGameEnded(event.gameEndInfo)
                )
            }
            // User exits the game in between!
            on<Event.OnHomeScreenDirectlyFromGameStarted> { event ->
                transitionTo(
                    State.Verified(verifiedUserDetails),
                    Effect.OnHomeScreenDirectlyFromGameStarted(event.reason)
                )
            }
            on<Event.CanRecordNewGame> { event ->
                verifiedUserDetails.canStartGame = event.canRecord
                transitionTo(this, Effect.OnCanRecordNewGame(reason = event.reason))
            }
            // system events
            on<Event.ServiceStopped> { event ->
                transitionTo(
                    State.UnInitialized(),
                    Effect.OnServiceStopped(
                        stoppedVia = event.stoppedVia,
                        reason = "User started game, initialized so un initialized"
                    )
                )
            }
        }

        state<State.GameEnded> {
            // He may stop service, so un verify
            on<Event.UnVerifyUser> { event ->
                val unverifiedUser = UnVerifiedUserDetails(
                    originalGameId = verifiedUserDetails.userId,
                    onBoarding = verifiedUserDetails.onBoarding,
                    originalGameUserName = verifiedUserDetails.gameCharId
                )
                transitionTo(State.Idle(unverifiedUser), Effect.OnUserUnVerified(event.reason))
            }
            // he moved over the ratings screen
            on<Event.OnGameResultScreen> {
                val gameResult = GameResult(
                    kills = UNKNOWN,
                    rank = UNKNOWN,
                    teamRank = UNKNOWN,
                    initialTier = UNKNOWN,
                    finalTier = UNKNOWN,
                    gameInfo = GameInfo(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN)
                )

                transitionTo(
                    State.FetchResult(
                        verifiedUserDetails = verifiedUserDetails,
                        gameResultDetails = GameResultDetails(
                            gameStartInfo = gameStartInfo,
                            gameEndInfo = gameEndInfo,
                            gameResult = gameResult
                        )
                    ),
                    Effect.OnFetchingResults(
                        verifiedUserDetails,
                        gameStartInfo = gameStartInfo,
                        gameEndInfo = gameEndInfo
                    )
                )
            }
            // User did not go to results screens!
            on<Event.OnHomeScreenDirectlyFromGameEnd> { event ->
                transitionTo(
                    State.Verified(verifiedUserDetails),
                    Effect.OnHomeScreenDirectlyFromGameEnd(event.reason)
                )
            }
            on<Event.CanRecordNewGame> { event ->
                verifiedUserDetails.canStartGame = event.canRecord
                transitionTo(
                    this, Effect.OnCanRecordNewGame(reason = event.reason)
                )
            }
        }

        state<State.FetchResult> {
            // He may stop service, so un verify
            on<Event.UnVerifyUser> { event ->
                val unverifiedUser = UnVerifiedUserDetails(
                    originalGameId = verifiedUserDetails.userId,
                    onBoarding = verifiedUserDetails.onBoarding,
                    originalGameUserName = verifiedUserDetails.gameCharId
                )
                transitionTo(State.Idle(unverifiedUser), Effect.OnUserUnVerified(event.reason))
            }
            // fetched kills
            on<Event.GotKill> { event ->
                if (event.kills != UNKNOWN) gameResultDetails.gameResult.kills = event.kills
//                gameResultDetails.gameResult.autoMlKills = event.callAutoMl
                transitionTo(this, Effect.OnReceivedKills(event.kills))
            }
            // fetched squad kills
            on<Event.GotSquadKill> { event ->
                if (event.squadScoring != UNKNOWN)
                    gameResultDetails.gameResult.squadScoring = event.squadScoring

//                gameResultDetails.gameResult.autoMlKills = event.callAutoMl
                transitionTo(this, Effect.OnReceivedSquadKills(event.squadScoring))
            }
            // fetched Rank
            on<Event.GotRank> { event ->
                if (event.rank != UNKNOWN) gameResultDetails.gameResult.rank = event.rank
//                gameResultDetails.gameResult.autoMlRank = event.callAutoMl
                transitionTo(this, Effect.OnReceivedRank(event.rank))
            }
            // fetched Tier
            on<Event.GotTier> { event ->
                if (event.initialTier != UNKNOWN) gameResultDetails.gameResult.initialTier =
                    event.initialTier
                if (event.finalTier != UNKNOWN) gameResultDetails.gameResult.finalTier =
                    event.finalTier
//                gameResultDetails.gameResult.autoMlTier = event.callAutoMl
                transitionTo(
                    this,
                    Effect.OnReceivedTier(
                        initialTier = event.initialTier,
                        finalTier = event.finalTier
                    )
                )
            }
            // fetched gameInfo
            on<Event.GotGameInfo> { event ->
                if (event.gameInfo.group != UNKNOWN) gameResultDetails.gameResult.gameInfo.group =
                    event.gameInfo.group
                if (event.gameInfo.mode != UNKNOWN) gameResultDetails.gameResult.gameInfo.mode =
                    event.gameInfo.mode
                if (event.gameInfo.type != UNKNOWN) gameResultDetails.gameResult.gameInfo.type =
                    event.gameInfo.type
                if (event.gameInfo.view != UNKNOWN) gameResultDetails.gameResult.gameInfo.view =
                    event.gameInfo.view

                transitionTo(this, Effect.OnReceivedGameInfo(event.gameInfo))
            }
            // fetched TeamRank
            on<Event.GotTeamRank> { event ->
                if (event.teamRank != UNKNOWN) gameResultDetails.gameResult.teamRank =
                    event.teamRank
                transitionTo(this, Effect.OnReceivedTeamRank(teamRank = event.teamRank))
            }
            // game have been finished
            on<Event.GameCompleted> { event ->
                val result = gameResultDetails.gameResult
                val start = gameResultDetails.gameStartInfo
                val end = gameResultDetails.gameEndInfo
                val gameInfoJson = Json.encodeToString(result.gameInfo)

                logWithIdentifier(start.gameId) {
                    it.setMessage("Game Completed")
                    it.addContext("reason", event.reason)
                    it.setCategory(LogCategory.MACHINE_EVENTS)
                }

                val game = Game(
                    gameId = start.gameId,
                    userId = verifiedUserDetails.userId,
                    startTimeStamp = start.startTimeStamp,
                    endTimestamp = end.endTimeStamp,
                    rank = result.rank,
                    kills = result.kills,
                    teamRank = result.teamRank,
                    gameInfo = gameInfoJson,
                    initialTier = result.initialTier,
                    finalTier = result.finalTier,
                    squadScoring = result.squadScoring,
                    valid = true,
                    metaInfoJson = UNKNOWN
                )

                verifiedUserDetails.gamesPlayed.add(game)

                var updatedUserDetails = verifiedUserDetails
                // now it's not on Boarding user
                if (verifiedUserDetails.onBoarding)
                    updatedUserDetails = verifiedUserDetails.copy(onBoarding = false)

                transitionTo(
                    State.Verified(updatedUserDetails),
                    Effect.OnGameCompleted(
                        reason = event.reason,
                        executeInBackground = event.executeInBackground,
                        gameResultDetails = gameResultDetails
                    )
                )
            }

            on<Event.CanRecordNewGame> { event ->
                verifiedUserDetails.canStartGame = event.canRecord
                transitionTo(this, Effect.OnCanRecordNewGame(reason = event.reason))
            }
        }
        onTransition { nextStep ->
            val validStep = nextStep as? StateMachine.Transition.Valid ?: return@onTransition
            validStep.sideEffect?.let { EffectHandler.invoke()?.handle(it) }
        }
    }
}

class EffectHandler private constructor(val logger: Logger?):KoinComponent {
    val machine = com.gamerboard.live.gamestatemachine.stateMachine.StateMachine.machine
    private val visionMachine = VisionStateMachine.visionImageSaver
    private val db : AppDatabase by inject()
    private val logHelper: LogHelper by inject(LogHelper::class.java)
    fun handle(effect: Effect) {
        var logMessage: String = ""
        when (effect) {
            is Effect.OnMachineInitialized -> {
                logMessage = ("User Initialized with ${effect.unVerifiedUserDetails}\n")
                //To start the onBoarding process
                if (!effect.unVerifiedUserDetails.onBoarding)
                    clearOnBoardingPreferences()
            }

            is Effect.OnMachineUnInitialized -> {
                logMessage = ("User Uninitialized reason: ${effect.reason}\n")
            }

            is Effect.OnUserVerified -> {
                logMessage = ("User Verified with ${effect.verifiedUserDetails}\n")
                userIsVerified(
                    effect.verifiedUserDetails.originalGameId,
                    effect.verifiedUserDetails.gameCharId
                )
            }

            is Effect.OnUserUnVerified -> {
                logMessage = ("User UnVerified reason: ${effect.reason}\n")
                unVerifyUserOnLogin()
                logHelper.completeLogging()
            }

            is Effect.OnEnteredLobby -> {
                logMessage = ("User ${effect.verifiedUserDetails.userId} entered lobby\n")
            }

            is Effect.OnEnteredGameFromLobby -> {
                logMessage =
                    ("User entered game from lobby at ${effect.gameStartInfo.startTimeStamp} game Id assigned: ${effect.gameStartInfo.gameId}\n")
                visionMachine.transition(
                    VisionEvent.StartRecordingVisionImages(
                        gameId = effect.gameStartInfo.gameId,
                        reason = "Vision Images started to record as user started game!"
                    )
                )
                startRecordingGame(effect.gameStartInfo.gameId)
            }

            is Effect.OnEnteredGame -> {
                logMessage =
                    ("User enter the game at ${effect.gameStartInfo.startTimeStamp} game Id assigned: ${effect.gameStartInfo.gameId}\n")
                visionMachine.transition(
                    VisionEvent.StartRecordingVisionImages(
                        gameId = effect.gameStartInfo.gameId,
                        reason = "Vision Images started to record as user started game!"
                    )
                )
                startRecordingGame(effect.gameStartInfo.gameId)
            }

            is Effect.OnFetchingResults -> {
                logMessage =
                    ("User is on result screen, ready to fetch results game Id: ${effect.gameStartInfo.gameId}\n")

                saveGameInDatabase((machine.state as CurrentGameProvider).activeGame)
                visionMachine.transition(
                    VisionEvent.StartRecordingVisionImages(
                        gameId = effect.gameStartInfo.gameId,
                        reason = "Started fetching results!"
                    )
                )
            }

            is Effect.OnReceivedRank -> {
                logMessage = ("Got rank as: ${effect.rank}\n")
                updateGameInDatabase((machine.state as CurrentGameProvider).activeGame)
            }

            is Effect.OnReceivedSquadKills -> {
                logMessage = ("Got squad scoring kills as: ${effect.squadScoring}\n")
                updateGameInDatabase((machine.state as CurrentGameProvider).activeGame)
            }

            is Effect.OnReceivedKills -> {
                logMessage = ("Got kills as: ${effect.kills}\n")
                updateGameInDatabase((machine.state as CurrentGameProvider).activeGame)

                //Cancel existing and start timer from here to end the game.
                if (effect.kills != UNKNOWN)  // has to be present else we will wait
                    startGameEndTimer()
            }

            is Effect.OnReceivedGameInfo -> {
                logMessage = ("Got game info as: ${effect.gameInfo}\n")
                updateGameInDatabase((machine.state as CurrentGameProvider).activeGame)
            }

            is Effect.OnReceivedTier -> {
                logMessage =
                    ("Got initialTier as: ${effect.initialTier} and finalTier as: ${effect.finalTier}\n")
                updateGameInDatabase((machine.state as CurrentGameProvider).activeGame)
            }

            is Effect.OnReceivedTeamRank -> {
                logMessage = ("Got team rank as: ${effect.teamRank}\n")
                updateGameInDatabase((machine.state as CurrentGameProvider).activeGame)
            }
            /*is Effect.OnCanRecordNewGame -> {
                logMessage = ("Can record new game: ${effect.reason}\n")
            }*/
            is Effect.OnGameEnded -> {
                logMessage =
                    ("User is out of game, ended the game at ${effect.gameEndInfo.endTimeStamp}\n")
                finishRecordingGameVideo()
            }

            is Effect.OnGameCompleted -> {
                var gameId  : String?
                if (debugMachine != DEBUGGER.DIRECT_HANDLE)
                    CoroutineScope(Dispatchers.IO).launch {
                        val savedGames = db.getGamesDao()
                            .getGameById(effect.gameResultDetails.gameStartInfo.gameId)
                        if (!savedGames.isNullOrEmpty()) {
                            val savedGame = savedGames.first()
                            gameId = savedGame.gameId
                            logWithIdentifier(savedGame.gameId, savedGame.toString())
                            MachineConstants.machineInputValidator.getProcessedRankData()
                                ?.let { processedRank ->
                                    MachineConstants.machineInputValidator.updateGameWithRank(
                                        processedRank,
                                        savedGame
                                    )
                                    updateGameInDB(savedGame)
                                }
                            val sendForAutoMl: Boolean =
                                if (effect.gameResultDetails.gameResult.gameInfo.group != "solo") {
                                    MachineConstants.machineInputValidator.validateResultForTeamGames(
                                        savedGame
                                    )
                                } else {
                                    MachineConstants.machineInputValidator.validateResultForSoloGames(
                                        savedGame
                                    )
                                }
                            logWithIdentifier(savedGame.gameId, "sendForAutoMl $sendForAutoMl")
                            if (sendForAutoMl) {
                                logMessage =
                                    ("User completed game, sending for auto ml: ${savedGame}, reason: ${effect.reason} \n details: ${effect.gameResultDetails}\n")

                                if (!visionMachine.state.ready && visionMachine.state.readyWithRatingsScreen
                                    && effect.gameResultDetails.gameResult.gameInfo.group != BgmiGroups.solo.rawValue
                                ) {
                                    // check if it's a team match, the kills screen could be missing if player dies before teammates, but this is still a valid game
                                    // if that is the case call AutoMl without the kills screen and submit the game
                                    logMessage =
                                        ("User completed team game(non solo), called for sending for auto ml with missing kills screen \n details: ${effect.gameResultDetails}\n")
                                    gameId = effect.gameResultDetails.gameStartInfo.gameId
                                    logWithIdentifier(gameId) {
                                        it.setMessage(logMessage)
                                        it.setCategory(LogCategory.AUTO_ML)
                                    }
                                    val visionImagesProvider =
                                        (visionMachine.state as VisionImagesProvider)

                                    val visionImages = arrayListOf(
                                        visionImagesProvider.ratingsImage!!,
                                        visionImagesProvider.ratingsImage!!
                                    )

                                    val visionLabels = arrayListOf(
                                        visionImagesProvider.ratingsLabels!!,
                                        visionImagesProvider.ratingsLabels!!
                                    )
                                    visionImagesProvider.performanceImage?.let {
                                        visionImages.add(it)
                                    }
                                    visionImagesProvider.performanceLabels?.let {
                                        visionLabels.add(it)
                                    }
                                    MachineMessageBroadcaster.invoke()
                                        ?.showLoader(true, "Analysing game...")
                                    sendGameForAutoMl(
                                        savedGame,
                                        visionImages,
                                        visionLabels
                                    )
                                    visionMachine.transition(VisionEvent.SendForVisionCall(reason = "Vision call was made, go to initial state without deleting images!"))

                                    logWithIdentifier(gameId) {
                                        it.setMessage(logMessage)
                                        it.setCategory(LogCategory.ENGINE)
                                    }
                                    return@launch
                                }

                                if (!visionMachine.state.ready) {
                                    logMessage =
                                        ("User completed game, called for sending for auto ml but required data not available: ${savedGame}, reason: ${effect.reason} \n details: ${effect.gameResultDetails}\n")
                                    visionMachine.transition(VisionEvent.ResetVision(reason = "User completed the game but, images were not available!"))
                                    logWithIdentifier(savedGame.gameId) {
                                        it.setMessage(logMessage)
                                        it.setCategory(LogCategory.AUTO_ML)
                                    }

                                    logWithIdentifier(savedGame.gameId) {
                                        it.setMessage("Missing Auto ML Images")
                                        it.addContext("game", savedGame)
                                        it.addContext("game_id", savedGame.gameId)
                                        it.addContext(
                                            "reason",
                                            GameRepository.GameFailureReason.MISSING_AUTOML_IMAGES
                                        )
                                        it.addContext("reason_detail", effect.reason)
                                        it.setCategory(LogCategory.ICM)
                                    }
                                    logHelper.completeLogging()
                                    MachineMessageBroadcaster.invoke()
                                        ?.showGameFailureAlert(GameRepository.GameFailureReason.MISSING_AUTOML_IMAGES)
                                    return@launch
                                }
                                val visionImagesProvider =
                                    (visionMachine.state as VisionImagesProvider)

                                // kills image, Ratings image
                                val visionImages = arrayListOf<String>(
                                    visionImagesProvider.killsImage!!,
                                    visionImagesProvider.ratingsImage!!,
                                )

                                val visionLabels = arrayListOf(
                                    visionImagesProvider.killsLabels!!,
                                    visionImagesProvider.ratingsLabels!!
                                )
                                visionImagesProvider.performanceImage?.let {
                                    visionImages.add(it)
                                }
                                visionImagesProvider.performanceLabels?.let {
                                    visionLabels.add(it)
                                }
                                sendGameForAutoMl(
                                    savedGame,
                                    visionImages,
                                    visionLabels
                                )
                                visionMachine.transition(VisionEvent.SendForVisionCall(reason = "Vision call was made, go to initial state without deleting images!"))
                                logWithIdentifier(savedGame.gameId) {
                                    it.setMessage(logMessage)
                                    it.setCategory(LogCategory.ENGINE)
                                }
                                return@launch
                            }

                            logMessage =
                                ("User completed game: ${savedGame}, reason: ${effect.reason} \n details: ${effect.gameResultDetails}\n")
                            sendGameToServer(
                                savedGame,
                                effect.gameResultDetails
                            )
                            visionMachine.transition(VisionEvent.ResetVision(reason = "Game Send to server, no vision call made!"))
                        }
                        MachineConstants.machineInputValidator.clear()
                    }
            }

            is Effect.OnGameStartWithoutVerify -> {
                logMessage =
                    ("User warned to not start game without verification: ${effect.unVerifiedUserDetails}\n")
                warnUserToVerifyBeforeStartingGame(effect.reason)
            }

            is Effect.OnHomeScreenDirectlyFromGameEnd -> {
                logMessage =
                    ("User exited from game end, did not visit result screen reason: ${effect.reason}\n")
                showIncompleteGameUi(effect.reason)
                removeRecordingGameVideo()
            }

            is Effect.OnHomeScreenDirectlyFromGameStarted -> {
                logMessage = ("User exited from game start reason: ${effect.reason}\n")
                showIncompleteGameUi(effect.reason)
                removeRecordingGameVideo()
            }

            is Effect.OnHomeScreenDirectlyFromLobby -> {
                logMessage = ("User exited from lobby reason: ${effect.reason}\n")
                showIncompleteGameUi(effect.reason)
            }

            is Effect.OnReturnedToHomeAfterVerified -> {
                logMessage =
                    ("User returned to home after user was warned to verify: ${effect.reason}\n")
                showCompletedWithoutVerifyingUI()

            }

            is Effect.OnServiceStopped -> {
                logMessage = ("Service Stopped by user :${effect.reason}\n")
                uploadLogs(effect.reason, effect.stoppedVia)
            }

            else -> {}
        }
        LabelUtils.testLogGrey(logMessage)
        logWithIdentifier(GameHelper.getOriginalGameId()) {
            it.setMessage(logMessage)
            it.setCategory(LogCategory.MACHINE_EVENTS)
        }
    }

    private fun showCompletedWithoutVerifyingUI() {
        MachineMessageBroadcaster.invoke()?.finishedGameWithoutVerification()
    }

    companion object {
        @Volatile
        private var instance: EffectHandler? = null
        private val logger: Logger by inject(Logger::class.java)
        operator fun invoke() = synchronized(this) {
            // for debug

            if (!test()) // for debug
                if (instance == null)
                    instance = EffectHandler(
                        logger = logger
                    )

            instance
        }
    }
}