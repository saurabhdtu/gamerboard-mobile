package com.gamerboard.live.gamestatemachine.stateMachine

import android.content.Context
import android.content.Intent
import com.gamerboard.live.common.BroadcastFilters
import com.gamerboard.live.gamestatemachine.games.GameHelper
import com.gamerboard.live.gamestatemachine.showToast
import com.gamerboard.live.models.TFResult
import com.gamerboard.live.repository.GameRepository
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.LogHelper
import com.gamerboard.logger.log
import com.gamerboard.logger.loggerWithIdentifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


/**
 * A channel to call for the action on the UI (UI Thread), from the State Machine Events and other events form background thread.
 * e.g: [showLoader], [setProfileVerification], [gameEndedBroadcast], [newLoginBroadcast], [firstTimeHomeScreen], [startRecordingGameVideo]
 * */
class MachineMessageBroadcaster private constructor() : KoinComponent{
    private val context : Context by inject()
    private val logHelper : LogHelper by inject()

    fun showLoader(show: Boolean, message: String, onScreen: Int = MachineConstants.ScreenName.OTHER.ordinal) {
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "screen_loader")
            intent.putExtra("show", show)
            intent.putExtra("message", message)
            intent.putExtra("on_screen", onScreen)
            context.sendBroadcast(intent)
        }
    }

    fun setProfileVerification(
        verified: Boolean, fetchedNumericId: String, fetchedCharacterId: String
    ) {
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "profile_verified")
            intent.putExtra("state", verified)
            intent.putExtra("fetchedNumericId", fetchedNumericId)
            intent.putExtra("fetchedCharacterId", fetchedCharacterId)
            context.sendBroadcast(intent)
        }
    }

    fun showKillsFetchedOverlay(kills: String) {
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "kills_fetched_overlay")
            intent.putExtra("kills", kills)
            context.sendBroadcast(intent)
        }
    }

    fun  preProfileVerification(
        fetchedNumericId: String, fetchedCharacterId: String, tfResult: TFResult?,

        ) {
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "pre_profile_verification")
            intent.putExtra("fetchedNumericId", fetchedNumericId)
            intent.putExtra("rect", tfResult?.getBoundingBox())
            intent.putExtra("fetchedCharacterId", fetchedCharacterId)
            context.sendBroadcast(intent)
        }
    }

    fun alertForUserNameChange(
        fetchedCharacterId: String, originalBGMIId: String?
    ) {
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "alert_esport_username")
            intent.putExtra("fetchedUserName", fetchedCharacterId)
            intent.putExtra("originalUserName", originalBGMIId)
            context.sendBroadcast(intent)
        }
    }

    fun newLoginBroadcast() {
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "new_login")
            context.sendBroadcast(intent)
        }
    }

    fun gameEndedBroadcast(gameId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            log("Broadcast gameEndedBroadcast from executeInBackground for gameId: $gameId")
            Intent(BroadcastFilters.SERVICE_COM).also { intent ->
                showToast("Game Finalized! $gameId")
                intent.putExtra("action", "game_ended_broadcast")
                intent.putExtra("game_id", gameId)
                context.sendBroadcast(intent)
            }
        }
    }


    fun startRecordingGameVideo(gameId: String) {
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "start_record_game_video")
            intent.putExtra("game_id", gameId)
            context.sendBroadcast(intent)
        }
    }

    fun stopRecordingGameVideo() {
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "stop_record_game_video")
            context.sendBroadcast(intent)
        }
    }

    fun removeGamePlayVideo() {
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "remove_record_game_video")
            context.sendBroadcast(intent)
        }
    }

    fun firstTimeHomeScreen() {
        log("Broadcast firstTimeHomeScreen")
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "first_home_screen")
            context.sendBroadcast(intent)
        }
    }

    fun firstGameScreen() {
        log("Broadcast firstGameScreen")
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "first_game_screen")
            context.sendBroadcast(intent)
        }
    }

    fun finishedIncompleteGame(message: String) {
        log("Broadcast finishedIncompleteGame")
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "finished_incomplete_game")
            intent.putExtra("message", message)
            context.sendBroadcast(intent)
        }
    }

    fun finishedGameWithoutVerification() {
        log("Broadcast finishedGameWithoutVerification")
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "finished_game_unverified")
            context.sendBroadcast(intent)
        }
    }

    fun warnUserToVerifyBeforeStart() {
        log("Broadcast finishedIncompleteGame")
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "warn_user_to_verify")
            context.sendBroadcast(intent)
        }
    }

    fun showGameFailureAlert(failureReason: GameRepository.GameFailureReason) {
        log("Message alert")
        Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "game_failure")
            intent.putExtra("error", failureReason.ordinal)
            context.sendBroadcast(intent)
        }
    }


    private fun log(
        message: String, category: LogCategory = LogCategory.ENGINE, commitLog: Boolean = false
    ) {
       val agent =  loggerWithIdentifier(GameHelper.getOriginalGameId())
        log(agent){
            it.setMessage(message)
            it.setCategory(category)
        }
        if(commitLog){
            logHelper.completeLogging()
        }
    }

    fun showVerificationError(
        message: String
    ) {
        if (!test()) Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "show_verification_error")
            intent.putExtra("message", message)
            context.sendBroadcast(intent)
        }
    }

    fun showUserIdMismatch(observedUserId:String, expectedUserId:String){
        if (!test()) Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "alert_game_id_mismatch")
            intent.putExtra("originalUserId", expectedUserId)
            intent.putExtra("fetchedUserId", observedUserId)
            context.sendBroadcast(intent)
        }
        log {
            it.setMessage("Profile verification success status")
            it.addContext("id_verification", false)
            it.addContext("observed_user_id", observedUserId)
            it.addContext("expected_user_id", expectedUserId)
        }
    }

    fun updateProfile(
        gameUserName: String
    ) {
        if (!test()) Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "update_profile")
            intent.putExtra("game_user_name", gameUserName)
            context.sendBroadcast(intent)
        }
    }

    fun firstGameEndScreen() {
        if (!test()) Intent(BroadcastFilters.SERVICE_COM).also { intent ->
            intent.putExtra("action", "game_ended")
            context.sendBroadcast(intent)
        }
    }

    companion object {
        @Volatile
        private var instance: MachineMessageBroadcaster? = null
        operator fun invoke() = synchronized(this) {
            if (!test()) // for debug
                if (instance == null) instance = MachineMessageBroadcaster()
            instance
        }
    }
}