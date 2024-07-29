package com.gamerboard.live.gamestatemachine.games

import android.util.Log
import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.gamestatemachine.games.bgmi.BGMILabelProcessor
import com.gamerboard.live.gamestatemachine.processor.Handle
import com.gamerboard.live.gamestatemachine.stateMachine.*
import com.gamerboard.live.models.ImageResultJsonFlat
import com.gamerboard.live.models.TFResult
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.log
import com.gamerboard.logger.logWithCategory
import com.gamerboard.logger.logWithIdentifier
import okhttp3.internal.immutableListOf

abstract class MachineLabelProcessor {
    var previousLoaderState = 0
    var loaderVisible = 0
    var unknownCount = 0
    var showLoaderCircularBuffer: MutableMap<List<Int>, Int> = mutableMapOf()
    var screenReceivedCounterBuffer: MutableMap<List<Int>, Int> = mutableMapOf()

    var readyToVerify = 0

    var firstHomeScreen = 0
    var profileScreenCount = 0
    var firstHomeScreenCount = 0
    var firstGameScreen = 0
    var firstGameEndScreen = 0
    var firstWaitingScreen = 0
    var firstWaitingScreenCount = 0
    var firstScoresScreen = 0
    var firstKillScreen = 0

    private var bufferCount = -1
    var currentBuffer = HashMap<List<Int>, ArrayList<ImageResultJsonFlat>>()
    private var previousBucket = MachineConstants.gameConstants.unknownScreenBucket()

    /**
     * Returns Boolean: do we actually need to run OCR base on the label and
    current state of the state machine. The input is [_input] and returns boolean.
     * */
    fun shouldRunOcr(input: List<TFResult>): Boolean {
        val original = LabelUtils.getListOfLabels(input)
        val cur: List<Int> = getBucket(LabelUtils.getListOfLabels(input))
        val shouldRun: Boolean

        when (cur) {

            (MachineConstants.gameConstants.homeScreenBucket()) -> {
                shouldRun = usefulHomeScreen(original)
            }

            (MachineConstants.gameConstants.waitingScreenBucket()) -> {
                shouldRun = true
            }

            (MachineConstants.gameConstants.gameScreenBucket()) -> {
                shouldRun = false
            }

            (MachineConstants.gameConstants.resultRankRating()) -> {
                shouldRun = true
            }

            (MachineConstants.gameConstants.gameEndScreen()) -> {
                shouldRun = true
            }

            (MachineConstants.gameConstants.resultRankKills()) -> {
                shouldRun = true
            }

            (MachineConstants.gameConstants.loginScreenBucket()) -> {
                shouldRun = true
            }

            else -> {
                shouldRun = false
            }
        }
        Log.d("shouldRunOcr", "runOcr = $shouldRun,  state = ${StateMachine.machine.state}")
        return shouldRun
    }

    /**
     * Stores continuous labels from stream up to max size and passes to buffer processor.
     * The input is [_input].
     * */
    fun processInputBuffer(_input: ImageResultJsonFlat) {
        var input: ImageResultJsonFlat? = null
        input = _input
        val labels = LabelUtils.getListOfLabels(input.labels)
        val inferredBucket = getBucket(labels)
        val curBufferSize: Int =
            MachineConstants.gameConstants.labelBufferSize()[inferredBucket] ?: return
        shouldResetBuffer(inferredBucket)
        currentBuffer.putIfAbsent(inferredBucket, arrayListOf())
        currentBuffer[inferredBucket]!!.add(input)
        bufferCount = currentBuffer[inferredBucket]!!.size


        if (bufferCount >= curBufferSize) {
            LabelUtils.testLogGrey(
                "Buffer Full : $bufferCount/$curBufferSize  CurrLabel: $inferredBucket  Top Label:${currentBuffer[inferredBucket]!!.last().labels}"
            )
            /*val fromBuffer = arrayListOf<ImageResultJsonFlat>()
            fromBuffer.addAll(currentBuffer[inferredBucket]!!)*/
            extractInfoFromBuffer(currentBuffer[inferredBucket]!!, inferredBucket)
        }


        if (bufferCount >= MAX_BUFFER_LIMIT) {
            // this behaves as the queue, we remove the oldest one to fit the new coming screen labels.
            if (currentBuffer[inferredBucket] != null && currentBuffer[inferredBucket]?.isNotEmpty()!!) currentBuffer[inferredBucket]?.removeAt(
                0
            )
        }
    }

    /**
     *There is some cases where the waiting screen reads the game labels, so if we get a waiting screen
    we clear the stack of gameScreen labels. The labels(In-game) will have to come continuously to start the game.
     */
    private fun shouldResetBuffer(inferredBucket: List<Int>) {
        if (inferredBucket == MachineConstants.gameConstants.waitingScreenBucket()) {
            currentBuffer[MachineConstants.gameConstants.gameScreenBucket()]?.clear()
        }

        if (inferredBucket == MachineConstants.gameConstants.gameScreenBucket()) {
            currentBuffer[MachineConstants.gameConstants.waitingScreenBucket()]?.clear()
        }

        if (inferredBucket == MachineConstants.gameConstants.gameEndScreen()) {
            currentBuffer[MachineConstants.gameConstants.waitingScreenBucket()]?.clear()
        }
    }

    /**
    Returns the nearest matching labels to the input labels. The buckets are nothing but an integer with the set bits at the
    label's index in [GameLabels]. Method [isSubsetOf] checks for the positions where the bits match the bucket labels and the
    actual received labels. See existing buckets to get the idea, e.g. [resultRankKills]
     */
    abstract fun getBucket(cur: List<Int>): List<Int>


    /** Now we receive the Buffer with size up to max size for the label, now your goal is to process them
    and reduce it to a single and valid information, if enough confidence pass to state machine else discard. */
    private fun extractInfoFromBuffer(
        inputBuffer: ArrayList<ImageResultJsonFlat>, curLabels: List<Int>
    ) {
        val machine = StateMachine.machine
        assert(inputBuffer.size != 0)
        Log.d("extractInfoFromBuffer", "labels $curLabels BufferSize:${inputBuffer.size}")
        var result: MachineResult? = null
        LabelUtils.testLogRed(curLabels.toString())

        when (curLabels) {

            (MachineConstants.gameConstants.homeScreenBucket()) -> {
//                log("clearValidatorScreenCache-homeScreenBucket")
//                clearValidatorScreenCache()
                result = MachineConstants.machineInputValidator.validateWithNoOcr(inputBuffer)
                if (profileScreenCount > 4 && curLabels.containsAll(MachineConstants.gameConstants.myProfileScreen())) {
                    MachineConstants.machineInputValidator.validateProfile(inputBuffer)
                }
            }

            (MachineConstants.gameConstants.gameScreenBucket()) -> {
                logWithIdentifier(GameHelper.getOriginalGameId(),"clearValidatorScreenCache-gameScreenBucket")
                clearValidatorScreenCache()
                result = MachineConstants.machineInputValidator.validateInGameWithNoOCR(inputBuffer)
            }

            (MachineConstants.gameConstants.gameEndScreen()) -> {
                clearValidatorScreenCache()
                result = MachineConstants.machineInputValidator.validateGameEnd(inputBuffer)
            }

            (MachineConstants.gameConstants.resultRankRating()) -> {
                result =
                    MachineConstants.machineInputValidator.validateRankRatingGameInfo(inputBuffer)
            }

//            (MachineConstants.gameConstants.rating()) -> {
//                result = MachineConstants.machineInputValidator.validateRating(inputBuffer)
//            }


            (MachineConstants.gameConstants.resultRankKills()) -> {
                val state = StateMachine.machine.state
                var originalBGBICharacterID: String? = null
                if (state is VerifiedUser) {
                    originalBGBICharacterID = (state as VerifiedUser).verifiedUserDetails.gameCharId
                } else {
                    logWithCategory(
                        "Could not send for auto ml, user was un verified!:",
                        category = LogCategory.ENGINE
                    )
                }
                result = MachineConstants.machineInputValidator.validateRankKillGameInfo(
                    inputBuffer, originalBGBICharacterID = originalBGBICharacterID
                )
            }

//            (MachineConstants.gameConstants.performanceScreen()) -> {
//                val state = StateMachine.machine.state
//                var originalBGBICharacterID: String? = null
//                if (state is VerifiedUser) {
//                    originalBGBICharacterID = (state as VerifiedUser).verifiedUserDetails.gameCharId
//                } else {
//                    MachineMessageBroadcaster.invoke()?.logToFile(
//                        "Could not send for auto ml, user was un verified!:",
//                        category = LogCategory.ENGINE
//                    )
//                }
//                result = MachineConstants.machineInputValidator.validatePerformanceScreen(
//                    inputBuffer, originalBGBICharacterID = originalBGBICharacterID
//                )
//            }

            (MachineConstants.gameConstants.waitingScreenBucket()) -> {
                log("clearValidatorScreenCache-waitingScreenBucket")
                clearValidatorScreenCache()
                result = MachineConstants.machineInputValidator.validateWaiting(inputBuffer)
            }

            (MachineConstants.gameConstants.loginScreenBucket()) -> {
                if (machine.state is VerifiedUser) {
                    val login = MachineConstants.machineInputValidator.validateLogin(inputBuffer)
                    if (login.accept == true && login.login == true) {
                        machine.transition(Event.UnVerifyUser(reason = "User un verified as login screen detected!"))
                    }
                }
                log("clearValidatorScreenCache-loginScreenBucket")
                clearValidatorScreenCache()
                result = MachineConstants.machineInputValidator.validateWithNoOcr(inputBuffer)
            }


            else -> {

            }
        }

        if (result != null && result.accept == true) {
            LabelUtils.testLogGreen(
                "Useful info extracted for :$curLabels :${result}"
            )
            Handle.handle(result, curLabels)
            if ((((curLabels != MachineConstants.gameConstants.resultRankRating())) && ((curLabels != MachineConstants.gameConstants.resultRankKills()))) || (curLabels != previousBucket)) {
                currentBuffer.clear()
                previousBucket = curLabels
            }
        } else {
            LabelUtils.testLogGrey(
                "No Useful info extracted for :$curLabels"
            )
        }
    }


    /** We have some labels present on more than one screens [GameLabels.GAME_INFO], so to use the previous screen labels we
     * store them in global arrays.  Check in [MachineInputValidator.validateRankKillGameInfo] to see how this is used. We clear this with
     * the reset of state machine*/
    fun clearValidatorScreenCache() {
        MachineConstants.machineInputValidator.clear()
    }

    /**
     * These flags are the flags that help with the first screen detection and perform an action only once.
     * check [BGMILabelProcessor.captureFirstScreen] and [BGMILabelProcessor.shouldShoLoader] to see how these are used.
     * */
    fun resetUIScreens() {
        previousLoaderState = 0
        loaderVisible = 0
        unknownCount = 0
        showLoaderCircularBuffer = mutableMapOf()
        firstHomeScreen = 0
        profileScreenCount = 0
        firstHomeScreenCount = 0
        readyToVerify = 0
        firstWaitingScreen = 0
        firstScoresScreen = 0
        firstWaitingScreenCount = 0
    }




    /**
    This method records and performs action when for the first time a screen is visible.
    e.g an action when the user is on the home screen for the first time.
     */
    protected abstract fun captureFirstScreen(
        bucket: List<Int>,
        original: List<Int> = MachineConstants.gameConstants.unknownScreenBucket()
    )

    /**
     * Shows a progress(fetching data) loader on the screen based on the screen(bucket) and the state of
    the state machine. e.g is the state is game started then when the user reaches the result screen
    we show `fetching rating....`.
     */
    fun shouldShoLoader(
        bucket: List<Int>,
        original: List<Int> = MachineConstants.gameConstants.unknownScreenBucket()
    ): Boolean {
        showLoaderCircularBuffer[bucket] = 1 + (showLoaderCircularBuffer[bucket] ?: 0)
        var showLoader = 0
        var screenName = MachineConstants.ScreenName.OTHER
        var message = ""
//        var showInCenter = false
        val state = StateMachine.machine.state
        captureFirstScreen(bucket, original)

        performActionOnScreenLabel(bucket, original)

        when (bucket) {

            (MachineConstants.gameConstants.homeScreenBucket()) -> {
                val useful = usefulHomeScreen(original)
                if (!useful) showLoaderCircularBuffer[MachineConstants.gameConstants.homeScreenBucket()] =
                    0/*showLoaderCircularBuffer[MachineConstants.gameConstants.homeScreenBucket()] =
                    -1 + (showLoaderCircularBuffer[MachineConstants.gameConstants.homeScreenBucket()]
                        ?: 0)*/

                if (useful && readyToVerify == 1) showLoader =
                    if ((showLoaderCircularBuffer[MachineConstants.gameConstants.homeScreenBucket()]
                            ?: 0) > TIMER_COUNT_PROFILE_VERIFY || (state is VerifiedUser)
                    ) 0 else 1
                message = "Fetching profile, please wait.."
                unknownCount = 0
                //Machine.stateMachine.transition(GameEvent.OnGameEnd)
            }

            (MachineConstants.gameConstants.resultRankRating()) -> {
                Log.d("DEBUG_LOADER", "resultRankRating")
                screenName = MachineConstants.ScreenName.RATING
                // show loader if not already
                // up to x captures
                if (StateMachine.machine.state is State.FetchResult) showLoader =
                    if ((showLoaderCircularBuffer[MachineConstants.gameConstants.resultRankRating()]
                            ?: 0) > TIMER_COUNT_RATING_RANK
                    ) 0 else 1

                message = "Fetching rating, please wait.."
                unknownCount = 0
                Log.d("DEBUG_LOADER", "showLoader $showLoader")
                Log.d(
                    "DEBUG_LOADER",
                    "showLoaderCircularBuffer ${showLoaderCircularBuffer[MachineConstants.gameConstants.resultRankRating()]}"
                )
                showLoaderCircularBuffer[MachineConstants.gameConstants.resultRankKills()] = 0
            }

            (MachineConstants.gameConstants.resultRankKills()) -> {
                Log.d("DEBUG_LOADER", "resultRankKills")
                screenName = MachineConstants.ScreenName.KILLS
                if (StateMachine.machine.state is State.FetchResult) showLoader =
                    if ((showLoaderCircularBuffer[MachineConstants.gameConstants.resultRankKills()]
                            ?: 0) > TIMER_COUNT_RANK_KILLS
                    ) 0 else 1
                message = "Fetching your kills. Please wait.."
                unknownCount = 0

                if ((showLoaderCircularBuffer[MachineConstants.gameConstants.resultRankKills()]
                        ?: 0) > 15
                ) if (StateMachine.machine.state is State.FetchResult) StateMachine.machine.transition(
                    Event.GameCompleted(reason = "Received more that 15 images for the kills screen for this game!")
                )

                Log.d("DEBUG_LOADER", "showLoader $showLoader")
                Log.d(
                    "DEBUG_LOADER",
                    "showLoaderCircularBuffer ${showLoaderCircularBuffer[MachineConstants.gameConstants.resultRankKills()]}"
                )

            }

            (MachineConstants.gameConstants.waitingScreenBucket()) -> {
                // Hide, if visible
                showLoader = 0
                unknownCount = 0
                showLoaderCircularBuffer.clear()
            }

            (MachineConstants.gameConstants.gameScreenBucket()) -> {
                showLoader = 0
                unknownCount = 0
                showLoaderCircularBuffer.clear()
            }

            (MachineConstants.gameConstants.gameEndScreen()) -> {
                showLoader = 0
                unknownCount = 0
                showLoaderCircularBuffer.clear()
            }

            (MachineConstants.gameConstants.loginScreenBucket()) -> {
                showLoader = 0
                unknownCount = 0
                showLoaderCircularBuffer.clear()
            }


            else -> {
                unknownCount++
                if (loaderVisible == 1 && unknownCount > UNKNOWN_TO_STOP_LOADER) {
                    showLoader = 0
                    unknownCount = 0
                } else if (message.contains("Loading").not()) showLoader = loaderVisible
            }
        }
        return showLoaderUI(showLoader, message, screenName) == 1
    }


    /**
     * The bucket returned form [getBucket] method is a constant but the original labels
     * might not have all the labels...or some extra labels. So if you want to perform some action over the UI based on the
     * exact labels being received use this method.
     * This method have two parts, one performs action based on the [bucket] and the other one based on [originalBucket]
     * */
    private fun performActionOnScreenLabel(bucket: List<Int>, originalBucket: List<Int>) {
        screenReceivedCounterBuffer[bucket] = 1 + (screenReceivedCounterBuffer[bucket] ?: 0)

        // if the actual labels have some extra or less labels than the bucket.
        if (originalBucket != bucket) screenReceivedCounterBuffer[originalBucket] =
            1 + (screenReceivedCounterBuffer[originalBucket] ?: 0)

        val machine = StateMachine.machine

        // perform actions based on the screen bucket it lies in
        when (bucket) {
            (MachineConstants.gameConstants.homeScreenBucket()) -> {
                screenReceivedCounterBuffer.clear()
            }

            (MachineConstants.gameConstants.resultRankRating()) -> {
                screenReceivedCounterBuffer[MachineConstants.gameConstants.resultRankKills()] = 0
            }

            (MachineConstants.gameConstants.resultRankKills()) -> {
                if ((screenReceivedCounterBuffer[MachineConstants.gameConstants.resultRankKills()]
                        ?: 0) > 15
                ) if (machine.state is State.FetchResult) machine.transition(
                    Event.GameCompleted(
                        reason = "Received more that 15 images for the kills screen for this game!"
                    )
                )

            }

//            (MachineConstants.gameConstants.performanceScreen()) -> {
//                if ((screenReceivedCounterBuffer[MachineConstants.gameConstants.performanceScreen()]
//                        ?: 0) > 15
//                ) if (machine.state is State.FetchResult) machine.transition(
//                    Event.GameCompleted(
//                        reason = "Received more that 15 images for the performance screen for this game!"
//                    )
//                )
//            }
            (MachineConstants.gameConstants.gameEndScreen()) -> {
                screenReceivedCounterBuffer.clear()
            }

            (MachineConstants.gameConstants.waitingScreenBucket()) -> {
                screenReceivedCounterBuffer.clear()
            }

            (MachineConstants.gameConstants.gameScreenBucket()) -> {
                screenReceivedCounterBuffer.clear()
            }

            (MachineConstants.gameConstants.loginScreenBucket()) -> {
                screenReceivedCounterBuffer.clear()
            }

            else -> {

            }
        }

        // perform action based on the actual labels.
        when (originalBucket) {
            (MachineConstants.gameConstants.resultRank()) -> {
                if ((screenReceivedCounterBuffer[MachineConstants.gameConstants.resultRank()]
                        ?: 0) > 10
                ) if (machine.state is State.FetchResult && MachineConstants.currentGame == SupportedGames.BGMI) machine.transition(
                    Event.GameCompleted(
                        reason = "Squad game ended, the player died early!"
                    )
                )
            }

            else -> {

            }
        }
    }


    /**
     * Call to display the loader on the UI with the passed message.
     * */
    private fun showLoaderUI(
        show: Int,
        message: String,
        screenName: MachineConstants.ScreenName
    ): Int {
//        if (show != loaderVisible) {
        loaderVisible = show
        if (!test()) MachineMessageBroadcaster.invoke()
            ?.showLoader(show == 1, message, screenName.ordinal)
//        }
        return loaderVisible
    }

    /**
     * Returns whether the home screen is actually user's home screen
    and not of someone's else's profile....then we show `fetching profile`.

    We perform some other actions on the home screen, but we only fetch
    the profile if we know user is on his profile.
     * */
    protected abstract fun usefulHomeScreen(original: List<Int> = immutableListOf(0)): Boolean


    enum class LabelSortOrder { HORIZONTAL, VERTICAL, SKIP, PERFORMANCE }

    fun getBucketFromString(bucketName: String): List<Int> {
        when (bucketName) {
            "homeScreen" -> return MachineConstants.gameConstants.homeScreenBucket()
            "waitingScreen" -> return MachineConstants.gameConstants.waitingScreenBucket()
            "gameScreen" -> return MachineConstants.gameConstants.gameScreenBucket()
            "rankRating" -> return MachineConstants.gameConstants.resultRankRating()
            "rankKill" -> return MachineConstants.gameConstants.resultRankKills()
            "gameEnd" -> return MachineConstants.gameConstants.gameEndScreen()
//            "performance" -> return MachineConstants.gameConstants.performanceScreen()
        }
        return MachineConstants.gameConstants.unknownScreenBucket()
    }
}