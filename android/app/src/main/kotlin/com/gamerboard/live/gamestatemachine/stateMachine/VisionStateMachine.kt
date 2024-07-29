package com.gamerboard.live.gamestatemachine.stateMachine

import android.content.Context
import android.os.Environment
import android.util.Log
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.gamestatemachine.games.GameHelper
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.showToast
import com.gamerboard.live.models.TFResult
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.logWithIdentifier
import com.tinder.StateMachine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import kotlin.streams.toList

// Auto ML

interface VisionImagesProvider {
    val ratingsImage: String?
    val ratingsLabels: ArrayList<TFResult>?
    val killsImage: String?
    val killsLabels: ArrayList<TFResult>?
    val performanceImage: String?
    val performanceLabels: ArrayList<TFResult>?
}


interface VisionParams : VisionImagesProvider {
    val recordKills: Boolean
    val recordRatings: Boolean
    val recordPerformance: Boolean
}

sealed class VisionState(
    var ready: Boolean,
    var readyWithRatingsScreen: Boolean,
    var readyWithKillsScreen: Boolean,
    var readyWithPerformanceScreen: Boolean
) {
    class NoVisionImages : VisionState(false, false, false, false), VisionParams {
        override val recordKills: Boolean
            get() = false
        override val recordRatings: Boolean
            get() = false
        override val recordPerformance: Boolean
            get() = false
        override val ratingsImage: String?
            get() = null
        override val ratingsLabels: ArrayList<TFResult>?
            get() = null
        override val killsImage: String?
            get() = null
        override val killsLabels: ArrayList<TFResult>?
            get() = null
        override val performanceImage: String?
            get() = null
        override val performanceLabels: ArrayList<TFResult>?
            get() = null

    }

    // We won't make a vision call if either of the images is not present as the game is anyways incomplete.
    class RecordImages(
        val gameId: String,
        var imageKills: String? = null,
        var countKills: Int,
        var labelsKills: ArrayList<TFResult>?,
        var imageRatings: String? = null,
        var countRatings: Int,
        var labelsRatings: ArrayList<TFResult>?,
        var imagePerformance: String? = null,
        var countPerformance: Int,
        var labelsPerformance: ArrayList<TFResult>?,
    ) : VisionState(
        imageKills != null && imageRatings != null,
        imageRatings != null,
        imageKills != null,
        imagePerformance != null
    ), VisionParams {
        override val recordKills: Boolean
            get() = countKills < captureAutoMlImages
        override val recordRatings: Boolean
            get() = countRatings < captureAutoMlImages
        override val ratingsImage: String?
            // returns from the constructor
            get() = imageRatings
        override val ratingsLabels: ArrayList<TFResult>?
            get() = labelsRatings
        override val killsImage: String?
            get() = imageKills
        override val killsLabels: ArrayList<TFResult>?
            get() = labelsKills
        override val performanceImage: String?
            get() = imagePerformance
        override val performanceLabels: ArrayList<TFResult>?
            get() = labelsPerformance
        override val recordPerformance: Boolean
            get() = countPerformance < captureAutoMlImages
    }
}

sealed class VisionEvent {
    class ReceivedKillsScreen(val imageKills: String, val labelsKills: List<TFResult>) :
        VisionEvent()

    class ReceivedRatingsScreen(val imageRatings: String, val labelsRatings: List<TFResult>) :
        VisionEvent()

    class ReceivedPerformanceScreen(
        val imagePerformance: String, val labelsPerformance: List<TFResult>
    ) : VisionEvent()

    class StartRecordingVisionImages(val gameId: String, val reason: String) : VisionEvent()
    class ResetVision(val reason: String) : VisionEvent()
    class SendForVisionCall(val reason: String) : VisionEvent()
}

sealed class VisionEffect {
    class OnReceivedKillsScreen(
        val imageKills: String,
        val labelsKills: List<TFResult>?,
        val previousToDelete: String?,
        var counter: Int
    ) : VisionEffect()

    class OnReceivedRatingsScreen(
        val imageRatings: String,
        val labelsRatings: List<TFResult>?,
        val previousToDelete: String?,
        var counter: Int
    ) : VisionEffect()

    class OnReceivedPerformanceScreen(
        val imagePerformance: String,
        val labelsPerformance: List<TFResult>?,
        val previousToDelete: String?,
        var counter: Int
    ) : VisionEffect()

    class OnStartRecordingVisionImages(val gameId: String, val reason: String) : VisionEffect()

    class OnResetVision(val reason: String, val recordedImages: VisionState.RecordImages) :
        VisionEffect()

    class OnSendForVisionCall(val reason: String, val recordedImages: VisionState.RecordImages) :
        VisionEffect()
}

fun deleteFileOnPath(filePath: String) {
    LabelUtils.testLogRed("Deleting image $filePath")
    if (test()) return
    File(
        "${(GamerboardApp.instance as Context).getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/ratings",
        filePath
    ).apply {

        if (exists()) {
            delete()
            showToast("Image file Deleted $filePath")
        } else showToast("Image file Does not exist! $filePath")
    }
}

const val captureAutoMlImages = 2

/**
 * With the state of the game this maintains the state of vision Images, this takes care of whether to save the images(if valid game) and when to delete them.
 * NOTE: This is separate from the game state machine because the game state machine will reset ar the game ends, but the Images should still exist
 * till we are finished with automl call. This also maintains separation of concerns.
 * */
object VisionStateMachine {
    var visionImageSaver = StateMachine.create<VisionState, VisionEvent, VisionEffect> {
        initialState(VisionState.NoVisionImages())

        state<VisionState.NoVisionImages> {
            on<VisionEvent.StartRecordingVisionImages> { event ->
                transitionTo(
                    VisionState.RecordImages(
                        event.gameId,
                        null,
                        0,
                        labelsKills = null,
                        null,
                        0,
                        labelsRatings = null,
                        imagePerformance = null,
                        countPerformance = 0,
                        labelsPerformance = null
                    ), VisionEffect.OnStartRecordingVisionImages(
                        gameId = event.gameId, reason = event.reason
                    )
                )
            }
        }
        state<VisionState.RecordImages> {
            on<VisionEvent.ReceivedKillsScreen> { event ->
                if (countKills > captureAutoMlImages) return@on transitionTo(this)
                val validLabelCount =
                    event.labelsKills.stream().filter { it.ocr.isNotEmpty() }.toList().size

                var minKillLabels = MachineConstants.currentGame.minKillScreenLabelCount
                Log.d(
                    "Vision-image",
                    "obtainedLabelCount: ${event.labelsKills.size}; Kill-validLabelCount: $validLabelCount; minKillScrLblCnt: $minKillLabels"
                )
                if (validLabelCount >= minKillLabels) {
                    val previousImage = imageKills
                    minKillLabels = validLabelCount
                    imageKills = event.imageKills
                    labelsKills = arrayListOf()
                    labelsKills!!.addAll(event.labelsKills)
                    countKills += 1
                    ready = countKills > 0 && countRatings > 0
                    readyWithKillsScreen = countKills > 0
                    readyWithRatingsScreen = countRatings > 0
                    Log.d(
                        "Vision-image", "readyRL: $ready"
                    )
                    return@on transitionTo(
                        this, VisionEffect.OnReceivedKillsScreen(
                            imageKills = event.imageKills,
                            labelsKills = event.labelsKills,
                            previousToDelete = previousImage,
                            countKills
                        )
                    )
                }
                return@on transitionTo(this)
            }
            on<VisionEvent.ReceivedRatingsScreen> { event ->
                if (countRatings > captureAutoMlImages) return@on transitionTo(this)
                val validLabelCount =
                    event.labelsRatings.stream().filter { it.ocr.isNotEmpty() }.toList().size

                var minRatingLabels = MachineConstants.currentGame.minRatingScreenLabelCount
                Log.d(
                    "Vision-image",
                    "obtainedLabelCount: ${event.labelsRatings.size}; Rat-validLabelCount: $validLabelCount; minRatingLabelCount: $minRatingLabels"
                )
                if (validLabelCount >= minRatingLabels) {
                    val previousImage = imageRatings
                    minRatingLabels = validLabelCount
                    imageRatings = event.imageRatings
                    labelsRatings = arrayListOf()
                    labelsRatings!!.addAll(event.labelsRatings)
                    countRatings += 1

                    ready = countKills > 0 && countRatings > 0
                    readyWithKillsScreen = countKills > 0
                    readyWithRatingsScreen = countRatings > 0
                    Log.d(
                        "Vision-image", "readyRR: $ready"
                    )
                    return@on transitionTo(
                        this, VisionEffect.OnReceivedRatingsScreen(
                            imageRatings = event.imageRatings,
                            labelsRatings = event.labelsRatings,
                            previousToDelete = previousImage,
                            countRatings
                        )
                    )
                }
                return@on transitionTo(this)
            }
            on<VisionEvent.ReceivedPerformanceScreen> { event ->
                if (countPerformance > captureAutoMlImages) return@on transitionTo(this)
                val validLabelCount =
                    event.labelsPerformance.stream().filter { it.ocr.isNotEmpty() }.toList().size

//                var minPerfImages = MachineConstants.currentGame.minPerformanceScreenLabelCount
//                Log.d(
//                    "Vision-image",
//                    "obtainedLabelCount: ${event.labelsPerformance.size}; Perf-validLabelCount: $validLabelCount; minPerfLabelCount: $minPerfImages"
//                )
//                if (validLabelCount >= minPerfImages) {
//                    val previousImage = imagePerformance
//                    minPerfImages = validLabelCount
//                    imagePerformance = event.imagePerformance
//                    labelsPerformance = arrayListOf()
//                    labelsPerformance!!.addAll(event.labelsPerformance)
//                    countPerformance += 1
//
//                    ready = countKills > 0 && countRatings > 0 && countPerformance > 0
//                    readyWithKillsScreen = countKills > 0
//                    readyWithRatingsScreen = countRatings > 0
//                    readyWithPerformanceScreen = countPerformance > 0
//                    Log.d(
//                        "Vision-image", "readyPerf: $ready")
//                    return@on transitionTo(
//                        this, VisionEffect.OnReceivedPerformanceScreen(
//                            imagePerformance = event.imagePerformance,
//                            labelsPerformance = event.labelsPerformance,
//                            previousToDelete = previousImage,
//                            countPerformance
//                        )
//                    )
//                }
                return@on transitionTo(this)
            }
            on<VisionEvent.ResetVision> { event ->
                transitionTo(
                    VisionState.NoVisionImages(),
                    VisionEffect.OnResetVision(reason = event.reason, this)
                )
            }
            on<VisionEvent.SendForVisionCall> { event ->
                transitionTo(
                    VisionState.NoVisionImages(),
                    VisionEffect.OnSendForVisionCall(reason = event.reason, this)
                )
            }
        }
        onTransition { nextStep ->
            val validStep = nextStep as? StateMachine.Transition.Valid ?: return@onTransition
            validStep.sideEffect?.let { VisionHandler.invoke()?.handle(it) }
        }
    }
}

class VisionHandler private constructor() : KoinComponent {
    private val context: Context by inject()
    fun handle(effect: VisionEffect) {
        var logMessage: String = ""
        var gameId : String? = GameHelper.getOriginalGameId()
        when (effect) {
            is VisionEffect.OnStartRecordingVisionImages -> {
                gameId = effect.gameId
                logMessage =
                    ("Started recording vision images for gameId:${effect.gameId}, reason:${effect.reason}\n")
            }

            is VisionEffect.OnSendForVisionCall -> {
                gameId = effect.recordedImages.gameId
                logMessage =
                    ("Vision query executed for :${effect.recordedImages}, reason: ${effect.reason}\n")
            }

            is VisionEffect.OnReceivedKillsScreen -> {
                logMessage =
                    ("Received kills screen to replace New:${effect.imageKills}, Old: ${effect.previousToDelete}, now has:${effect.counter}\n")
                effect.previousToDelete?.let { deleteFileOnPath(it) }
            }

            is VisionEffect.OnReceivedRatingsScreen -> {

                logMessage =
                    ("Received ratings screen to replace New:${effect.imageRatings}, Old: ${effect.previousToDelete}, now has:${effect.counter}\n")
                effect.previousToDelete?.let { deleteFileOnPath(it) }
            }

            is VisionEffect.OnReceivedPerformanceScreen -> {

                logMessage =
                    ("Received performance screen to replace New:${effect.imagePerformance}, Old: ${effect.previousToDelete}, now has:${effect.counter}\n")
                effect.previousToDelete?.let { deleteFileOnPath(it) }
            }

            is VisionEffect.OnResetVision -> {
                gameId = effect.recordedImages.gameId
                logMessage =
                    ("Call for reset vision image capture for reason: ${effect.reason} of ${effect.recordedImages.imageKills}, ${effect.recordedImages.imageRatings}\n")
                effect.recordedImages.imageRatings?.let { deleteFileOnPath(it) }
                effect.recordedImages.imageKills?.let { deleteFileOnPath(it) }
            }
        }
        LabelUtils.testLogGrey(logMessage)
        logWithIdentifier(gameId) {
            it.setMessage(logMessage)
            it.setCategory(LogCategory.ENGINE)
        }
    }

    companion object {
        @Volatile
        private var instance: VisionHandler? = null
        operator fun invoke() = synchronized(this) {
            if (!test()) // for debug
                if (instance == null) instance = VisionHandler()

            instance
        }
    }
}

