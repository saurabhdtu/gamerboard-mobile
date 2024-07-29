package com.gamerboard.live.gamestatemachine

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.gamestatemachine.stateMachine.*
import com.gamerboard.live.models.ImageResultJsonFlat
import com.gamerboard.live.repository.SessionManager
import com.gamerboard.live.utils.UiUtils
import com.gamerboard.logger.gson
import com.google.api.client.json.Json
import com.google.common.reflect.TypeToken
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.serialization.Serializable
import org.json.JSONObject
import java.io.File
import java.io.IOException


sealed class RestoreSMObject

@Serializable
data class RPUnInitialized(val onBoarding: Boolean? = null, val originalGameId: String?, val originalBgmiName:String?) :
    RestoreSMObject()

@Serializable
data class RPIdle(val unVerifiedUserDetails: UnVerifiedUserDetails) : RestoreSMObject()

@Serializable
data class RPVerified(val verifiedUserDetails: VerifiedUserDetails) : RestoreSMObject()

@Serializable
data class RPLobby(val verifiedUserDetails: VerifiedUserDetails) : RestoreSMObject()

@Serializable
data class RPGameStarted(
    val verifiedUserDetails: VerifiedUserDetails,
    val gameStartInfo: GameStartInfo
) : RestoreSMObject()

@Serializable
data class RPGameEnded(
    val verifiedUserDetails: VerifiedUserDetails,
    val gameStartInfo: GameStartInfo,
    val gameEndInfo: GameEndInfo
) : RestoreSMObject()

@Serializable
data class RPFetchResult(
    val verifiedUserDetails: VerifiedUserDetails,
    val gameResultDetails: GameResultDetails
) : RestoreSMObject()

@Serializable
data class RPWarnedToVerify(val unVerifiedUserDetails: UnVerifiedUserDetails) : RestoreSMObject()

@Serializable
data class RPWarnedDidNotPlayGame(val verifiedUserDetails: VerifiedUserDetails) : RestoreSMObject()

@Serializable
data class RPWarnedNotFinishedGame(
    val verifiedUserDetails: VerifiedUserDetails,
    val gameStartInfo: GameStartInfo
) : RestoreSMObject()

@Serializable
enum class StateEnum {
    RSUnInitialized,
    RSIdle,
    RSVerified,
    RSLobby,
    RSGameStarted,
    RSGameEnded,
    RSFetchResult,
    RSWarnedToVerify,
    RSWarnedDidNotPlayGame,
    RSWarnedNotFinishedGame,
}

// STATE MACHINE RESTORE
data class SavedMachineState(
    var buffer: HashMap<List<Int>, ArrayList<ImageResultJsonFlat>>,
    var restoreState: StateEnum,
    val restoreSMParams: RestoreSMObject
)

var prevToast: Int = 0
fun showToast(message: String, code: Int? = null, force: Boolean = false) {
    if (force)
        Handler(Looper.getMainLooper()).post {
            if (code == null || code != prevToast) {
                UiUtils.showToast(GamerboardApp.instance as Context, message, Toast.LENGTH_LONG)
                prevToast = code ?: prevToast
            }
        }
}
/**
 *[MachineManager]This takes care of restoring our state machine to the same point if the state machine is reset due to crash.
 */

object MachineManager {
    var restoring = false
    var savingState = false
    fun saveMachine() {
        if (restoring)
            return
        try {
            val file = File(
                "${(GamerboardApp.instance as Context).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/stateMachine",
                "saved_state.json"
            )
            if (!file.exists()) {
                file.parentFile?.mkdirs()
                file.createNewFile()
            }
            if (!file.exists())
                return

            file.writeText("")

            var restoreState: StateEnum? = null
            var restoreSMParams: RestoreSMObject? = null

            when (val onState = StateMachine.machine.state) {
                is State.UnInitialized -> {
                    restoreState = StateEnum.RSUnInitialized
                    restoreSMParams =
                        RPUnInitialized(onState.onBoarding, onState.originalGameId, onState.originalUserName)
                }
                is State.Idle -> {
                    restoreState = StateEnum.RSIdle
                    restoreSMParams =
                        RPIdle(unVerifiedUserDetails = onState.unVerifiedUserDetails)
                }
                is State.Verified -> {
                    restoreState = StateEnum.RSVerified
                    restoreSMParams = RPVerified(verifiedUserDetails = onState.verifiedUserDetails)

                }
                is State.Lobby -> {
                    restoreState = StateEnum.RSLobby
                    restoreSMParams = RPLobby(verifiedUserDetails = onState.verifiedUserDetails)
                }
                is State.GameStarted -> {
                    restoreState = StateEnum.RSGameStarted
                    restoreSMParams = RPGameStarted(
                        verifiedUserDetails = onState.verifiedUserDetails,
                        gameStartInfo = onState.gameStartInfo
                    )
                }
                is State.GameEnded -> {
                    restoreState = StateEnum.RSGameEnded
                    restoreSMParams = RPGameEnded(
                        verifiedUserDetails = onState.verifiedUserDetails,
                        gameStartInfo = onState.gameStartInfo,
                        gameEndInfo = onState.gameEndInfo
                    )
                }
                is State.FetchResult -> {
                    restoreState = StateEnum.RSFetchResult
                    restoreSMParams = RPFetchResult(
                        verifiedUserDetails = onState.verifiedUserDetails,
                        gameResultDetails = onState.gameResultDetails
                    )
                }
                is State.WarnedToVerify -> {
                    restoreState = StateEnum.RSWarnedToVerify
                    restoreSMParams =
                        RPWarnedToVerify(unVerifiedUserDetails = onState.unVerifiedUserDetails)
                }
                is State.WarnedDidNotPlayGame -> {
                    restoreState = StateEnum.RSWarnedToVerify
                    restoreSMParams =
                        RPWarnedDidNotPlayGame(verifiedUserDetails = onState.verifiedUserDetails)
                }
                is State.WarnedNotFinishedGame -> {
                    restoreState = StateEnum.RSWarnedNotFinishedGame
                    restoreSMParams =
                        RPWarnedNotFinishedGame(
                            verifiedUserDetails = onState.verifiedUserDetails,
                            gameStartInfo = onState.gameStartInfo
                        )
                }
            }

            val data = SavedMachineState(
                MachineConstants.machineLabelProcessor.currentBuffer,
                restoreState,
                restoreSMParams
            )
            val json = gson.toJson(data)
            if(savingState) return
            savingState = true
            file.writeText(json)
            savingState = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun restoreMachine() {
        restoring = true
        try {
            val resultRestoreMachine = restoreStateMachineState()
            resultRestoreMachine?.let {
                StateMachine.machine.transition(Event.OnRestoreMachine(it))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        restoring = false
    }

    private fun restoreStateMachineState(): SavedMachineState? {
        File(
            "${(GamerboardApp.instance as Context).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/stateMachine",
            "saved_state.json"
        ).apply {
            if (exists()) {
                if (readText().isEmpty())
                    return null
                try {
                    val json = readText()
                    val jsonObj = JSONObject(json)
                    val type = object : TypeToken<HashMap<List<Int>, ArrayList<ImageResultJsonFlat>>>() {}.type
                    val state = StateEnum.valueOf(jsonObj.getString("restoreState"))
                    val restoreObj:RestoreSMObject = when(state){
                        StateEnum.RSUnInitialized -> gson.fromJson(jsonObj.getJSONObject("restoreSMParams").toString(), RPUnInitialized::class.java)
                        StateEnum.RSIdle -> gson.fromJson(jsonObj.getJSONObject("restoreSMParams").toString(), RPIdle::class.java)
                        StateEnum.RSVerified -> gson.fromJson(jsonObj.getJSONObject("restoreSMParams").toString(), RPVerified::class.java)
                        StateEnum.RSLobby -> gson.fromJson(jsonObj.getJSONObject("restoreSMParams").toString(), RPLobby::class.java)
                        StateEnum.RSGameStarted -> gson.fromJson(jsonObj.getJSONObject("restoreSMParams").toString(), RPGameStarted::class.java)
                        StateEnum.RSGameEnded -> gson.fromJson(jsonObj.getJSONObject("restoreSMParams").toString(), RPGameEnded::class.java)
                        StateEnum.RSFetchResult -> gson.fromJson(jsonObj.getJSONObject("restoreSMParams").toString(), RPFetchResult::class.java)
                        StateEnum.RSWarnedToVerify -> gson.fromJson(jsonObj.getJSONObject("restoreSMParams").toString(), RPWarnedToVerify::class.java)
                        StateEnum.RSWarnedDidNotPlayGame ->  gson.fromJson(jsonObj.getJSONObject("restoreSMParams").toString(), RPWarnedDidNotPlayGame::class.java)
                        StateEnum.RSWarnedNotFinishedGame -> gson.fromJson(jsonObj.getJSONObject("restoreSMParams").toString(), RPWarnedNotFinishedGame::class.java)
                    }
                    return SavedMachineState(
                        buffer = gson.fromJson(jsonObj.getJSONObject("buffer").toString(), type),
                        restoreState = state,
                        restoreSMParams = restoreObj
                    )
//                    return gson.fromJson(readText(), SavedMachineState::class.java)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                } finally {
                    delete()
                }
            }
        }
        return null
    }

    suspend fun clearMachine() {
        File(
            "${(GamerboardApp.instance as Context).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/stateMachine",
            "saved_state.json"
        ).apply {
            try {
                SessionManager.clearSession()
                if (exists())
                    this.delete()
            } catch (e: java.lang.Exception) {
                Log.d("saveMachine", "File already removed!")
            }
        }
    }
}
