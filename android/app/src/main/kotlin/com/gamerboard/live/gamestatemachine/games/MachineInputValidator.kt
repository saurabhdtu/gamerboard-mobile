package com.gamerboard.live.gamestatemachine.games

import com.gamerboard.live.common.RemoteConfigConstants
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.Event
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.MachineMessageBroadcaster
import com.gamerboard.live.gamestatemachine.stateMachine.MachineResult
import com.gamerboard.live.gamestatemachine.stateMachine.OnBoardingInfoProvider
import com.gamerboard.live.gamestatemachine.stateMachine.State
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachine
import com.gamerboard.live.gamestatemachine.stateMachine.UserDetails
import com.gamerboard.live.gamestatemachine.stateMachine.VerifiedUser
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.live.models.ImageResultJsonFlat
import com.gamerboard.live.models.db.Game
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.log
import com.gamerboard.logger.logWithCategory
import com.gamerboard.logger.logWithIdentifier
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.serialization.json.Json
import okhttp3.internal.immutableListOf
import org.json.JSONObject


abstract class MachineInputValidator {


    val rankOCRCount = HashMap<String, Int>()
    val validTypesCache = arrayListOf<String>()
    val validModesCache = arrayListOf<String>()
    val validGroupsCache = arrayListOf<String>()
    val validMapsCache = arrayListOf<String>()
    val killOCRCount = HashMap<Int, HashMap<String, Int>>()
    val squadUserNames = HashMap<Int, HashMap<String, Int>>()
    var sendForAutoML = false
    private var visionCallForLabel = mutableMapOf<Int, VisionStateForLabel>()
    var firstKillScreenTS = -1L
    val ocrWeight = if (debugMachine == DEBUGGER.DIRECT_HANDLE) JSONObject(
        "{\n" +
                "            \"base_weight\": 1,\n" +
                "            \"contains_1\": 2,\n" +
                "            \"starts_with_1\": 10,\n" +
                "            \"vision_ocr\": 30\n" +
                "            }"
    ) else JSONObject(
        FirebaseRemoteConfig.getInstance().getString(
            RemoteConfigConstants.OCR_WEIGHT
        )
    )
    val weightVisionOCR = ocrWeight.getInt("vision_ocr")
    val weightStartsWith1 = ocrWeight.getInt("starts_with_1")
    val baseWeight = ocrWeight.getInt("base_weight")
    val contains1 = ocrWeight.getInt("contains_1")
    fun clear() {
        sendForAutoML = false
        validTypesCache.clear()
        rankOCRCount.clear()
        validModesCache.clear()
        validGroupsCache.clear()
        validMapsCache.clear()
        killOCRCount.clear()
        squadUserNames.clear()
        firstKillScreenTS = -1
    }

    /**
     * Checks and validates the profile id ocr for the [_input]
     */
    abstract fun validateProfileId(_input: ArrayList<ImageResultJsonFlat>): MachineResult

    abstract fun validateCharacterId(
        _input: ArrayList<ImageResultJsonFlat>,
        originalBGMIId: String?
    ): MachineResult

    abstract fun validateProfileIdAndLevel(_input: ArrayList<ImageResultJsonFlat>): MachineResult

    abstract fun validateRankRatingGameInfo(
        _input: ArrayList<ImageResultJsonFlat>,
        originalBGMIId: String? = null,
        originalBGBICharacterID: String? = null,
        isFromAutoMl: Boolean = false
    ): MachineResult

//    abstract fun validateRating(
//        _input: ArrayList<ImageResultJsonFlat>,
//        originalBGMIId: String? = null,
//        originalBGBICharacterID: String? = null,
//        isFromAutoMl: Boolean = false
//    ): MachineResult

    abstract fun validateRankKillGameInfo(
        _input: ArrayList<ImageResultJsonFlat>,
        originalBGMIId: String? = null,
        originalBGBICharacterID: String? = null,
        isFromAutoMl: Boolean = false
    ): MachineResult


//    abstract fun validatePerformanceScreen(
//        _input: ArrayList<ImageResultJsonFlat>,
//        originalBGMIId: String? = null,
//        originalBGBICharacterID: String? = null,
//        isFromAutoMl: Boolean = false
//    ): MachineResult

    abstract fun sendForAutoMLHelper(
        result: MachineResult.Builder,
        validInputOcr: ArrayList<Pair<String, String>>
    ): Boolean

    abstract fun compareUserId(ocrText: String, originalGameCharId: String?): Boolean

    abstract fun validateGameInfo(gameInfo: String): Array<String>

    abstract fun validateLogin(_input: ArrayList<ImageResultJsonFlat>): MachineResult

    abstract fun validateWaiting(_input: ArrayList<ImageResultJsonFlat>): MachineResult

    abstract fun validateProfileLevel(_input: ArrayList<ImageResultJsonFlat>): MachineResult


    abstract fun validateGameHistory(_input: ArrayList<ImageResultJsonFlat>): MachineResult


    abstract fun getTeamRankFromHistory(history: String): MachineResult

    abstract fun getRankFromHistory(history: String): MachineResult

    abstract fun getPlayersDefeatedFromHistory(history: String): MachineResult

    abstract fun getGameInfoFromHistory(history: String): MachineResult

    abstract fun getGameTimeStampFromHistory(history: String): Array<String>

    fun validateWithNoOcr(_input: ArrayList<ImageResultJsonFlat>): MachineResult {
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()

        if (validateStart(input).accept == true)
            result.setAccepted()

        if (validateProfileId(input).accept == true)
            result.setAccepted()

        if (validateProfileIdAndLevel(input).accept == true)
            result.setAccepted()

        if (validateProfileLevel(input).accept == true)
            result.setAccepted()

        if (validateLogin(input).accept == true) {
            result.setAccepted()
            result.setLogin()
        }

        return result.build()
    }

    abstract fun validateStart(_input: ArrayList<ImageResultJsonFlat>): MachineResult

    abstract fun validateInGameWithNoOCR(_input: ArrayList<ImageResultJsonFlat>): MachineResult
    abstract fun validateGameEnd(_input: ArrayList<ImageResultJsonFlat>): MachineResult

    fun getSquadScoringArray(jsonString: String): kotlinx.serialization.json.JsonArray =
        Json.decodeFromString(jsonString)


    abstract fun validateResultForTeamGames(game: Game): Boolean

    abstract fun validateResultForSoloGames(game: Game): Boolean

    abstract fun getProcessedRankData(isFromAutoMl: Boolean = false): String?

    fun validateProfile(inputBuffer: ArrayList<ImageResultJsonFlat>): List<MachineResult>? {
        val machine = StateMachine.machine
        if (machine.state is State.UnInitialized) {
            val onBoarding = (machine.state as OnBoardingInfoProvider).onBoarding ?: return null
            // Check if this is a placement match.
            if (onBoarding) {
                val id = MachineConstants.machineInputValidator.validateProfileId(
                    inputBuffer
                )
                val charId = MachineConstants.machineInputValidator.validateCharacterId(
                    inputBuffer, id.id
                )

                if (id.id == null || charId.charId == null) return immutableListOf(id, charId)

                MachineMessageBroadcaster.invoke()?.preProfileVerification(
                    fetchedNumericId = id.id,
                    fetchedCharacterId = charId.charId,
                    inputBuffer.first().labels.firstOrNull { it.label == MachineConstants.gameConstants.profileIdLabel() }
                )
                return immutableListOf(id, charId)
            }
            return null
        }
        val originalGameId =
            (machine.state as UserDetails).unVerifiedUserDetails.originalGameId
        val originalGameUserName =
            (machine.state as UserDetails).unVerifiedUserDetails.originalGameUserName
        //val isPlacement = (machine.state as UserDetails).unVerifiedUserDetails.isPlacement
        val id =
            MachineConstants.machineInputValidator.validateProfileId(inputBuffer)
        val charId = MachineConstants.machineInputValidator.validateCharacterId(
            inputBuffer, originalGameId
        )
        if (charId.accept == true && id.accept == true && !charId.charId.isNullOrBlank() && !id.id.isNullOrEmpty()) {
            if (machine.state is VerifiedUser) return immutableListOf(id, charId)
            val isValidCharacterId = compareAndMatchGameUserId(id.id, originalGameId)
            if (isValidCharacterId.not()) {
                when (visionCallForLabel[MachineConstants.gameConstants.profileIdLabel()]) {
                    VisionStateForLabel.UNPROCESSED, VisionStateForLabel.PROCESSING -> {
                    }

                    VisionStateForLabel.PROCESSED -> {
                        MachineMessageBroadcaster.invoke()
                            ?.showUserIdMismatch(id.id, originalGameId)
                    }

                    null -> visionCallForLabel[MachineConstants.gameConstants.profileIdLabel()] =
                        VisionStateForLabel.UNPROCESSED
                }
            } else {
                log {
                    it.setMessage("Profile verification success status")
                    it.addContext("id_verification", true)
                    it.addContext("observed_user_id", id.id)
                    it.addContext("expected_user_id", originalGameId)
                }
                //   gameProfileId = "55501298194",
                //      gameCharId = "RONIN"
                StateMachine.machine.transition(Event.VerifyUser(gameCharId = id.charId.toString(), gameProfileId = id.id))
            }

            val isValidUsername  = compareAndMatchGameUsername(charId.charId, originalGameUserName)
            if (updateGameIdActive == 1) return immutableListOf(id, charId)
            if (isValidUsername.not()) {
                updateGameIdActive = 1
                MachineMessageBroadcaster.invoke()?.alertForUserNameChange(
                    fetchedCharacterId = charId.charId,
                    originalBGMIId = originalGameUserName
                )
            } else if (isValidCharacterId) {
                updateGameIdActive = 1
                logToFile("Starting profile verification for returning user,  update query send with id:${id.id}, charId:${charId.charId}, original: $originalGameId")
                MachineMessageBroadcaster.invoke()?.updateProfile(charId.charId)
            }
        }
        return immutableListOf(id, charId)
    }

    abstract fun compareAndMatchGameUsername(obtained: String?, expected: String?): Boolean

    fun compareAndMatchGameUserId(obtained: String?, expected: String?) =
        obtained.isNullOrEmpty().not() && expected.isNullOrEmpty()
            .not() && expected.equals(obtained) /*Boolean{
        if (obtained != null && expected != null) {
            return if (obtained.length > expected.length) {
                obtained.contains(expected)
            } else {
                expected.contains(obtained)
            }
        }
        return false
    }*/

    fun updateGameWithRank(processedRank: String, savedGame: Game) {

        logWithIdentifier(savedGame.gameId) {
            it.setMessage("processedRank: $processedRank")
            it.setCategory(LogCategory.RANK_KILL)
        }
        log("Game:$savedGame | Processed rank $processedRank")
        savedGame.rank = processedRank
        savedGame.teamRank = null
        savedGame.gameInfo?.contains("solo")?.not()?.let {
            if (it) savedGame.teamRank = processedRank
        }
    }


    protected fun logToFile(message: String, logCategory: LogCategory = LogCategory.ENGINE) {
        logWithCategory(message, logCategory)
    }

    fun shouldPerformVisionCallForLabel(label: Float): Boolean {
        return if (visionCallForLabel.containsKey(label.toInt())) {
            visionCallForLabel[label.toInt()] == VisionStateForLabel.UNPROCESSED
        } else {
            false
        }
    }

    fun processedVisionForLabel(label: Float) {
        visionCallForLabel[label.toInt()] = VisionStateForLabel.PROCESSED
    }

    fun processVisionForLabel(label: Float) {
        visionCallForLabel[label.toInt()] = VisionStateForLabel.PROCESSING
    }

    enum class VisionStateForLabel {
        UNPROCESSED,
        PROCESSING,
        PROCESSED
    }
}

var updateGameIdActive = 0