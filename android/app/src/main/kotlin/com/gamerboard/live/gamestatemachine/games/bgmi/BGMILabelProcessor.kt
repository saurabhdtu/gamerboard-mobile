package com.gamerboard.live.gamestatemachine.games.bgmi

import com.gamerboard.live.gamestatemachine.games.LabelUtils.isSameAs
import com.gamerboard.live.gamestatemachine.games.MachineLabelProcessor
import com.gamerboard.live.gamestatemachine.stateMachine.*

class BGMILabelProcessor : MachineLabelProcessor() {

    /**
    Returns the nearest matching labels to the input labels. The buckets are nothing but an integer with the set bits at the
    label's index in [GameLabels]. Method [isSameAs] checks for the positions where the bits match the bucket labels and the
    actual received labels. See existing buckets to get the idea, e.g. [resultRankKills]
     mandatory [true, true]
     non mandatory [true, false, true]
     */
    override fun getBucket(cur: List<Int>): List<Int> {

        if (MachineConstants.gameConstants.matchBucket(MachineConstants.gameConstants.buckets()?.homeScreenBucket!!.bucketFields, cur)) {
            return MachineConstants.gameConstants.homeScreenBucket()
        }

        if (MachineConstants.gameConstants.matchBucket(MachineConstants.gameConstants.buckets()?.gameScreenBucket!!.bucketFields, cur)) {
            return MachineConstants.gameConstants.gameScreenBucket()
        }

        if (MachineConstants.gameConstants.matchBucket(MachineConstants.gameConstants.buckets()?.gameScreenBucket!!.bucketFields, cur)) {
            return MachineConstants.gameConstants.gameScreenBucket()
        }

        if (MachineConstants.gameConstants.matchBucket(MachineConstants.gameConstants.buckets()?.resultRankRating!!.bucketFields, cur)) {
//            if (cur.contains(BGMIConstants.GameLabels.CLASSIC_RANK_GAME_INFO.ordinal) && cur.contains(
//                    BGMIConstants.GameLabels.CLASSIC_RATING.ordinal
//                )
//            ) {
                return MachineConstants.gameConstants.resultRankRating()
//            }
        }

         if (MachineConstants.gameConstants.matchBucket(MachineConstants.gameConstants.buckets()?.resultRankKills!!.bucketFields, cur)) {

            /* This case was removed because, in some cases the rank label
             might be detected from the rating screen. On rating screen we have rankRating label,
             on kills screen we have rank label. So we check it must have kill label to be recognized as kills screen.*/
//            if (cur.contains(BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal))
                return MachineConstants.gameConstants.resultRankKills()

        }

        if (MachineConstants.gameConstants.matchBucket(MachineConstants.gameConstants.buckets()?.loginScreenBucket!!.bucketFields, cur)) {
            return MachineConstants.gameConstants.loginScreenBucket()
        }

        return MachineConstants.gameConstants.unknownScreenBucket()
    }


    /**
    This method records and performs action when for the first time a screen is visible.
    e.g an action when the user is on the home screen for the first time.
     */
    override fun captureFirstScreen(bucket: List<Int>, original: List<Int>) {
        if (debugMachine != DEBUGGER.DISABLED && debugMachine != DEBUGGER.RUN_WITH_IMAGES)
            return



        when (bucket) {
            (MachineConstants.gameConstants.homeScreenBucket()) -> {
                // this is profile screen
                if (original.contains(MachineConstants.gameConstants.profileIdLabel())) {
                    firstHomeScreen = 1
                    profileScreenCount++
                    return
                }
                //first home screen
                if (firstHomeScreenCount > 1 && firstHomeScreen == 0) {
                    firstHomeScreen = 1
                    MachineMessageBroadcaster.invoke()?.firstTimeHomeScreen()
                }
                firstHomeScreenCount++
            }

            (MachineConstants.gameConstants.gameScreenBucket()) -> {
                if (firstGameScreen == 0 && firstWaitingScreen == 0) {
                    if (StateMachine.machine.state is State.GameStarted) {
                        firstGameScreen = 1
                        MachineMessageBroadcaster.invoke()?.firstGameScreen()
                    }
                }
                firstScoresScreen = 0
            }

            (MachineConstants.gameConstants.waitingScreenBucket()) -> {
                if (firstWaitingScreen == 0 && firstGameScreen == 0 && firstWaitingScreenCount > 2) {
                    if (StateMachine.machine.state is VerifiedUser) {
                        firstWaitingScreen = 1
                        MachineMessageBroadcaster.invoke()?.firstGameScreen()
                    }
                }
                firstWaitingScreenCount++
            }
            else -> {
                profileScreenCount = 0
            }
        }
    }


    /**
     * Returns whether the home screen is actually user's home screen
    and not of someone's else's profile....then we show `fetching profile`.

    We perform some other actions on the home screen, but we only fetch
    the profile if we know user is on his profile.
     * */
    override fun usefulHomeScreen(original: List<Int>): Boolean {
        return original.containsAll(
            MachineConstants.gameConstants.myProfileScreen()
        )
    }
}

