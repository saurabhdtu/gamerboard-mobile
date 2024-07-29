package com.gamerboard.live.service.screencapture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Environment
import android.util.Base64
import android.util.Log
import androidx.core.graphics.scale
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.gamestatemachine.games.GameHelper
import com.gamerboard.live.gamestatemachine.games.LabelHelper
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.games.LabelUtils.getListOfLabels
import com.gamerboard.live.gamestatemachine.games.LabelUtils.putToMetaInfoJson
import com.gamerboard.live.gamestatemachine.games.LabelUtils.trimSpace
import com.gamerboard.live.gamestatemachine.games.freefire.FreeFireInputValidator
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.MachineMessageBroadcaster
import com.gamerboard.live.gamestatemachine.stateMachine.MachineResult
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants.UNKNOWN
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.live.models.ImageResultJsonFlat
import com.gamerboard.live.models.TFResult
import com.gamerboard.live.models.db.AppDatabase
import com.gamerboard.live.models.db.Game
import com.gamerboard.live.repository.GameRepository
import com.gamerboard.live.type.SortOrder
import com.gamerboard.live.utils.logException
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.LogHelper
import com.gamerboard.logger.gson
import com.gamerboard.logger.logWithIdentifier
import com.gamerboard.logger.model.OcrInfoMessage
import com.gamerboard.logger.stackTrace
import com.google.android.gms.tasks.Task
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.internal.immutableListOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

typealias ResultRankMachineResultCallback = (rank: String?, teamRank: String?, gameInfo: String?, kills: String?, squadScoring: String?, metaInfo: String?) -> Unit
typealias RankRatingMachineResultCallback = (initialTier: String?, finalTier: String?, rank: String?, gameInfo: String?, metaInfo: String?) -> Unit

class MLKitOCR : KoinComponent {
    private val db: AppDatabase by inject()
    private var functions: FirebaseFunctions = FirebaseFunctions.getInstance()
    var bitmapHeight = 720
    private val logHelper: LogHelper by inject(LogHelper::class.java)
    suspend fun queryOcrIndividually(
        bitmapList: ArrayList<Bitmap>,
        tfResultsList: ArrayList<ArrayList<TFResult>>,
        imagePathList: ArrayList<String>,
        originalGame: Game,
        originalGameId: String,
        originalGameCharacterID: String,
        tfCoordinatesList: ArrayList<HashMap<TFResult, Pair<Int, Int>>>,
        callback: ((Game?) -> Unit)?,
        retry: Int = 0,
    ) {

        val gameClone = originalGame.copy()
        tfResultsList.forEachIndexed { index, tfResults ->
            val bitmap = bitmapList[index]
            tfResults.forEach { tfResult ->
                if (tfResult.shouldPerformIndividualOcr()) {
                    processTFResult(bitmap, tfResult)
                }
            }
            try {
                val resultJsonFlat = arrayListOf(
                    ImageResultJsonFlat(
                        0, if (index < imagePathList.size) imagePathList[index] else "", tfResults
                    )
                )
                correctGameData(
                    gameClone,
                    labelsArray = tfResults,
                    resultJsonFlat = resultJsonFlat,
                    originalGameId = originalGameId,
                    originalGameCharacterID = originalGameCharacterID,
                )
            } catch (ex: IndexOutOfBoundsException) {
                ex.printStackTrace()
            }
        }

        logWithIdentifier(originalGame.gameId) {
            it.setMessage("Query with individual ocr completed!")
            it.setCategory(LogCategory.AUTO_ML)
        }
        val correctedGame = buildGameDataFromOriginalGame(gameClone)
        updateRankFromCorrectedGame(correctedGame)

        logWithIdentifier(originalGame.gameId) {
            it.setMessage("OCR_GAME:")
            it.addContext("corrected_game", correctedGame)
            it.setCategory(LogCategory.AUTO_ML)
        }
        onOcrFinished(correctedGame, originalGame)
        tfResultsList.flatten().forEach { it.clear() }
        tfResultsList.clear()
        callback?.invoke(correctedGame)

    }

    suspend fun visionCallForSingleImage(bitmap: Bitmap, tfResults: List<TFResult>) {
        tfResults.forEach { tfResult ->
            if (tfResult.shouldPerformIndividualOcr())
                processTFResult(bitmap, tfResult)
        }
    }

    suspend fun visionCallForSingleImage(bitmap: Bitmap, tfResult: TFResult) {
        processTFResult(bitmap, tfResult)
    }


    private suspend fun processTFResult(
        bitmap: Bitmap,
        tfResult: TFResult,
    ) {

        val copiedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val croppedImage = LabelHelper.getCroppedImage(tfResult, copiedBitmap, null)
        Log.d("VISION_CALL", "call performed ${tfResult.label}")
        val jsonObject = performVision(croppedImage) ?: return

        val tfResultListToProcess = arrayListOf(arrayListOf(tfResult))
        val coordinateList = arrayListOf<HashMap<TFResult, Pair<Int, Int>>>()
        val resolutionList = arrayListOf<Pair<Int, Int>>()
        resolutionList.add(Pair(copiedBitmap.width, copiedBitmap.height))
        coordinateList.addAll(mapTfResult(tfResultListToProcess, resolutionList, 1))

        val preResult = formatResponse(annotation = jsonObject)

        processVisionAnnotation(
            tfResult,
            copiedBitmap.height,
            preResult,
            mapTfResult(tfResultListToProcess, resolutionList, 1).first()
        )
    }


    suspend fun performVision(bitmapLabel: Bitmap): JsonObject? {
        val base64 = getBase64String(bitmapLabel)
        val request = buildRequest(base64)

        return annotateImage(request).await()
    }

    suspend fun queryOcr(
        bitmapList: ArrayList<Bitmap>,
        tfResultsList: List<List<TFResult>>,
        imagePathList: ArrayList<String>,
        originalGame: Game,
        originalBGMIId: String,
        originalBGMICharacterID: String,
        tfCoordinatesList: ArrayList<HashMap<TFResult, Pair<Int, Int>>>,
        callback: ((Game?) -> Unit)?,
        retry: Int = 0,
    ) {
        //saveOcr(Json.encodeToString(tfResults), "tf_${imagePath.substringBeforeLast(".jpg")}")

        val len = bitmapList.size
        if (len > 0) bitmapHeight = bitmapList.first().height

        val resolutionList = arrayListOf<Pair<Int, Int>>()
        for (i in 0 until len) resolutionList.add(Pair(bitmapList[i].width, bitmapList[i].height))
        tfCoordinatesList.addAll(mapTfResult(tfResultsList, resolutionList, len))

        val newBitmap = stitchBitmap(this, bitmapList, tfResultsList, len)
        val base64 = getBase64String(newBitmap)
        val request = buildRequest(base64)

        if (debugMachine == DEBUGGER.DIRECT_HANDLE) FileAndDataSync(GamerboardApp.instance).captureImageForUse(
            newBitmap, "${System.currentTimeMillis()}_ocr_vision_image.jpg", quality = 90
        )
        MachineMessageBroadcaster.invoke()?.showLoader(true, "Sending scores")

        if (retry == 0) {
            logHelper.logImage(gameId = originalGame.gameId, newBitmap)
        }

        // clears the cache from the rating and rank screens.
        annotateImage(request)
            .addOnSuccessListener { jsonElement ->
                jsonElement?.let {
                    val game = processAnnotationResult(
                        it,
                        tfResultsList,
                        tfCoordinatesList,
                        imagePathList,
                        originalGame,
                        originalBGMIId,
                        originalBGMICharacterID
                    )
                    saveStitchedBitmap(newBitmap, bitmapList, imagePathList)
                    callback?.invoke(game)
                    tfResultsList.flatten().forEach { it.clear() }
                    //tfResultsList.clear()
                    MachineMessageBroadcaster.invoke()?.showLoader(false, "")
                }

            }.addOnFailureListener { exception ->
                if (retry <= 3) {
                    retry(
                        exception,
                        imagePathList,
                        originalGame,
                        tfResultsList,
                        originalBGMICharacterID,
                        originalBGMIId,
                        retry
                    )
                    return@addOnFailureListener
                }
                logFailedOcr(exception, originalGame, bitmapList, newBitmap, callback)
                tfResultsList.flatten().forEach { it.clear() }
                //   tfResultsList.clear()
            }
    }

    private fun logFailedOcr(
        throwable: Throwable,
        originalGame: Game,
        bitmapList: ArrayList<Bitmap>,
        newBitmap: Bitmap,
        callback: ((Game?) -> Unit)?,
    ) {

        logWithIdentifier(originalGame.gameId) {
            it.setMessage("Auto ML task failed")
            it.setOcrInfo(
                OcrInfoMessage(
                    vision = OcrInfoMessage.Vision(
                        success = false,
                        failureReason = throwable.message
                    )
                )
            )
            it.addContext("error", throwable.stackTraceToString())
            it.addContext("game_id", originalGame.gameId)
            it.setCategory(LogCategory.AUTO_ML)
        }

        logWithIdentifier(originalGame.gameId) {
            it.setMessage("Vision call failed")
            it.addContext("game", originalGame)
            it.addContext("game_id", originalGame.gameId)
            it.setOcrInfo(
                OcrInfoMessage(
                    vision = OcrInfoMessage.Vision(
                        success = false,
                        failureReason = GameRepository.GameFailureReason.AUTO_ML_CALL_FAILED.getMessage()
                    )
                )
            )
            it.addContext("reason", GameRepository.GameFailureReason.AUTO_ML_CALL_FAILED)
            it.setCategory(LogCategory.CME)
        }
        logHelper.completeLogging()
        try {
            bitmapList.forEach { it.recycle() }
            newBitmap.recycle()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance()
                .log("Auto ML: ${e.stackTraceToString()} Query for Ocr failed with : ${throwable}")
        }
        callback?.invoke(null)
        MachineMessageBroadcaster.invoke()?.showLoader(false, "")
    }

    private fun retry(
        exception: Exception,
        imagePathList: ArrayList<String>,
        originalGame: Game,
        tfResultsList: List<List<TFResult>>,
        originalBGMICharacterID: String,
        originalBGMIId: String,
        retry: Int,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            logException(exception)

            logWithIdentifier(originalGame.gameId) {
                it.setMessage("Re-trying AutoMl")
                it.setOcrInfo(
                    OcrInfoMessage(
                        vision = OcrInfoMessage.Vision(
                            success = false,
                            failureReason = exception.message
                        )
                    )
                )
                it.addContext("error", exception.stackTraceToString())
                it.addContext("game_id", originalGame.gameId)
                it.setCategory(LogCategory.AUTO_ML)
            }
            AutoMLQueryHelper().runQuery(
                ctx = (GamerboardApp.instance as Context),
                imagePaths = imagePathList,
                game = originalGame,
                labels = tfResultsList,

                // these are required to pass to the job, it can be used for squad games.
                originalBGBICharacterID = originalBGMICharacterID,
                originalBGMIId = originalBGMIId,
                retry = retry + 1
            )
        }
    }

    private fun processAnnotationResult(
        annotation: JsonObject,
        tfResultsList: List<List<TFResult>>,
        tfCoordinatesList: ArrayList<HashMap<TFResult, Pair<Int, Int>>>,
        imagePathList: ArrayList<String>,
        originalGame: Game,
        originalBGMIId: String,
        originalBGBICharacterID: String,
    ): Game? {
        try {
            pageText = ""
            val correctedGame = updateResultWithResponse(
                annotation,
                tfResultsList,
                tfCoordinatesList,
                imagePathList,
                originalGame,
                originalBGMIId,
                originalBGBICharacterID,
                bitmapHeight
            )

            onOcrFinished(correctedGame, originalGame)
            return correctedGame
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().log("Auto ML ${e.stackTraceToString()}")
            logWithIdentifier(originalGame.gameId) { builder ->
                builder.setMessage("Annotation Failed : processAnnotationResult")
                builder.setOcrInfo(
                    OcrInfoMessage(
                        vision = OcrInfoMessage.Vision(
                            success = false,
                            failureReason = e.message
                        )
                    )
                )
                builder.setCategory(LogCategory.AUTO_ML)
            }
            return null
        }

    }

    private fun onOcrFinished(
        correctedGame: Game,
        originalGame: Game,
    ) {

        logWithIdentifier(originalGame.gameId) {
            it.setMessage("Query successful!")
            it.setOcrInfo(
                OcrInfoMessage(
                    vision = OcrInfoMessage.Vision(
                        success = true
                    )
                )
            )
            it.addContext("corrected_game", correctedGame)
            it.addContext("game_id", originalGame.gameId)
            it.setCategory(LogCategory.AUTO_ML)
        }
        Log.d("MlKit", immutableListOf(originalGame.copy(), correctedGame.copy()).toString())

        // GamerboardApp.updateGame(correctedGame)


        CoroutineScope(Dispatchers.IO).launch {

            db.getGamesDao().updateGame(
                gameId = correctedGame.gameId,
                correctedGame.kills,
                correctedGame.gameInfo,
                correctedGame.teamRank,
                correctedGame.rank,
                correctedGame.initialTier,
                correctedGame.finalTier,
                correctedGame.metaInfoJson,
                correctedGame.squadScoring
            )
            if (correctedGame.gameId != null) MachineMessageBroadcaster.invoke()
                ?.gameEndedBroadcast(correctedGame.gameId!!)
        }
    }

    private fun saveStitchedBitmap(
        newBitmap: Bitmap, bitmapList: ArrayList<Bitmap>, imagePathList: ArrayList<String>,
    ) {
        newBitmap.recycle()

        for (i in 0 until bitmapList.size) {
            bitmapList[i].recycle()
            if (i < imagePathList.size) {
                File(
                    "${GamerboardApp.instance.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/ratings",
                    imagePathList[i]
                ).apply { if (exists()) delete() }
            }
        }
    }

    companion object {

        private val characterConfidence: Double =
            if (debugMachine == DEBUGGER.DIRECT_HANDLE) 0.4 else FirebaseRemoteConfig.getInstance()
                .getDouble(com.gamerboard.live.common.RemoteConfigConstants.VISION_CHARACTER_CONFIDENCE)

        fun updateResultWithResponse(
            annotation: JsonObject, tfResultsList: List<List<TFResult>>,
            tfCoordinatesList: ArrayList<HashMap<TFResult, Pair<Int, Int>>>,
            imagePaths: ArrayList<String>, originalGame: Game,
            originalGameId: String, originalGameCharacterID: String,
            bitmapHeight: Int = 720,
        ): Game {
            val preResult = formatResponse(annotation)
            /*val coordinateMap = listOf<HashMap<String, Any>> ()
            tfCoordinatesList.forEach { it.entries.forEach {  }coordinateMap.put("tfResult":) }*/

            logWithIdentifier(originalGame.gameId) {
                it.setMessage("1:updateResultWithResponse()")
                it.addContext("annotation", annotation)
                it.addContext("tf_result_list", tfResultsList)
                it.addContext("image_paths", imagePaths)
                it.addContext("original_game", originalGame)
                it.addContext("original_game_id", originalGameId)
                it.addContext("original_game_character_id", originalGameCharacterID)
                it.addContext("game_id", originalGame.gameId)
                it.setCategory(LogCategory.ENGINE)
            }
            idx = 0


            if (tfCoordinatesList.size == 0) {
                logWithIdentifier(originalGame.gameId) {
                    it.setMessage("Auto ML tfCoordinatesList was empty")
                    it.addContext("image_paths", imagePaths)
                    it.addContext("game_id", originalGame.gameId)
                    it.setCategory(LogCategory.AUTO_ML)
                }
            }

            val gameClone = originalGame.copy()

            for (i in 0 until tfCoordinatesList.size) {
                val tfResults = tfResultsList[i]
                val tfCoordinates = tfCoordinatesList[i]
                reMap(
                    originalGame,
                    tfResults,
                    preResult,
                    tfCoordinates,
                    originalImageHeight = bitmapHeight
                )
                val resultJsonFlat = arrayListOf(
                    ImageResultJsonFlat(
                        0, if (i < imagePaths.size) imagePaths[i] else "", tfResults
                    )
                )
                /*resultJsonFlatList.add(resultJsonFlat)*/
                correctGameData(
                    gameClone,
                    labelsArray = tfResults,
                    resultJsonFlat = resultJsonFlat,
                    originalGameId = originalGameId,
                    originalGameCharacterID = originalGameCharacterID,
                )

            }

            transferFreeFixSquadScoring(gameClone)

            logWithIdentifier(originalGame.gameId) {
                it.setMessage("Query Completed!")
                it.setOcrInfo(
                    OcrInfoMessage(
                        vision = OcrInfoMessage.Vision(
                            success = true
                        )
                    )
                )
                it.setCategory(LogCategory.AUTO_ML)
            }
            val correctedGame = buildGameDataFromOriginalGame(gameClone)

            updateRankFromCorrectedGame(correctedGame)

            logWithIdentifier(originalGame.gameId) {
                it.setMessage("OCR_GAME")
                it.setOcrInfo(
                    OcrInfoMessage(
                        vision = OcrInfoMessage.Vision(
                            success = true
                        )
                    )
                )
                it.addContext("corrected_game", correctedGame)
                it.setCategory(LogCategory.AUTO_ML)
            }
            return correctedGame
        }

        private fun buildGameDataFromOriginalGame(originalGame: Game) = Game(
            userId = originalGame.userId,
            valid = true,
            rank = originalGame.rank,
            gameInfo = originalGame.gameInfo,
            kills = originalGame.kills,
            teamRank = originalGame.teamRank,
            startTimeStamp = originalGame.startTimeStamp,
            endTimestamp = originalGame.endTimestamp,
            gameId = originalGame.gameId,
            initialTier = originalGame.initialTier,
            finalTier = originalGame.finalTier,
            metaInfoJson = originalGame.metaInfoJson,
            squadScoring = originalGame.squadScoring,
            synced = 0
        )

        private fun transferFreeFixSquadScoring(
            originalGame: Game,
        ) {
            if (MachineConstants.currentGame != SupportedGames.FREEFIRE) {
                return
            }
            val originalgameinfo =
                gson.fromJson(originalGame.gameInfo, JsonObject::class.java)
            val gameInfoObj =
                MachineConstants.machineLabelUtils.getGameInfo(originalGame.gameInfo!!)
            val isSolo = immutableListOf("solo", UNKNOWN)
            if (isSolo.contains(
                    originalgameinfo?.get("group")?.toString()?.lowercase() ?: ""
                ) || isSolo.contains(gameInfoObj.group.lowercase())
            ) {
                gameInfoObj.group = "solo"
                originalGame.gameInfo = Json.encodeToString(gameInfoObj)
                originalGame.squadScoring?.let {
                    originalGame.kills =
                        (MachineConstants.machineInputValidator as FreeFireInputValidator).updateKillResultFromSquadScoringForSolo(
                            it
                        ) ?: "-1"
                    originalGame.squadScoring = null
                }
            }
        }

        private fun correctGameData(
            originalGame: Game,
            labelsArray: List<TFResult>,
            resultJsonFlat: ArrayList<ImageResultJsonFlat>,
            originalGameId: String,
            originalGameCharacterID: String,
        ) {
            when (MachineConstants.machineLabelProcessor.getBucket(getListOfLabels(labelsArray))) {
                MachineConstants.gameConstants.resultRankKills() -> {
                    validateRankScreen(
                        originalGame = originalGame,
                        resultJsonFlat = resultJsonFlat,
                        originalGameId = originalGameId,
                        originalGameCharacterID = originalGameCharacterID,
                    )
                }

                MachineConstants.gameConstants.resultRankRating() -> {

                    logWithIdentifier(originalGame.gameId) {
                        it.setMessage("Correcting data for rating screen")
                        it.setCategory(LogCategory.AUTO_ML)
                    }
                    val resultRankRating =
                        MachineConstants.machineInputValidator.validateRankRatingGameInfo(
                            resultJsonFlat,
                            originalGameId,
                            originalGameCharacterID,
                            isFromAutoMl = true
                        )
                    LabelUtils.testLogGreen("Initial Tier: ${resultRankRating.initialTier} ${resultRankRating.initialTier}")
                    LabelUtils.testLogGreen("Final Tier: ${resultRankRating.finalTier} ${resultRankRating.finalTier}")
                    LabelUtils.testLogGreen("Rank: ${resultRankRating.rank} ${resultRankRating.rank}")

                    processRatingDetails(
                        "correctRankRatings",
                        resultRankRating = resultRankRating,
                        originalGame = originalGame,
                        resultJsonFlat = resultJsonFlat,
                    )
                }
            }
        }

        private fun validateRankScreen(
            originalGame: Game,
            resultJsonFlat: ArrayList<ImageResultJsonFlat>,
            originalGameId: String,
            originalGameCharacterID: String,
        ) {
            logWithIdentifier(originalGame.gameId) {
                it.setMessage("Correcting data for kill screen")
                it.setCategory(LogCategory.AUTO_ML)
            }
            val resultRankKill =
                MachineConstants.machineInputValidator.validateRankKillGameInfo(
                    resultJsonFlat,
                    originalGameId,
                    originalGameCharacterID,
                    isFromAutoMl = true
                )

            LabelUtils.testLogGreen("Rank: ${resultRankKill.rank} ${resultRankKill.rank}")
            LabelUtils.testLogGreen("Kills: ${resultRankKill.kill} ${resultRankKill.kill}")

            processGameDetails(
                tag = "correctRankRatings",
                resultRankKill = resultRankKill,
                originalGame = originalGame,
                resultJsonFlat = resultJsonFlat,
            )

        }
//        private fun validatePerformanceScreen(
//            originalGame: Game,
//            resultJsonFlat: ArrayList<ImageResultJsonFlat>,
//            originalGameId: String,
//            originalGameCharacterID: String,
//        ) {
//            MachineMessageBroadcaster.invoke()
//                ?.logToFile(
//                    "Correcting data for performance screen",
//                    LogCategory.AUTO_ML
//                )
//            val resultRankKill =
//                MachineConstants.machineInputValidator.validatePerformanceScreen(
//                    resultJsonFlat,
//                    originalGameId,
//                    originalGameCharacterID,
//                    isFromAutoMl = true
//                )
//            LabelUtils.testLogGreen("Rank: ${resultRankKill.rank} ${resultRankKill.rank}")
//            //                    LabelUtils.testLogGreen("Kills: ${resultRankKill.kill} ${resultRankKill.kill}")
//
//            processGameDetails(
//                tag = "correctRankGameinfo",
//                resultRankKill = resultRankKill,
//                originalGame = originalGame,
//                resultJsonFlat = resultJsonFlat,
//            )
//
//        }

        private fun processRatingDetails(
            tag: String,
            resultRankRating: MachineResult,
            originalGame: Game,
            resultJsonFlat: ArrayList<ImageResultJsonFlat>,
        ) {

            if (MachineConstants.currentGame == SupportedGames.BGMI) {
                originalGame.gameInfo =
                    LabelUtils.updateGameInfo(originalGame.gameInfo, resultRankRating.gameInfo)
                resultRankRating.rank?.let {
                    originalGame.rank = it
                }
            }
            originalGame.initialTier =
                if (originalGame.initialTier == null || originalGame.initialTier == UNKNOWN) (resultRankRating.initialTier
                    ?: UNKNOWN) else originalGame.initialTier
            originalGame.finalTier =
                if (originalGame.finalTier == null || originalGame.finalTier == UNKNOWN) (resultRankRating.finalTier
                    ?: UNKNOWN) else originalGame.finalTier
            originalGame.metaInfoJson =
                if (originalGame.metaInfoJson == null || originalGame.metaInfoJson == UNKNOWN) UNKNOWN.putToMetaInfoJson(
                    key = "Auto ML $tag",
                    value = " initial-tier: ${originalGame.initialTier}->${resultRankRating.initialTier},  final-tier: ${originalGame.finalTier}->${resultRankRating.finalTier} from from ${resultJsonFlat[0].labels.joinToString { it.ocr + " \n " }} of : ${pageText}"
                ) else originalGame.metaInfoJson?.putToMetaInfoJson(
                    key = "Auto ML $tag",
                    value = " initial-tier: ${originalGame.initialTier}->${resultRankRating.initialTier},  final-tier: ${originalGame.finalTier}->${resultRankRating.finalTier},  ${resultJsonFlat[0].labels.joinToString { it.ocr + " \n " }} of : ${pageText}"
                )

        }

        private fun processGameDetails(
            tag: String,
            resultRankKill: MachineResult,
            originalGame: Game,
            resultJsonFlat: ArrayList<ImageResultJsonFlat>,
        ) {
            resultRankKill.rank?.let {
                originalGame.rank = it
            }
            resultRankKill.teamRank?.let {
                originalGame.teamRank = it
            }
            resultRankKill.kill?.let {
                originalGame.kills = it
            }

            originalGame.gameInfo =
                LabelUtils.updateGameInfo(originalGame.gameInfo, resultRankKill.gameInfo)

            originalGame.squadScoring =
                mapKillsBetweenLocalAndVisionApi(
                    resultRankKill.squadScoring,
                    originalGame.squadScoring
                )

            originalGame.metaInfoJson =
                if (originalGame.metaInfoJson == null || originalGame.metaInfoJson == UNKNOWN) UNKNOWN.putToMetaInfoJson(
                    key = "Auto ML $tag",
                    value = " Rank: ${originalGame.rank}->${resultRankKill.rank} , Kills: ${originalGame.kills}->${resultRankKill.kill} from from ${resultJsonFlat[0].labels.joinToString { it.ocr + " \n " }} of : ${pageText}"
                ) else originalGame.metaInfoJson?.putToMetaInfoJson(
                    key = "Auto ML $tag",
                    value = " Rank: ${originalGame.rank}->${resultRankKill.rank}, Kills: ${originalGame.kills}->${resultRankKill.kill},  ${resultJsonFlat[0].labels.joinToString { it.ocr + " \n " }} of : ${pageText}"
                )

        }

        private fun mapKillsBetweenLocalAndVisionApi(
            visionScoring: String?, localScoring: String?,
        ): String? {
            return if (visionScoring != null) {
                try {
                    if ((MachineConstants.machineInputValidator.getSquadScoringArray(visionScoring).size) >= ((localScoring?.let {
                            MachineConstants.machineInputValidator.getSquadScoringArray(
                                it
                            ).size
                        }) ?: 0)) {
                        visionScoring
                    } else {
                        localScoring
                    }
                } catch (ex: Exception) {
                    visionScoring
                }
            } else {
                localScoring
            }
        }

        var pageText = ""
        var maxW = 0
        var maxH = 0


        // bitmap list of bitmaps.
        // tfResults is a list of pairs, key=tfResult, value=the index of the Image in the list of bitmaps
        suspend fun stitchBitmap(
            mlKitOCR: MLKitOCR,
            originalList: ArrayList<Bitmap>,
            tfResultsList: List<List<TFResult>>,
            len: Int,
        ): Bitmap {
            val bitmap = Bitmap.createBitmap(maxW, maxH, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.BLACK)
            var height = 0f

            for (i in 0 until len) {
                val tfResults = tfResultsList[i]

                val findGameInfo =
                    tfResults.firstOrNull { MachineConstants.gameConstants.getGameInfoLabelFilter(it.label.toFloat()) }

                val gameInfoBoundingBox = findGameInfo?.getBoundingBox()

                for (tfResult in tfResults) {

                    val original = originalList[i]

                    if (tfResult.shouldSkip()) continue


                    var croppedBitmap = LabelHelper.getCroppedImage(
                        tfResult,
                        original,
                        gameInfoBoundingBox
                    )

                    if (MachineConstants.currentGame == SupportedGames.FREEFIRE
                        && MachineConstants.gameConstants.killLabel() == tfResult.label.toInt()
                    ) {
                        val newBitmap = croppedBitmap.scale(
                            maxW,
                            (croppedBitmap.height * maxW.toFloat() / croppedBitmap.width.toFloat()).toInt()
                        )
                        // croppedBitmap.recycle()
                        croppedBitmap = newBitmap
                    }
                    canvas.drawBitmap(croppedBitmap, 0f, height, null)
                    height += croppedBitmap.height + 80
                    //croppedBitmap.recycle()
                    // croppedBitmap = null
                }
            }
            return bitmap
        }


        fun mapTfResult(
            tfResultsList: List<List<TFResult>>,
            originalList: ArrayList<Pair<Int, Int>>,
            len: Int,
        ): ArrayList<HashMap<TFResult, Pair<Int, Int>>> {
            maxH = 0
            maxW = 0
            calculateMaxWidthFromTFResultList(tfResultsList, originalList)
            val tfCoordinatesList = arrayListOf<HashMap<TFResult, Pair<Int, Int>>>()
            for (i in 0 until len) {
                val tfCoordinates = HashMap<TFResult, Pair<Int, Int>>()
                for (tfResult in tfResultsList[i]) {
                    if ((MachineConstants.gameConstants.getSortOrderForLabel(tfResult) == SortOrder.SKIP)) continue
                    val bitmapHeight = originalList[i].second

                    val y1 = (tfResult.box[0] * bitmapHeight).toInt()
                    val y2 = (tfResult.box[2] * bitmapHeight).toInt()
                    var h = min(y2 - y1, bitmapHeight - y1)
                    if (MachineConstants.currentGame == SupportedGames.FREEFIRE
                        && MachineConstants.gameConstants.killLabel() == tfResult.label.toInt()
                    ) {
                        val bitmapWidth = originalList[i].first
                        val x1 = (tfResult.box[1] * bitmapWidth).toInt()
                        val x2 = (tfResult.box[3] * bitmapWidth).toInt()
                        val w = min(x2 - x1, bitmapWidth - x1)
                        h = (h * (maxW.toFloat() / w.toFloat())).toInt()
                    }
                    val pair = Pair(maxH, maxH + h)
                    maxH += max(32, h)
                    tfCoordinates[tfResult] = pair
                    maxH += 80
                }
                tfCoordinatesList.add(tfCoordinates)
            }
            return tfCoordinatesList
        }

        private fun updateRankFromCorrectedGame(correctedGame: Game) {
            MachineConstants.machineInputValidator.getProcessedRankData(isFromAutoMl = true)
                ?.let { processedRank ->
                    MachineConstants.machineInputValidator.updateGameWithRank(
                        processedRank, correctedGame
                    )
                }
        }

        private fun calculateMaxWidthFromTFResultList(
            tfResultsList: List<List<TFResult>>,
            originalList: ArrayList<Pair<Int, Int>>,
        ) {
            for (i in 0 until tfResultsList.size) {
                for (tfResult in tfResultsList[i]) {
                    if (tfResult.shouldSkip()) continue
                    val bitmapWidth = originalList[i].first
                    val x1 = (tfResult.box[1] * bitmapWidth).toInt()
                    val x2 = (tfResult.box[3] * bitmapWidth).toInt()
                    maxW = max(maxW, max(32, min(x2 - x1, bitmapWidth - x1)) + 10)
                }
            }

        }

        private var idx = 0
        fun reMap(
            originalGame: Game,
            tfResults: List<TFResult>,
            jsonArray: ArrayList<JsonObject>,
            tfCoordinates: HashMap<TFResult, Pair<Int, Int>>,
            originalImageHeight: Int = 720,
        ) {

            tfResults.forEach { tfResult ->
                processVisionAnnotation(
                    tfResult,
                    originalImageHeight,
                    jsonArray,
                    tfCoordinates
                )
            }
            logWithIdentifier(originalGame.gameId) { builder ->
                builder.setMessage("Vision OCR Labels")
                builder.setOcrInfo(OcrInfoMessage(
                    vision = OcrInfoMessage.Vision(
                        success = true,
                        ocrTextList = tfResults.map {
                            OcrInfoMessage.Ocr(
                                ocrText = it.ocr,
                                label = it.label.toInt()
                            )
                        }
                    )
                ))
                builder.setCategory(LogCategory.AUTO_ML)
            }
        }

        private fun processVisionAnnotation(
            tfResult: TFResult,
            originalImageHeight: Int,
            jsonArray: ArrayList<JsonObject>,
            tfCoordinates: HashMap<TFResult, Pair<Int, Int>>,
        ) {
            if (tfResult.shouldSkip()) return

            val horizontalList = mutableListOf<Pair<Rect, String>>()
            val smallestPixelCharHeight =
                (MachineConstants.currentGame.smallestCharPixelPercentage * originalImageHeight)
            val largestPixelCharHeight =
                (MachineConstants.currentGame.largestCharPixelPercentage * originalImageHeight)


            jsonArray.forEach { jsonObject ->
                val item: Rect = rectFromCoordinates(jsonObject["boundingBox"] as JsonObject)
                val pair = tfCoordinates[tfResult]!!
                Log.d(
                    "boxFrom: ",
                    "item: $item  \n pair:$pair  \n label:${tfResult.label}  \n tfResult:$tfResult"
                )

                // Save ocr boxes
                /* saveOcr("item: $item \n pair:$pair \n label:${tfResult.label}  \n tfResult:$tfResult", "${System.currentTimeMillis()}_ocr_boxes")*/

                logWithIdentifier(GameHelper.getGameId(stackTrace())) {
                    it.setMessage("processVisionAnnotation para")
                    it.addContext("para", jsonObject["para"])
                    it.addContext("item", item)
                    it.addContext("pair", pair)
                    it.addContext("label", tfResult.label)
                    it.addContext("tf_result", tfResult)
                    it.setCategory(LogCategory.ENGINE)
                }

                val matchTop = item.top > (pair.first - 20)
                val matchBottom = item.bottom < (pair.second + 20)
                val height = item.bottom - item.top
                val matchSmallestHeight = height > smallestPixelCharHeight
                val matchLargestHeight = height < largestPixelCharHeight
                if (matchTop
                    && matchBottom
                    && matchSmallestHeight
                    && matchLargestHeight
                ) {
                    horizontalList.add(Pair(item, jsonObject["para"].asString))
                }
            }


            logWithIdentifier(GameHelper.getOriginalGameId()) {
                it.setMessage("processVisionAnnotation")
                it.addContext("horizontal_list", horizontalList)
                it.setCategory(LogCategory.ENGINE)
            }
            val text = readTextFromVisionResponse(tfResult, horizontalList)
            tfResult.ocr = text
        }

        private fun readTextFromVisionResponse(
            tfResult: TFResult,
            horizontalList: MutableList<Pair<Rect, String>>,
        ): String {
            var text = ""
            val sortOrder = MachineConstants.gameConstants.getSortOrderForLabel(tfResult)

            if (sortOrder == SortOrder.HORIZONTAL) {
                horizontalList.sortWith(compareBy { it.first.left })
                horizontalList.forEach { text += "[.]${it.second}" }
                text = text.trim()
            } else if ((sortOrder == SortOrder.VERTICAL)) {
                horizontalList.sortWith(compareBy({ it.first.top }, { it.first.left }))
                horizontalList.forEach {
                    it.second.split(System.lineSeparator()).forEach { t ->
                        if (t.trim()
                                .isNotEmpty() && t.trimSpace().length <= 60  // to avoid un wanted line 60 is the line length,
                        ) text += "[.]${t.trim()}"          // the largest length we have is "ranked Classic mode: (TPP) squad random map name"
                        else {
                            logWithIdentifier(GameHelper.getOriginalGameId()) { builder ->
                                builder.setMessage("Text ignored for processing from Auto ML, length limit exceeded")
                                builder.addContext("text", t.trimSpace())
                                builder.addContext("text_length", t.trimSpace().length)
                                builder.addContext("tf_result", tfResult)
                                builder.setCategory(LogCategory.ENGINE)
                            }

                        }
                    }
                }

                text = text.trim()
            } else if (sortOrder == SortOrder.PERFORMANCE) {
                text = sortOCRValues(horizontalList)
            }
            return text
        }

        private fun sortOCRValues(horizontalList: MutableList<Pair<Rect, String>>): String {
            horizontalList.sortWith(
                compareBy({ it.first.left })
            )
            var nameDetails: Rect? = null
            horizontalList.forEach {
                if (it.second == "NAME ") {
                    nameDetails = it.first
                    return@forEach
                }
            }
            if (nameDetails == null) {
                return "[..]"
            }

            var leftValue = horizontalList[0].first.left
            var minLeft = horizontalList[0].first.left
            var usernames = mutableListOf<Pair<Rect, String>>()
            var left = mutableListOf<Pair<Rect, String>>()

            horizontalList.forEach {
                if (nameDetails!!.left - 100 < it.first.left && it.first.left < nameDetails!!.left + 100) {
                    usernames.add(it)
                } else if (!(nameDetails!!.bottom - 20 < it.first.bottom && it.first.bottom < nameDetails!!.bottom + 20)) {
                    if (it.first.left - leftValue < 20) {
                        if (minLeft > it.first.left) minLeft = it.first.left
                        it.first.left = leftValue
                    } else {
                        leftValue = it.first.left
                        left.add(it)
                    }
                }
            }

            horizontalList.removeAll(usernames)
            horizontalList.sortWith(
                compareBy({ it.first.bottom })
            )
            var kills = mutableListOf<Pair<Rect, String>>()
            var count = 1
            var bottom = mutableListOf<Pair<Rect, String>>()
            var mintop = horizontalList[0].first.bottom
            var bottomValue = horizontalList[0].first.bottom
            horizontalList.forEach {
                if (nameDetails!!.left + 400 - 50 < it.first.left && it.first.left < nameDetails!!.left + 400 + 50) {
                    kills.add(it)
                }
                if (it.first.bottom - bottomValue < 20) {
                    if (mintop > it.first.bottom) mintop = it.first.bottom
                    it.first.bottom = bottomValue
                } else {
                    bottomValue = it.first.bottom
                    bottom.add(it)
                    count++
                }
            }

            usernames.sortWith(
                compareBy {
                    it.first.top
                }
            )
            kills.sortWith(
                compareBy { it.first.top }
            )
            bottom.sortWith(
                compareBy { it.first.top }
            )

            var usernameText = MutableList(count) { "None" }
            var killText = MutableList(count) { "None" }

            var maxCount = count.absoluteValue
            bottom.forEach {
                count--
                var mean = (abs(it.first.bottom) + abs(it.first.top)).toDouble() / 2
                usernames.forEach { username ->
                    var text = ""
                    username.second.split(System.lineSeparator()).forEach { t ->
                        text += "[.]${t.trim()}"
                    }

                    var usernameMean =
                        (abs(username.first.bottom) + abs(username.first.top)).toDouble() / 2

                    if (abs(mean - username.first.bottom) < 20 || abs(mean - username.first.top) < 20 || abs(
                            usernameMean - mean
                        ) < 20
                    ) {
                        usernameText[maxCount - count] = text

                    }
                }

                kills.forEach { kill ->
                    var killMean = (abs(kill.first.bottom) + abs(kill.first.top)).toDouble() / 2
                    if (abs(mean - kill.first.bottom) < 20 || abs(mean - kill.first.top) < 20 || abs(
                            mean - killMean
                        ) < 20
                    ) {
                        killText[maxCount - count] = kill.second
                    }
                }
            }


            var isSquad: Boolean = false
            if (count > 2 || left.size > 4) {
                isSquad = true
            }
            var text = "$usernameText[..]$killText[..]$isSquad"
            return text
        }

        // Function to check
        private fun calc(a: String, b: String): Int {
            for (i in 1 until a.length) {
                if (b.startsWith(a.substring(i))) {
                    return b.length - a.length + i
                }
            }

            // Return size of b
            return b.length
        }


        private fun rectFromCoordinates(coordinates: JsonObject): Rect {
            val squad: MutableList<MutableList<Int>> = mutableListOf(
                mutableListOf(-1, -1),
                mutableListOf(-1, -1),
                mutableListOf(-1, -1),
                mutableListOf(-1, -1)
            )
            for ((i, obj) in coordinates["vertices"].asJsonArray.withIndex()) {
                squad[i][0] = obj.asJsonObject["x"].asInt
                squad[i][1] = obj.asJsonObject["y"].asInt
            }

            val top = minOf(squad[0][1], squad[1][1])
            val left = minOf(squad[0][0], squad[3][0])
            val bottom = maxOf(squad[2][1], squad[3][1])
            val right = maxOf(squad[1][0], squad[2][0])

            val rect = Rect()
            rect.top = top
            rect.bottom = bottom
            rect.right = right
            rect.left = left
            LabelUtils.testLogRed("$left $top $right $bottom $rect")
            return rect
        }

        fun formatResponse(annotation: JsonObject): ArrayList<JsonObject> {
            val response: ArrayList<JsonObject> = arrayListOf()

            for (page in annotation["pages"].asJsonArray) {
                for (block in page.asJsonObject["blocks"].asJsonArray) {
                    var blockText = ""
                    var addDataToArray = true
                    for (para in block.asJsonObject["paragraphs"].asJsonArray) {
                        var paraText = ""
                        var wordBoundingBoxes = JsonArray()
                        for (word in para.asJsonObject["words"].asJsonArray) {
                            var wordText = ""
                            var boundingBoxes = JsonArray()
                            for (symbol in word.asJsonObject["symbols"].asJsonArray) {
                                if (symbol.asJsonObject["confidence"].asFloat > characterConfidence) {
                                    wordText += symbol.asJsonObject["text"].asString
                                    boundingBoxes.add(symbol.asJsonObject["boundingBox"].asJsonObject["vertices"].asJsonArray)
                                } else {
                                    ocrCount++
                                    totalConfidence =
                                        (symbol.asJsonObject["confidence"].asFloat + totalConfidence)
                                    Log.d(
                                        "TestVisionCallOnImages",
                                        "confidence: ${symbol.asJsonObject["confidence"].asFloat} symbol:${symbol.asJsonObject["text"].asString}"
                                    )
                                    Log.d(
                                        "TestVisionCallOnImages",
                                        "avgConfidence: ${totalConfidence / ocrCount}"
                                    )
                                }
                            }
                            paraText = String.format("%s%s ", paraText, wordText)
                            wordBoundingBoxes.add(boundingBoxes)
                        }

                        val tempObj = JsonObject()
                        tempObj.addProperty("para", paraText)
                        tempObj.add("wordBoundingBoxes", wordBoundingBoxes)
                        //filtering noise in ocr which pollutes the username
                        try {
                            para.asJsonObject["boundingBox"].asJsonObject["vertices"].asJsonArray.forEach {
                                if (it.asJsonObject["y"].asInt < 0 || it.asJsonObject["x"].asInt < 0) throw Exception(
                                    "Coordinate observed less than 0"
                                )
                            }
                        } catch (ex: Exception) {
                            addDataToArray = false
                            ex.printStackTrace()
                            logWithIdentifier(GameHelper.getOriginalGameId()) {
                                it.setMessage("Format response exception")
                                it.setOcrInfo(
                                    OcrInfoMessage(
                                        vision = OcrInfoMessage.Vision(
                                            success = false,
                                            failureReason = ex.message
                                        )
                                    )
                                )
                                it.addContext("error", ex.stackTraceToString())
                                it.addContext("error_message", ex.message)
                                it.setCategory(LogCategory.AUTO_ML)
                            }
                        }
                        if (addDataToArray) {
                            tempObj.add(
                                "boundingBox", para.asJsonObject["boundingBox"].asJsonObject
                            )
                            response.add(tempObj)
                            blockText += paraText
                        }

                        /*val tempObj = JsonObject()
                        tempObj.addProperty("para", paraText)
                        tempObj.add("boundingBox", para.asJsonObject["boundingBox"].asJsonObject)
                        tempObj.addProperty("confidence", para.asJsonObject["confidence"].asFloat)
                        response.add(tempObj)*/


                    }
                    if (addDataToArray) pageText += blockText
                }
            }
            return response
        }
    }


    private fun annotateImage(requestJson: String): Task<JsonObject?> {
        return functions.getHttpsCallable("annotateImage").call(requestJson).continueWith { task ->
            val result = task.result!!.data
            Log.e("annotateImage", task.result.toString())
            val element = JsonParser.parseString(Gson().toJson(result))
            element.asJsonArray?.firstOrNull()?.asJsonObject?.get("fullTextAnnotation")?.asJsonObject
        }
    }

    private fun buildRequest(base64encoded: String): String {
        val request = JsonObject()
        val imagePath = JsonObject()
        imagePath.add("content", JsonPrimitive(base64encoded))
        request.add("image", imagePath)

        val feature = JsonObject()
        feature.add("type", JsonPrimitive("DOCUMENT_TEXT_DETECTION"))
        val features = JsonArray()
        features.add(feature)
        request.add("features", features)
        return request.toString()
    }

    private fun getBase64String(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream)
        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP)
    }
}

class AutoMLQueryHelper {

    fun runQuery(
        ctx: Context,
        imagePaths: ArrayList<String>,
        game: Game,
        labels: List<List<TFResult>>,
        originalBGMIId: String,
        originalBGBICharacterID: String,
        retry: Int = 0,
    ) {


        val data = Data.Builder()
        data.putString("imagePathList", Json.encodeToString(imagePaths))
        data.putString("game", Json.encodeToString(game))
        data.putString("labelsList", Json.encodeToString(labels))
        data.putString("originalBGMIId", originalBGMIId)
        data.putString("originalBGBICharacterID", originalBGBICharacterID)
        data.putInt("retry", retry)

        val internetConstants =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val mlQueryRequest =
            OneTimeWorkRequestBuilder<AutoMLQueryWorker>()
                .setInputData(data.build())
                .setConstraints(internetConstants)
                /*.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .also{
                       if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                        it.setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofSeconds(10) )
                }*/.build()
        WorkManager.getInstance(ctx).enqueueUniqueWork(
            "com.gamerboard.live-auto-ml-query", ExistingWorkPolicy.KEEP, mlQueryRequest
        )

        labels.flatten().forEach { it.clear() }
    }
}

class AutoMLQueryWorker(val ctx: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(ctx, workerParameters) {
    private val logHelper: LogHelper by inject<LogHelper>(LogHelper::class.java)

    override suspend fun doWork(): Result {
        //showToast("Executing Query!", force = true)

        val imagePathsJson = inputData.getString("imagePathList") ?: ""
        val gameJson = inputData.getString("game") ?: ""
        val labelsJson = inputData.getString("labelsList") ?: ""

        val originalBGMIId: String = inputData.getString("originalBGMIId") ?: ""
        val originalBGBICharacterID: String = inputData.getString("originalBGBICharacterID") ?: ""

        val executeInBackground: Boolean = inputData.getBoolean("executeInBackground", false)

        val retry = inputData.getInt("retry", 0)

        var originalGame: Game? = null
        var labelsList: ArrayList<List<TFResult>>? = null

        try {
            if (gameJson.isNotEmpty()) {
                originalGame = Json.decodeFromString(gameJson)
                if (labelsJson.isNotEmpty()) {
                    labelsList = Json.decodeFromString(labelsJson)
                }
            }
            logWithIdentifier(originalGame?.gameId) {
                it.setMessage("Executing query, doWork called game:")
                it.addContext("game", gameJson)
                it.setCategory(LogCategory.AUTO_ML)
            }
            if (imagePathsJson.isEmpty() || originalGame == null || labelsList == null) return Result.failure()

            val imagePathsList: ArrayList<String> = Json.decodeFromString(imagePathsJson)
            val bitmapsList = getBitmaps(originalGame = originalGame, imagePaths = imagePathsList)

            val tfCoordinates: ArrayList<HashMap<TFResult, Pair<Int, Int>>> = arrayListOf()
            //bitmapsList.forEach { _ -> tfCoordinates.add(hashMapOf()) }

            MLKitOCR().queryOcr(
                bitmapsList,
                labelsList,
                imagePathsList,
                originalGame,
                originalBGMIId,
                originalBGBICharacterID,
                tfCoordinatesList = tfCoordinates,
                null,
                retry
            )

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log(e.stackTraceToString())
            logWithIdentifier(originalGame?.gameId) {
                it.setMessage("Auto ML task failed")
                it.setOcrInfo(
                    OcrInfoMessage(
                        vision = OcrInfoMessage.Vision(
                            success = false,
                            failureReason = e.message
                        )
                    )
                )
                it.addContext("error", e.stackTraceToString())
                it.setCategory(LogCategory.AUTO_ML)
            }
            logWithIdentifier(originalGame?.gameId) {
                it.setMessage("Auto ML task failed")
                it.addContext("original_game", originalGame)
                it.setOcrInfo(
                    OcrInfoMessage(
                        vision = OcrInfoMessage.Vision(
                            success = false,
                            failureReason = GameRepository.GameFailureReason.AUTOML_TASK_WORKER_FAILED.getMessage()
                        )
                    )
                )
                it.addContext("reason", GameRepository.GameFailureReason.AUTOML_TASK_WORKER_FAILED)
                it.addContext("game_id", originalGame?.gameId)
                it.addContext("error_message", e.message)
                it.addContext("error", e.stackTraceToString())
                it.setCategory(LogCategory.CME)
            }
            logHelper.completeLogging()
            return Result.failure()
        }
        return Result.success()
    }


    private fun getBitmaps(originalGame: Game, imagePaths: ArrayList<String>): ArrayList<Bitmap> {
        val bitmaps: ArrayList<Bitmap> = arrayListOf()
        for (imagePath in imagePaths) {
            val file = File(
                "${ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/ratings", imagePath
            )
            if (!file.exists()) continue

            val bitmap: Bitmap = BitmapFactory.decodeFile(file.path)
            File(
                "${ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/ratings", file.path
            ).apply { if (exists()) delete() }
            bitmaps.add(bitmap)
        }

        if (bitmaps.size == 0) {
            logWithIdentifier(originalGame.gameId) {
                it.setMessage("No bitmaps were created for automl")
                it.setCategory(LogCategory.AUTO_ML)
            }
        }
        return bitmaps
    }

    /*private fun hasLowConfidence(originalGame: Game): Boolean {
        val query = originalGame.metaInfoJson?.getFromMetaInfoJson("query_auto_ml")
        if (query == "True")
            return true
        return false
    }*/
}

var ocrCount = 0
var totalConfidence = 0f